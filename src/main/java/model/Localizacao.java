/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author liliano
 */
public class Localizacao {
    private double latitude;
    private double longitude;
    private String endereco;
    private String provincia;
    private String distrito;
    private String bairro;
    
    public Localizacao() {}
    
    public Localizacao(double latitude, double longitude, String endereco) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.endereco = endereco;
    }
    
    public double getLatitude() { 
        return latitude; 
    }
    public void setLatitude(double latitude) { 
        this.latitude = latitude; 
    }
    
    public double getLongitude() { 
        return longitude; 
    }
    public void setLongitude(double longitude) { 
        this.longitude = longitude; 
    }
    
    public String getEndereco() { 
        return endereco; 
    }
    public void setEndereco(String endereco) { 
        this.endereco = endereco; 
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
    
    public String getBairro() { 
        return bairro; 
    }
    public void setBairro(String bairro) { 
        this.bairro = bairro; 
    }
    
    @Override
    public String toString() {
        return String.format("Lat: %.6f, Long: %.6f - %s", latitude, longitude, endereco);
    }
}