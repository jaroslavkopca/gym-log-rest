package cz.cvut.fel.ear.posilovna.dao;

import cz.cvut.fel.ear.posilovna.model.Client;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

@Repository
public class ClientDao extends BaseDao<Client>{
    public ClientDao(){super(Client.class);}


    public Client findByUsername(String username) {
        try {
            return em.createQuery(
                            "SELECT u FROM Client u WHERE u.username = :username", Client.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    public Client findById(int id) {
        try {
            return em.createQuery(
                            "SELECT u FROM Client u WHERE u.id = :id", Client.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
