package service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import model.Agricultor;
import model.Comprador;
import model.Pessoa;
import static service.Validacoes.validarEmail;
import static service.Validacoes.validarRegiao;
import static service.Validacoes.validarSenha;
import static service.Validacoes.validarTelefone;

public class UsuarioService {
    private List<Pessoa> usuarios = new ArrayList<>();
    private int ultimoId;
    NotificacaoService comService = new NotificacaoService();
    
    // Metodo Responsavel por fazer o cadastro do Agricultor e do comprador
    
     public boolean cadastrarAgricultor(String nome, String email, String telefone, 
                                     String provincia, String distrito, String bairro, String senha) {
        return cadastrarUsuario(nome, email, telefone, provincia, distrito, bairro, senha, "AGRICULTOR");
    }
     
    public boolean cadastrarComprador(String nome, String email, String telefone,
                                        String provincia, String distrito, String bairro, String senha){
        return cadastrarUsuario(nome, email, telefone, provincia, distrito, bairro, senha, "Comprador"); 
    }
    
    public boolean cadastrarUsuario(String nome, String email, String telefone, String provincia,
                                     String distrito, String bairro, String senha, String tipoUsuario){
        
        if (buscarUsuarioPorEmail(email) != null) {
            JOptionPane.showMessageDialog(
                null,
                "O email '" + email + "' já existe!\nPor favor, verifique o E-mail e volte a tentar",
                "Usuário já cadastrado",
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
        
        //validacoes necessarias
        
        ResultadoValidacao resultadoEmail = validarEmail(email);
        if (!resultadoEmail.valido) {
            JOptionPane.showMessageDialog(null, resultadoEmail.mensagem, "Email inválido", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        ResultadoValidacao resultadoNome = validarEmail(nome);
        if(!resultadoEmail.valido){
            JOptionPane.showMessageDialog(null, resultadoNome.mensagem, "Nome Invalido", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        ResultadoValidacao  resultadoSenha = validarSenha(senha);
        if(!resultadoSenha.valido){
            JOptionPane.showMessageDialog(null, resultadoSenha.mensagem, "Senha invalido", JOptionPane.ERROR_MESSAGE );
            return false;
        }
        
        ResultadoValidacao  resultadoTelefone = validarTelefone(telefone);
        if(!resultadoTelefone.valido){
            JOptionPane.showMessageDialog(null, resultadoTelefone.mensagem, "Telefone invalido", JOptionPane.ERROR_MESSAGE );
            return false;
        }
        
        ResultadoValidacao resultadoProvincia = validarRegiao(provincia);
        if(!resultadoProvincia.valido){
            JOptionPane.showMessageDialog(null, resultadoProvincia.mensagem, "Provincia invalido", JOptionPane.ERROR_MESSAGE );
            return false;
        }
        
        ResultadoValidacao resultadoDistrito = validarRegiao(distrito);
        if(!resultadoDistrito.valido){
            JOptionPane.showMessageDialog(null, resultadoDistrito.mensagem, "Distrito invalido", JOptionPane.ERROR_MESSAGE );
            return false;
        }
        
        ResultadoValidacao resultadoBairro= validarRegiao(bairro);
        if(!resultadoBairro.valido){
            JOptionPane.showMessageDialog(null, resultadoBairro.mensagem, "Bairro invalido", JOptionPane.ERROR_MESSAGE );
            return false;
        }
        
        
        try{
            Pessoa novoUsuario;
            
            if(tipoUsuario.equalsIgnoreCase("AGRICULTOR")){
                novoUsuario = new  Agricultor(senha, nome, email, telefone, provincia, distrito, bairro);
            }else {
                novoUsuario = new Comprador(senha, nome, email, telefone, provincia, distrito, bairro);
            }
            
            novoUsuario.setId(gerarId());
            usuarios.add(novoUsuario);
            
             mostrarSucesso("Cadastro realizado", 
                          tipoUsuario + " '" + nome + "' cadastrado com sucesso!");
             
            comService.configurarEmail("smtp.gmail.com", "587", "lilianolicumba42@gmail.com", "jwqa yltv iqic iqpr");
            comService.enviarEmailBoasVindas(email, nome);
             
            return true;
        }catch(Exception e){
              mostrarErro("Erro ao cadastrar", 
                          tipoUsuario + " '" + nome + "' Verifique os dados e volte a tentar!");
            return true;
        }
        
    
    }
    
    //Metodos de busca e consulta
    
    public Pessoa buscarUsuarioPorEmail(String email){
        if(email == null) return null;
        
        return usuarios.stream()
                .filter(p -> p.getEmail() != null && p.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }
    
    
    public Pessoa buscarUsuarioPorId(int id){
        return usuarios.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }
    
    public List<Pessoa> listarTodosUsuarios(){
        return new ArrayList<>(usuarios);
    }
    
    public List<Pessoa> listarPorTipo(String tipo){
        List<Pessoa> resultado = new ArrayList<>();
        
        for(Pessoa usuario : usuarios){
            if(tipo.equalsIgnoreCase("Agricultor") && usuario instanceof Agricultor){
                resultado.add(usuario);
            }else if(tipo.equalsIgnoreCase("Comprador") && usuario instanceof Comprador){
                resultado.add(usuario);
            }
        }
        
        return resultado;
        
    }
    
    public List<Pessoa> filtrarUsuariosPorStatus(boolean ativo){
        List<Pessoa> resultado = new ArrayList<>();
        
        for (Pessoa usuario : usuarios){
            if(usuario.isAtivo() == ativo){
                resultado.add(usuario);
            }
        }
        
        return resultado;
    }
    
    private void mostrarErro(String titulo, String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem, titulo, JOptionPane.ERROR_MESSAGE);
    }
    
    private void mostrarSucesso(String titulo, String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem, titulo, JOptionPane.INFORMATION_MESSAGE);
    }
    
    public int gerarId(){
        return ++ultimoId;
    }
    
    
    
    public boolean removerUsuario(String email) {
        Pessoa usuario = buscarUsuarioPorEmail(email);
        if (usuario != null) {
            usuarios.remove(usuario);
            return true;
        }
        return false;
    }
}
