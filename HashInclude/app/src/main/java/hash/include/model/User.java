package hash.include.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {
    public String uid;
    public String username;
    public String email;
    public String picUrl;
    private Map<String, String> timeStamp;

    public User() {
    }

    public User(String uId, String username,String email, String picUrl,Map<String, String> timeStamp) {
        this.uid = uId;
        this.username = username;
        this.picUrl = picUrl;
        this.email = email;
        this.timeStamp=timeStamp;

    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("username", username);
        result.put("email", email);
        result.put("picUrl", picUrl);
        result.put("timeStamp", timeStamp);
        return result;
    }

}
