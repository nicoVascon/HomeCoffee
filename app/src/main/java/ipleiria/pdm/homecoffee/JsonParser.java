package ipleiria.pdm.homecoffee;

import static ipleiria.pdm.homecoffee.ui.home.ConfigurationRoomSave.KEY_ID;
import static ipleiria.pdm.homecoffee.ui.home.ConfigurationRoomSave.KEY_IMAGE;
import static ipleiria.pdm.homecoffee.ui.home.ConfigurationRoomSave.KEY_NAME;
import static ipleiria.pdm.homecoffee.ui.home.ConfigurationRoomSave.KEY_USERS;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonParser {

    private JSONArray rooms = null;

    private String json;

    public JsonParser(String json) {
        this.json = json;
    }

    protected void parseJSON() {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
            rooms = jsonObject.getJSONArray(KEY_USERS);

            HouseManager.getInstance().getRooms().clear();
            for (int i = 0; i < rooms.length(); i++) {
                JSONObject jo = rooms.getJSONObject(i);
                HouseManager.getInstance().adicionarContacto(new Room(jo.getString(KEY_NAME), RoomType.valueOf(jo.getString(KEY_IMAGE))));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
