package cz.cvut.fel.ear.posilovna.dao;

import cz.cvut.fel.ear.posilovna.model.Receptionist;
import org.springframework.stereotype.Repository;

@Repository
public class ReceptionistDao extends BaseDao<Receptionist> {
    public ReceptionistDao() {super(Receptionist.class);}
}
