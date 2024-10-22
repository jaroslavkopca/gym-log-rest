package cz.cvut.fel.ear.posilovna.service;

import cz.cvut.fel.ear.posilovna.model.Member;
import cz.cvut.fel.ear.posilovna.model.WorkoutRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WorkoutRecordDecoratorService implements IWorkoutRecordService {

    private final IWorkoutRecordService decoratedService;

    @Autowired
    public WorkoutRecordDecoratorService(IWorkoutRecordService decoratedService) {
        this.decoratedService = decoratedService;
    }

    public List<WorkoutRecord> findAllDeleted(Member member) {
        return decoratedService.findAll(member).stream().filter(WorkoutRecord::isRemoved).toList();
    }

    @Override
    public List<WorkoutRecord> findAll(Member member) {
        return decoratedService.findAll(member);
    }

    @Override
    public WorkoutRecord findById(Member member, Integer id) {
        return decoratedService.findById(member, id);
    }

    @Override
    public void remove(WorkoutRecord workoutRecord) {
        decoratedService.remove(workoutRecord);
    }

    @Override
    public void update(Member member, WorkoutRecord workoutRecord) {
        decoratedService.update(member, workoutRecord);
    }
}
