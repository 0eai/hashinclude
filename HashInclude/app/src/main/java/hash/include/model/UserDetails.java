package hash.include.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class UserDetails {
    public String phone;
    public String about;
    public String regNo;
    public String dob;
    public String githubLink;
    public String hackerearthLink;
    public String twitterLink;
    public String facebookLink;
    private Map<String, String> timeStamp;

    public UserDetails() {
    }

    public UserDetails(String phone, String about, String regNo, String dob, String githubLink, String hackerearthLink, String twitterLink, String facebookLink, Map<String, String> timeStamp) {
        this.phone = phone;
        this.about = about;
        this.regNo = regNo;
        this.dob = dob;
        this.githubLink = githubLink;
        this.hackerearthLink = hackerearthLink;
        this.twitterLink = twitterLink;
        this.facebookLink = facebookLink;
        this.timeStamp = timeStamp;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("phone", phone);
        result.put("about", about);
        result.put("regNo", regNo);
        result.put("dob", dob);
        result.put("githubLink", githubLink);
        result.put("hackerearthLink", hackerearthLink);
        result.put("twitterLink", twitterLink);
        result.put("facebookLink", facebookLink);
        result.put("timeStamp", timeStamp);
        return result;
    }

}
