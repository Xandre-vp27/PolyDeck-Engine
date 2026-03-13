package com.mycompany.polydeck.engine;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.mycompany.polydeck.engine.model.Carta;

public class CartaDAO {

    /**
     * Busca una carta a la base de dades pel seu ID.
     * @param em L'EntityManager actiu.
     * @param id L'identificador de la carta (long).
     * @return L'objecte Carta trobat o null si no existeix.
     */
    
    public static Carta buscarPerId(EntityManager em, long id) {
        // Utiliza el método find de JPA. 
        return em.find(Carta.class, id);
    }

    public static void eliminarCartaPorId(long id) {


        EntityManagerFactory emf = Persistence.createEntityManagerFactory("db/polydeck.odb");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            // 1. Recuperar la carta (debe estar MANAGED)
            Carta carta = em.find(Carta.class, id);

            // 2. Si existe, eliminarla
            if (carta != null) {
                em.remove(carta);
            }

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    
}