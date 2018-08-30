package hash.include.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Reinforce {
    public String uid;
    public String picUrl;
    public String title;
    public String about;
    public String startDate;
    private Map<String, String> timeStamp;

    public Reinforce() {
    }

    public Reinforce(String uid, String picUrl, String title, String about,
                     String startDate, Map<String, String> timeStamp) {
        this.uid = uid;
        this.picUrl = picUrl;
        this.title = title;
        this.about = about;
        this.startDate = startDate;
        this.timeStamp = timeStamp;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("picUrl", picUrl);
        result.put("title", title);
        result.put("about", about);
        result.put("startDate", startDate);
        result.put("timeStamp", timeStamp);
        return result;
    }
}
