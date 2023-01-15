package ipleiria.pdm.homecoffee.ui.home;

/**
 * Classe que contém as configurações para salvar as informações de uma sala no App Script.
 */
public class ConfigurationRoomSave {
    /**
     * URL da aplicação web no App Script.
     */
    public static final String APP_SCRIPT_WEB_APP_URL = "https://script.google.com/macros/s/AKfycbwNyJdDJT63S0suFsUWE7D0RT7YIRiYSAhH3N_Z-VPjTVQbdMB9Z5m5JjpHXUGDhDq9/exec";
    /**
     * URL para salvar as informações de uma sala no App Script.
     */
    public static final String USER_URL = APP_SCRIPT_WEB_APP_URL;
    /**
     * URL para listar as informações de todas as salas no App Script.
     */
    public static final String LIST_USER_URL = APP_SCRIPT_WEB_APP_URL + "?action=readAll";
    /**
     * Chave para identificação do ID de uma sala no App Script.
     */
    public static final String KEY_ID = "uId";
    /**
     * Chave para identificação do nome de uma sala no App Script.
     */
    public static final String KEY_NAME = "room_name";
    /**
     * Chave para identificação do tipo de uma sala no App Script.
     */
    public static final String KEY_IMAGE = "room_type";
    /**
     * Chave para identificação da ação a ser realizada no App Script.
     */
    public static final String KEY_ACTION = "action";
    /**
     * Chave para identificação da linha de uma sala no App Script.
     */
    public static final String KEY_ROW = "uRow";

    /**
     * Chave para identificação de todas as salas no App Script.
     */
    public static final String KEY_USERS = "records";
    /**
     * Chave para identificação de uma sala no App Script.
     */
    public static final String KEY_USER = "file";
}
