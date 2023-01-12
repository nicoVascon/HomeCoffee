package ipleiria.pdm.homecoffee;

import static ipleiria.pdm.homecoffee.ui.home.ConfigurationRoomSave.KEY_IMAGE;
import static ipleiria.pdm.homecoffee.ui.home.ConfigurationRoomSave.KEY_NAME;
import static ipleiria.pdm.homecoffee.ui.home.ConfigurationRoomSave.KEY_USERS;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import ipleiria.pdm.homecoffee.Enums.RoomType;
import ipleiria.pdm.homecoffee.model.Room;

/**
 * Classe JsonParser é responsável por fazer o parse de uma string JSON para objetos Room.
 */
public class JsonParser implements Serializable {
    /**
     * Array de objetos JSON para armazenar as informações das salas
     */
    private JSONArray rooms = null;
    /**
     * String que contém o JSON a ser parseado
     */
    private String json;

    /**
     * Construtor da classe, que recebe uma string JSON como argumento.
     * @param json String que contém o JSON a ser parseado
     */
    public JsonParser(String json) {
        this.json = json;
    }

    /**
     * Método que faz o parse da string JSON para objetos Room.
     *
     * As informações são armazenadas no ArrayList de Room na classe HouseManager.
     */
    protected void parseJSON() {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
            rooms = jsonObject.getJSONArray(KEY_USERS);

            HouseManager.getInstance().getRooms().clear();
            for (int i = 0; i < rooms.length(); i++) {
                JSONObject jo = rooms.getJSONObject(i);
                HouseManager.getInstance().addRoom(new Room(jo.getString(KEY_NAME), RoomType.valueOf(jo.getString(KEY_IMAGE))));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
