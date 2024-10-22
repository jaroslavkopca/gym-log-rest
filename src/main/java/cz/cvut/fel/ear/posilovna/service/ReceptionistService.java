package cz.cvut.fel.ear.posilovna.service;

import cz.cvut.fel.ear.posilovna.dao.ReceptionistDao;
import cz.cvut.fel.ear.posilovna.model.Client;
import cz.cvut.fel.ear.posilovna.model.Receptionist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReceptionistService {

    private final ReceptionistDao dao;

    @Autowired
    public ReceptionistService(ReceptionistDao dao) {
        this.dao = dao;
    }

    // creates new receptionist from parent class object
    public void createReceptionistFromClient(Client client) {
        Receptionist newReceptionist = new Receptionist();
        newReceptionist.setName(client.getName());
        newReceptionist.setSurname(client.getSurname());
        dao.persist(newReceptionist);
    }

    // assigns hourly salary to the receptionist
    public void defineSalary(Receptionist receptionist, int salary){
        if (salary > 0){
            receptionist.setSalary(salary);
        }
    }
}
