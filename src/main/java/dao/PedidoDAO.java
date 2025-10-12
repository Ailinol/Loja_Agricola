/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.util.List;
import javax.persistence.EntityManager;
import model.Pedido;
import util.HibernateUtil;

/**
 *
 * @author Thomas
 */
public class PedidoDAO {
    public void salvarPedido(Pedido novoPedido){
        EntityManager em = HibernateUtil.getEntityManager();
        try{
            em.getTransaction().begin();
            em.persist(novoPedido);
            em.getTransaction().commit();
            
        } catch(Exception e){
            em.getTransaction().rollback();
            e.printStackTrace();
        }finally{
            em.close();
        }
    }
    
    public Pedido buscarPorId(int id){
        EntityManager em = HibernateUtil.getEntityManager();
        
        try{
            return em.find(Pedido.class, id);
        } finally{
            em.close();
        }
    }
    
    public List<Pedido> listarPedidos(){
        EntityManager em = HibernateUtil.getEntityManager();
        try{
            return em.createQuery("FROM Pedido", Pedido.class).getResultList();
        } finally{
            em.close();
        }
    }
    
    public void atualizarPedido(Pedido pedido){
        EntityManager em = HibernateUtil.getEntityManager();
        try{
            em.getTransaction().begin();
            em.merge(pedido);
            em.getTransaction().commit();
            
        }catch(Exception e){
            em.getTransaction().rollback();
            e.printStackTrace();
            
        }finally{
            em.close();
        }
    }
    
    public void removerPedido(int id){
        EntityManager em = HibernateUtil.getEntityManager();
        try{
            Pedido pedido = em.find(Pedido.class, id);
            if(pedido != null){
                em.getTransaction().begin();
                em.remove(pedido);
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
