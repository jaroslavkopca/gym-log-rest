package cz.cvut.fel.ear.posilovna.rest;


import cz.cvut.fel.ear.posilovna.config.SecurityConfig;
import cz.cvut.fel.ear.posilovna.environment.Environment;
import cz.cvut.fel.ear.posilovna.environment.Generator;
import cz.cvut.fel.ear.posilovna.environment.TestConfiguration;
import cz.cvut.fel.ear.posilovna.model.Client;
import cz.cvut.fel.ear.posilovna.model.Member;
import cz.cvut.fel.ear.posilovna.model.Role;
import cz.cvut.fel.ear.posilovna.rest.facade.MemberStatisticsFacadeImpl;
import cz.cvut.fel.ear.posilovna.security.model.UserDetails;
import cz.cvut.fel.ear.posilovna.service.ClientService;
import cz.cvut.fel.ear.posilovna.service.MemberService;
import cz.cvut.fel.ear.posilovna.service.WorkoutRecordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.ArrayList;

import static cz.cvut.fel.ear.posilovna.environment.Environment.setCurrentClient;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MemberController.class)
@ContextConfiguration(
        classes = {MemberControllerSecurityTest.TestConfig.class,
                SecurityConfig.class})
public class MemberControllerSecurityTest extends BaseControllerTestRunner {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberService memberService;
    @Autowired
    private ClientService clientService;

    private Member member;
    private Client client;
    private static final Generator generator = Generator.getInstance();

    @BeforeEach
    public void setUp() {
        this.objectMapper = Environment.getObjectMapper();
        this.client = generator.generateClient();
        this.member = generator.generateMember();
    }

    @AfterEach
    public void tearDown() {
        Environment.clearSecurityContext();
        Mockito.reset(memberService);
    }

    @Configuration
    @TestConfiguration
    public static class TestConfig {

        @MockBean
        private static MemberService memberService;
        @MockBean
        private static ClientService clientService;
        @MockBean
        private static MemberStatisticsFacadeImpl memberFacade;
        @Mock
        private static WorkoutRecordService workoutRecordService;


        @Bean
        public MemberController memberController() {
            return new MemberController(memberService, clientService, workoutRecordService, memberFacade);
        }

        public static MemberService getMemberService() {
            return memberService;
        }

        public static ClientService getClientService() {
            return clientService;
        }
    }


    @WithAnonymousUser
    @Test
    public void createClient_Success() throws Exception {
        // Arrange
        final Client toRegister = generator.generateClient();
        String jsonRegister = toJson(toRegister);

        when(clientService.findByUsername(toRegister.getUsername())).thenReturn(null);
        when(clientService.createClient(any(Client.class))).thenReturn(toRegister);

        // Act & Assert
        mockMvc.perform(post("/rest/member/createClient")
                        .content(jsonRegister)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
        verify(clientService).findByUsername(toRegister.getUsername());
        verify(clientService).createClient(any(Client.class));
        // Do not verify persist if it's internally handled by createClient
    }


    @WithAnonymousUser
    @Test
    public void createClient_AlreadyExists() throws Exception {
        // Arrange
        final Client toRegister = generator.generateClient();
        String username = toRegister.getUsername();
        String jsonRegister = toJson(toRegister);

        when(clientService.findByUsername(toRegister.getUsername())).thenReturn(toRegister);
        when(clientService.createClient(any(Client.class))).thenReturn(toRegister);


        // Act & Assert
        mockMvc.perform(
                        post("/rest/member/createClient")
                                .content(toJson(toRegister))
                                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
        verify(clientService).findByUsername(toRegister.getUsername());
        verify(memberService, never()).persist(any(Member.class));
    }

    @WithMockUser
    @Test
    public void createClient_AlreadyLoggedIn() throws Exception {
        // Arrange
        final Client toRegister = generator.generateClient();
        toRegister.setRole(Role.MEMBER);
        setCurrentClient(toRegister);

        // Act & Assert
        mockMvc.perform(
                        post("/rest/member/createClient")
                                .content(toJson(toRegister))
                                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
        verify(clientService, never()).findByUsername(toRegister.getUsername());
        verify(memberService, never()).persist(any(Member.class));
    }


    @Test
    public void whenRoleClient_thenActivateMembership() throws Exception {
        // Arrange
        Client client = generator.generateClient();
        client.setRole(Role.CLIENT);
        Member newMember = generator.generateMember();
        client.setMember(newMember);
        setCurrentClient(client);

        when(memberService.createMemberFromClient(any(Client.class))).thenReturn(newMember);
        when(memberService.activateMembership(any(Member.class))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/rest/member/payMembership")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals("Membership activated for client",
                        result.getResponse().getContentAsString()));
    }

    @Test
    public void whenRoleMember_thenActivateMembership() throws Exception {
        // Arrange
        Client client = generator.generateClient();
        client.setRole(Role.MEMBER);
        Member member = generator.generateMember();
        client.setMember(member);
        setCurrentClient(client);

        when(memberService.activateMembership(any(Member.class))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/rest/member/payMembership")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals("Membership activated for registered client",
                        result.getResponse().getContentAsString()));
    }


    @Test
    public void whenRoleClientLoggedIn_thenCreateMemberSuccess() throws Exception {
        Client client = generator.generateClient();
        client.setRole(Role.CLIENT);
        setCurrentClient(client); // Set the custom UserDetails

        Member newMember = generator.generateMember();
        when(memberService.createMemberFromClient(any(Client.class))).thenReturn(newMember);

        mockMvc.perform(post("/rest/member/register")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(memberService).createMemberFromClient(client);
    }

    @Test
    @WithAnonymousUser// User without ROLE_CLIENT
    public void whenUserWithoutRoleClient_thenAccessDenied() throws Exception {
        mockMvc.perform(post("/rest/member/register") // Adjust the URL based on your application
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized()); // Expecting 401
    }




    @Test
    @WithAnonymousUser
    public void whenUserWithoutRequiredRole_thenAccessDenied() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/rest/member/payMembership")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized()); // Expecting 401
    }


}

