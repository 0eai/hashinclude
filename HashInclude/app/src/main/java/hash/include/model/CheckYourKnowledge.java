package hash.include.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class CheckYourKnowledge {
    public String uid;
    public String questionPicUrl;
    public String title;
    public String tags;
    public int point;
    public String level;
    public String question;
    public String questionType;
    public String instruction;

    public String testCaseInput1;
    public String testCaseInput2;
    public String testCaseInput3;
    public String testCaseInput4;
    public String testCaseInput5;

    public String testCaseOutput1;
    public String testCaseOutput2;
    public String testCaseOutput3;
    public String testCaseOutput4;
    public String testCaseOutput5;

    public String option1;
    public String option2;
    public String option3;
    public String option4;
    public String answer;
    public String queUID;
    private Map<String, String> timeStamp;

    public CheckYourKnowledge() {
    }

    public CheckYourKnowledge(String uid, String questionPicUrl, String title, String tags, int point, String level, String question, String questionType, String instruction, String option1, String option2, String option3, String option4,
                              String testCaseInput1, String testCaseInput2, String testCaseInput3, String testCaseInput4, String testCaseInput5,
                              String testCaseOutput1, String testCaseOutput2, String testCaseOutput3, String testCaseOutput4, String testCaseOutput5,
                              String answer, String queUID, Map<String, String> timeStamp) {
        this.uid = uid;
        this.questionPicUrl = questionPicUrl;
        this.title = title;
        this.tags = tags;
        this.point = point;
        this.level = level;
        this.question = question;
        this.questionType = questionType;
        this.instruction = instruction;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.testCaseInput1 = testCaseInput1;
        this.testCaseInput2 = testCaseInput2;
        this.testCaseInput3 = testCaseInput3;
        this.testCaseInput4 = testCaseInput4;
        this.testCaseInput5 = testCaseInput5;
        this.testCaseOutput1 = testCaseOutput1;
        this.testCaseOutput2 = testCaseOutput2;
        this.testCaseOutput3 = testCaseOutput3;
        this.testCaseOutput4 = testCaseOutput4;
        this.testCaseOutput5 = testCaseOutput5;
        this.answer = answer;
        this.queUID = queUID;
        this.timeStamp = timeStamp;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("questionPicUrl", questionPicUrl);
        result.put("title", title);
        result.put("tags", tags);
        result.put("point", point);
        result.put("level", level);
        result.put("question", question);
        result.put("questionType", questionType);
        result.put("instruction", instruction);
        result.put("option1", option1);
        result.put("option2", option2);
        result.put("option3", option3);
        result.put("option4", option4);
        result.put("testCaseInput1", testCaseInput1);
        result.put("testCaseInput2", testCaseInput2);
        result.put("testCaseInput3", testCaseInput3);
        result.put("testCaseInput4", testCaseInput4);
        result.put("testCaseInput5", testCaseInput5);
        result.put("testCaseOutput1", testCaseOutput1);
        result.put("testCaseOutput2", testCaseOutput2);
        result.put("testCaseOutput3", testCaseOutput3);
        result.put("testCaseOutput4", testCaseOutput4);
        result.put("testCaseOutput5", testCaseOutput5);
        result.put("answer", answer);
        result.put("queUID", queUID);
        result.put("timeStamp", timeStamp);
        return result;
    }
}
