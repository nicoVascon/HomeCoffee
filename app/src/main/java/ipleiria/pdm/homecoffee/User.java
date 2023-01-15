package ipleiria.pdm.homecoffee;

import com.google.firebase.firestore.CollectionReference;

import java.io.Serializable;

/**
 * Classe User que representa um usuário do aplicativo.
 *
 * Implementa a interface Serializable para permitir a sua serialização e armazenamento em disco ou transmissão através de redes.
 */
public class User implements Serializable {
    /**
     * Atributo que armazena o email do usuário.
     */
    private String email;
    /**
     * Referencia na firebase dos quartos do utilizador
     */
    private CollectionReference roomsRef;

    /**
     * Construtor vazio da classe User.
     */
    public User() {
    }

    /**
     * Construtor da classe User que recebe o email do usuário como parâmetro.
     * @param email email do usuário.
     */
    public User(String email) {
        this.email = email;
    }

    /**
     * Método que retorna o email do usuário.
     * @return email do usuário.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Método que seta o email do usuário.
     * @param email novo email do usuário.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    public CollectionReference getRoomsRef() {
        return roomsRef;
    }

    public void setRoomsRef(CollectionReference roomsRef) {
        this.roomsRef = roomsRef;
    }
}
