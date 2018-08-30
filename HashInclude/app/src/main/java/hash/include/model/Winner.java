package hash.include.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Winner {
    public String uid;
    public String position;
    private Map<String, String> timeStamp;

    public Winner() {

    }

    public Winner(String uId, String position, Map<String, String> timeStamp) {
        this.uid = uId;
        this.position = position;
        this.timeStamp = timeStamp;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("position", position);
        result.put("timeStamp", timeStamp);
        return result;
    }
}
