package hash.include.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Session {
    public String uid;
    public String title;
    public String youtubeVideoId;
    public String keyLearning;
    public String references;
    public String sessionNo;
    public String sessionDate;
    public String sessionTime;
    public String queUID;
    private Map<String, String> timeStamp;

    public Session() {
    }

    public Session(String uid, String title, String youtubeVideoId, String keyLearning, String references, String sessionNo, String sessionDate, String sessionTime, String queUID, Map<String, String> timeStamp) {
        this.uid = uid;
        this.title = title;
        this.youtubeVideoId = youtubeVideoId;
        this.keyLearning = keyLearning;
        this.references = references;
        this.sessionNo = sessionNo;
        this.sessionDate = sessionDate;
        this.sessionTime = sessionTime;
        this.queUID = queUID;
        this.timeStamp = timeStamp;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("title", title);
        result.put("youtubeVideoId", youtubeVideoId);
        result.put("keyLearning", keyLearning);
        result.put("references", references);
        result.put("sessionNo", sessionNo);
        result.put("sessionDate", sessionDate);
        result.put("sessionTime", sessionTime);
        result.put("queUID", queUID);
        result.put("timeStamp", timeStamp);
        return result;
    }
}
