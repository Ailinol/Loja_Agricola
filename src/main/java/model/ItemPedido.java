/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import javax.persistence.*;

/**
 *
 * @author Thomas
 */
@Entity
public class ItemPedido {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;
    
    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;
    
    private int quantidade;


    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
    
    
    // para cada item adicionado, calcula-se o subtotal
    public double getSubtotal(){
        return quantidade * getProduto().getPesoUnitario();
    }
    
}
