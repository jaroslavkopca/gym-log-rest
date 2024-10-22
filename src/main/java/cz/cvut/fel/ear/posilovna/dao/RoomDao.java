package cz.cvut.fel.ear.posilovna.dao;

import cz.cvut.fel.ear.posilovna.model.Room;
import org.springframework.stereotype.Repository;

@Repository
public class RoomDao extends BaseDao<Room>{
    public RoomDao(){super(Room.class);}
}
