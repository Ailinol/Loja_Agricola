package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import service.MonitoramentoService;
import model.Monitorizacao; 
import java.util.List;
import java.util.stream.Collectors;

public class MonitorizacaoController {

    @FXML
    private TextArea textAreaInconsistencias;

    @FXML
    private Label lblStatus;

    @FXML
    private Label lblBeneficio;
    
    @FXML
    private Button btnVerificar;

    private MonitoramentoService monitoramentoService;

    public MonitorizacaoController() {
        this.monitoramentoService = new MonitoramentoService();
    }

    @FXML
    public void initialize() {
        // Inicializa o benefício e o status
        lblBeneficio.setText(MonitoramentoService.getBeneficioPadrao());
        lblStatus.setText("Status: Aguardando verificação...");
        textAreaInconsistencias.setEditable(false);
        lblStatus.setStyle("-fx-text-fill: #b3e5d1;"); // Cor padrão
    }

    @FXML
    private void handleVerificarInconsistencias() {
        // 1. Prepara a UI para a execução
        textAreaInconsistencias.clear();
        lblStatus.setText("Status: Executando monitoramento...");
        lblStatus.setStyle("-fx-text-fill: #FFC107; -fx-font-weight: bold;"); // Amarelo (Em progresso)
        btnVerificar.setDisable(true); 
        
        try {
            // 2. Executa o Service e obtém o Model estruturado
            Monitorizacao resultado = monitoramentoService.executarMonitoramento();
            
            // Converte a lista de strings para o formato de exibição
            String logContent = resultado.getInconsistenciasDetectadas().stream().collect(Collectors.joining("\n"));

            // 3. Processa e exibe os resultados
            if (resultado.isSistemaIntegro()) {
                // Caso de sucesso (sem inconsistências)
                textAreaInconsistencias.setText("✓ Monitoramento Concluído com Sucesso.\n" + logContent);
                lblStatus.setText("Status: ÍNTEGRO (✓)");
                lblStatus.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;"); // Verde GreenMatch
                
            } else {
                // Caso de falha (com inconsistências ou erro fatal)
                
                String logHeader = "✗ Monitoramento Concluído: FALHA DE INTEGRIDADE.\n"
                                 + "-------------------------------------------------------\n";
                
                textAreaInconsistencias.setText(logHeader + logContent);
                lblStatus.setText("Status: FALHA DE INTEGRIDADE (✗)");
                lblStatus.setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;"); // Vermelho
            }

        } catch (Exception e) {
            // Caso de erro inesperado
            textAreaInconsistencias.setText("ERRO TÉCNICO FATAL:\n" + e.getMessage());
            lblStatus.setText("Status: ERRO (✗)");
            lblStatus.setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;");
        } finally {
            btnVerificar.setDisable(false); // Habilita o botão novamente
        }
    }
}