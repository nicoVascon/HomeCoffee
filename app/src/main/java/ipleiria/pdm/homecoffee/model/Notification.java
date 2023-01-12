package ipleiria.pdm.homecoffee.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Classe Notification, contém as informações de data e descrição de uma notificação.
 */
public class Notification implements Serializable {
    /**
     * Data da notificação
     */
    private Date date;
    /**
     * Descrição da notificação
     */
    private String description;
    /**
     * Construtor que recebe a data e descrição da notificação
     *
     * @param date data da notificação
     * @param description descrição da notificação
     */
    public Notification(Date date, String description){
        this.date = date;
        this.description = description;
    }

    /**
     * Construtor que cria uma notificação com data e hora atuais e a descrição fornecida.
     *
     * @param description descrição da notificação
     */
    public Notification(String description){
        this(new Date(), description); // Get the current time
    }

    /**
     * Obtém a data armazenada na notificação
     * @return A data da notificação
     */
    public Date getDate() {
        return date;
    }

    /**
     * Define a data armazenada na notificação
     * @param date A nova data da notificação
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Obtém a descrição armazenada na notificação
     * @return A descrição da notificação
     */
    public String getDescription() {
        return description;
    }

    /**
     * Define a descrição armazenada na notificação
     * @param description A nova descrição da notificação
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Obtém a data armazenada na notificação em um formato específico
     * @return A data da notificação no formato "dd/MM/yyyy HH:mm:ss"
     */
    public String getDateFormatted(){
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return dateFormatter.format(date);
    }
}
