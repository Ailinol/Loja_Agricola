/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

/**
 *
 * @author liliano
 */
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LocalizacaoService {
    
    private static final Random random = new Random();
    
    // Coordenadas centrais aproximadas de cada província (latitude, longitude)
    private static final Map<String, double[]> COORDENADAS_PROVINCIAS = new HashMap<>();
    
    static {
        // Coordenadas centrais aproximadas de cada província
        COORDENADAS_PROVINCIAS.put("Maputo", new double[]{-25.9692, 32.5732});
        COORDENADAS_PROVINCIAS.put("Maputo Cidade", new double[]{-25.9692, 32.5732});
        COORDENADAS_PROVINCIAS.put("Gaza", new double[]{-24.5725, 32.6451});
        COORDENADAS_PROVINCIAS.put("Inhambane", new double[]{-23.8650, 35.3833});
        COORDENADAS_PROVINCIAS.put("Sofala", new double[]{-19.8333, 34.8500});
        COORDENADAS_PROVINCIAS.put("Manica", new double[]{-18.9333, 32.8750});
        COORDENADAS_PROVINCIAS.put("Tete", new double[]{-16.1667, 33.6000});
        COORDENADAS_PROVINCIAS.put("Zambézia", new double[]{-17.8333, 36.6667});
        COORDENADAS_PROVINCIAS.put("Nampula", new double[]{-15.1167, 39.2667});
        COORDENADAS_PROVINCIAS.put("Cabo Delgado", new double[]{-12.9600, 40.5078});
        COORDENADAS_PROVINCIAS.put("Niassa", new double[]{-13.3000, 36.0667});
    }
    
    // Variação por distrito (pequenos ajustes baseados no distrito)
    private static final Map<String, double[]> AJUSTES_DISTRITOS = new HashMap<>();
    
    static {
        // Maputo
        AJUSTES_DISTRITOS.put("Boane", new double[]{-0.02, 0.01});
        AJUSTES_DISTRITOS.put("Magude", new double[]{-0.03, -0.02});
        AJUSTES_DISTRITOS.put("Manhica", new double[]{0.01, 0.02});
        AJUSTES_DISTRITOS.put("Marracuene", new double[]{0.02, 0.03});
        AJUSTES_DISTRITOS.put("Matola", new double[]{0.01, -0.01});
        AJUSTES_DISTRITOS.put("Matutuine", new double[]{-0.04, -0.03});
        AJUSTES_DISTRITOS.put("Moamba", new double[]{-0.01, -0.02});
        AJUSTES_DISTRITOS.put("Namaacha", new double[]{-0.05, -0.04});
        
        // Maputo Cidade
        AJUSTES_DISTRITOS.put("KaMpfumo", new double[]{0.001, 0.001});
        AJUSTES_DISTRITOS.put("Nlhamankulu", new double[]{0.002, -0.001});
        AJUSTES_DISTRITOS.put("KaMaxaquene", new double[]{-0.001, 0.002});
        AJUSTES_DISTRITOS.put("KaMavota", new double[]{0.003, 0.001});
        AJUSTES_DISTRITOS.put("KaMubukwana", new double[]{-0.002, -0.002});
        AJUSTES_DISTRITOS.put("KaTembe", new double[]{-0.004, 0.003});
        AJUSTES_DISTRITOS.put("KaNyaka", new double[]{-0.005, 0.004});
        
        // Gaza (exemplos)
        AJUSTES_DISTRITOS.put("Xai-Xai", new double[]{0.01, 0.01});
        AJUSTES_DISTRITOS.put("Chókwè", new double[]{-0.02, 0.02});
        AJUSTES_DISTRITOS.put("Bilene", new double[]{0.03, -0.01});
        // Adicione mais distritos conforme necessário
    }
    
    public static class Coordenadas {
        private final double latitude;
        private final double longitude;
        
        public Coordenadas(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
        
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        
        @Override
        public String toString() {
            return String.format("Lat: %.6f, Lng: %.6f", latitude, longitude);
        }
    }
    
    /**
     * Gera coordenadas realistas baseadas na província e distrito
     */
    public static Coordenadas gerarCoordenadasRealistas(String provincia, String distrito) {
        // Verifica se a província existe
        if (!COORDENADAS_PROVINCIAS.containsKey(provincia)) {
            // Fallback para Maputo se província não encontrada
            provincia = "Maputo";
        }
        
        // Obtém coordenadas base da província
        double[] coordBase = COORDENADAS_PROVINCIAS.get(provincia);
        double latBase = coordBase[0];
        double lngBase = coordBase[1];
        
        // Aplica ajuste do distrito se disponível
        if (distrito != null && AJUSTES_DISTRITOS.containsKey(distrito)) {
            double[] ajuste = AJUSTES_DISTRITOS.get(distrito);
            latBase += ajuste[0];
            lngBase += ajuste[1];
        }
        
        // Gera variação aleatória (aproximadamente 1-3km de raio)
        double variacaoLat = (random.nextDouble() * 0.03) - 0.015; // ± ~1.5km
        double variacaoLng = (random.nextDouble() * 0.03) - 0.015; // ± ~1.5km
        
        double latitudeFinal = latBase + variacaoLat;
        double longitudeFinal = lngBase + variacaoLng;
        
        // Garante que as coordenadas estão dentro de Moçambique
        latitudeFinal = Math.max(-27.0, Math.min(-10.0, latitudeFinal));
        longitudeFinal = Math.max(30.0, Math.min(41.0, longitudeFinal));
        
        System.out.println("📍 Coordenadas geradas para " + provincia + 
                         "/" + distrito + ": " + latitudeFinal + ", " + longitudeFinal);
        
        return new Coordenadas(latitudeFinal, longitudeFinal);
    }
    
    /**
     * Gera coordenadas para um agricultor específico (mais consistentes)
     */
    public static Coordenadas gerarCoordenadasParaAgricultor(String provincia, String distrito, String nome) {
        // Usa o hash do nome para gerar coordenadas consistentes para o mesmo agricultor
        int hash = Math.abs(nome.hashCode());
        Random seededRandom = new Random(hash);
        
        double[] coordBase = COORDENADAS_PROVINCIAS.getOrDefault(provincia, COORDENADAS_PROVINCIAS.get("Maputo"));
        double latBase = coordBase[0];
        double lngBase = coordBase[1];
        
        // Aplica ajuste do distrito
        if (distrito != null && AJUSTES_DISTRITOS.containsKey(distrito)) {
            double[] ajuste = AJUSTES_DISTRITOS.get(distrito);
            latBase += ajuste[0];
            lngBase += ajuste[1];
        }
        
        // Variação baseada no hash (sempre a mesma para o mesmo nome)
        double variacaoLat = (seededRandom.nextDouble() * 0.02) - 0.01; // ± ~1km
        double variacaoLng = (seededRandom.nextDouble() * 0.02) - 0.01; // ± ~1km
        
        return new Coordenadas(latBase + variacaoLat, lngBase + variacaoLng);
    }
    
    /**
     * Calcula distância aproximada entre duas coordenadas (em km)
     */
    public static double calcularDistancia(Coordenadas coord1, Coordenadas coord2) {
        double lat1 = Math.toRadians(coord1.getLatitude());
        double lon1 = Math.toRadians(coord1.getLongitude());
        double lat2 = Math.toRadians(coord2.getLatitude());
        double lon2 = Math.toRadians(coord2.getLongitude());
        
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        
        double a = Math.pow(Math.sin(dlat / 2), 2)
                 + Math.cos(lat1) * Math.cos(lat2)
                 * Math.pow(Math.sin(dlon / 2), 2);
        
        double c = 2 * Math.asin(Math.sqrt(a));
        
        // Raio da Terra em km
        double r = 6371;
        
        return c * r;
    }
}