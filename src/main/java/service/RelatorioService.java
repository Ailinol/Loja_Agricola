package service;

import dao.AgricultorDAO;
import dao.CompradorDAO;
import dao.PedidoDAO;
import dao.ProdutoDAO;
import model.Agricultor;
import model.Comprador;
import model.Pedido;
import model.Produto;
import util.HibernateUtil;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.Collectors;
import model.ItemPedido;

/**
 * Serviço responsável por gerar os dados necessários para os relatórios globais do sistema.
 */
public class RelatorioService {

    private final AgricultorDAO agricultorDAO;
    private final CompradorDAO compradorDAO;
    private final ProdutoDAO produtoDAO;
    private final PedidoDAO pedidoDAO;

    public RelatorioService() {
        EntityManager em = HibernateUtil.getEntityManager();
        this.agricultorDAO = new AgricultorDAO(em);
        this.compradorDAO = new CompradorDAO();
        this.produtoDAO = new ProdutoDAO();
        this.pedidoDAO = new PedidoDAO();
    }

    /** =================== Relatórios Globais =================== */

    /** Total de clientes ativos */
    public long totalClientesAtivos() {
        return compradorDAO.listarCompradores().stream()
                .filter(Comprador::isAtivo)
                .count();
    }

    /** Top N agricultores mais produtivos */
    public List<Agricultor> agricultoresMaisProdutivos(int topN) {
        return agricultorDAO.listarTodos().stream()
                .sorted((a1, a2) -> Integer.compare(
                        a2.getProdutos().size(),
                        a1.getProdutos().size()))
                .limit(topN)
                .toList();
    }
public Map<Produto, Long> produtosMaisProcurados(int topN) {
    List<Pedido> pedidos = pedidoDAO.listarPedidos();

    // Contar quantas vezes cada produto aparece
    Map<Integer, Long> contagemPorId = new HashMap<>();
    for (Pedido pedido : pedidos) {
        for (ItemPedido produto : pedido.getItensPedidos()) {
            contagemPorId.put(produto.getId(),
                    contagemPorId.getOrDefault(produto.getId(), 0L) + 1);
        }
    }

    // Converter para lista de Entry e ordenar manualmente
    List<Map.Entry<Integer, Long>> listaOrdenada = new ArrayList<>(contagemPorId.entrySet());
    listaOrdenada.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue())); // do maior para o menor

    // Pegar os topN e mapear de volta para Produto
    Map<Produto, Long> resultado = new LinkedHashMap<>();
    int count = 0;
    for (Map.Entry<Integer, Long> entry : listaOrdenada) {
        if (count >= topN) break;
        Produto produto = produtoDAO.buscarPorId(entry.getKey());
        if (produto != null) {
            resultado.put(produto, entry.getValue());
            count++;
        }
    }

    return resultado;
}



    /** Áreas geográficas com mais transações */
    public Map<String, Long> areasMaisTransacoes() {
        List<Pedido> pedidos = pedidoDAO.listarPedidos();

        return pedidos.stream()
                .collect(Collectors.groupingBy(
                        pedido -> pedido.getComprador().getProvincia(),
                        Collectors.counting()
                ));
    }

    /** =================== Contadores e Estatísticas =================== */

    public int getTotalAgricultores() {
        return agricultorDAO.getTotalAgricultores();
    }

    public int getTotalAgricultoresAtivos() {
        return agricultorDAO.getTotalAgricultoresAtivos();
    }

    /** =================== Métodos auxiliares =================== */

    public List<Agricultor> listarAgricultores() {
        return agricultorDAO.listarTodos();
    }

    public List<Comprador> listarCompradores() {
        return compradorDAO.listarCompradores();
    }

    public List<Pedido> listarPedidos() {
        return pedidoDAO.listarPedidos();
    }
}
