package service;

import model.Produto;
import model.Monitorizacao;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import util.HibernateUtil; // Assumindo que você usa esta classe

public class MonitoramentoService {

    // Texto do benefício usado no FXML e no Model
    private static final String BENEFICIO_PADRAO = "Garante qualidade, confiança e integridade da plataforma de acordo com os dados que já tens.";

    // Método principal para ser chamado pelo Controller
    public Monitorizacao executarMonitoramento() {
        EntityManager em = HibernateUtil.getEntityManager();
        List<String> logs = new ArrayList<>();

        try {
            // 1. Verificação de Produtos sem preço ou com preço inválido
            TypedQuery<Produto> query = em.createQuery(
                "SELECT p FROM Produto p WHERE p.preco IS NULL OR p.preco <= 0", Produto.class);
            List<Produto> produtosInconsistentes = query.getResultList();

            if (!produtosInconsistentes.isEmpty()) {
                logs.add("--- INCONSISTÊNCIAS DETECTADAS ---");
                for (Produto produto : produtosInconsistentes) {
                    // Assumindo que a entidade Produto tem getNome() e getId()
                    String produtoId = (produto.getId() != 0) ? String.valueOf(produto.getId()) : "N/A";
                    logs.add("[PRODUTO - ID: " + produtoId + "] Preço inválido/nulo para: " + produto.getNome());
                }
            } else {
                logs.add("Verificação de Preços de Produto: OK. Nenhuma inconsistência detectada.");
            }
            
            // Cria o objeto Monitorizacao com o resultado
            Monitorizacao resultado = new Monitorizacao();
            resultado.setBeneficio(BENEFICIO_PADRAO);
            resultado.setInconsistenciasDetectadas(logs);
            
            // O sistema é considerado íntegro se a lista de logs for 1 e contiver o "OK".
            boolean integro = logs.size() == 1 && logs.get(0).contains("OK");
            resultado.setSistemaIntegro(integro);
            
            return resultado;

        } catch (Exception e) {
            logs.add("ERRO FATAL: Falha na conexão ou na execução da consulta. " + e.getMessage());
            e.printStackTrace();
            
            Monitorizacao erro = new Monitorizacao();
            erro.setBeneficio(BENEFICIO_PADRAO);
            erro.setInconsistenciasDetectadas(logs);
            erro.setSistemaIntegro(false);
            return erro;
            
        } finally {
            if (em != null && em.isOpen()) {
                em.close(); // Garante que o EntityManager seja fechado
            }
        }
    }
    
    // Método auxiliar para o Controller acessar o benefício na inicialização
    public static String getBeneficioPadrao() {
        return BENEFICIO_PADRAO;
    }
}