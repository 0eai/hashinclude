package hash.include.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Message {
    public String mid;
    public String uid;
    public String mtext;
    public String mPicUrl;
    public String mType;
    public boolean sendStatus;
    public String mError;
    private Map<String, String> timeStamp;


    public Message() {
    }

    public Message(String mId, String uId, String mText, String mPicUrl, String mType, String error, Map<String, String> timeStamp) {
        this.mid = mId;
        this.uid = uId;
        this.mtext = mText;
        this.mPicUrl = mPicUrl;
        this.mType = mType;
        this.mError = error;
        this.timeStamp = timeStamp;
        this.sendStatus = false;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("mid", mid);
        result.put("uid", uid);
        result.put("mtext", mtext);
        result.put("mPicUrl", mPicUrl);
        result.put("mType", mType);
        result.put("sendStatus", sendStatus);
        result.put("mError", mError);
        result.put("timeStamp", timeStamp);
        return result;
    }
}
