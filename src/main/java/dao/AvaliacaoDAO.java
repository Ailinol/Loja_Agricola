/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 *
 * @author liliano
 */
import java.util.List;
import javax.persistence.EntityManager;
import model.Avaliacao;
import util.HibernateUtil;

public class AvaliacaoDAO {
    
    public void salvarAvaliacao(Avaliacao avaliacao) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(avaliacao);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    
    public Avaliacao buscarAvaliacaoPorPedido(int pedidoId) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery("FROM Avaliacao a WHERE a.pedido.id = :pedidoId", Avaliacao.class)
                    .setParameter("pedidoId", pedidoId)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            em.close();
        }
    }
}