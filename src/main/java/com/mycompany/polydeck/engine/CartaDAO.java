package com.mycompany.polydeck.engine;

import javax.persistence.EntityManager;
import com.mycompany.polydeck.engine.model.Carta;

public class CartaDAO {

    /**
     * Busca una carta a la base de dades pel seu ID.
     * @param em L'EntityManager actiu.
     * @param id L'identificador de la carta (long).
     * @return L'objecte Carta trobat o null si no existeix.
     */
    
    public static Carta buscarPerId(EntityManager em, long id) {
        return em.find(Carta.class, id);
    }

    public static void eliminarCartaPorId(EntityManager em, long id) {
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
        }
    }
}