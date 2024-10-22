package cz.cvut.fel.ear.posilovna.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import cz.cvut.fel.ear.posilovna.environment.Generator;
import cz.cvut.fel.ear.posilovna.model.*;
import cz.cvut.fel.ear.posilovna.rest.handler.ErrorInfo;
import cz.cvut.fel.ear.posilovna.service.ClientService;
import cz.cvut.fel.ear.posilovna.service.MemberService;
import cz.cvut.fel.ear.posilovna.service.WorkoutRecordService;
import org.hibernate.jdbc.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static cz.cvut.fel.ear.posilovna.environment.Environment.setCurrentClient;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class WorkoutRecordTest extends BaseControllerTestRunner  {
    @Mock
    private WorkoutRecordService workoutRecordServiceMock;

    @Mock
    private MemberService memberServiceMock;

    @Mock
    private ClientService clientServiceMock;
    private static final Generator generator = Generator.getInstance();

    @InjectMocks
    private MemberController sut;

    @BeforeEach
    public void setUp() {
        super.setUp(sut);
    }

    @Test
    public void listAllReturnsWorkoutRecordsByMemberService() throws Exception {

        Client client = generator.generateClient();
        client.setRole(Role.MEMBER_WITH_MEMBERSHIP);
        Member member = generator.generateMember();
        member.setId(3);
        client.setMember(member);
        setCurrentClient(client);

        final List<WorkoutRecord> workoutRecords = IntStream.range(0, 5).mapToObj(i -> {
            final WorkoutRecord w = new WorkoutRecord();
            w.setDuration(i);
            return w;
        }).toList();

        when(memberServiceMock.getMemberById(member.getId().longValue())).thenReturn(member);
        when(workoutRecordServiceMock.findAll(member)).thenReturn(workoutRecords);

        final MvcResult mvcResult = mockMvc.perform(get("/rest/member/3/workoutrecords")).andReturn();
        final List<WorkoutRecord> result = readValue(mvcResult, new TypeReference<List<WorkoutRecord>>() {});
        assertEquals(workoutRecords.size(), result.size());
        verify(workoutRecordServiceMock).findAll(member);
    }

    @Test
    public void memberServiceAddsWorkoutRecord() throws Exception {

        Client client = generator.generateClient();
        client.setRole(Role.MEMBER_WITH_MEMBERSHIP);
        Member member = generator.generateMember();
        member.setId(3);
        client.setMember(member);
        setCurrentClient(client);
        WorkoutRecord toCreate = new WorkoutRecord();
        toCreate.setDuration(60);

        when(memberServiceMock.getMemberById(member.getId().longValue())).thenReturn(member);

        mockMvc.perform(post("/rest/member/3/workoutrecords").content(toJson(toCreate)).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated());
        final ArgumentCaptor<WorkoutRecord> captor = ArgumentCaptor.forClass(WorkoutRecord.class);
        verify(memberServiceMock).addWorkoutRecord(eq(member),captor.capture());
        assertEquals(toCreate.getDuration(), captor.getValue().getDuration());
    }

    @Test
    public void createWorkoutRecordReturnsResponseWithLocationHeader() throws Exception {
        Client client = generator.generateClient();
        client.setRole(Role.MEMBER_WITH_MEMBERSHIP);
        Member member = generator.generateMember();
        member.setId(3);
        client.setMember(member);
        setCurrentClient(client);
        WorkoutRecord toCreate = new WorkoutRecord();
        toCreate.setId(5L);
        toCreate.setDuration(60);

        when(memberServiceMock.getMemberById(member.getId().longValue())).thenReturn(member);

        final MvcResult mvcResult = mockMvc
                .perform(post("/rest/member/3/workoutrecords").content(toJson(toCreate)).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated()).andReturn();
        verifyLocationEquals("/rest/member/3/workoutrecords/" + toCreate.getId(), mvcResult);
    }

    @Test
    public void getByIdReturnsMatchingWorkoutRecord() throws Exception {
        Client client = generator.generateClient();
        client.setRole(Role.MEMBER_WITH_MEMBERSHIP);
        Member member = generator.generateMember();
        member.setId(3);
        client.setMember(member);
        setCurrentClient(client);
        WorkoutRecord workoutRecord = new WorkoutRecord();
        workoutRecord.setId(5L);
        workoutRecord.setDuration(60);

        when(memberServiceMock.getMemberById(member.getId().longValue())).thenReturn(member);
        when(workoutRecordServiceMock.findById(member,5)).thenReturn(workoutRecord);

        final MvcResult mvcResult = mockMvc.perform(get("/rest/member/3/workoutrecords/" + workoutRecord.getId())).andReturn();
        final WorkoutRecord result = readValue(mvcResult, WorkoutRecord.class);
        assertNotNull(result);
        assertEquals(workoutRecord.getId(), result.getId());
        assertEquals(workoutRecord.getDuration(), result.getDuration());
    }

    @Test
    public void getByIdThrowsNotFoundForUnknownWorkoutRecordId() throws Exception {
        Client client = generator.generateClient();
        client.setRole(Role.MEMBER_WITH_MEMBERSHIP);
        Member member = generator.generateMember();
        member.setId(3);
        client.setMember(member);
        setCurrentClient(client);
        final int id = 16;
        when(memberServiceMock.getMemberById(member.getId().longValue())).thenReturn(member);
        final MvcResult mvcResult = mockMvc.perform(get("/rest/member/3/workoutrecords/" + id)).andExpect(status().isNotFound())
                .andReturn();
        final ErrorInfo result = readValue(mvcResult, ErrorInfo.class);
        assertNotNull(result);
        assertThat(result.getMessage(), containsString("Workout record identified by"));
        assertThat(result.getMessage(), containsString(Integer.toString(id)));
    }
    @Test
    public void removeWorkoutRecordRemovesWorkoutRecord() throws Exception {
        Client client = generator.generateClient();
        client.setRole(Role.MEMBER_WITH_MEMBERSHIP);
        Member member = generator.generateMember();
        member.setId(3);
        client.setMember(member);
        setCurrentClient(client);
        final WorkoutRecord workoutRecord = new WorkoutRecord();
        workoutRecord.setDuration(60);
        workoutRecord.setId(5L);
        when(memberServiceMock.getMemberById(member.getId().longValue())).thenReturn(member);
        when(workoutRecordServiceMock.findById(member,workoutRecord.getId().intValue())).thenReturn(workoutRecord);
        member.addWorkoutRecord(workoutRecord);
        mockMvc.perform(delete("/rest/member/3/workoutrecords/" + workoutRecord.getId()))
                .andExpect(status().isNoContent());
        verify(workoutRecordServiceMock).remove(workoutRecord);
    }
    @Test
    public void removeWorkoutRecordThrowsNotFoundForUnknownWorkoutRecordId() throws Exception {
        Client client = generator.generateClient();
        client.setRole(Role.MEMBER_WITH_MEMBERSHIP);
        Member member = generator.generateMember();
        member.setId(3);
        client.setMember(member);
        setCurrentClient(client);
        final WorkoutRecord workoutRecord = new WorkoutRecord();
        workoutRecord.setDuration(60);
        workoutRecord.setId(5L);
        member.addWorkoutRecord(workoutRecord);
        when(memberServiceMock.getMemberById(member.getId().longValue())).thenReturn(member);
        final int unknownId = 123;
        mockMvc.perform(delete("/rest/member/3/workoutrecords/" + unknownId))
                .andExpect(status().isNotFound());
        verify(workoutRecordServiceMock, never()).remove(any());

    }
}
