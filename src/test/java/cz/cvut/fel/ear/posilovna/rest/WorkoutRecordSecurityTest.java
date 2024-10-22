package cz.cvut.fel.ear.posilovna.rest;

import cz.cvut.fel.ear.posilovna.config.SecurityConfig;
import cz.cvut.fel.ear.posilovna.environment.Environment;
import cz.cvut.fel.ear.posilovna.environment.Generator;
import cz.cvut.fel.ear.posilovna.environment.TestConfiguration;
import cz.cvut.fel.ear.posilovna.model.Client;
import cz.cvut.fel.ear.posilovna.model.Member;
import cz.cvut.fel.ear.posilovna.model.Role;
import cz.cvut.fel.ear.posilovna.model.WorkoutRecord;
import cz.cvut.fel.ear.posilovna.rest.facade.MemberStatisticsFacadeImpl;
import cz.cvut.fel.ear.posilovna.service.ClientService;
import cz.cvut.fel.ear.posilovna.service.MemberService;
import cz.cvut.fel.ear.posilovna.service.WorkoutRecordService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static cz.cvut.fel.ear.posilovna.environment.Environment.setCurrentClient;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(controllers = MemberController.class)
@ContextConfiguration(
        classes = {WorkoutRecordSecurityTest.TestConfig.class, SecurityConfig.class})
public class WorkoutRecordSecurityTest extends BaseControllerTestRunner {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberService memberService;

    @Autowired
    private WorkoutRecordService workoutRecordService;

    private Client user;
    private static final Generator generator = Generator.getInstance();

    @BeforeEach
    public void setUp() {
        this.objectMapper = Environment.getObjectMapper();
        this.user = generator.generateClient();
    }

    @AfterEach
    public void tearDown() {
        Environment.clearSecurityContext();
        Mockito.reset(memberService, workoutRecordService);
    }

    @Configuration
    @TestConfiguration
    public static class TestConfig {

        @MockBean
        private MemberService memberService;
        @MockBean
        private WorkoutRecordService workoutRecordService;
        @MockBean
        private ClientService clientService;
        @MockBean
        private MemberStatisticsFacadeImpl memberFacade;

        @Bean
        public MemberController memberController() {
            return new MemberController(memberService, clientService, workoutRecordService, memberFacade);
        }
    }

    @WithAnonymousUser
    @Test
    public void createWorkoutRecordFromAnonymousUser_thenReturnUnauthorized() throws Exception {
        final WorkoutRecord toCreate = new WorkoutRecord();
        toCreate.setDuration(15);
        mockMvc.perform(
                        post("/rest/member/2/workoutrecords").content(toJson(toCreate)).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isUnauthorized());
        verify(memberService, never()).persist(any());
    }

    @Test
    public void createWorkoutRecordToMemberWithoutMembership_thenReturnForbidden() throws Exception {
        Client client = generator.generateClient();
        client.setRole(Role.MEMBER);
        Member member = generator.generateMember();
        member.setId(3);
        client.setMember(member);
        setCurrentClient(client);
        final WorkoutRecord toCreate = new WorkoutRecord();
        toCreate.setDuration(15);
        when(memberService.getMemberById(member.getId().longValue())).thenReturn(member);
        mockMvc.perform(
                        post("/rest/member/2/workoutrecords").content(toJson(toCreate)).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden());
        verify(memberService, never()).persist(any());
    }

    @Test
    public void createWorkoutRecordToDifferentMember_thenReturnForbidden() throws Exception {
        Client client = generator.generateClient();
        client.setRole(Role.MEMBER_WITH_MEMBERSHIP);
        Member member = generator.generateMember();
        member.setId(3);
        client.setMember(member);
        setCurrentClient(client);
        final WorkoutRecord toCreate = new WorkoutRecord();
        toCreate.setDuration(15);
        when(memberService.getMemberById(member.getId().longValue())).thenReturn(member);
        mockMvc.perform(
                        post("/rest/member/2/workoutrecords").content(toJson(toCreate)).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden());
        verify(memberService, never()).persist(any());
    }

    @Test
    public void createWorkoutRecordToMember_thenReturnOk() throws Exception {
        Client client = generator.generateClient();
        client.setRole(Role.MEMBER_WITH_MEMBERSHIP);
        Member member = generator.generateMember();
        member.setId(3);
        client.setMember(member);
        setCurrentClient(client);
        final WorkoutRecord toCreate = new WorkoutRecord();
        toCreate.setDuration(15);
        when(memberService.getMemberById(member.getId().longValue())).thenReturn(member);
        mockMvc.perform(
                        post("/rest/member/3/workoutrecords").content(toJson(toCreate)).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated());
        final ArgumentCaptor<WorkoutRecord> captor = ArgumentCaptor.forClass(WorkoutRecord.class);
        verify(memberService).addWorkoutRecord(eq(member), captor.capture());
        assertEquals(toCreate.getId(), captor.getValue().getId());
    }

    @WithAnonymousUser
    @Test
    public void listWorkoutRecordsFromAnonymousUser_thenReturnUnauthorized() throws Exception {
        final WorkoutRecord toCreate = new WorkoutRecord();
        toCreate.setDuration(15);
        mockMvc.perform(
                        get("/rest/member/2/workoutrecords"))
                .andExpect(status().isUnauthorized());
        verify(memberService, never()).persist(any());
    }

    @Test
    public void listWorkoutRecordsToMemberWithoutMembership_thenReturnForbidden() throws Exception {
        Client client = generator.generateClient();
        client.setRole(Role.MEMBER);
        Member member = generator.generateMember();
        member.setId(3);
        client.setMember(member);
        setCurrentClient(client);
        final WorkoutRecord toCreate = new WorkoutRecord();
        toCreate.setDuration(15);
        when(memberService.getMemberById(member.getId().longValue())).thenReturn(member);
        mockMvc.perform(
                        get("/rest/member/2/workoutrecords"))
                .andExpect(status().isForbidden());
        verify(memberService, never()).persist(any());
    }

    @Test
    public void listWorkoutRecordsToDifferentMember_thenReturnForbidden() throws Exception {
        Client client = generator.generateClient();
        client.setRole(Role.MEMBER);
        Member member = generator.generateMember();
        member.setId(3);
        client.setMember(member);
        setCurrentClient(client);
        final WorkoutRecord toCreate = new WorkoutRecord();
        toCreate.setDuration(15);
        when(memberService.getMemberById(member.getId().longValue())).thenReturn(member);
        mockMvc.perform(
                        get("/rest/member/2/workoutrecords"))
                .andExpect(status().isForbidden());
        verify(memberService, never()).persist(any());
    }

    @Test
    public void listWorkoutRecordsToMember_thenReturnList() throws Exception {
        Client client = generator.generateClient();
        client.setRole(Role.MEMBER_WITH_MEMBERSHIP);
        Member member = generator.generateMember();
        member.setId(3);
        client.setMember(member);
        setCurrentClient(client);
        final WorkoutRecord toCreate = new WorkoutRecord();
        toCreate.setDuration(15);
        when(memberService.getMemberById(member.getId().longValue())).thenReturn(member);
        mockMvc.perform(
                        get("/rest/member/3/workoutrecords"))
                .andExpect(status().isOk());
        verify(memberService, never()).persist(any());
    }
}
