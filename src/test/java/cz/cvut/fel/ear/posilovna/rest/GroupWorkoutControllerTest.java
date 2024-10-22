package cz.cvut.fel.ear.posilovna.rest;

import cz.cvut.fel.ear.posilovna.config.SecurityConfig;
import cz.cvut.fel.ear.posilovna.environment.Environment;
import cz.cvut.fel.ear.posilovna.environment.Generator;
import cz.cvut.fel.ear.posilovna.environment.TestConfiguration;
import cz.cvut.fel.ear.posilovna.model.Client;
import cz.cvut.fel.ear.posilovna.model.GroupWorkout;
import cz.cvut.fel.ear.posilovna.model.Member;
import cz.cvut.fel.ear.posilovna.model.Role;
import cz.cvut.fel.ear.posilovna.rest.util.GroupWorkoutObject;
import cz.cvut.fel.ear.posilovna.security.model.UserDetails;
import cz.cvut.fel.ear.posilovna.service.ClientService;
import cz.cvut.fel.ear.posilovna.service.GroupWorkoutService;
import cz.cvut.fel.ear.posilovna.service.MemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

import javax.swing.*;
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

@WebMvcTest(controllers = GroupWorkoutController.class)
@ContextConfiguration(
        classes = {GroupWorkoutControllerTest.TestConfig.class,
                SecurityConfig.class})
public class GroupWorkoutControllerTest extends BaseControllerTestRunner {

    @Autowired
    private MockMvc mockMvc;

    private Client user;

    @Autowired
    private GroupWorkoutService groupWorkoutService;

    private static final Generator generator = Generator.getInstance();

    @BeforeEach
    public void setUp() {
        this.objectMapper = Environment.getObjectMapper();
        this.user = generator.generateClient();
    }

    @AfterEach
    public void tearDown() {
        Environment.clearSecurityContext();
        Mockito.reset(groupWorkoutService);
    }

    @Configuration
    @TestConfiguration
    public static class TestConfig {

        @MockBean
        private static GroupWorkoutService groupWorkoutService;

        @MockBean
        private static ClientService clientService;

        @Bean
        public GroupWorkoutController groupWorkoutController() {
            return new GroupWorkoutController(groupWorkoutService, clientService);
        }

        public static GroupWorkoutService getGroupWorkoutService() {
            return groupWorkoutService;
        }

    }

    @WithAnonymousUser
    @Test
    public void createWorkout_NotAuthorized() throws Exception {
        // Arrange
        String workoutName = "Your Workout Name";
        String timeFrom = "Your Time From";
        String timeTo = "Your Time To";
        int capacity = 10;
        int roomId = 1;

        // Create a request object
        GroupWorkoutObject request = new GroupWorkoutObject();
        request.setWorkoutName(workoutName);
        request.setTimeFrom(timeFrom);
        request.setTimeTo(timeTo);
        request.setCapacity(capacity);
        request.setRoomId(roomId);

        // Convert the request object to JSON
        String jsonPayload = objectMapper.writeValueAsString(request);

        // Act & Assert
        mockMvc.perform(post("/rest/groupworkout")
                        .content(jsonPayload)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isUnauthorized());
    }
    //@WithMockUser(roles = "MEMBER_WITH_MEMBERSHIP")
    @Test
    public void createWorkout_canBeCreatedByMemberWithMembership() throws Exception {
        user.setRole(Role.MEMBER_WITH_MEMBERSHIP);
        Environment.setCurrentClient(user);
        // Arrange
        String workoutName = "Your Workout Name";
        String timeFrom = "2023-01-01T12:00:00";
        String timeTo = "2023-01-01T13:00:00";
        int capacity = 10;
        int roomId = 1;

        // Create a request object
        GroupWorkoutObject request = new GroupWorkoutObject();
        request.setWorkoutName(workoutName);
        request.setTimeFrom(timeFrom);
        request.setTimeTo(timeTo);
        request.setCapacity(capacity);
        request.setRoomId(roomId);

        // Convert the request object to JSON
        String jsonPayload = objectMapper.writeValueAsString(request);

        when(groupWorkoutService.createGroupWorkout(0, workoutName, timeFrom, timeTo, capacity, roomId))
                .thenReturn(new GroupWorkout());

        // Act & Assert
        mockMvc.perform(post("/rest/groupworkout")
                        .content(jsonPayload)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated());
    }
}