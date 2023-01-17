package ipleiria.pdm.homecoffee;

import com.google.firebase.firestore.CollectionReference;

import java.io.Serializable;
import java.util.Random;

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
     * User Id
     */
    private String id;

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
//        Random random = new Random();
//        this.id = random.nextInt(1000);
    }

    /**
     * Método que retorna o email do usuário.
     * @return email do usuário.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Método para obter o User ID
     * @return User ID
     */
    public String getId() {
        return id;
    }

    /**
     * Método para definir o User ID
     * @param id User Id
     */
    public void setId(String id) {
        this.id = id;
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
