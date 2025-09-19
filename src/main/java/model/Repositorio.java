package model;

import java.io.*;
import java.util.*;

/**
 * 
 */
public interface Repositorio {

    /**
     * @param t 
     * @return
     */
    public void salvar(Object t);

    /**
     * @param id 
     * @return
     */
    public Object buscar(int id);

    /**
     * @return
     */
    public List<Object> listar();

}