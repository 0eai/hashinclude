package hash.include.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class UserPointLog {
    public String uid;
    public int point;
    public String cykId;

    public UserPointLog() {
    }

    public UserPointLog(String uId, int point, String cykId) {
        this.uid = uId;
        this.point = point;
        this.cykId = cykId;

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("point", point);
        result.put("cykId", cykId);
        return result;
    }
}
