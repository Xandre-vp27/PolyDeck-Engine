package com.mycompany.polydeck.engine;

import com.mycompany.polydeck.engine.model.*;
import java.util.List;
import javax.persistence.*;

public class ConsultesDAO {

    public static List<Carta> buscarCriaturesVoladoresNegres(EntityManager em, int costNegre) {
        String jpql = "SELECT c FROM com.mycompany.polydeck.engine.model.Criatura c " +
                      "WHERE c.volar = true AND c.cost.negre > :n";
        
        TypedQuery<Carta> query = em.createQuery(jpql, Carta.class);
        query.setParameter("n", costNegre);
        return query.getResultList();
    }

    public static Double mitjanaForçaJugador(EntityManager em, String nick) {
        String jpql = "SELECT AVG(c.força) FROM com.mycompany.polydeck.engine.model.Jugador j " +
                      "JOIN j.mazos m, com.mycompany.polydeck.engine.model.Criatura c " +
                      "WHERE c MEMBER OF m.cartes AND j.nick = :nick";
        
        TypedQuery<Double> query = em.createQuery(jpql, Double.class);
        query.setParameter("nick", nick);
        
        try {
            Double res = query.getSingleResult();
            return (res != null) ? res : 0.0;
        } catch (NoResultException e) {
            return 0.0;
        }
    }

    public static List<Encanteri> buscarEncanterisSenseBlauBlanc(EntityManager em, int incolor) {
        String jpql = "SELECT e FROM com.mycompany.polydeck.engine.model.Encanteri e " +
                      "WHERE e.cost.blau = 0 AND e.cost.blanc = 0 AND e.cost.incolor > :i";
        
        TypedQuery<Encanteri> query = em.createQuery(jpql, Encanteri.class);
        query.setParameter("i", incolor);
        return query.getResultList();
    }
}