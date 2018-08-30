package hash.include.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class CykPractice {
    public String title;
    public String questionType;

    public CykPractice() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public CykPractice(String title, String questionType) {
        this.questionType = questionType;
        this.title = title;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("questionType", questionType);
        result.put("title", title);
        return result;
    }
}
