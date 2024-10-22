package cz.cvut.fel.ear.posilovna.model;

import jakarta.persistence.*;
import java.util.List;
@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String roomName;
    private int capacity;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<GroupWorkout> groupWorkouts;

    @ManyToOne
    @JoinColumn(name = "gym_id")
    private Gym gym;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<GroupWorkout> getGroupWorkouts() {
        return groupWorkouts;
    }

    public void setGroupWorkouts(List<GroupWorkout> groupWorkouts) {
        this.groupWorkouts = groupWorkouts;
    }

    public Gym getGym() {
        return gym;
    }

    public void setGym(Gym gym) {
        this.gym = gym;
    }
}
