package cz.cvut.fel.ear.posilovna.rest.facade;

import cz.cvut.fel.ear.posilovna.model.Member;
import cz.cvut.fel.ear.posilovna.model.WorkoutRecord;
import cz.cvut.fel.ear.posilovna.service.MemberService;
import cz.cvut.fel.ear.posilovna.service.WorkoutRecordService;
import cz.cvut.fel.ear.posilovna.service.WorkoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// facade aggregates data from multiple resources
@Service
public class MemberStatisticsFacadeImpl implements MemberStatisticsFacade {

    private final MemberService memberService;

    private final WorkoutRecordService workoutRecordService;

    @Autowired
    public MemberStatisticsFacadeImpl(MemberService memberService, WorkoutRecordService workoutRecordService){
        this.memberService = memberService;
        this.workoutRecordService = workoutRecordService;
    }
    @Override
    public List<MemberStatisticsDTO> getAllMembersAggregatedData() {
        List<Member> members = memberService.getAllMembers().stream().toList();
        List<MemberStatisticsDTO> aggregatedDataList = new ArrayList<>();

        for (Member member : members) {
            MemberStatisticsDTO aggregatedData = new MemberStatisticsDTO();
            aggregatedData.setId(member);
            aggregatedData.setMemberName(member);
            aggregatedData.setOverallDuration(workoutRecordService.findAll(member).stream().mapToInt(WorkoutRecord::getDuration).reduce(0, (x, y) -> x + y));
            aggregatedData.setAverageIntensity(workoutRecordService.findAll(member)
                    .stream()
                    .mapToInt(WorkoutRecord::getIntensityLevel)
                    .average()
                    .orElse(0.0));
            aggregatedDataList.add(aggregatedData);
        }

        return aggregatedDataList;
    }
}
