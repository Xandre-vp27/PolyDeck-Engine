import javax.persistence.*; 

public class GestorPersistencia {
    
    public static void inicializar() {
        
        // Busca la carpeta "db" en la raíz del proyecto y crea el archivo polydeck.odb 
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("db/polydeck.odb"); 
        EntityManager em = emf.createEntityManager();
        
        // Aquí irá la lógica
        
        em.close(); 
        emf.close();
    }
}