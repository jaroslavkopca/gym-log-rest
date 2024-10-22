package cz.cvut.fel.ear.posilovna.service;

import cz.cvut.fel.ear.posilovna.dao.ClientDao;
import cz.cvut.fel.ear.posilovna.model.Client;

import cz.cvut.fel.ear.posilovna.model.Role;
import cz.cvut.fel.ear.posilovna.util.Constants;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
@Transactional
public class ClientService {

    private final ClientDao clientDao;
    private final MemberService memberService;
    private final ReceptionistService receptionistService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ClientService(ClientDao clientDao, MemberService memberService, ReceptionistService receptionistService, PasswordEncoder passwordEncoder) {
        this.clientDao = clientDao;
        this.memberService = memberService;
        this.receptionistService = receptionistService;
        this.passwordEncoder = passwordEncoder;
    }


    public void becomeReceptionist(int clientId){
        Client client = clientDao.find(clientId);

        if (client != null) {
            receptionistService.createReceptionistFromClient(client);
        } else {
            // Handle client not found error
            System.out.println("Error: Client with ID " + clientId + " not found.");
        }
    }

    @Transactional
    public Client createClient(Client client) {
        Client newClient = new Client();
        newClient.setName(client.getName());
        newClient.setSurname(client.getSurname());
        newClient.setUsername(client.getUsername());
        newClient.setPassword(client.getPassword());
        newClient.setRole(Role.CLIENT);
        newClient.setRegistered(false);
        newClient.encodePassword(passwordEncoder);
        clientDao.persist(newClient);
        return newClient;
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public boolean exists(String username) {
        return clientDao.findByUsername(username) != null;
    }


    public void persist(Client user) {
        Objects.requireNonNull(user);
        user.encodePassword(passwordEncoder);
        if (user.getRole() == null) {
            user.setRole(Constants.DEFAULT_ROLE);
        }
        clientDao.persist(user);
}

    public Client findByUsername(String username) {
        return clientDao.findByUsername(username);
    }

    public Client findById(int id) {
        return clientDao.findById(id);
    }

    public Client findById(Integer clientId) {
        return clientDao.find(clientId);
    }

    public void deleteClient(Integer clientId) {
        clientDao.remove(clientDao.find(clientId));
    }
}
