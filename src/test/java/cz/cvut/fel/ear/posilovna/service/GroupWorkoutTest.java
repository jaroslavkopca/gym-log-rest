package cz.cvut.fel.ear.posilovna.service;

import cz.cvut.fel.ear.posilovna.dao.GroupWorkoutDao;
import cz.cvut.fel.ear.posilovna.dao.MemberDao;
import cz.cvut.fel.ear.posilovna.dao.RoomDao;
import cz.cvut.fel.ear.posilovna.environment.Generator;
import cz.cvut.fel.ear.posilovna.exception.MemberNotFoundException;
import cz.cvut.fel.ear.posilovna.exception.RoomNotAvailableException;
import cz.cvut.fel.ear.posilovna.model.GroupWorkout;
import cz.cvut.fel.ear.posilovna.model.Member;
import cz.cvut.fel.ear.posilovna.model.Room;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@Transactional
@SpringBootTest
public class GroupWorkoutTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private GroupWorkoutService groupWorkoutService;

    @Autowired
    private GroupWorkoutDao groupWorkoutDao;

    @Autowired
    private RoomDao roomDao;

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private RoomService roomService;
    private static final Generator generator = Generator.getInstance();

    @Transactional
    @Test
    public void testCreateGroupWorkout() {
        String workoutName = "Test Workout";
        String timeFrom = "2023-01-01T12:00:00";
        String timeTo = "2023-01-01T13:00:00";
        int capacity = 10;

        Member member = generator.generateMember();
        memberDao.persist(member);
        int memberId = member.getId();

        Room room = new Room();
        roomDao.persist(room);
        int roomId = room.getId();


        final GroupWorkout result = groupWorkoutService.createGroupWorkout(memberId, workoutName, timeFrom, timeTo, capacity, roomId);

        assertEquals(result.getAuthor(),member);

    }
    @Transactional
    @Test
    public void testCreateGroupWorkoutWithInvalidMember() {
        // Arrange
        int memberId = 14;
        String workoutName = "Test Workout";
        String timeFrom = "2023-01-01T12:00:00";
        String timeTo = "2023-01-01T13:00:00";
        int capacity = 10;
        int roomId = 1;



        Room room = new Room();
        roomDao.persist(room);
        room.setId(1);
        roomDao.update(room);

        // Act and Assert
        assertThrows(MemberNotFoundException.class,
                () -> groupWorkoutService.createGroupWorkout(memberId, workoutName, timeFrom, timeTo, capacity, roomId));
    }
    @Transactional
    @Test
    public void testCreateGroupWorkoutWithUnavailableRoom() {
        String workoutName = "Test Workout";
        String timeFrom = "2023-01-01T12:00:00";
        String timeTo = "2023-01-01T13:00:00";
        int capacity = 10;

        Member member = generator.generateMember();
        memberDao.persist(member);
        int memberId = member.getId();

        Room room = new Room();
        roomDao.persist(room);
        int roomId = room.getId();

        groupWorkoutService.createGroupWorkout(memberId, workoutName, timeFrom, timeTo, capacity, roomId);

        assertThrows(RoomNotAvailableException.class,
                () -> groupWorkoutService.createGroupWorkout(memberId, workoutName, timeFrom, timeTo, capacity, roomId));
    }
}
