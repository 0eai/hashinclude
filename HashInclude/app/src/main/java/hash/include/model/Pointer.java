package hash.include.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Pointer {

    public String uid;
    public String title;
    public String about;
    public String link;
    public String dateType;
    public String date;
    private Map<String, String> timeStamp;


    public Pointer() {
    }

    public Pointer(String uid, String title, String about, String link,
                   String dateType, String date, Map<String, String> timeStamp) {
        this.uid = uid;
        this.title = title;
        this.about = about;
        this.link = link;
        this.dateType = dateType;
        this.date = date;
        this.timeStamp = timeStamp;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("title", title);
        result.put("about", about);
        result.put("link", link);
        result.put("dateType", dateType);
        result.put("date", date);
        result.put("timeStamp", timeStamp);
        return result;
    }
}
