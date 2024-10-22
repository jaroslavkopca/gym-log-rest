package cz.cvut.fel.ear.posilovna.service.security;

import cz.cvut.fel.ear.posilovna.dao.ClientDao;
import cz.cvut.fel.ear.posilovna.model.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final ClientDao clientDao;

    @Autowired
    public UserDetailsService(ClientDao clientDao) {
        this.clientDao = clientDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Client user = clientDao.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User with username " + username + " not found.");
        }
        return new cz.cvut.fel.ear.posilovna.security.model.UserDetails(user);
    }
}
