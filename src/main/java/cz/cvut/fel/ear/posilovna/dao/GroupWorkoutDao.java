package cz.cvut.fel.ear.posilovna.dao;

import cz.cvut.fel.ear.posilovna.model.GroupWorkout;
import cz.cvut.fel.ear.posilovna.model.Room;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public class GroupWorkoutDao extends BaseDao<GroupWorkout> {
    public GroupWorkoutDao(){super(GroupWorkout.class);}

    public List<GroupWorkout> findByRoomAndTimeSlot(Room room, String timeFrom, String timeTo) {
        try {
            return em.createQuery(
                            "SELECT gw FROM GroupWorkout gw " +
                                    "WHERE gw.room = :room " +
                                    "AND ((gw.timeFrom <= :timeTo AND gw.timeTo >= :timeFrom) " +
                                    "OR (gw.timeFrom >= :timeFrom AND gw.timeTo <= :timeTo) " +
                                    "OR (gw.timeFrom <= :timeFrom AND gw.timeTo >= :timeTo))", GroupWorkout.class)
                    .setParameter("room", room)
                    .setParameter("timeFrom", timeFrom)
                    .setParameter("timeTo", timeTo)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
