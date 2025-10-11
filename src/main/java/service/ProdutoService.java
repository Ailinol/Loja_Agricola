package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.swing.JOptionPane;
import model.Agricultor;
import model.Produto;
import static service.Validacoes.validarProduto;

/**
 *
 * @author liliano
 */
public class ProdutoService {
    private EntityManagerFactory emf;
    private EntityManager em;
    
    public ProdutoService(){
        this.emf = Persistence.createEntityManagerFactory("greenMatch-jpa");
        this.em = emf.createEntityManager();
    }
    
    public boolean cadastrarProduto(Produto produto, Agricultor agricultor){
        if(produto == null || agricultor == null){
            JOptionPane.showMessageDialog(null, "Agricultor e Produto nao podem ser nulos");
            return false;
        }
        
        try{
            // Buscar agricultor do banco para garantir que está managed
            Agricultor agricultorManaged = em.find(Agricultor.class, agricultor.getId());
            if (agricultorManaged == null) {
                JOptionPane.showMessageDialog(null, "Agricultor não encontrado no banco de dados");
                return false;
            }
            
            produto.setAgricultorId(agricultorManaged.getId());
            produto.setAgricultorNome(agricultorManaged.getNome());
            produto.setDataCadastro(new java.sql.Date(System.currentTimeMillis()).toLocalDate());
            
            ResultadoValidacao resultadoProduto = validarProduto(produto);
            if (!resultadoProduto.valido) {
                JOptionPane.showMessageDialog(null, resultadoProduto.mensagem, "Dados do producto nao estao correctos", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // SALVAR NO BANCO DE DADOS
            em.getTransaction().begin();
            em.persist(produto);
            
            // Adicionar produto ao agricultor (se necessário)
            agricultorManaged.cadastrarProduto(produto);
            em.merge(agricultorManaged);
            
            em.getTransaction().commit();
            
            JOptionPane.showMessageDialog(null, "Produto cadastrado com sucesso");
            return true;
            
        } catch(Exception e){
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
            return false;
        }
    }
    
    // TODOS OS MÉTODOS DE BUSCA ADAPTADOS PARA JPA:
    
    public Produto buscarProdutoPorId(int id) {
        try {
            return em.find(Produto.class, id);
        } catch (Exception e) {
            return null;
        }
    }
    
    // Metodo responsavel por fazer a busca de produtos por agricultor
    public List<Produto> buscarProdutosPorAgricultor(int agricultorId){
        try {
            TypedQuery<Produto> query = em.createQuery(
                "SELECT p FROM Produto p WHERE p.agricultorId = :agricultorId", Produto.class);
            query.setParameter("agricultorId", agricultorId);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    //Metodo responsavel por fazer a busca de produtos por Categoria
    public List<Produto> buscarProdutosPorCategoria(String categoria){
        try {
            TypedQuery<Produto> query = em.createQuery(
                "SELECT p FROM Produto p WHERE p.categoria = :categoria AND p.disponivel = true", Produto.class);
            query.setParameter("categoria", categoria);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    //Metodo responsavel por listar todos os produtos
    public List<Produto> listarTodosProdutos(){
        try {
            return em.createQuery("SELECT p FROM Produto p", Produto.class).getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    //Metodo responsavel por listar os produtos disponiveis
    public List<Produto> listarProdutosDisponiveis(){
        try {
            TypedQuery<Produto> query = em.createQuery(
                "SELECT p FROM Produto p WHERE p.disponivel = true AND p.quantidadeDisponivel > 0", Produto.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    //Metodo para buscar Produtos por Nome
    public List<Produto> buscarProdutosPorNome(String nome){
        if(nome == null || nome.trim().isEmpty()){
            return new ArrayList<>();
        }
        
        try {
            TypedQuery<Produto> query = em.createQuery(
                "SELECT p FROM Produto p WHERE LOWER(p.nome) LIKE LOWER(:nome) AND p.disponivel = true", Produto.class);
            query.setParameter("nome", "%" + nome + "%");
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    /** 
     * Outros metodos atualizados para JPA
     */
    
    public ResultadoValidacao actualizarProduto(Produto produtoActualizado){
        ResultadoValidacao resultado = new ResultadoValidacao();
        
        if(produtoActualizado == null){
            resultado.valido = false;
            resultado.mensagem = "O produto nao pode ser nulo";
            return resultado;
        }
        
        try{
            ResultadoValidacao resultadoProduto = validarProduto(produtoActualizado);
            if (!resultadoProduto.valido) {
                return resultadoProduto;
            }
            
            em.getTransaction().begin();
            Produto produtoManaged = em.merge(produtoActualizado);
            em.getTransaction().commit();
            
            resultado.valido = true;
            resultado.mensagem = "Produto atualizado com sucesso";
            
        } catch(Exception e){
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            resultado.valido = false;
            resultado.mensagem = "Erro: " + e.getMessage();
        }
        
        return resultado;
    }
    
    public ResultadoValidacao removerProduto(int produtoId, int agricultorId){
        ResultadoValidacao resultado = new ResultadoValidacao();
        
        try{
            em.getTransaction().begin();
            
            Produto produto = em.find(Produto.class, produtoId);
            
            if(produto == null){
                resultado.valido = false;
                resultado.mensagem = "O produto não existe";
                em.getTransaction().rollback();
                return resultado;
            }
            
            if(produto.getAgricultorId() != agricultorId){
                resultado.valido = false;
                resultado.mensagem = "O produto nao pertence a esse agricultor";
                em.getTransaction().rollback();
                return resultado;
            }
            
            produto.setDisponivel(false);
            em.merge(produto);
            em.getTransaction().commit();
            
            resultado.valido = true;
            resultado.mensagem = "Produto removido com sucesso";
            
        } catch(Exception e){
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            resultado.valido = false;
            resultado.mensagem = "Erro: " + e.getMessage();
        }
        
        return resultado;
    }
    
    //Metodo para efectuar uma venda
    public ResultadoValidacao reduzirEstoque(int produtoId, int quantidade){
        ResultadoValidacao resultado = new ResultadoValidacao();
        
        try{
            em.getTransaction().begin();
            
            Produto produto = em.find(Produto.class, produtoId);
            
            if(produto ==  null) {
                resultado.valido = false;
                resultado.mensagem = "Produto nao encontrado";
                em.getTransaction().rollback();
                return resultado;
            }
            
            if(!produto.isDisponivel()){
                resultado.valido = false;
                resultado.mensagem = "O produto nao esta disponivel";
                em.getTransaction().rollback();
                return resultado;
            }
            
            if(produto.getQuantidadeDisponivel() < quantidade) {
                resultado.valido =  false;
                resultado.mensagem = "Stoque insuficiente";
                em.getTransaction().rollback();
                return resultado;
            }
            
            boolean sucesso = produto.reduzirEstoque(quantidade);
            if(sucesso){
                em.merge(produto);
                em.getTransaction().commit();
                
                resultado.valido = true;
                resultado.mensagem = "Estoque reduzido com sucesso";
                
                //Verificar antes se o estock esta baixo
                if(produto.getQuantidadeDisponivel() <= produto.getQuantidadeMinima()){
                    JOptionPane.showMessageDialog(null, "Estoque baixo para: " + produto.getNome());
                }
            } else {
                em.getTransaction().rollback();
                resultado.valido = false;
                resultado.mensagem = "Erro ao reduzir estoque";
            }
            
        } catch(Exception e){
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            resultado.valido = false;
            resultado.mensagem = "Erro ao reduzir estoque: " + e.getMessage();
        }
        
        return resultado;
    }
    
    // MÉTODOS DE ESTATÍSTICAS ATUALIZADOS:
    
    // Metodo responsavel por calcular a quantidade total de produtos
    public int getTotalProdutos() {
        try {
            Long count = em.createQuery("SELECT COUNT(p) FROM Produto p", Long.class).getSingleResult();
            return count.intValue();
        } catch (Exception e) {
            return 0;
        }
    }
    
    // Metodo responsavel por calcular a quantidade de produtos disponiveis 
    public int getTotalProdutosDisponiveis() {
        try {
            Long count = em.createQuery("SELECT COUNT(p) FROM Produto p WHERE p.disponivel = true", Long.class).getSingleResult();
            return count.intValue();
        } catch (Exception e) {
            return 0;
        }
    }
    
    // Metodo responsavel por calcular a quantidade de produtos por agricultor
    public int getTotalProdutosPorAgricultor(int agricultorId) {
        try {
            Long count = em.createQuery("SELECT COUNT(p) FROM Produto p WHERE p.agricultorId = :agricultorId", Long.class)
                          .setParameter("agricultorId", agricultorId)
                          .getSingleResult();
            return count.intValue();
        } catch (Exception e) {
            return 0;
        }
    }
    
    public Map<String, Long> getEstatisticasPorCategoria() {
        try {
            return em.createQuery("SELECT p.categoria, COUNT(p) FROM Produto p GROUP BY p.categoria", Object[].class)
                    .getResultStream()
                    .collect(Collectors.toMap(
                        obj -> (String) obj[0],
                        obj -> (Long) obj[1]
                    ));
        } catch (Exception e) {
            return Map.of();
        }
    }
    
    // Método para fechar conexão
    public void fecharConexao() {
        if (em != null && em.isOpen()) {
            em.close();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}