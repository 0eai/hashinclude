package hash.include.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Register {
    public String uid;
    public String status;
    private Map<String, String> timeStamp;

    public Register() {

    }

    public Register(String uId, String status, Map<String, String> timeStamp) {
        this.uid = uId;
        this.status = status;
        this.timeStamp = timeStamp;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("status", status);
        result.put("timeStamp", timeStamp);
        return result;
    }
}
