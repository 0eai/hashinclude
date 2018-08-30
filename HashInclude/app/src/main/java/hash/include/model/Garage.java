package hash.include.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Garage {
    public String uid;
    public String picUrl;
    public String title;
    public String about;
    public String link;
    public String startDate;
    public String progress;

    private Map<String, String> timeStamp;

    public Garage() {
    }

    public Garage(String uid, String picUrl, String title, String about, String link ,
                 String startDate, String progress,Map<String, String> timeStamp) {
        this.uid = uid;
        this.picUrl = picUrl;
        this.title = title;
        this.about = about;
        this.link = link;
        this.startDate = startDate;
        this.progress = progress;

        this.timeStamp = timeStamp;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("picUrl", picUrl);
        result.put("title", title);
        result.put("about", about);
        result.put("link", link);
        result.put("startDate", startDate);
        result.put("progress", progress);
        result.put("timeStamp", timeStamp);
        return result;
    }
    // [END post_to_map]
}
