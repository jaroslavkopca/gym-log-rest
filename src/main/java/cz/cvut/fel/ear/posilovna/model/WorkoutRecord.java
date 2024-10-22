package cz.cvut.fel.ear.posilovna.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



@Entity

@NamedQueries({
        @NamedQuery(name = "WorkoutRecord.findByMember", query = "SELECT wr from WorkoutRecord wr WHERE wr.member = :member")
})
public class WorkoutRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Boolean removed = false;
    private String start;
    private int intensityLevel;
    private int duration;

    public Boolean isRemoved() {
        return removed;
    }

    public void setRemoved(Boolean removed) {
        this.removed = removed;
    }
    public int getDuration(){
        return this.duration;
    }
    public void setDuration(int duration){
        this.duration = duration;
    }
    public int getIntensityLevel(){
        return this.intensityLevel;
    }
    public void setIntensityLevel(int intensityLevel){
        this.intensityLevel = intensityLevel;
    }
    public String getStart(){
        return this.start;
    }
    public void setStart(String start){
        this.start = start;
    }
    public Long getId(){
        return this.id;
    }
    public void setId(Long id){
        this.id = id;
    }
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "workoutRecord")
    private List<Workout> workouts;

    // Constructors, getters, setters

    public void setMember(Member member){
        Objects.requireNonNull(member);
        this.member = member;
    }

    public void addWorkout(Workout workout) {
        Objects.requireNonNull(workout);
        if (workouts == null) {
            this.workouts = new ArrayList<>();
        }
        workouts.add(workout);
    }

    public void removeWorkouts(Workout workout) {
        Objects.requireNonNull(workout);
        if (workouts == null) {
            return;
        }
        workouts.removeIf(c -> Objects.equals(c.getId(), workout.getId()));
    }
}
