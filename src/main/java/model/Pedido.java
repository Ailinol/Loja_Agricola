package model;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import javax.persistence.*;

/**
 * 
 */
@Entity
public class Pedido {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "comprador_id")
    private Comprador comprador;

    @OneToMany(mappedBy = "pedido")
    private List<ItemPedido> itensPedidos = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private StatusPedido status;

    private LocalDateTime dataHora;


    public Pedido(){
        this.dataHora = LocalDateTime.now();
        this.status = StatusPedido.PENDENTE;
    }


    public Comprador getComprador() {
        return comprador;
    }

    public void setComprador(Comprador comprador) {
        this.comprador = comprador;
    }

    public List<ItemPedido> getItensPedidos() {
        return itensPedidos;
    }

    public void setItensPedidos(List<ItemPedido> itensPedidos) {
        this.itensPedidos = itensPedidos;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }


    // 
    public double getValorTotal(){
        double total = 0.0;
        if(itensPedidos != null){
            for(ItemPedido i : itensPedidos){
                total += i.getSubtotal();
            }  
            return total;
        }
        else return total;
    }
    
    

    /**
     * @return
     */
    public void confirmar() {
        // TODO implement here

    }

    /**
     * @return
     */
    public void cancelar() {
        // TODO implement here

    }

    /**
     * @param status 
     * @return
     */
    public void atualizarStatus(String status) {
        // TODO implement here

    }

    /**
     * 
     */
    public void Operation2() {
        // TODO implement here
    }

}