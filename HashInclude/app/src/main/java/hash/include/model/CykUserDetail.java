package hash.include.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class CykUserDetail {
    public String uid;
    public String answer;
    public String success;
    public int point;
    public String queUID;
    private Map<String, String> timeStamp;

    public CykUserDetail() {
    }

    public CykUserDetail(String uid, String success, int point,
                         String answer, String queUID, Map<String, String> timeStamp) {
        this.uid = uid;
        this.success = success;
        this.point = point;
        this.answer = answer;
        this.queUID = queUID;
        this.timeStamp = timeStamp;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("success", success);
        result.put("point", point);
        result.put("answer", answer);
        result.put("queUID", queUID);
        result.put("timeStamp", timeStamp);
        return result;
    }
}
