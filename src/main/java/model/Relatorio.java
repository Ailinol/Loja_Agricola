package model;

import java.util.List;
import java.util.Map;

public class Relatorio {

    private int numeroClientesAtivos;
    private List<String> agricultoresMaisProdutivos;
    private List<String> produtosMaisProcurados;
    private Map<String, Integer> transacoesPorProvincia;

    public Relatorio() {
    }

    public int getNumeroClientesAtivos() {
        return numeroClientesAtivos;
    }

    public void setNumeroClientesAtivos(int numeroClientesAtivos) {
        this.numeroClientesAtivos = numeroClientesAtivos;
    }

    public List<String> getAgricultoresMaisProdutivos() {
        return agricultoresMaisProdutivos;
    }

    public void setAgricultoresMaisProdutivos(List<String> agricultoresMaisProdutivos) {
        this.agricultoresMaisProdutivos = agricultoresMaisProdutivos;
    }

    public List<String> getProdutosMaisProcurados() {
        return produtosMaisProcurados;
    }

    public void setProdutosMaisProcurados(List<String> produtosMaisProcurados) {
        this.produtosMaisProcurados = produtosMaisProcurados;
    }

    public Map<String, Integer> getTransacoesPorProvincia() {
        return transacoesPorProvincia;
    }

    public void setTransacoesPorProvincia(Map<String, Integer> transacoesPorProvincia) {
        this.transacoesPorProvincia = transacoesPorProvincia;
    }
}
