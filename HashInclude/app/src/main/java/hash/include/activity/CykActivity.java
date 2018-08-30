package hash.include.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import hash.include.R;
import hash.include.model.CheckYourKnowledge;
import hash.include.model.CykUserDetail;
import hash.include.model.UserPointLog;
import hash.include.model.UserValues;
import hash.include.util.FirebaseUtils;
import hash.include.util.HashUtil;

public class CykActivity extends AppCompatActivity implements
        View.OnClickListener {
    public static final String EXTRA = "EXTRA";

    @BindView(R.id.layout_check_your_knowledge_radio)
    LinearLayout radioLayout;
    @BindView(R.id.layout_check_your_knowledge_check)
    LinearLayout checkLayout;
    @BindView(R.id.layout_check_your_knowledge_output)
    LinearLayout outputLayout;
    @BindView(R.id.layout_check_your_knowledge_program)
    LinearLayout programLayout;
    @BindView(R.id.layout_check_your_knowledge_true_false)
    LinearLayout trueFalseLayout;

    @BindView(R.id.image_check_your_knowledge)
    ImageView checkYourKnowledgePic;
    @BindView(R.id.cyk_success_indicator)
    ImageView successIndicator;

    @BindView(R.id.text_check_your_knowledge)
    TextView title;
    @BindView(R.id.text_check_your_knowledge_que)
    TextView que;
    @BindView(R.id.text_check_your_knowledge_instruction)
    TextView instruction;
    @BindView(R.id.cyk_point_text)
    TextView pointText;

    @BindView(R.id.button_submit)
    Button buttonSubmit;

    @BindView(R.id.radio_button_1)
    RadioButton radioOption1;
    @BindView(R.id.radio_button_2)
    RadioButton radioOption2;
    @BindView(R.id.radio_button_3)
    RadioButton radioOption3;
    @BindView(R.id.radio_button_4)
    RadioButton radioOption4;

    @BindView(R.id.radio_button_true)
    RadioButton radioOptionTrue;
    @BindView(R.id.radio_button_false)
    RadioButton radioOptionFalse;

    @BindView(R.id.check_box_1)
    CheckBox checkOption1;
    @BindView(R.id.check_box_2)
    CheckBox checkOption2;
    @BindView(R.id.check_box_3)
    CheckBox checkOption3;
    @BindView(R.id.check_box_4)
    CheckBox checkOption4;

    @BindView(R.id.edit_text_output)
    EditText editTextOutput;
    @BindView(R.id.edit_text_program)
    EditText editTextProgram;
    CheckYourKnowledge cyk = null;
    private TextView activityTitle;
    private String extra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cyk);
        ButterKnife.bind(this);

        extra = getIntent().getStringExtra(EXTRA);
        if (extra == null) {
            throw new IllegalArgumentException("Must pass EXTRA");
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (toolbar != null) {
            activityTitle = toolbar.findViewById(R.id.activity_title);
            activityTitle.setTypeface(HashUtil.getInstance().typefaceComfortaaBold);
            setSupportActionBar(toolbar);

            final ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setFonts();
        sync();
        buttonSubmit.setOnClickListener(this);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void sync() {
        FirebaseUtils.GetDbRef().child("check-your-knowledge").child(extra).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // ...
                cyk = dataSnapshot.getValue(CheckYourKnowledge.class);
                if (cyk != null) {
                    title.setText(cyk.title);
                    que.setText(cyk.question);
                    //Toast toast = Toast.makeText(CykActivity.this, cyk.answer, Toast.LENGTH_SHORT);
                    //toast.show();
                    setQueInstruction(cyk.questionType);
                    if (cyk.questionPicUrl == null) {
                        checkYourKnowledgePic.setVisibility(View.GONE);
                    } else {
                        try {
                            Glide.with(checkYourKnowledgePic.getContext())
                                    .load(cyk.questionPicUrl)
                                    .into(checkYourKnowledgePic);
                        } catch (IllegalArgumentException e) {

                        }
                        checkYourKnowledgePic.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(v.getContext(), ImageViewerActivity.class);
                                intent.putExtra(ImageViewerActivity.EXTRA, cyk.title + "@" + cyk.tags + "@" + cyk.questionPicUrl);
                                ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(CykActivity.this,
                                        new Pair<View, String>(checkYourKnowledgePic,
                                                ImageViewerActivity.VIEW_NAME_IMAGE));
                                startActivity(intent, activityOptions.toBundle());


                            }
                        });
                    }
                    if (cyk.questionType.equals("True/False")) {
                        trueFalseLayout.setVisibility(View.VISIBLE);
                        radioLayout.setVisibility(View.GONE);
                        checkLayout.setVisibility(View.GONE);
                        outputLayout.setVisibility(View.GONE);
                        programLayout.setVisibility(View.GONE);
                    } else if (cyk.questionType.equals("Mcq-One")) {
                        trueFalseLayout.setVisibility(View.GONE);
                        radioLayout.setVisibility(View.VISIBLE);
                        checkLayout.setVisibility(View.GONE);
                        outputLayout.setVisibility(View.GONE);
                        programLayout.setVisibility(View.GONE);
                        radioOption1.setText(cyk.option1);
                        radioOption2.setText(cyk.option2);
                        radioOption3.setText(cyk.option3);
                        radioOption4.setText(cyk.option4);
                    } else if (cyk.questionType.equals("Mcq-Multiple")) {
                        trueFalseLayout.setVisibility(View.GONE);
                        radioLayout.setVisibility(View.GONE);
                        checkLayout.setVisibility(View.VISIBLE);
                        outputLayout.setVisibility(View.GONE);
                        programLayout.setVisibility(View.GONE);
                        checkOption1.setText(cyk.option1);
                        checkOption2.setText(cyk.option2);
                        checkOption3.setText(cyk.option3);
                        checkOption4.setText(cyk.option4);
                    } else if (cyk.questionType.equals("Output")) {
                        trueFalseLayout.setVisibility(View.GONE);
                        radioLayout.setVisibility(View.GONE);
                        checkLayout.setVisibility(View.GONE);
                        outputLayout.setVisibility(View.VISIBLE);
                        programLayout.setVisibility(View.GONE);
                        editTextOutput.setText("");
                    } else if (cyk.questionType.equals("Program")) {
                        trueFalseLayout.setVisibility(View.GONE);
                        radioLayout.setVisibility(View.GONE);
                        checkLayout.setVisibility(View.GONE);
                        outputLayout.setVisibility(View.GONE);
                        programLayout.setVisibility(View.VISIBLE);
                        editTextProgram.setText("#include<stdio.h>\nvoid main(){\n\n}");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
        FirebaseUtils.GetDbRef().child("cyk-details").child(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail())).child(extra).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CykUserDetail cykUserDetail = dataSnapshot.getValue(CykUserDetail.class);
                if (cykUserDetail != null) {
                    //Toast toast = Toast.makeText(CykActivity.this, cykUserDetail.answer + "  " + cykUserDetail.answer + "  " + cykUserDetail.success + "  " + cykUserDetail.uid + "  ", Toast.LENGTH_SHORT);
                    //toast.show();
                    pointText.setText(cykUserDetail.point + "");
                    if (cyk != null && cyk.questionType.equals("Output")) {
                        editTextOutput.setText(cykUserDetail.answer);
                    }
                    if (cyk != null && cyk.questionType.equals("Program")) {
                        editTextProgram.setText(cykUserDetail.answer);
                    }
                    if (cyk != null && cyk.questionType.equals("Mcq-One")) {
                        int anInt = 0;
                        try {
                            anInt = Integer.parseInt(cykUserDetail.answer);
                        } catch (Exception e) {
                        }
                        if (anInt == 1) {
                            radioOption1.setChecked(true);
                        } else if (anInt == 2) {
                            radioOption2.setChecked(true);
                        } else if (anInt == 3) {
                            radioOption3.setChecked(true);
                        } else {
                            radioOption4.setChecked(true);
                        }
                    }
                    if (cyk != null && cyk.questionType.equals("Mcq-Multiple")) {
                        int anInt = 0;
                        try {
                            anInt = Integer.parseInt(cykUserDetail.answer);
                        } catch (Exception e) {
                        }
                        if (anInt == 1) {
                            checkOption2.setChecked(true);
                        } else if (anInt == 2) {
                            checkOption3.setChecked(true);
                        } else if (anInt == 3) {
                            checkOption4.setChecked(true);
                        } else if (anInt == 1) {
                            checkOption1.setChecked(true);
                        }
                    }
                    if (cyk != null && cyk.questionType.equals("True/False")) {
                        if (cykUserDetail.answer.equals("true")) {
                            radioOptionTrue.setChecked(true);
                        } else if (cykUserDetail.answer.equals("false")) {
                            radioOptionFalse.setChecked(true);
                        }
                    }
                    if (cykUserDetail.success.equals("true")) {
                        successIndicator.setImageResource(R.drawable.ic_check);
                    } else {
                        successIndicator.setImageResource(R.drawable.ic_close);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }

    private void updateUserPoint(int p, String success) {
        FirebaseUtils.GetDbRef().child("user-point-log").child("cyk").child(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail())).child(extra).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserPointLog userPointLog = dataSnapshot.getValue(UserPointLog.class);
                if (userPointLog == null && success.equals("true")) {
                    FirebaseUtils.GetDbRef().child("users-values").child(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail())).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserValues value = dataSnapshot.getValue(UserValues.class);
                            if (value != null) {
                                UserPointLog l = new UserPointLog(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail()), p, extra);
                                FirebaseUtils.GetDbRef().child("users-values").child(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail())).child("point").setValue(value.point + p);
                                FirebaseUtils.GetDbRef().child("user-point-log").child("cyk").child(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail())).child(extra).setValue(l);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // ...
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_submit:
                checkAnswer();
                break;
        }
    }

    private void setFonts() {
        ((TextView) findViewById(R.id.text_check_your_knowledge)).setTypeface(HashUtil.GetTypeface());

        que.setTypeface(HashUtil.GetTypeface());
        instruction.setTypeface(HashUtil.GetTypeface());
        radioOption1.setTypeface(HashUtil.GetTypeface());
        radioOption2.setTypeface(HashUtil.GetTypeface());
        radioOption3.setTypeface(HashUtil.GetTypeface());
        radioOption4.setTypeface(HashUtil.GetTypeface());
        radioOptionTrue.setTypeface(HashUtil.GetTypeface());
        radioOptionFalse.setTypeface(HashUtil.GetTypeface());
        checkOption1.setTypeface(HashUtil.GetTypeface());
        checkOption2.setTypeface(HashUtil.GetTypeface());
        checkOption3.setTypeface(HashUtil.GetTypeface());
        checkOption4.setTypeface(HashUtil.GetTypeface());
        editTextOutput.setTypeface(HashUtil.GetTypeface());
        editTextProgram.setTypeface(HashUtil.GetTypeface());
        buttonSubmit.setTypeface(HashUtil.GetTypeface());
        pointText.setTypeface(HashUtil.typefaceLatoHairline);
    }


    public void checkAnswer() {
        if (cyk != null && cyk.questionType.equals("Output")) {
            if (editTextOutput.getText().toString().equals(cyk.answer)) {
                writeNewCykUserDetail("true", editTextOutput.getText().toString(), cyk.point);
                successIndicator.setImageResource(R.drawable.ic_check);
                pointText.setText(cyk.point + "");
            } else {
                writeNewCykUserDetail("false", editTextOutput.getText().toString(), 0);
                successIndicator.setImageResource(R.drawable.ic_close);
                pointText.setText("0");
            }
        }
        if (cyk != null && cyk.questionType.equals("Mcq-One")) {
            if (radioOption1.isChecked() && cyk.answer.trim().equals("1")) {
                writeNewCykUserDetail("true", "1", cyk.point);
                successIndicator.setImageResource(R.drawable.ic_check);
                pointText.setText(cyk.point + "");
            } else if (radioOption2.isChecked() && cyk.answer.trim().equals("2")) {
                writeNewCykUserDetail("true", "2", cyk.point);
                successIndicator.setImageResource(R.drawable.ic_check);
                pointText.setText(cyk.point + "");
            } else if (radioOption3.isChecked() && cyk.answer.trim().equals("3")) {
                writeNewCykUserDetail("true", "3", cyk.point);
                successIndicator.setImageResource(R.drawable.ic_check);
                pointText.setText(cyk.point + "");
            } else if (radioOption4.isChecked() && cyk.answer.trim().equals("4")) {
                writeNewCykUserDetail("true", "4", cyk.point);
                successIndicator.setImageResource(R.drawable.ic_check);
                pointText.setText(cyk.point + "");
            } else if (radioOption1.isChecked() && !cyk.answer.trim().equals("1")) {
                writeNewCykUserDetail("false", "1", 0);
                successIndicator.setImageResource(R.drawable.ic_close);
                pointText.setText("0");
            } else if (radioOption2.isChecked() && !cyk.answer.trim().equals("2")) {
                writeNewCykUserDetail("false", "2", 0);
                successIndicator.setImageResource(R.drawable.ic_close);
                pointText.setText("0");
            } else if (radioOption3.isChecked() && !cyk.answer.trim().equals("3")) {
                writeNewCykUserDetail("false", "3", 0);
                successIndicator.setImageResource(R.drawable.ic_close);
                pointText.setText("0");
            } else if (radioOption4.isChecked() && !cyk.answer.trim().equals("4")) {
                writeNewCykUserDetail("false", "4", 0);
                successIndicator.setImageResource(R.drawable.ic_close);
                pointText.setText("0");
            }
        }
        if (cyk != null && cyk.questionType.equals("Mcq-Multiple")) {
            if (cyk.answer.equals(getCheck())) {
                writeNewCykUserDetail("true", getCheck(), cyk.point);
                successIndicator.setImageResource(R.drawable.ic_check);
                pointText.setText(cyk.point + "");
            } else {
                writeNewCykUserDetail("false", getCheck(), 0);
                successIndicator.setImageResource(R.drawable.ic_close);
                pointText.setText("0");
            }
        }
        if (cyk.questionType.equals("True/False")) {
            if (radioOptionTrue.isChecked() && cyk.answer.trim().equals("true")) {
                writeNewCykUserDetail("true", "true", cyk.point);
                successIndicator.setImageResource(R.drawable.ic_check);
                pointText.setText(cyk.point + "");
            } else if (radioOptionTrue.isChecked() && cyk.answer.trim().equals("false")) {
                writeNewCykUserDetail("false", "true", 0);
                successIndicator.setImageResource(R.drawable.ic_close);
                pointText.setText("0");
            } else if (radioOptionFalse.isChecked() && cyk.answer.trim().equals("false")) {
                writeNewCykUserDetail("true", "false", cyk.point);
                successIndicator.setImageResource(R.drawable.ic_check);
                pointText.setText(cyk.point + "");
            } else if (radioOptionFalse.isChecked() && cyk.answer.trim().equals("true")) {
                writeNewCykUserDetail("false", "false", 0);
                successIndicator.setImageResource(R.drawable.ic_close);
                pointText.setText("0");
            }
        }
    }

    private String getCheck() {
        String s = "";
        if (checkOption1.isChecked()) {
            s = s + "1";
        }
        if (checkOption2.isChecked()) {
            s = s + "2";
        }
        if (checkOption3.isChecked()) {
            s = s + "3";
        }
        if (checkOption4.isChecked()) {
            s = s + "4";
        }
        return s;
    }

    private void writeNewCykUserDetail(String success, String ans, int point) {
        Map<String, Object> childUpdates = new HashMap<>();
        CykUserDetail cykUserDetail = new CykUserDetail(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail()), success, point, ans, extra, ServerValue.TIMESTAMP);
        Map<String, Object> cykValues = cykUserDetail.toMap();
        updateUserPoint(point, success);
        childUpdates.put("/cyk-details/" + HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail()) + "/" + extra, cykValues);
        FirebaseUtils.GetDbRef().updateChildren(childUpdates);
    }
    private void setQueInstruction(String queType){
        String insCheck = "Select the option(s) you think are correct, then hit SUBMIT";
        String insRadio = "Select the option you think are correct, then hit SUBMIT";
        String insOutput ="Type output you think are correct, then hit SUBMIT";
        String insProgram = "Type correct code, then hit SUBMIT";

        if (cyk.questionType.equals("Output")) {
            instruction.setText(insOutput);
        }
        if (cyk.questionType.equals("Program")) {
            instruction.setText(insProgram);
        }
        if (cyk.questionType.equals("Mcq-One")) {
            instruction.setText(insRadio);
        }
        if (cyk.questionType.equals("Mcq-Multiple")) {
            instruction.setText(insCheck);
        }
        if (cyk.questionType.equals("True/False")) {
            instruction.setText(insRadio);
        }
    }
}
