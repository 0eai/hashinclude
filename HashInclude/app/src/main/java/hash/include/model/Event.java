package hash.include.model;

import android.text.TextUtils;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Event {
    public String uid;
    public String picUrl;
    public String title;
    public String about;
    public String org;
    public String link;
    public String startDate;
    public String endDate;
    public String eventType;
    public String duration;
    public String totalReg;
    public String totalPart;
    private Map<String, String> timeStamp;

    public Event() {
    }

    public Event(String uid, String picUrl, String title, String about, String org, String link,
                 String startDate, String endDate, String duration, String eventType, Map<String, String> timeStamp, String totalReg, String totalPart) {
        this.uid = uid;
        this.picUrl = picUrl;
        this.title = title;
        this.about = about;
        this.org = org;
        this.link = link;
        this.startDate = startDate;
        this.endDate = endDate;
        this.duration = duration;
        this.eventType = eventType;
        this.timeStamp = timeStamp;
        if (TextUtils.isEmpty(totalReg)) {
            this.totalReg = "0";
        } else {
            this.totalReg = totalReg;
        }
        if (TextUtils.isEmpty(totalPart)) {
            this.totalPart = "0";
        } else {
            this.totalPart = totalPart;
        }
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("picUrl", picUrl);
        result.put("title", title);
        result.put("about", about);
        result.put("org", org);
        result.put("link", link);
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        result.put("duration", duration);
        result.put("eventType", eventType);
        result.put("timeStamp", timeStamp);
        result.put("totalReg", totalReg);
        result.put("totalPart", totalPart);
        return result;
    }
}
