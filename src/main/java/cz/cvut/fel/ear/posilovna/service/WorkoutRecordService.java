package cz.cvut.fel.ear.posilovna.service;

import cz.cvut.fel.ear.posilovna.dao.WorkoutRecordDao;
import cz.cvut.fel.ear.posilovna.model.Member;
import cz.cvut.fel.ear.posilovna.model.WorkoutRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class WorkoutRecordService implements IWorkoutRecordService{

    private final WorkoutRecordDao dao;

    @Autowired
    public WorkoutRecordService(WorkoutRecordDao dao) {
        this.dao = dao;
    }

    @Transactional(readOnly = true)
    public List<WorkoutRecord> findAll(Member member) {
        return dao.findAll(member);
    }

    @Transactional(readOnly = true)
    public WorkoutRecord findById(Member member, Integer id) {
        return dao.find(id);
    }

    @Transactional
    public void remove(WorkoutRecord workoutRecord) {
        Objects.requireNonNull(workoutRecord);
        workoutRecord.setRemoved(true);
        dao.update(workoutRecord);
    }

    @Transactional
    public void update(Member member, WorkoutRecord workoutRecord) {
        workoutRecord.setMember(member);
        dao.update(workoutRecord);
    }

}
