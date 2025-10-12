/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.util.List;
import javax.persistence.EntityManager;
import model.ItemPedido;
import util.HibernateUtil;

/**
 *
 * @author Thomas
 */

public class ItemPedidoDAO {
    public void adicionarItem(ItemPedido novoItem){
        EntityManager em = HibernateUtil.getEntityManager();
        try{
            em.getTransaction().begin();
            em.persist(novoItem);
            em.getTransaction().commit();
            
        } catch(Exception e){
            em.getTransaction().rollback();
            e.printStackTrace();
        }finally{
            em.close();
        }
    }
    
    public ItemPedido buscarPorId(int id){
        EntityManager em = HibernateUtil.getEntityManager();
        
        try{
            return em.find(ItemPedido.class, id);
        } finally{
            em.close();
        }
    }
    
    public List<ItemPedido> listarItensPedidos(){
        EntityManager em = HibernateUtil.getEntityManager();
        try{
            return em.createQuery("FROM ItemPedido", ItemPedido.class).getResultList();
        } finally{
            em.close();
        }
    }
    
    public void atualizarItem(ItemPedido item){
        EntityManager em = HibernateUtil.getEntityManager();
        try{
            em.getTransaction().begin();
            em.merge(item);
            em.getTransaction().commit();
            
        }catch(Exception e){
            em.getTransaction().rollback();
            e.printStackTrace();
            
        }finally{
            em.close();
        }
    }
    
    public void removerItem(int id){
        EntityManager em = HibernateUtil.getEntityManager();
        try{
            ItemPedido itemPedido = em.find(ItemPedido.class, id);
            if(itemPedido != null){
                em.getTransaction().begin();
                em.remove(itemPedido);
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
