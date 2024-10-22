package cz.cvut.fel.ear.posilovna.dao;

import cz.cvut.fel.ear.posilovna.model.Workout;
import org.springframework.stereotype.Repository;

@Repository
public class WorkoutDao extends BaseDao<Workout> {
    public WorkoutDao() {super(Workout.class);}
}
