/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import model.Agricultor;
import model.Produto;
import static service.Validacoes.validarProduto;

/**
 *
 * @author liliano
 */
public class ProdutoService {
    private List<Produto> produtos;
    private int ultimoId;
    
    public ProdutoService(){
        this.produtos = new ArrayList<>();
        this.ultimoId = 0;
    }
    
    public boolean cadastrarProduto(Produto produto, Agricultor agricultor){
        if(produto == null || agricultor == null){
            JOptionPane.showMessageDialog(null, "Agricultor e Produto nao podem ser nulos");
            return false;
        }
        
        try{
            produto.setAgricultorId(agricultor.getId());
            produto.setAgricultorNome(agricultor.getNome());
            produto.setId(gerarNovoId()); 
            produto.setDataCadastro(new java.sql.Date(System.currentTimeMillis()).toLocalDate());
            
            
            ResultadoValidacao resultadoProduto = validarProduto(produto);
            if (!resultadoProduto.valido) {
                JOptionPane.showMessageDialog(null, resultadoProduto.mensagem, "Dados do producto nao estao correctos", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Preciso subistituir por insert para banco de dados
            produtos.add(clonarProduto(produto));
            
            agricultor.cadastrarProduto(clonarProduto(produto));
            
            JOptionPane.showConfirmDialog(null, "Produto cadastrado com sucesso");
            return true;
            
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Erro: " + e);
            return false;
        }
    }
    
    public int gerarNovoId(){
        return ++ultimoId;
    }
    
    
    
    // Metodo responsavel por clonar o produto pra evitar referencias repetidas
    private Produto clonarProduto(Produto original) {
        Produto clone = new Produto();
        clone.setId(original.getId());
        clone.setNome(original.getNome());
        clone.setDescricao(original.getDescricao());
        clone.setCategoria(original.getCategoria());
        clone.setPreco(original.getPreco());
        clone.setQuantidadeDisponivel(original.getQuantidadeDisponivel());
        clone.setDisponivel(original.isDisponivel());
        clone.setAgricultorId(original.getAgricultorId());
        clone.setAgricultorNome(original.getAgricultorNome());
        clone.setOrganico(original.isOrganico());
        clone.setClassificacaoMedia(original.getClassificacaoMedia());
        clone.setTotalAvaliacoes(original.getTotalAvaliacoes());
        clone.setTotalVendidos(original.getTotalVendidos());
        return clone;
    }
    
    
    // Todos os metodos de busca substituir por //substituir por SELECT * FROM produtos WHERE id = ?
    public Produto buscarProdutoPorId(int id) {
        return produtos.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .map(this::clonarProduto)
                .orElse(null);
    }
    
    // Metodo responsavel por fazer a busca de produtos por agricultor
    public List<Produto> buscarProdutosPorAgricultor(int agricultorId){
        return produtos.stream()
                .filter(p -> p.getAgricultorId() == agricultorId)
                .collect(Collectors.toList());
    }
    
    //Metodo responsavel por fazer a busca de produtos por Categoria
    public List<Produto> buscarProdutosPorCategoria(String categoria){
        return produtos.stream()
                .filter(p -> p.getCategoria() != null && p.getCategoria().equalsIgnoreCase(categoria))
                //.filter(Produto::isDisponivel())
                .collect(Collectors.toList());
    }
    
    
    //Metodo responvel por fazer a busca de produtos por Localizacao
    public List<Produto> buscarProdutosProximos(List<Agricultor> agricultoresProximos) {
        List<Produto> produtosProximos = new ArrayList<>();
        
        for (Agricultor agricultor : agricultoresProximos) {
            produtosProximos.addAll(buscarProdutosPorAgricultor(agricultor.getId()));
        }
        
        return produtosProximos.stream()
                .filter(Produto::isDisponivel)
                .collect(Collectors.toList());
    }
    
    /** Outros metodos:
     * 1.Atualizar produto
     * 2.
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
                JOptionPane.showMessageDialog(null, resultadoProduto.mensagem, "Dados do producto nao estao correctos", JOptionPane.ERROR_MESSAGE);
                return resultadoProduto;
            }
            
             for (int i = 0; i < produtos.size(); i++) {
                if (produtos.get(i).getId() == produtoActualizado.getId()) {
                    produtos.set(i, clonarProduto(produtoActualizado));
                    resultado.valido = true;
                    resultado.mensagem = "Produto atualizado com sucesso";
                    return resultado;
                }
            }
            
            resultado.valido = false;
            resultado.mensagem = "Produto não encontrado";
        
        }catch(Exception e){
            resultado.valido = false;
            resultado.mensagem = "Erro: " + e.getMessage();
            return resultado;
        }
        
        return resultado;
    }
    
    public ResultadoValidacao removerProduto(int produtoId, int agricultorId){
        ResultadoValidacao resultado = new ResultadoValidacao();
        
        try{
            Produto produto = buscarProdutoPorId(produtoId);
            
            if(produto == null){
                resultado.valido = false;
                resultado.mensagem = "O produto existe";
                return resultado;
            }
            if(produto.getAgricultorId() != agricultorId){
                resultado.valido = false;
                resultado.mensagem = "O produto nao pertence a esse agricultor";
                return resultado;
            }
            
            produto.setDisponivel(false);
            actualizarProduto(produto);
            
            resultado.valido = true;
            resultado.mensagem = "Produto removido com sucesso";
            return resultado;
            
        }catch(Exception e){
            resultado.valido = false;
            resultado.mensagem = "Erro: " + e.getMessage();
        }
        
        return resultado;
    }
    
    //Metodo responsavel por listar todos os produtos
    public List<Produto> listarTodosProdutos(){
        return produtos.stream()
                .map(this::clonarProduto)
                .collect(Collectors.toList());
    }
    
    //Metodo responsavel por listar os produtos disponiveis
    public List<Produto> listarProdutosDisponiveis(){
        return produtos.stream()
                .filter(Produto::isDisponivel)
                .filter(P -> P.getQuantidadeDisponivel() > 0)
                .map(this::clonarProduto)
                .collect(Collectors.toList());
    }
    
    //Metodo para buscar Produtos por Nome
    public List<Produto> buscarProdutosPorNome(String nome){
        if(nome == null || nome.trim().isEmpty()){
            return new ArrayList<>();
        }
        
        String termo = nome.toLowerCase();
        return produtos.stream()
                .filter(P -> P.getNome().toLowerCase().contains(termo))
                .filter(Produto::isDisponivel)
                .map(this::clonarProduto)
                .collect(Collectors.toList());
    }
    
    //Metodo para efectuar uma venda
    public ResultadoValidacao reduzirEstoque(int produtoId, int quantidade){
        ResultadoValidacao resultado = new ResultadoValidacao();
        
        try{
            Produto produto = buscarProdutoPorId(produtoId);
            
            if(produto ==  null) {
                resultado.valido = false;
                resultado.mensagem = "Produto nao encontrado";
                return resultado;
            }
            if(!produto.isDisponivel()){
                resultado.valido = false;
                resultado.mensagem = "O produto nao esta disponivel";
                return resultado;
            }
            if(produto.getQuantidadeDisponivel() < quantidade) {
                resultado.valido =  false;
                resultado.mensagem = "Stoque insuficiente";
                return resultado;
            }
            
            boolean sucesso = produto.reduzirEstoque(quantidade);
            if(sucesso){
                actualizarProduto(produto);
                
                resultado.valido = true;
                resultado.mensagem = "Estoque reduzido com sucesso";
                
                //Verificar antes se o estock esta baixo
                if(produto.getQuantidadeDisponivel() <= produto.getQuantidadeMinima()){
                    JOptionPane.showMessageDialog(null, "Estoque baixo");
                }
            } else {
                resultado.valido = false;
                resultado.mensagem = "Erro ao reduzir estoque";
            }
            
         
            
        }catch(Exception e){
            resultado.valido = false;
            resultado.mensagem = "Erro ao reduzir estoque: " + e.getMessage();
            
        }
        
        return resultado;
    }
    
    //Metodo para Aumentar o estoque
    public ResultadoValidacao reporEstoque(int produtoId, int quantidade) {
        ResultadoValidacao resultado = new ResultadoValidacao();
        
        try{
            Produto produto = buscarProdutoPorId(produtoId);
            
            if(produto == null){
                resultado.valido = false;
                resultado.mensagem = "Produto nao encontrado";
                return resultado;
            }
            
            if(quantidade <= 0){
                resultado.valido= false;
                resultado.mensagem = "Quantidade deve ser maior que 0";
                return resultado;
            }
            
            produto.reporEstoque(quantidade);
            actualizarProduto(produto);
            
            resultado.valido = true;
            resultado.mensagem = "Estoque reposto com sucesso";
            //resutado.dados = produto;
            
            
            
        }catch(Exception e){
            resultado.valido = false;
            resultado.mensagem = "Erro ao aumentar a quantidade de produtos: " + e.getMessage();
   
        
        }
        
        return resultado;
    }
    
    //Metodo responsavel pela classificao do produto
    public ResultadoValidacao actualizarClassificacao(int produtoId, int novaAvaliacao){
        ResultadoValidacao  resultado = new ResultadoValidacao();
        
        try{
            if(novaAvaliacao < 1 || novaAvaliacao > 5) {
                resultado.valido = false;
                resultado.mensagem = "Avaliacao invalida";
                return resultado;
            }
            
            Produto produto = buscarProdutoPorId(produtoId);
            
            if(produto == null) {
                resultado.valido = false;
                resultado.mensagem = "Produto nao encontrado";
                return resultado;
            }
            
            produto.atualizarClassificacaoMedia(novaAvaliacao);
            actualizarProduto(produto);
            
            resultado.valido = true;
            resultado.mensagem = "Classificacao actualizada com sucesso";
            //resultado.dados = produto;
            
        }catch(Exception e){
            resultado.valido = false;
            resultado.mensagem = "Erro ao actuaizar o produto";
        }
        
        return resultado;
    }
    
    //Metodo responsavel por listar os mais vendidos
    public List<Produto> listarProdutosMaisvendidos(int limite) {
        return produtos.stream()
                .filter(Produto::isDisponivel)
                .sorted((p1, p2) -> Integer.compare(p2.getTotalVendidos(), p1.getTotalVendidos()))
                .limit(limite)
                .map(this::clonarProduto)
                .collect(Collectors.toList());
    }
    
    //Metodo responsavel por listar produtos mais bem avaliados
    public List<Produto> listarProdutosMelhorAvaliados(int limite) {
        return produtos.stream()
                .filter(p -> p.getTotalAvaliacoes() >= 3)
                .filter(Produto::isDisponivel)
                .sorted((p1, p2) -> Double.compare(p2.getClassificacaoMedia(), p1.getClassificacaoMedia()))
                .limit(limite)
                .map(this::clonarProduto)
                .collect(Collectors.toList());
    }
    
    // metodo responsavel por verificar produtos com o estoque baixo
    public List<Produto> getProdutosComEstoqueBaixo() {
        return produtos.stream()
                .filter(p -> p.getQuantidadeDisponivel() <= p.getQuantidadeMinima())
                .filter(Produto::isDisponivel)
                .map(this::clonarProduto)
                .collect(Collectors.toList());
    }
    
    
    //Metodo responsavel por listar produtos por faixa de precos
    public List<Produto> buscarProdutosPorPreco(double precoMin, double precoMax) {
        return produtos.stream()
                .filter(p -> p.getPreco() >= precoMin && p.getPreco() <= precoMax)
                .filter(Produto::isDisponivel)
                .map(this::clonarProduto)
                .collect(Collectors.toList());
    }
    
    
    // Metodo responsavel por calcular a quantidade total de produtos
     public int getTotalProdutos() {
        return produtos.size();
    }
    
    // Metodo responsavel por calcular a quantidade de produtos disponiveis 
    public int getTotalProdutosDisponiveis() {
        return (int) produtos.stream()
                .filter(Produto::isDisponivel)
                .count();
    }
    
    // Metodo responsavel por calcular a quantidade de produtos por agricultor
    public int getTotalProdutosPorAgricultor(int agricultorId) {
        return (int) produtos.stream()
                .filter(p -> p.getAgricultorId() == agricultorId)
                .count();
    }
    
    public Map<String, Long> getEstatisticasPorCategoria() {
        return produtos.stream()
                .collect(Collectors.groupingBy(
                    Produto::getCategoria, 
                    Collectors.counting()
                ));
    }
}
