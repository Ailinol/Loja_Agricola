
package dao;

import java.util.*;
import javax.persistence.*;
import model.*;

import java.util.List;
import util.HibernateUtil;


/**
 *
 * @author Thomas
 */
public class AgricultorDAO {
    public void salvarAgricultor(Agricultor novoAgricultor){
        EntityManager em = HibernateUtil.getEntityManager();
        //EntityManagerFactory emf = Persistence.createEntityManagerFactory("greenMatch-jpa");
        //EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            em.persist(novoAgricultor);
            em.getTransaction().commit();
            
        } catch(Exception e){
            em.getTransaction().rollback();
            e.printStackTrace();
        }finally{
            em.close();
        }
    }
    
    public Agricultor buscarPorId(int id){
        EntityManager em = HibernateUtil.getEntityManager();
        
        try{
            return em.find(Agricultor.class, id);
        } finally{
            em.close();
        }
    }
    
    public List<Agricultor> listarAgricultores(){
        EntityManager em = HibernateUtil.getEntityManager();
        try{
            return em.createQuery("FROM Agricultor", Agricultor.class).getResultList();
        } finally{
            em.close();
        }
    }
    
    public void atualizarAgricultor(Agricultor agricultor){
        EntityManager em = HibernateUtil.getEntityManager();
        try{
            em.getTransaction().begin();
            em.merge(agricultor);
            em.getTransaction().commit();
            
        }catch(Exception e){
            em.getTransaction().rollback();
            e.printStackTrace();
            
        }finally{
            em.close();
        }
    }
    
    public void removerAgricultor(int id){
        EntityManager em = HibernateUtil.getEntityManager();
        try{
            Agricultor agricultor = em.find(Agricultor.class, id);
            if(agricultor != null){
                em.getTransaction().begin();
                em.remove(agricultor);
                em.getTransaction().commit();
            }
        }catch(Exception e){
            em.getTransaction().rollback();
            e.printStackTrace();
        }finally{
            em.close();
        }
    }
}
