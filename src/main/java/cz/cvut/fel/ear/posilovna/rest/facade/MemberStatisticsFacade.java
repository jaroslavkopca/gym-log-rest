package cz.cvut.fel.ear.posilovna.rest.facade;

import java.util.List;

public interface MemberStatisticsFacade {
    List<MemberStatisticsDTO> getAllMembersAggregatedData();
}
