
import jakarta.persistence.EntityManager;
import model.Agricultor;
import model.Pessoa;
import util.HibernateUtil;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Thomas
 */
public class AgricultorTest {
    
    public static void main(String[] args) {
        EntityManager em = (EntityManager) HibernateUtil.getEntityManager();
        Pessoa agri = new Agricultor(
                "0000",
                "Tomás", 
                "tomas@gmail.com", 
                "846543121","Maputo", 
                "Kamaxaquene", 
                "MAXAQUENE D");
        
        em.getTransaction().begin();
        em.persist(agri);
        em.getTransaction().commit();
    }
}
