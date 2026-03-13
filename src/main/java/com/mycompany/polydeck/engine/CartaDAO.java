package com.mycompany.polydeck.engine;

import com.mycompany.polydeck.engine.model.Carta;
import javax.persistence.EntityManager;

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
}