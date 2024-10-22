package cz.cvut.fel.ear.posilovna.rest.util;

public class GroupWorkoutObject {

    public GroupWorkoutObject(){
    }
    private String workoutName;
    private String timeFrom;
    private String timeTo;
    private int capacity;
    private int roomId;

    private int memberId;

    public String getWorkoutName() {
        return workoutName;
    }

    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }

    public String getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(String timeFrom) {
        this.timeFrom = timeFrom;
    }

    public String getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(String timeTo) {
        this.timeTo = timeTo;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getMemberId() {
        return roomId;
    }

    public void setMemberId(int roomId) {
        this.roomId = roomId;
    }

}

