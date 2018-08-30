package hash.include.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Member {
    public String uid;
    public String title;
    private Map<String, String> timeStamp;

    public Member() {

    }
    public Member(String uId,String title,Map<String, String> timeStamp) {
        this.uid = uId;
        this.title = title;
        this.timeStamp=timeStamp;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("title", title);
        result.put("timeStamp", timeStamp);
        return result;
    }
}
