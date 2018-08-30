package hash.include.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 2/24/18.
 */

public class UserRegister {
    public String uid;
    public String fuid;

    public UserRegister() {
    }

    public UserRegister(String uId, String fuid) {
        this.uid = uId;
        this.fuid = fuid;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("fuid", fuid);
        return result;
    }
}
