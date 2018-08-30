package hash.include.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Feedback {
    public String uid;
    public String fid;
    public String text;
    private Map<String, String> timeStamp;

    public Feedback() {
    }

    public Feedback(String uid, String fid, String text, Map<String, String> timeStamp) {
        this.uid = uid;
        this.fid = fid;
        this.text = text;
        this.timeStamp = timeStamp;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("fid", fid);
        result.put("text", text);
        result.put("timeStamp", timeStamp);
        return result;
    }
}
