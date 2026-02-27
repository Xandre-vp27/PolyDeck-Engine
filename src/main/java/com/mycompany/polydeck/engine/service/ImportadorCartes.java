/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.polydeck.engine.service;


import com.mycompany.polydeck.engine.model.CostMana;
import com.mycompany.polydeck.engine.model.Criatura;
import com.mycompany.polydeck.engine.model.Encanteri;
import com.mycompany.polydeck.engine.model.Terra;
import com.polydeck.model.Color;
import com.polydeck.model.Raresa;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.persistence.EntityManager;

public class ImportadorCartes {

    public static void importar(String rutaArxiu, EntityManager em) {
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArxiu))) {
            String linia;
            
            em.getTransaction().begin();

            while ((linia = br.readLine()) != null) {
                if (linia.trim().isEmpty() || linia.startsWith("#")) {
                    continue;
                }

                String[] dades = linia.split("\\|");
                
                // VALIDACIÓN CRÍTICA: Ignorar líneas rotas que causarían OutOfBounds
                if (dades.length < 5) {
                    System.out.println("Línia omesa per format incorrecte (massa curta): " + linia);
                    continue;
                }

                String tipus = dades[0].trim();
                String nom = dades[1].trim();
                String descripcio = dades[2].trim();
                Raresa raresa = Raresa.valueOf(dades[3].trim().toUpperCase());
                String edicio = "Primera Edició";

                switch (tipus) {
                    case "CRIATURA":
                        CostMana costCriatura = parsejarCost(dades[4].trim());
                        int forca = Integer.parseInt(dades[5].trim());
                        int resistencia = Integer.parseInt(dades[6].trim());
                        String tipusCriatura = dades[7].trim();
                        boolean vola = Boolean.parseBoolean(dades[8].trim());
                        
                        Criatura c = new Criatura(nom, descripcio, edicio, raresa, costCriatura, forca, resistencia, tipusCriatura, vola);
                        em.persist(c);
                        break;

                    case "TERRA":
                        // Corrección: Es ColorProduccio, no Color
                        Color color = Color.valueOf(dades[4].trim().toUpperCase());
                        boolean esBasica = Boolean.parseBoolean(dades[5].trim());
                        
                        CostMana costTerra = new CostMana(0,0,0,0,0,0);
                        
                        Terra t = new Terra(nom, descripcio, edicio, raresa, costTerra, color, esBasica);
                        em.persist(t);
                        break;

                    case "ENCANTERI":
                        CostMana costEncanteri = parsejarCost(dades[4].trim());
                        String tipusEncanteri = dades[5].trim();
                        boolean esInstantani = Boolean.parseBoolean(dades[6].trim());
                        
                        Encanteri e = new Encanteri(nom, descripcio, edicio, raresa, costEncanteri, tipusEncanteri, esInstantani);
                        em.persist(e);
                        break;
                        
                    default:
                        System.out.println("Tipus de carta desconegut ignorat: " + tipus);
                }
            }
            
            em.getTransaction().commit();
            System.out.println("Importació de cartes completada amb èxit.");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error crític durant la importació. No s'ha guardat res: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static CostMana parsejarCost(String costString) {
        String[] valors = costString.split(",");
        
        int blau = Integer.parseInt(valors[0].trim());
        int negre = Integer.parseInt(valors[1].trim());
        int vermell = Integer.parseInt(valors[2].trim());
        int verd = Integer.parseInt(valors[3].trim());
        int blanc = Integer.parseInt(valors[4].trim());
        int incolor = Integer.parseInt(valors[5].trim());
        
        return new CostMana(blanc, blau, negre, vermell, verd, incolor);
    }
}