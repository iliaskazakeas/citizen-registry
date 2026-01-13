package gr.registry.service;

import gr.registry.domain.Citizen;
import jakarta.persistence.*;
import java.util.List;

public class CitizenRepository {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("citizenPU");

    public void save(Citizen citizen) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(citizen);
        em.getTransaction().commit();
        em.close();
    }

    public Citizen findById(String idNumber) {
        EntityManager em = emf.createEntityManager();
        Citizen c = em.find(Citizen.class, idNumber);
        em.close();
        return c;
    }

    public void delete(String idNumber) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Citizen c = em.find(Citizen.class, idNumber);
        if (c != null) em.remove(c);
        em.getTransaction().commit();
        em.close();
    }

    public void update(Citizen citizen) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(citizen);
        em.getTransaction().commit();
        em.close();
    }

    public List<Citizen> findAll() {
        EntityManager em = emf.createEntityManager();
        List<Citizen> list = em.createQuery("SELECT c FROM Citizen c", Citizen.class).getResultList();
        em.close();
        return list;
    }
}
