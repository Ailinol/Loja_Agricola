/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

  ESTA CLASSE É USADA PARA PERMITIR A INICIALIZAÇÃO DO Objecto "EntityManager"
 */
package util;
import javax.persistence.*;

/**
 *
 * @author Thomas
 */
public class HibernateUtil {
    
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("greenMatch-jpa");
    
    public static EntityManager getEntityManager(){
        return emf.createEntityManager();
    }
}

