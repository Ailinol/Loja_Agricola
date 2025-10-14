package model;

import java.util.List;

public class Monitorizacao {

    private List<String> inconsistenciasDetectadas;
    private boolean sistemaIntegro;
    private String beneficio; // texto explicando o ganho qualitativo da monitorização

    public Monitorizacao() {
    }

    public List<String> getInconsistenciasDetectadas() {
        return inconsistenciasDetectadas;
    }

    public void setInconsistenciasDetectadas(List<String> inconsistenciasDetectadas) {
        this.inconsistenciasDetectadas = inconsistenciasDetectadas;
    }

    public boolean isSistemaIntegro() {
        return sistemaIntegro;
    }

    public void setSistemaIntegro(boolean sistemaIntegro) {
        this.sistemaIntegro = sistemaIntegro;
    }

    public String getBeneficio() {
        return beneficio;
    }

    public void setBeneficio(String beneficio) {
        this.beneficio = beneficio;
    }
}
