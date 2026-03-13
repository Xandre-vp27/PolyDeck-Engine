/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.polydeck.engine;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.mycompany.polydeck.engine.model.Carta;

/**
 *
 * @author alumnet
 */
public class CartaDAO {

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
