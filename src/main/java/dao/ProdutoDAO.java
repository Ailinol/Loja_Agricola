/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.util.List;
import javax.persistence.EntityManager;
import model.Produto;
import util.HibernateUtil;

/**
 *
 * @author Thomas
 */
public class ProdutoDAO {
    
    public void salvarProduto(Produto novoProduto){
        EntityManager em = HibernateUtil.getEntityManager();
        
        try{
            em.getTransaction().begin();
            em.persist(novoProduto);
            em.getTransaction().commit();
            
        } catch(Exception e){
            em.getTransaction().rollback();
            e.printStackTrace();
        }finally{
            em.close();
        }
    }
    
    public Produto buscarPorId(int id){
        EntityManager em = HibernateUtil.getEntityManager();
        
        try{
            return em.find(Produto.class, id);
        } finally{
            em.close();
        }
    }
    
    public List<Produto> listarProdutos(){
        EntityManager em = HibernateUtil.getEntityManager();
        try{
            return em.createQuery("FROM Produto", Produto.class).getResultList();
        } finally{
            em.close();
        }
    }
    
    public static void atualizarProduto(Produto produto){
        EntityManager em = HibernateUtil.getEntityManager();
        try{
            em.getTransaction().begin();
            em.merge(produto);
            em.getTransaction().commit();
            
        }catch(Exception e){
            em.getTransaction().rollback();
            e.printStackTrace();
            
        }finally{
            em.close();
        }
    }
    
    public static void removerProduto(int id){
        EntityManager em = HibernateUtil.getEntityManager();
        try{
            Produto produto = em.find(Produto.class, id);
            if(produto != null){
                em.getTransaction().begin();
                em.remove(produto);
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
