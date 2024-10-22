package cz.cvut.fel.ear.posilovna.service;

import cz.cvut.fel.ear.posilovna.dao.WorkoutRecordDao;
import cz.cvut.fel.ear.posilovna.model.Member;
import cz.cvut.fel.ear.posilovna.model.Workout;
import cz.cvut.fel.ear.posilovna.dao.WorkoutDao;
import cz.cvut.fel.ear.posilovna.model.WorkoutRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class WorkoutService {
/*
    private final WorkoutDao dao;
    private final WorkoutRecordDao recordDao;

    @Autowired
    public WorkoutService(WorkoutDao dao, WorkoutRecordDao recordDao) {
        this.dao = dao;
        this.recordDao = recordDao;
    }

    @Transactional(readOnly = true)
    public List<Workout> findAll() {
        return dao.findAll();
    }

    @Transactional(readOnly = true)
    public Workout find(Integer id) {
        return dao.find(id);
    }

    @Transactional
    public void persist(Workout workout) {
        dao.persist(workout);
    }

    @Transactional
    public void update(Workout workout) {
        dao.update(workout);
    }

    @Transactional
    public void addWorkoutRecord(WorkoutRecord workoutRecord){
        Objects.requireNonNull(workoutRecord);
        recordDao.persist(workoutRecord);
    }

    public void addWorkout(WorkoutRecord workoutRecord, Workout workout){
        Objects.requireNonNull(workout);
        Objects.requireNonNull(workoutRecord);
        workoutRecord.addWorkout(workout);
        recordDao.update(workoutRecord);
    }

    public List<WorkoutRecord> findAll(Member member){

    }

    @Transactional
    public void remove(Workout workout) {
        Objects.requireNonNull(workout);
        workout.setRemoved(true);
        dao.update(workout);
    }*/
}

