package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.*;

@MappedSuperclass
public abstract class Pessoa implements Serializable {
    // Identificação

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    protected Integer id;
    protected String codigoUnico;
    
    // Informações pessoais
    protected String nome;
    protected String email;
    protected String telefone;
    
    // Localização para integração com Google Maps
    protected String bairro;
    protected String provincia;
    protected String distrito;
    protected String rua;
    protected String numeroCasa;
    
    // Coordenadas geográficas (para Google Maps)
    @Transient
    protected Double latitude;
    @Transient
    protected Double longitude;
    
    // Datas importantes
    protected LocalDate dataCadastro;
    protected LocalDateTime dataUltimaAtualizacao;
    
    // Status
    protected boolean ativo;
    
    // Construtores
    public Pessoa() {
        this.dataCadastro = LocalDate.now();
        this.dataUltimaAtualizacao = LocalDateTime.now();
        this.ativo = true;
    }
    
    // Construtor mantido para compatibilidade (mas não recomendado para uso futuro)
    public Pessoa(String nome, String email, String telefone, String bairro) {
        this();
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.bairro = bairro;
    }
    
    // NOVO CONSTRUTOR COMPLETO - RECOMENDADO
    public Pessoa(String nome, String email, String telefone, String provincia, 
                 String distrito, String bairro, String rua, String numeroCasa) {
        this();
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.provincia = provincia;
        this.distrito = distrito;
        this.bairro = bairro;
        this.rua = rua;
        this.numeroCasa = numeroCasa;
    }
    
    // Getters e Setters (mantidos iguais)
    public Integer getId() { 
        return id; 
    }
    public void setId(Integer id) { 
        this.id = id; 
    }
    
    public String getCodigoUnico() { 
        return codigoUnico; 
    }
    public void setCodigoUnico(String codigoUnico) { 
        this.codigoUnico = codigoUnico; 
    }
    
    public String getNome() { 
        return nome; 
    }
    public void setNome(String nome) { 
        this.nome = nome; 
    }
    
    public String getEmail() { 
        return email; 
    }
    public void setEmail(String email) { 
        this.email = email; 
    
    }
    
    public String getTelefone() { 
        return telefone; 
    }
    public void setTelefone(String telefone) { 
        this.telefone = telefone; 
    }
    
    public String getBairro() { 
        return bairro; 
    }
    public void setBairro(String bairro) { 
        this.bairro = bairro; 
    }
    
    public String getProvincia() { 
        return provincia; 
    }
    public void setProvincia(String provincia) { 
        this.provincia = provincia; 
    }
    
    public String getDistrito() { 
        return distrito; 
    }
    public void setDistrito(String distrito) { 
        this.distrito = distrito;
    }
    
    public String getRua() { 
        return rua; 
    }
    public void setRua(String rua) { 
        this.rua = rua; 
    }
    
    public String getNumeroCasa() { 
        return numeroCasa; 
    }
    public void setNumeroCasa(String numeroCasa) {
        this.numeroCasa = numeroCasa;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    public Double getLongitude() { 
        return longitude; 
    }
    public void setLongitude(Double longitude) { 
        this.longitude = longitude;
    }
    
    public LocalDate getDataCadastro() { 
        return dataCadastro;
    }
    public void setDataCadastro(LocalDate dataCadastro) { 
        this.dataCadastro = dataCadastro;
    }
    
    public LocalDateTime getDataUltimaAtualizacao() { 
        return dataUltimaAtualizacao;
    }
    public void setDataUltimaAtualizacao(LocalDateTime dataUltimaAtualizacao) { 
        this.dataUltimaAtualizacao = dataUltimaAtualizacao; 
    }
    
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    
    public abstract String getInfo();
    
    public void atualizarContato(String email, String telefone, String bairro) {
        this.email = email;
        this.telefone = telefone;
        this.bairro = bairro;
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }
    
    // Método para atualizar coordenadas geográficas
    public void atualizarLocalizacao(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }
    
    // Método para calcular distância entre esta pessoa e outra (fórmula de Haversine)
    public double calcularDistancia(Pessoa outraPessoa) {
        if (this.latitude == null || this.longitude == null || 
            outraPessoa.getLatitude() == null || outraPessoa.getLongitude() == null) {
            return -1;
        }
        
        final int R = 6371; // Raio da Terra em quilômetros
        
        double latDistance = Math.toRadians(outraPessoa.getLatitude() - this.latitude);
        double lonDistance = Math.toRadians(outraPessoa.getLongitude() - this.longitude);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(outraPessoa.getLatitude()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // Distância em quilômetros
        
        return distance;
    }
    
    // Método para obter endereço completo formatado
    public String getEnderecoCompleto() {
        return String.format("%s, %s, %s - %s, %s", 
                            rua, numeroCasa, bairro, distrito, provincia);
    }
}