package hash.include.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class UserValues {
    public String uid;
    public int point = 0;
    public String userType;
    public int rank;
    public UserValues() {
    }

    public UserValues(String uId, int point, String userType) {
        this.uid = uId;
        this.point = point;
        this.userType = userType;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("point", point);
        result.put("userType", userType);
        return result;
    }
}
