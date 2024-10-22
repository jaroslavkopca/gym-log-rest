package cz.cvut.fel.ear.posilovna.service;

import cz.cvut.fel.ear.posilovna.dao.GroupWorkoutDao;
import cz.cvut.fel.ear.posilovna.dao.RoomDao;
import cz.cvut.fel.ear.posilovna.model.GroupWorkout;
import cz.cvut.fel.ear.posilovna.model.Room;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@Transactional
public class RoomService {

    private final RoomDao roomDao;
    private final GroupWorkoutDao groupWorkoutDao;

    @Autowired
    public RoomService(RoomDao roomDao, GroupWorkoutDao groupWorkoutDao) {
        this.roomDao = roomDao;
        this.groupWorkoutDao = groupWorkoutDao;
    }

    public boolean isRoomAvailable(Integer roomId, String timeFrom, String timeTo) {
        Room room = roomDao.find(roomId);

        if (room != null) {
            // Get the list of group workouts scheduled for the given time slot in the specified room
            List<GroupWorkout> scheduledWorkouts = groupWorkoutDao.findByRoomAndTimeSlot(room, timeFrom, timeTo);

            // If there are no scheduled group workouts, the room is available
            return scheduledWorkouts.isEmpty();
        }

        // If the room doesn't exist, it's not available
        return false;
    }

}
