package model; 

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import javax.persistence.*;

/**
 * 
 */
@Entity
public class Avaliacao {

    /**
     * Default constructor
     */
    public Avaliacao() {
    }

    /**
     * 
     */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    /**
     * 
     */
    private Comprador autor;

    /**
     * 
     */
    private Agricultor alvoAgricultor;

    /**
     * 
     */
    private int nota;

    /**
     * 
     */
    private String comentario;

    /**
     * 
     */
    private LocalDateTime dataHora;



    /**
     * @return
     */
    public void registrar() {
        // TODO implement here

    }

    /**
     * @param nota 
     * @param comentario 
     * @return
     */
    public void editar(int nota, String comentario) {
        // TODO implement here

    }

    /**
     * @return
     */
    public void remover() {
        // TODO implement here

    }

}