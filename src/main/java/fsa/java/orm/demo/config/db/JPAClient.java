package fsa.java.orm.demo.config.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.springframework.stereotype.Component;

@Component
public class JPAClient {
    private final EntityManagerFactory emf;

    public JPAClient() {
        emf = Persistence.createEntityManagerFactory("pu.main");
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
