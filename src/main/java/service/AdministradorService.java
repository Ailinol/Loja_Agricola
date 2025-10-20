package service;

import dao.AgricultorDAO;
import dao.CompradorDAO;
import dao.ProdutoDAO;
import model.Agricultor;
import model.Comprador;
import model.Produto;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Serviço de gestão do Administrador.
 * 
 * Responsável por:
 *  - Gestão de usuários (aprovar, bloquear, alterar dados)
 *  - Gestão de produtos (validar, remover produtos inapropriados)
 *  - Geração de relatórios globais
 *  - Monitorização do sistema
 * 
 * Benefício: Garante qualidade, confiança e integridade da plataforma.
 */
public class AdministradorService {

    private EntityManager entityManager;
    private AgricultorDAO agricultorDAO;
    private CompradorDAO compradorDAO;
    private ProdutoDAO produtoDAO;

    /**
     * Construtor do serviço do administrador.
     */
    public AdministradorService() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("GreenMatchPU");
        this.entityManager = emf.createEntityManager();
        this.agricultorDAO = new AgricultorDAO(entityManager);
        this.compradorDAO = new CompradorDAO();
        this.produtoDAO = new ProdutoDAO();
    }

    // ===================== Gestão de Usuários =====================

    /**
     * Aprova um agricultor para estar ativo na plataforma.
     * @param agricultorId ID do agricultor
     */
    public void aprovarAgricultor(Long agricultorId) {
        Optional<Agricultor> agricultorOpt = agricultorDAO.buscarPorId(agricultorId);
        agricultorOpt.ifPresent(agricultor -> {
            agricultor.setDisponivelParaContato(true);
            agricultorDAO.atualizar(agricultor);
        });
    }

    /**
     * Bloqueia um agricultor impedindo contato e visibilidade.
     * @param agricultorId ID do agricultor
     */
    public void bloquearAgricultor(Long agricultorId) {
        Optional<Agricultor> agricultorOpt = agricultorDAO.buscarPorId(agricultorId);
        agricultorOpt.ifPresent(agricultor -> {
            agricultor.setDisponivelParaContato(false);
            agricultorDAO.atualizar(agricultor);
        });
    }

    /**
     * Atualiza dados de um agricultor.
     * @param agricultor Agricultor com dados atualizados
     */
    public void atualizarDadosAgricultor(Agricultor agricultor) {
        agricultorDAO.atualizar(agricultor);
    }

    // ===================== Gestão de Produtos =====================

    /**
     * Remove produto inapropriado da plataforma.
     * @param produtoId ID do produto
     */
    public void removerProduto(int produtoId) {
        ProdutoDAO.removerProduto(produtoId);
    }

    /**
     * Atualiza os dados de um produto.
     * @param produto Produto com dados atualizados
     */
    public void atualizarProduto(Produto produto) {
        ProdutoDAO.atualizarProduto(produto);
    }

    // ===================== Relatórios Globais =====================

    /**
     * Retorna total de agricultores na plataforma.
     * @return total de agricultores
     */
    public int totalAgricultores() {
        return agricultorDAO.getTotalAgricultores();
    }

    /**
     * Retorna total de agricultores ativos na plataforma.
     * @return total de agricultores ativos
     */
    public int totalAgricultoresAtivos() {
        return agricultorDAO.getTotalAgricultoresAtivos();
    }

    /**
     * Retorna estatísticas de agricultores por província.
     * @return mapa província -> total de agricultores
     */
    public Map<String, Long> estatisticasPorProvincia() {
        return agricultorDAO.getEstatisticasPorProvincia();
    }

    /**
     * Lista todos os produtos cadastrados.
     * @return lista de produtos
     */
    public List<Produto> listarProdutos() {
        return produtoDAO.listarProdutos();
    }

    /**
     * Lista todos os compradores cadastrados.
     * @return lista de compradores
     */
    public List<Comprador> listarCompradores() {
        return compradorDAO.listarCompradores();
    }

    // ===================== Monitorização =====================

    /**
     * Identifica inconsistências simples no sistema (exemplo: agricultores sem produtos).
     * @return lista de agricultores inconsistentes
     */
    public List<Agricultor> verificarInconsistenciasAgricultores() {
        List<Agricultor> todos = agricultorDAO.listarTodos();
        return todos.stream()
                .filter(a -> a.getTipoAgricultura() == null || a.getNome().isEmpty())
                .toList();
    }

    /**
     * Fecha o EntityManager ao encerrar o serviço.
     */
    public void fechar() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }
}
