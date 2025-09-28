
package dao;

import javax.persistence.EntityManager;
import model.*;
import util.*;

import java.util.List;
/**
 *
 * @author Thomas
 */
public class CompradorDAO {
    
    public void salvarComprador(Comprador novoComprador){
        EntityManager em = HibernateUtil.getEntityManager();
        
        try{
            em.getTransaction().begin();
            em.persist(novoComprador);
            em.getTransaction().commit();
            
        } catch(Exception e){
            em.getTransaction().rollback();
            e.printStackTrace();
        }finally{
            em.close();
        }
    }
    
    public Comprador buscarPorId(int id){
        EntityManager em = HibernateUtil.getEntityManager();
        
        try{
            return em.find(Comprador.class, id);
        } finally{
            em.close();
        }
    }
    
    public List<Comprador> listarCompradores(){
        EntityManager em = HibernateUtil.getEntityManager();
        try{
            return em.createQuery("FROM Comprador", Comprador.class).getResultList();
        } finally{
            em.close();
        }
    }
    
    public void atualizarComprador(Comprador comprador){
        EntityManager em = HibernateUtil.getEntityManager();
        try{
            em.getTransaction().begin();
            em.merge(comprador);
            em.getTransaction().commit();
            
        }catch(Exception e){
            em.getTransaction().rollback();
            e.printStackTrace();
            
        }finally{
            em.close();
        }
    }
    
    public void removerComprador(int id){
        EntityManager em = HibernateUtil.getEntityManager();
        try{
            Comprador comprador = em.find(Comprador.class, id);
            if(comprador != null){
                em.getTransaction().begin();
                em.remove(comprador);
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
