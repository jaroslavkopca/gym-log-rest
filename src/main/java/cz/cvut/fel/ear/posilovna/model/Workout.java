package cz.cvut.fel.ear.posilovna.model;

import jakarta.persistence.*;
@Entity
public class Workout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private WorkoutExercise type;

    private int intensity;

    @ManyToOne
    @JoinColumn(name = "workout_record_id")
    private WorkoutRecord workoutRecord;

    public int getId(){
        return this.id;
    }
}
