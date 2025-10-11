
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

import model.Agricultor;
import javax.persistence.*;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.stream.Collectors;

public class AgricultorDAO {
    
    private EntityManager entityManager;
    
    public AgricultorDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    public void salvar(Agricultor agricultor) {
        entityManager.persist(agricultor);
    }
    
    public void atualizar(Agricultor agricultor) {
        entityManager.merge(agricultor);
    }
    
    public Optional<Agricultor> buscarPorId(Long id) {
        try {
            Agricultor agricultor = entityManager.find(Agricultor.class, id);
            return Optional.ofNullable(agricultor);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    public Optional<Agricultor> buscarPorEmail(String email) {
        try {
            TypedQuery<Agricultor> query = entityManager.createQuery(
                "SELECT a FROM Agricultor a WHERE a.email = :email", Agricultor.class);
            query.setParameter("email", email);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    
    public Optional<Agricultor> buscarPorNome(String nome) {
        try {
            TypedQuery<Agricultor> query = entityManager.createQuery(
                "SELECT a FROM Agricultor a WHERE a.nome = :nome", Agricultor.class);
            query.setParameter("nome", nome);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    
    public List<Agricultor> listarTodos() {
        try {
            TypedQuery<Agricultor> query = entityManager.createQuery(
                "SELECT a FROM Agricultor a ORDER BY a.nome", Agricultor.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar agricultores", e);
        }
    }
    
    public List<Agricultor> buscarPorLocalizacao(String provincia, String distrito) {
        try {
            TypedQuery<Agricultor> query = entityManager.createQuery(
                "SELECT a FROM Agricultor a WHERE a.provincia = :provincia AND a.distrito = :distrito ORDER BY a.nome", 
                Agricultor.class);
            query.setParameter("provincia", provincia);
            query.setParameter("distrito", distrito);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar agricultores por localização", e);
        }
    }
    
    public List<Agricultor> buscarPorTipoAgricultura(String tipoAgricultura) {
        try {
            TypedQuery<Agricultor> query = entityManager.createQuery(
                "SELECT a FROM Agricultor a WHERE a.tipoAgricultura = :tipo ORDER BY a.nome", 
                Agricultor.class);
            query.setParameter("tipo", tipoAgricultura);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar agricultores por tipo", e);
        }
    }
    
    public List<Agricultor> listarOrganicos() {
        try {
            TypedQuery<Agricultor> query = entityManager.createQuery(
                "SELECT a FROM Agricultor a WHERE a.certificadoOrganico = true ORDER BY a.nome", 
                Agricultor.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar agricultores orgânicos", e);
        }
    }
    
    public List<Agricultor> listarComEntrega() {
        try {
            TypedQuery<Agricultor> query = entityManager.createQuery(
                "SELECT a FROM Agricultor a WHERE a.ofereceEntrega = true ORDER BY a.nome", 
                Agricultor.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar agricultores com entrega", e);
        }
    }
    
    public List<Agricultor> buscarPorClassificacaoMinima(double classificacaoMinima) {
        try {
            TypedQuery<Agricultor> query = entityManager.createQuery(
                "SELECT a FROM Agricultor a WHERE a.classificacaoMedia >= :classificacao ORDER BY a.classificacaoMedia DESC", 
                Agricultor.class);
            query.setParameter("classificacao", classificacaoMinima);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar agricultores por classificação", e);
        }
    }
    
    public int getTotalAgricultores() {
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(a) FROM Agricultor a", Long.class);
            return query.getSingleResult().intValue();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao obter total de agricultores", e);
        }
    }
    
    public int getTotalAgricultoresAtivos() {
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(a) FROM Agricultor a WHERE a.disponivelParaContato = true", Long.class);
            return query.getSingleResult().intValue();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao obter total de agricultores ativos", e);
        }
    }
    
    public Map<String, Long> getEstatisticasPorProvincia() {
        try {
            TypedQuery<Object[]> query = entityManager.createQuery(
                "SELECT a.provincia, COUNT(a) FROM Agricultor a GROUP BY a.provincia", Object[].class);
            
            return query.getResultList().stream()
                .collect(Collectors.toMap(
                    result -> (String) result[0],
                    result -> (Long) result[1]
                ));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao obter estatísticas por província", e);
        }
    }
}