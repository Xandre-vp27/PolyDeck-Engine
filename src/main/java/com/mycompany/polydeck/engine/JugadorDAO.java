package com.mycompany.polydeck.engine;

import com.mycompany.polydeck.engine.model.Carta;
import com.mycompany.polydeck.engine.model.Jugador;
import com.mycompany.polydeck.engine.model.Mazo;
import java.util.List;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public class JugadorDAO {

    public static void crearJugadorsIMazosDeProva(EntityManager em) {
        System.out.println("\n--- CREANT JUGADORS I MAZOS ---");
        
        try {
            em.getTransaction().begin();

            // 1. Recuperar algunas cartas de la BD para ponerlas en el mazo
            TypedQuery<Carta> query = em.createQuery("SELECT c FROM Carta c", Carta.class);
            query.setMaxResults(4); // Cogemos solo 4 cartas para hacer la prueba
            List<Carta> cartesRecuperades = query.getResultList();

            // 2. Instanciar un nuevo Mazo (Estado: New)
            Mazo mazo1 = new Mazo("Mazo Foc Primigeni", new Date());
            
            // Añadimos las cartas recuperadas al mazo
            for (Carta c : cartesRecuperades) {
                mazo1.afegirCarta(c);
            }

            // 3. Instanciar un nuevo Jugador (Estado: New)
            Jugador jugador1 = new Jugador("XandrePro", 42);
            
            // Le asignamos el mazo al jugador
            jugador1.afegirMazo(mazo1);

            // 4. Persistir el objeto padre
            em.persist(jugador1);

            em.getTransaction().commit();
            System.out.println("Jugador i Mazo guardats a la base de dades amb èxit!");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error al crear jugador i mazo: " + e.getMessage());
        }
    }
}