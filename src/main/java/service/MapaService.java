package service;

import model.Localizacao;
import javafx.scene.web.WebEngine;
import javafx.concurrent.Worker;
import netscape.javascript.JSObject;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MapaService {
    
    private WebEngine webEngine;
    private Map<String, Localizacao> marcadores = new HashMap<>();
    private boolean mapaPronto = false;
    private Consumer<String> onMapaCarregadoCallback;
    
    public MapaService(WebEngine webEngine) {
        this.webEngine = webEngine;
        configurarMapa();
    }
    
    private void configurarMapa() {
        webEngine.setJavaScriptEnabled(true);
        
        // IMPORTANTE: Usar apenas ASCII e converter caracteres especiais
        String htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>GreenMatch - Mapa</title>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
            <link rel="stylesheet" href="https://unpkg.com/leaflet-routing-machine@3.2.12/dist/leaflet-routing-machine.css" />
            
            <style>
                * { margin: 0; padding: 0; }
                html, body { 
                    width: 100%; 
                    height: 100%; 
                    overflow: hidden;
                }
                #map { 
                    width: 100%; 
                    height: 100%; 
                    background: #94a3b8;
                }
                
                .leaflet-container {
                    background: #94a3b8;
                }
                
                .custom-marker-user {
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    width: 40px;
                    height: 40px;
                    border-radius: 50%;
                    border: 3px solid white;
                    box-shadow: 0 4px 15px rgba(102, 126, 234, 0.6);
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-size: 20px;
                    animation: pulse 2s infinite;
                }
                
                .custom-marker-farmer {
                    background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
                    width: 40px;
                    height: 40px;
                    border-radius: 50%;
                    border: 3px solid white;
                    box-shadow: 0 4px 15px rgba(76, 175, 80, 0.6);
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-size: 20px;
                }
                
                /* Tooltip customizado do Leaflet */
                .custom-tooltip {
                    background: rgba(26, 30, 46, 0.95);
                    color: white;
                    border: 2px solid #4CAF50;
                    border-radius: 8px;
                    padding: 4px 10px;
                    font-size: 11px;
                    font-weight: bold;
                    box-shadow: 0 2px 10px rgba(0,0,0,0.4);
                }
                
                .custom-tooltip::before {
                    border-top-color: rgba(26, 30, 46, 0.95);
                }
                
                @keyframes pulse {
                    0%, 100% { transform: scale(1); }
                    50% { transform: scale(1.15); }
                }
                
                .leaflet-popup-content-wrapper {
                    background: rgba(26, 30, 46, 0.98);
                    color: white;
                    border-radius: 12px;
                    border: 2px solid #4CAF50;
                }
                
                .leaflet-popup-content {
                    margin: 15px;
                    color: white;
                }
                
                .leaflet-popup-tip {
                    background: rgba(26, 30, 46, 0.98);
                }
                
                .leaflet-routing-container {
                    background: rgba(26, 30, 46, 0.98) !important;
                    color: white !important;
                    border-radius: 12px !important;
                    border: 2px solid #4CAF50 !important;
                }
                
                .leaflet-routing-alt {
                    background: transparent !important;
                    color: white !important;
                }
                
                .leaflet-routing-alt h2, .leaflet-routing-alt h3 {
                    color: #4CAF50 !important;
                }
                
                #loading {
                    position: absolute;
                    top: 50%;
                    left: 50%;
                    transform: translate(-50%, -50%);
                    background: rgba(26, 30, 46, 0.98);
                    padding: 30px;
                    border-radius: 15px;
                    color: white;
                    z-index: 9999;
                    border: 2px solid #4CAF50;
                    text-align: center;
                }
                
                .spinner {
                    border: 4px solid rgba(76, 175, 80, 0.3);
                    border-top: 4px solid #4CAF50;
                    border-radius: 50%;
                    width: 40px;
                    height: 40px;
                    animation: spin 1s linear infinite;
                    margin: 0 auto 15px;
                }
                
                @keyframes spin {
                    0% { transform: rotate(0deg); }
                    100% { transform: rotate(360deg); }
                }
            </style>
        </head>
        <body>
            <div id="loading">
                <div class="spinner"></div>
                <div>Carregando mapa...</div>
            </div>
            <div id="map"></div>
            
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <script src="https://unpkg.com/leaflet-routing-machine@3.2.12/dist/leaflet-routing-machine.js"></script>
            
            <script>
                var map = null;
                var marcadores = {};
                var userMarker = null;
                var currentRoute = null;
                var mapReady = false;
                
                var userIcon = L.divIcon({
                    className: '',
                    html: '<div class="custom-marker-user">📍</div>',
                    iconSize: [40, 40],
                    iconAnchor: [20, 40]
                });
                
                var farmerIcon = L.divIcon({
                    className: '',
                    html: '<div class="custom-marker-farmer">🌾</div>',
                    iconSize: [40, 40],
                    iconAnchor: [20, 40]
                });
                
                function inicializarMapa() {
                    try {
                        console.log("🌍 Inicializando mapa...");
                        
                        map = L.map('map', {
                            center: [-25.9692, 32.5732],
                            zoom: 12,
                            zoomControl: true,
                            maxZoom: 18,
                            minZoom: 6,
                            // Configurações para melhor rendering
                            preferCanvas: false,
                            renderer: L.canvas(),
                            zoomAnimation: true,
                            fadeAnimation: true,
                            markerZoomAnimation: true,
                            // Performance
                            trackResize: true,
                            boxZoom: true,
                            doubleClickZoom: true,
                            scrollWheelZoom: true
                        });
                        
                        // SOLUÇÃO ROBUSTA: Múltiplas camadas com fallback
                        var tileUrls = [
                            'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
                            'https://tile.openstreetmap.org/{z}/{x}/{y}.png',
                            'https://a.tile.openstreetmap.org/{z}/{x}/{y}.png'
                        ];
                        
                        var currentTileUrlIndex = 0;
                        var tileLoadErrors = 0;
                        var maxTileLoadErrors = 3;
                        
                        function createTileLayer() {
                            return L.tileLayer(tileUrls[currentTileUrlIndex], {
                                attribution: '&copy; OpenStreetMap contributors',
                                maxZoom: 18,
                                minZoom: 6,
                                tileSize: 256,
                                zoomOffset: 0,
                                // CRÍTICO: Configurações anti-tile-cinza
                                keepBuffer: 16,        // Mantém mais tiles em cache
                                maxNativeZoom: 18,
                                updateWhenIdle: false, // Atualiza durante movimento
                                updateWhenZooming: false,
                                updateInterval: 150,
                                crossOrigin: true,
                                // Retry automático
                                errorTileUrl: '',
                                // Timeout
                                timeout: 5000,
                                // Subdomínios para load balancing
                                subdomains: ['a', 'b', 'c'],
                                // Detectar retina
                                detectRetina: false,
                                // Opacidade
                                opacity: 1.0,
                                // Z-index
                                zIndex: 1,
                                // ClassName
                                className: 'leaflet-tile-loaded'
                            });
                        }
                        
                        var tiles = createTileLayer();
                        tiles.addTo(map);
                        
                        // Handler para tiles carregados com sucesso
                        tiles.on('load', function() {
                            console.log("✅ Tiles carregados!");
                            tileLoadErrors = 0; // Reset contador de erros
                            setTimeout(function() {
                                hideLoading();
                            }, 500);
                        });
                        
                        // Handler para tiles com erro - RETRY automático
                        tiles.on('tileerror', function(error) {
                            console.warn("⚠️ Erro em tile, tentando reload...");
                            tileLoadErrors++;
                            
                            // Tentar recarregar o tile específico
                            if (error.tile) {
                                setTimeout(function() {
                                    if (error.tile.src) {
                                        var originalSrc = error.tile.src;
                                        error.tile.src = ''; // Limpar
                                        setTimeout(function() {
                                            error.tile.src = originalSrc; // Recarregar
                                        }, 100);
                                    }
                                }, 200);
                            }
                            
                            // Se muitos erros, trocar servidor
                            if (tileLoadErrors >= maxTileLoadErrors) {
                                console.log("🔄 Muitos erros, trocando servidor...");
                                currentTileUrlIndex = (currentTileUrlIndex + 1) % tileUrls.length;
                                map.removeLayer(tiles);
                                tiles = createTileLayer();
                                tiles.addTo(map);
                                tileLoadErrors = 0;
                            }
                        });
                        
                        // Handler para início de carregamento
                        tiles.on('loading', function() {
                            console.log("⏳ Carregando tiles...");
                        });
                        
                        // Forçar invalidação de tamanho após carregar
                        tiles.on('load', function() {
                            setTimeout(function() {
                                map.invalidateSize();
                            }, 100);
                        });
                        
                        // Listeners do mapa para forçar re-render
                        map.on('moveend', function() {
                            setTimeout(function() {
                                map.invalidateSize();
                            }, 50);
                        });
                        
                        map.on('zoomend', function() {
                            console.log("📍 Zoom:", map.getZoom());
                            setTimeout(function() {
                                map.invalidateSize();
                                // Forçar re-render de todos os tiles visíveis
                                var bounds = map.getBounds();
                                map.fitBounds(bounds);
                            }, 100);
                        });
                        
                        // Forçar atualização periódica (previne tiles cinzas persistentes)
                        setInterval(function() {
                            if (map && !map._animatingZoom) {
                                var center = map.getCenter();
                                var zoom = map.getZoom();
                                map.setView(center, zoom, { animate: false });
                            }
                        }, 10000); // A cada 10 segundos
                        
                        // Timeout de segurança
                        setTimeout(function() {
                            if (!mapReady) {
                                hideLoading();
                            }
                        }, 3000);
                        
                        // Controle de escala
                        L.control.scale({
                            position: 'bottomleft',
                            metric: true
                        }).addTo(map);
                        
                        console.log("✅ Mapa inicializado com sistema anti-tile-cinza!");
                        
                    } catch (error) {
                        console.error("❌ Erro:", error);
                        document.getElementById('loading').innerHTML = 
                            '<div style="color: #ff5252;">Erro: ' + error.message + '</div>';
                    }
                }
                
                function hideLoading() {
                    document.getElementById('loading').style.display = 'none';
                    mapReady = true;
                    console.log("✅ Mapa pronto!");
                    
                    if (window.javaBridge) {
                        window.javaBridge.mapaPronto();
                    }
                }
                
                function definirLocalizacaoUsuario(lat, lng) {
                    try {
                        if (userMarker) {
                            map.removeLayer(userMarker);
                        }
                        
                        // Usar HTML entities para caracteres especiais
                        var popupHtml = '<div style="text-align: center;">' +
                                       '<b style="font-size: 14px;">Sua Localiza&ccedil;&atilde;o</b><br>' +
                                       '<span style="font-size: 11px;">Lat: ' + lat.toFixed(5) + '<br>Lng: ' + lng.toFixed(5) + '</span>' +
                                       '</div>';
                        
                        userMarker = L.marker([lat, lng], { icon: userIcon })
                            .addTo(map)
                            .bindPopup(popupHtml)
                            .bindTooltip("Voc&ecirc;", {
                                permanent: true,
                                direction: 'top',
                                offset: [0, -35],
                                className: 'custom-tooltip'
                            });
                        
                        map.setView([lat, lng], 13);
                        console.log("Usuario definido:", lat, lng);
                    } catch (error) {
                        console.error("Erro usuario:", error);
                    }
                }
                
                function adicionarMarcadorAgricultor(id, lat, lng, nome, produto) {
                    try {
                        if (marcadores[id]) {
                            map.removeLayer(marcadores[id]);
                        }
                        
                        // Decodificar HTML entities do Java
                        var tempDiv = document.createElement('div');
                        tempDiv.innerHTML = nome;
                        var nomeDecoded = tempDiv.textContent || tempDiv.innerText;
                        
                        tempDiv.innerHTML = produto;
                        var produtoDecoded = tempDiv.textContent || tempDiv.innerText;
                        
                        var popup = '<div style="text-align: center; min-width: 150px; padding: 5px;">' +
                                   '<b style="color: #4CAF50; font-size: 14px;">' + nomeDecoded + '</b><br>' +
                                   '<span style="color: #b3e5d1; font-size: 13px;">' + produtoDecoded + '</span>' +
                                   '</div>';
                        
                        var marker = L.marker([lat, lng], { icon: farmerIcon })
                            .addTo(map)
                            .bindPopup(popup)
                            .bindTooltip(nomeDecoded, {
                                permanent: true,
                                direction: 'top',
                                offset: [0, -35],
                                className: 'custom-tooltip'
                            });
                        
                        marcadores[id] = marker;
                        console.log("Agricultor adicionado:", nomeDecoded);
                    } catch (error) {
                        console.error("Erro marcador:", error);
                    }
                }
                
                function focarNoMarcador(id) {
                    try {
                        if (marcadores[id]) {
                            var marker = marcadores[id];
                            map.setView(marker.getLatLng(), 14, { animate: true });
                            setTimeout(function() {
                                marker.openPopup();
                            }, 500);
                        }
                    } catch (error) {
                        console.error("❌ Erro focar:", error);
                    }
                }
                
                function calcularRotaParaAgricultor(idAgricultor) {
                    try {
                        if (!userMarker || !marcadores[idAgricultor]) {
                            console.error("❌ Faltam marcadores");
                            return;
                        }
                        
                        var userPos = userMarker.getLatLng();
                        var agricultorPos = marcadores[idAgricultor].getLatLng();
                        
                        console.log("🛣️ Calculando rota...");
                        
                        if (currentRoute) {
                            map.removeControl(currentRoute);
                        }
                        
                        currentRoute = L.Routing.control({
                            waypoints: [
                                L.latLng(userPos.lat, userPos.lng),
                                L.latLng(agricultorPos.lat, agricultorPos.lng)
                            ],
                            router: L.Routing.osrmv1({
                                serviceUrl: 'https://router.project-osrm.org/route/v1',
                                profile: 'car'
                            }),
                            lineOptions: {
                                styles: [
                                    { color: '#4CAF50', weight: 8, opacity: 0.8 }
                                ]
                            },
                            showAlternatives: false,
                            addWaypoints: false,
                            draggableWaypoints: false,
                            fitSelectedRoutes: true,
                            createMarker: function() { return null; }
                        }).addTo(map);
                        
                        var distancia = (userPos.distanceTo(agricultorPos) / 1000).toFixed(1);
                        var tempo = (distancia * 2.5).toFixed(0);
                        
                        console.log("✅ Rota:", distancia + " km");
                        
                        if (window.javaBridge) {
                            window.javaBridge.rotaCalculada(distancia, tempo);
                        }
                        
                        currentRoute.on('routesfound', function(e) {
                            var routes = e.routes;
                            if (routes && routes[0]) {
                                var summary = routes[0].summary;
                                var dist = (summary.totalDistance / 1000).toFixed(1);
                                var time = Math.round(summary.totalTime / 60);
                                
                                console.log("✅ Rota OSRM:", dist + " km,", time + " min");
                                
                                if (window.javaBridge) {
                                    window.javaBridge.rotaCalculada(dist, time);
                                }
                            }
                        });
                        
                    } catch (error) {
                        console.error("❌ Erro rota:", error);
                    }
                }
                
                function limparRota() {
                    if (currentRoute) {
                        map.removeControl(currentRoute);
                        currentRoute = null;
                    }
                }
                
                window.onload = inicializarMapa;
                
            </script>
        </body>
        </html>
        """;
        
        webEngine.loadContent(htmlContent);
        
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                try {
                    JSObject window = (JSObject) webEngine.executeScript("window");
                    window.setMember("javaBridge", new JavaBridge());
                    System.out.println("✅ Bridge configurado!");
                } catch (Exception e) {
                    System.err.println("❌ Erro bridge: " + e.getMessage());
                }
            } else if (newValue == Worker.State.FAILED) {
                System.err.println("❌ Falha ao carregar!");
            }
        });
    }
    
    public void quandoMapaPronto(Consumer<String> callback) {
        if (mapaPronto) {
            callback.accept("Mapa pronto");
        } else {
            this.onMapaCarregadoCallback = callback;
        }
    }
    
    public void definirLocalizacaoUsuario(double latitude, double longitude) {
        executarJavaScript("definirLocalizacaoUsuario(" + latitude + ", " + longitude + ")");
    }
    
    public void adicionarMarcadorAgricultor(String id, Localizacao loc, String nome, String produto) {
        marcadores.put(id, loc);
        
        // Converter para HTML entities para preservar acentos
        String nomeHTML = converterParaHTMLEntities(nome);
        String produtoHTML = converterParaHTMLEntities(produto);
        
        executarJavaScript(
            "adicionarMarcadorAgricultor('" + id + "', " + 
            loc.getLatitude() + ", " + loc.getLongitude() + ", " + 
            "'" + nomeHTML + "', '" + produtoHTML + "')"
        );
    }
    
    private String converterParaHTMLEntities(String texto) {
        if (texto == null) return "";
        
        return texto
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
            .replace("á", "&aacute;")
            .replace("à", "&agrave;")
            .replace("â", "&acirc;")
            .replace("ã", "&atilde;")
            .replace("é", "&eacute;")
            .replace("è", "&egrave;")
            .replace("ê", "&ecirc;")
            .replace("í", "&iacute;")
            .replace("ì", "&igrave;")
            .replace("î", "&icirc;")
            .replace("ó", "&oacute;")
            .replace("ò", "&ograve;")
            .replace("ô", "&ocirc;")
            .replace("õ", "&otilde;")
            .replace("ú", "&uacute;")
            .replace("ù", "&ugrave;")
            .replace("û", "&ucirc;")
            .replace("ç", "&ccedil;")
            .replace("Á", "&Aacute;")
            .replace("À", "&Agrave;")
            .replace("Â", "&Acirc;")
            .replace("Ã", "&Atilde;")
            .replace("É", "&Eacute;")
            .replace("È", "&Egrave;")
            .replace("Ê", "&Ecirc;")
            .replace("Í", "&Iacute;")
            .replace("Ì", "&Igrave;")
            .replace("Î", "&Icirc;")
            .replace("Ó", "&Oacute;")
            .replace("Ò", "&Ograve;")
            .replace("Ô", "&Ocirc;")
            .replace("Õ", "&Otilde;")
            .replace("Ú", "&Uacute;")
            .replace("Ù", "&Ugrave;")
            .replace("Û", "&Ucirc;")
            .replace("Ç", "&Ccedil;");
    }
    
    public void focarNoAgricultor(String id) {
        executarJavaScript("focarNoMarcador('" + id + "')");
    }
    
    public void calcularRotaParaAgricultor(String idAgricultor) {
        executarJavaScript("calcularRotaParaAgricultor('" + idAgricultor + "')");
    }
    
    public void limparRota() {
        executarJavaScript("limparRota()");
    }
    
    private void executarJavaScript(String script) {
        quandoMapaPronto(status -> {
            try {
                webEngine.executeScript(script);
            } catch (Exception e) {
                System.err.println("❌ Erro JS: " + e.getMessage());
            }
        });
    }
    
    public class JavaBridge {
        public void mapaPronto() {
            System.out.println("🎉 Mapa pronto!");
            mapaPronto = true;
            if (onMapaCarregadoCallback != null) {
                onMapaCarregadoCallback.accept("Mapa pronto");
            }
        }
        
        public void rotaCalculada(String distancia, String tempo) {
            System.out.println("🛣️ Rota: " + distancia + " km, " + tempo + " min");
        }
    }
}