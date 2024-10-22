package cz.cvut.fel.ear.posilovna.dao;

import cz.cvut.fel.ear.posilovna.model.Statistics;
import org.springframework.stereotype.Repository;

@Repository
public class StatisticsDao extends BaseDao<Statistics>{
    public StatisticsDao(){super(Statistics.class);}
}
