package hash.include.model;

public class GetMessage {
    public String mid;
    public String uid;
    public String mtext;
    public String mPicUrl;
    public String mType;
    public boolean sendStatus;
    public String mError;
    public long timeStamp;

    public GetMessage() {

    }

    public String getMId() {
        return mid;
    }

    public String getMText() {
        return mtext;
    }

    public String getUId() {
        return uid;
    }

    public String getError() {
        return mError;
    }

    public String getmPicUrl() {
        return mPicUrl;
    }

    public String getmType() {
        return mType;
    }

    public boolean getSendStatus() {
        return sendStatus;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

}
