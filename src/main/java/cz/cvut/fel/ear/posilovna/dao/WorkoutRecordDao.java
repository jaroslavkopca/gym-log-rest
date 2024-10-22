package cz.cvut.fel.ear.posilovna.dao;

import cz.cvut.fel.ear.posilovna.model.Member;
import cz.cvut.fel.ear.posilovna.model.WorkoutRecord;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
public class WorkoutRecordDao extends BaseDao<WorkoutRecord> {
    public WorkoutRecordDao(){super(WorkoutRecord.class);}

    public List<WorkoutRecord> findAll(Member member) {
        Objects.requireNonNull(member);
        return em.createNamedQuery("WorkoutRecord.findByMember", WorkoutRecord.class).setParameter("member", member)
                .getResultList();
    }

    public WorkoutRecord findById(Member member, Integer id) {
        Objects.requireNonNull(member);
        return em.createNamedQuery("WorkoutRecord.findByMember", WorkoutRecord.class).setParameter("member", member)
                .getResultList().stream().filter(r -> r.getId() == id.longValue()).findFirst().get();
    }
}
