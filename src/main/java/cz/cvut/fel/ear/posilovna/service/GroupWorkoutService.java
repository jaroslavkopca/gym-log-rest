package cz.cvut.fel.ear.posilovna.service;

import cz.cvut.fel.ear.posilovna.dao.GroupWorkoutDao;
import cz.cvut.fel.ear.posilovna.dao.MemberDao;
import cz.cvut.fel.ear.posilovna.dao.RoomDao;
import cz.cvut.fel.ear.posilovna.model.GroupWorkout;
import cz.cvut.fel.ear.posilovna.model.Member;
import cz.cvut.fel.ear.posilovna.model.Room;
import cz.cvut.fel.ear.posilovna.exception.RoomNotAvailableException;
import cz.cvut.fel.ear.posilovna.exception.MemberNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class GroupWorkoutService {

    private final GroupWorkoutDao groupWorkoutDao;
    private final MemberDao memberDao;
    private final RoomDao roomDao;
    private final RoomService roomService;

    @Autowired
    public GroupWorkoutService(GroupWorkoutDao groupWorkoutDao, MemberDao memberDao, RoomDao roomDao, RoomService roomService) {
        this.groupWorkoutDao = groupWorkoutDao;
        this.memberDao = memberDao;
        this.roomDao = roomDao;
        this.roomService = roomService;
    }


    /**
     * Přidat oveření membershipu
     *
     * Creates group workout with author if the room is available, if not nothing is created.
     *
     * @param memberId
     * @param workoutName
     * @param timeFrom
     * @param timeTo
     * @param capacity
     * @param roomId
     * @return
     */
    public GroupWorkout createGroupWorkout(int memberId, String workoutName, String timeFrom, String timeTo, int capacity, int roomId) {
        Room room = roomDao.find(roomId);
        Member member = memberDao.find(memberId);

        // checks whether founding member exists
        if (member == null) {
            throw new MemberNotFoundException("Member cannot create group workout because it doesn't exist");
        }
        // checks whether room is available and exists
        if (room != null && !roomService.isRoomAvailable(room.getId(), timeFrom, timeTo)) {
            throw new RoomNotAvailableException("Room doesn't exists or is unavailable");
        }

        GroupWorkout newGroupWorkout = new GroupWorkout();
        newGroupWorkout.setWorkoutName(workoutName);
        newGroupWorkout.setTimeFrom(timeFrom);
        newGroupWorkout.setTimeTo(timeTo);
        newGroupWorkout.setCapacity(capacity);
        newGroupWorkout.setAuthor(member);

        // Assign the group workout to the room
        newGroupWorkout.setRoom(room);

        groupWorkoutDao.persist(newGroupWorkout);
        return newGroupWorkout;
    }


    public void addMemberToGroupWorkout(int groupWorkoutId, int memberId) {
        GroupWorkout groupWorkout = groupWorkoutDao.find(groupWorkoutId);
        Member member = memberDao.find(memberId);


        if (groupWorkout != null && member != null) {
            List<Member> members = groupWorkout.getMembers();
            members.add(member);
            groupWorkoutDao.update(groupWorkout);
        }
    }

    public void removeMemberFromGroupWorkout(int groupWorkoutId, int memberId) {
        GroupWorkout groupWorkout = groupWorkoutDao.find(groupWorkoutId);
        Member member = memberDao.find(memberId);

        if (groupWorkout != null && member != null) {
            List<Member> members = groupWorkout.getMembers();
            members.remove(member);
            groupWorkoutDao.update(groupWorkout);
        }
    }
}
