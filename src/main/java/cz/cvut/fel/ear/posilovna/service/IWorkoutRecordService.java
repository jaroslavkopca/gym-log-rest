package cz.cvut.fel.ear.posilovna.service;

import cz.cvut.fel.ear.posilovna.model.Member;
import cz.cvut.fel.ear.posilovna.model.WorkoutRecord;

import java.util.List;

public interface IWorkoutRecordService {

    List<WorkoutRecord> findAll(Member member);

    WorkoutRecord findById(Member member, Integer id);

    void remove(WorkoutRecord workoutRecord);

    void update(Member member, WorkoutRecord workoutRecord);
}