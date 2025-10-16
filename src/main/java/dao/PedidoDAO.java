/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.util.List;
import javax.persistence.EntityManager;
import model.Pedido;
import model.Produto;
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
    
    public Pedido buscarPedidoCompletoPorId(int pedidoId) {
    EntityManager em = HibernateUtil.getEntityManager();
    try {
        String jpql = "SELECT DISTINCT p FROM Pedido p " +
                     "LEFT JOIN FETCH p.itensPedidos ip " +
                     "LEFT JOIN FETCH ip.produto " +
                     "WHERE p.id = :pedidoId";
        
        return em.createQuery(jpql, Pedido.class)
                .setParameter("pedidoId", pedidoId)
                .getSingleResult();
    } finally {
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
    
    public List<Pedido> buscarPedidosPorComprador(int compradorId) {
    EntityManager em = HibernateUtil.getEntityManager();
    try {
        String jpql = "SELECT DISTINCT p FROM Pedido p " +
                     "LEFT JOIN FETCH p.itensPedidos ip " +
                     "LEFT JOIN FETCH ip.produto " +
                     "WHERE p.comprador.id = :compradorId " +
                     "ORDER BY p.dataHora DESC";
        
        return em.createQuery(jpql, Pedido.class)
                .setParameter("compradorId", compradorId)
                .getResultList();
    } finally {
        em.close();
    }
}
    
    // MÉTODO CORRIGIDO - Versão mais segura
    public List<Pedido> buscarPedidosPorAgricultor(int agricultorId) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            // Primeiro busca os produtos do agricultor
            String jpqlProdutos = "SELECT p FROM Produto p WHERE p.agricultorId = :agricultorId";
            List<Produto> produtosAgricultor = em.createQuery(jpqlProdutos, Produto.class)
                    .setParameter("agricultorId", agricultorId)
                    .getResultList();
            
            if (produtosAgricultor.isEmpty()) {
              //  return new ArrayList<>();
            }
            
            // Depois busca os pedidos que contêm esses produtos
            String jpql = "SELECT DISTINCT p FROM Pedido p " +
                         "LEFT JOIN FETCH p.itensPedidos ip " +
                         "LEFT JOIN FETCH ip.produto " +
                         "LEFT JOIN FETCH p.comprador " +
                         "WHERE ip.produto IN :produtos " +
                         "ORDER BY p.dataHora DESC";
            
            return em.createQuery(jpql, Pedido.class)
                    .setParameter("produtos", produtosAgricultor)
                    .getResultList();
        } finally {
            em.close();
        }
    }
    
    
}