package cz.cvut.fel.ear.posilovna.service;

import cz.cvut.fel.ear.posilovna.model.Client;
import cz.cvut.fel.ear.posilovna.model.Role;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class SystemInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(SystemInitializer.class);

    /**
     * Default admin username
     */
    private static final String ADMIN_USERNAME = "ear";

    private final ClientService clientService;

    private final PlatformTransactionManager txManager;

    @Autowired
    public SystemInitializer(ClientService clientService,
                             PlatformTransactionManager txManager) {
        this.clientService = clientService;
        this.txManager = txManager;
    }

    @PostConstruct
    private void initSystem() {
        TransactionTemplate txTemplate = new TransactionTemplate(txManager);
        txTemplate.execute((status) -> {
            generateAdmin();
            return null;
        });
    }

    /**
     * Generates an admin account if it does not already exist.
     */
    private void generateAdmin() {
        if (clientService.exists(ADMIN_USERNAME)) {
            return;
        }
        final Client admin = new Client();
        admin.setUsername(ADMIN_USERNAME);
        admin.setName("System");
        admin.setSurname("Administrator");
        admin.setPassword("adm1n");
        admin.setRole(Role.ADMIN);
        LOG.info("Generated admin user with credentials " + admin.getUsername() + "/" + admin.getPassword());
        clientService.persist(admin);
    }
}
