package hash.include.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import hash.include.R;
import hash.include.model.CheckYourKnowledge;
import hash.include.transitions.FabTransform;
import hash.include.ui.transitions.MorphTransform;
import hash.include.util.FirebaseUtils;
import hash.include.util.HashUtil;

public class AddCykActivity extends AppCompatActivity implements
        View.OnClickListener {
    public static final String EXTRA = "EXTRA";
    private static final int RC_PHOTO_PICKER = 2;

    @BindView(R.id.cyk_option_layout)
    LinearLayout optionLayout;
    @BindView(R.id.cyk_testcase_layout)
    LinearLayout testcaseLayout;

    @BindView(R.id.cyk_choosed_image)
    ImageView checkYourKnowledgePic;

    @BindView(R.id.cyk_answer_Text)
    TextView answerText;
    @BindView(R.id.cyk_answer_edittext)
    EditText editTextAnswer;

    @BindView(R.id.cyk_option_1_edittext)
    EditText editTextOption1;
    @BindView(R.id.cyk_option_2_edittext)
    EditText editTextOption2;
    @BindView(R.id.cyk_option_3_edittext)
    EditText editTextOption3;
    @BindView(R.id.cyk_option_4_edittext)
    EditText editTextOption4;

    @BindView(R.id.cyk_question_edittext)
    EditText editTextQuestion;
    @BindView(R.id.cyk_title_edittext)
    EditText editTextTitle;
    @BindView(R.id.cyk_tags_edittext)
    EditText editTextTag;

    @BindView(R.id.cyk_testcase_input_1_edittext)
    EditText editTextInput1;
    @BindView(R.id.cyk_testcase_output_1_edittext)
    EditText editTextOutput1;
    @BindView(R.id.cyk_testcase_input_2_edittext)
    EditText editTextInput2;
    @BindView(R.id.cyk_testcase_output_2_edittext)
    EditText editTextOutput2;
    @BindView(R.id.cyk_testcase_input_3_edittext)
    EditText editTextInput3;
    @BindView(R.id.cyk_testcase_output_3_edittext)
    EditText editTextOutput3;
    @BindView(R.id.cyk_testcase_input_4_edittext)
    EditText editTextInput4;
    @BindView(R.id.cyk_testcase_output_4_edittext)
    EditText editTextOutput4;
    @BindView(R.id.cyk_testcase_input_5_edittext)
    EditText editTextInput5;
    @BindView(R.id.cyk_testcase_output_5_edittext)
    EditText editTextOutput5;
    @BindView(R.id.cyk_point_seekbar)
    SeekBar seekBar;
    @BindView(R.id.delete_button)
    Button delButton;
    @BindView(R.id.container)
    ViewGroup container;
    Uri mNotUpImageUri = null;
    Uri mUpImageUri = null;
    Typeface tf;
    ArrayAdapter<String> spinnerAdapter;
    ArrayAdapter<String> levelspinnerAdapter;
    private StorageReference mProfilePicStorageReference;
    private FirebaseStorage mFirebaseStorage;
    private String extra;
    private TextView activityTitle;
    private Spinner spinner;
    private Spinner levelSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cyk);
        ButterKnife.bind(this);
        if (!FabTransform.setup(this, container)) {
            MorphTransform.setup(this, container,
                    ContextCompat.getColor(this, R.color.background_light),
                    getResources().getDimensionPixelSize(R.dimen.dimen_2dp));
        }
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
        init();
        setFonts();
        spinnerInit();
        levelspinnerInit();
        findViewById(R.id.cyk_choose_image).setOnClickListener(this);
        if (!extra.equals("ADD")) {
            delButton.setVisibility(View.VISIBLE);
            sync();
        } else {

        }
        delButton.setOnClickListener(this);
    }

    public void sync() {
        FirebaseUtils.GetDbRef().child("check-your-knowledge").child(extra).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // ...
                try {
                    CheckYourKnowledge checkYourKnowledge = dataSnapshot.getValue(CheckYourKnowledge.class);
                    if (checkYourKnowledge != null) {
                        editTextQuestion.setText(checkYourKnowledge.question);
                        editTextTag.setText(checkYourKnowledge.tags);
                        editTextTitle.setText(checkYourKnowledge.title);
                        spinner.setSelection(getTypePosition(checkYourKnowledge.questionType));
                        levelSpinner.setSelection(getLevelPosition(checkYourKnowledge.level));
                        seekBar.setProgress(checkYourKnowledge.point);
                        if (!TextUtils.isEmpty(checkYourKnowledge.questionPicUrl)) {
                            mUpImageUri = Uri.parse(checkYourKnowledge.questionPicUrl);
                            try {
                                Glide.with(checkYourKnowledgePic.getContext())
                                        .load(checkYourKnowledge.questionPicUrl)
                                        .into(checkYourKnowledgePic);
                            } catch (IllegalArgumentException e) {

                            }
                        }
                        if (checkYourKnowledge.questionType.equals("True/False")) {
                            testcaseLayout.setVisibility(View.GONE);
                            optionLayout.setVisibility(View.GONE);
                            editTextAnswer.setVisibility(View.VISIBLE);
                            answerText.setVisibility(View.VISIBLE);
                            answerText.setText("True/False i.e. true");
                            editTextAnswer.setText(checkYourKnowledge.answer);
                        }
                        if (checkYourKnowledge.questionType.equals("Mcq-One")) {
                            testcaseLayout.setVisibility(View.GONE);
                            optionLayout.setVisibility(View.VISIBLE);
                            editTextAnswer.setVisibility(View.VISIBLE);
                            answerText.setVisibility(View.VISIBLE);
                            answerText.setText("Correct option");
                            editTextAnswer.setText(checkYourKnowledge.answer);
                            editTextOption1.setText(checkYourKnowledge.option1);
                            editTextOption2.setText(checkYourKnowledge.option2);
                            editTextOption3.setText(checkYourKnowledge.option3);
                            editTextOption4.setText(checkYourKnowledge.option4);
                        }
                        if (checkYourKnowledge.questionType.equals("Mcq-Multiple")) {
                            testcaseLayout.setVisibility(View.GONE);
                            optionLayout.setVisibility(View.VISIBLE);
                            editTextAnswer.setVisibility(View.VISIBLE);
                            answerText.setVisibility(View.VISIBLE);
                            answerText.setText("Correct option(s) i.e 14");
                            editTextAnswer.setText(checkYourKnowledge.answer);
                            editTextOption1.setText(checkYourKnowledge.option1);
                            editTextOption2.setText(checkYourKnowledge.option2);
                            editTextOption3.setText(checkYourKnowledge.option3);
                            editTextOption4.setText(checkYourKnowledge.option4);
                        }
                        if (checkYourKnowledge.questionType.equals("Output")) {
                            testcaseLayout.setVisibility(View.GONE);
                            optionLayout.setVisibility(View.GONE);
                            editTextAnswer.setVisibility(View.VISIBLE);
                            answerText.setVisibility(View.VISIBLE);
                            answerText.setText("Output");
                            editTextAnswer.setText(checkYourKnowledge.answer);
                        }
                        if (checkYourKnowledge.questionType.equals("Program")) {
                            testcaseLayout.setVisibility(View.VISIBLE);
                            optionLayout.setVisibility(View.GONE);
                            editTextAnswer.setVisibility(View.GONE);
                            answerText.setVisibility(View.GONE);
                            editTextInput1.setText(checkYourKnowledge.testCaseInput1);
                            editTextOutput1.setText(checkYourKnowledge.testCaseOutput1);
                            editTextInput2.setText(checkYourKnowledge.testCaseInput2);
                            editTextOutput2.setText(checkYourKnowledge.testCaseOutput2);
                            editTextInput3.setText(checkYourKnowledge.testCaseInput3);
                            editTextOutput3.setText(checkYourKnowledge.testCaseOutput3);
                            editTextInput4.setText(checkYourKnowledge.testCaseInput4);
                            editTextOutput4.setText(checkYourKnowledge.testCaseOutput4);
                            editTextInput5.setText(checkYourKnowledge.testCaseInput5);
                            editTextOutput5.setText(checkYourKnowledge.testCaseOutput5);
                        }
                    }
                } catch (Exception e) {

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
            case R.id.cyk_choose_image:
                //changeLogo();
                HashUtil.chooseImage(AddCykActivity.this);
                break;
            case R.id.delete_button:
                Toast toast = Toast.makeText(this, "Deleting", Toast.LENGTH_SHORT);
                toast.show();
                FirebaseUtils.GetDbRef().child("check-your-knowledge").child(extra).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        Toast toast = Toast.makeText(AddCykActivity.this, "Deleted", Toast.LENGTH_SHORT);
                        toast.show();
                        finish();
                    }
                });
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            mNotUpImageUri = selectedImageUri;
            checkYourKnowledgePic.setImageURI(selectedImageUri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.publish, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_publish) {
            publish();
            return true;
        }
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setFonts() {
        ((TextView) findViewById(R.id.cyk_basic_details_text)).setTypeface(HashUtil.typefaceLatoRegular);
        ((TextView) findViewById(R.id.cyk_question_Text)).setTypeface(HashUtil.typefaceLatoRegular);
        ((TextView) findViewById(R.id.cyk_title_Text)).setTypeface(HashUtil.typefaceLatoRegular);
        ((TextView) findViewById(R.id.cyk_tags_Text)).setTypeface(HashUtil.typefaceLatoRegular);
        ((TextView) findViewById(R.id.cyk_question_type_text)).setTypeface(HashUtil.GetTypeface());
        ((TextView) findViewById(R.id.cyk_answer_Text)).setTypeface(HashUtil.typefaceLatoRegular);
        ((TextView) findViewById(R.id.cyk_option_1_Text)).setTypeface(HashUtil.typefaceLatoRegular);
        ((TextView) findViewById(R.id.cyk_option_2_Text)).setTypeface(HashUtil.GetTypeface());
        ((TextView) findViewById(R.id.cyk_option_3_Text)).setTypeface(HashUtil.typefaceLatoRegular);
        ((TextView) findViewById(R.id.cyk_option_4_Text)).setTypeface(HashUtil.typefaceLatoRegular);
        ((TextView) findViewById(R.id.cyk_testcase_input_1_Text)).setTypeface(HashUtil.GetTypeface());
        ((TextView) findViewById(R.id.cyk_testcase_input_2_Text)).setTypeface(HashUtil.typefaceLatoRegular);
        ((TextView) findViewById(R.id.cyk_testcase_input_4_Text)).setTypeface(HashUtil.typefaceLatoRegular);
        ((TextView) findViewById(R.id.cyk_testcase_input_5_Text)).setTypeface(HashUtil.GetTypeface());
        ((TextView) findViewById(R.id.cyk_testcase_output_1_Text)).setTypeface(HashUtil.GetTypeface());
        ((TextView) findViewById(R.id.cyk_testcase_output_2_Text)).setTypeface(HashUtil.GetTypeface());
        ((TextView) findViewById(R.id.cyk_testcase_output_3_Text)).setTypeface(HashUtil.GetTypeface());
        ((TextView) findViewById(R.id.cyk_testcase_output_4_Text)).setTypeface(HashUtil.GetTypeface());
        ((TextView) findViewById(R.id.cyk_testcase_output_5_Text)).setTypeface(HashUtil.GetTypeface());
        ((TextView) findViewById(R.id.cyk_level_Text)).setTypeface(HashUtil.GetTypeface());
        ((TextView) findViewById(R.id.cyk_point_Text)).setTypeface(HashUtil.GetTypeface());

        editTextTitle.setTypeface(HashUtil.GetTypeface());
        editTextTag.setTypeface(HashUtil.GetTypeface());
        editTextQuestion.setTypeface(HashUtil.GetTypeface());
        editTextAnswer.setTypeface(HashUtil.GetTypeface());
        editTextInput1.setTypeface(HashUtil.GetTypeface());
        editTextInput2.setTypeface(HashUtil.GetTypeface());
        editTextInput3.setTypeface(HashUtil.GetTypeface());
        editTextInput4.setTypeface(HashUtil.GetTypeface());
        editTextInput5.setTypeface(HashUtil.GetTypeface());
        editTextOption1.setTypeface(HashUtil.GetTypeface());
        editTextOption2.setTypeface(HashUtil.GetTypeface());
        editTextOption3.setTypeface(HashUtil.GetTypeface());
        editTextOption4.setTypeface(HashUtil.GetTypeface());
        editTextOutput1.setTypeface(HashUtil.GetTypeface());
        editTextOutput2.setTypeface(HashUtil.GetTypeface());
        editTextOutput3.setTypeface(HashUtil.GetTypeface());
        editTextOutput4.setTypeface(HashUtil.GetTypeface());
        editTextOutput5.setTypeface(HashUtil.GetTypeface());
    }

    public void init() {
        mFirebaseStorage = FirebaseStorage.getInstance();
        mProfilePicStorageReference = mFirebaseStorage.getReference().child("check-your-knowledge-image");
        spinner = findViewById(R.id.cyk_question_type_spinner);
        levelSpinner = findViewById(R.id.cyk_question_level_spinner);
    }

    @Override
    @SuppressLint("NewApi")
    public void onEnterAnimationComplete() {

    }

    @Override
    public void onBackPressed() {
        dismiss(null);
    }


    public void dismiss(View view) {
        finishAfterTransition();
    }

    public <ViewGroup> void spinnerInit() {
        String[] myResArray = getResources().getStringArray(R.array.que_type_array);
        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myResArray) {
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                tf = HashUtil.GetTypeface();
                TextView v = (TextView) super.getView(position, convertView, parent);
                v.setTypeface(tf);
                v.setTextColor(Color.BLACK);
                v.setTextSize(12);
                return v;
            }

            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                TextView v = (TextView) super.getView(position, convertView, parent);
                v.setTypeface(tf);
                v.setTextColor(Color.BLACK);
                v.setBackgroundResource(R.drawable.tab_backround);
                v.setHeight(20);
                v.setGravity(Gravity.CENTER);
                return v;
            }
        };

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                if (selectedItemText.equals("True/False")) {
                    testcaseLayout.setVisibility(View.GONE);
                    optionLayout.setVisibility(View.GONE);
                    editTextAnswer.setVisibility(View.VISIBLE);
                    answerText.setVisibility(View.VISIBLE);
                    answerText.setText("True/False i.e. true");
                    //editTextAnswer.setText("true");
                }
                if (selectedItemText.equals("Mcq-One")) {
                    testcaseLayout.setVisibility(View.GONE);
                    optionLayout.setVisibility(View.VISIBLE);
                    editTextAnswer.setVisibility(View.VISIBLE);
                    answerText.setVisibility(View.VISIBLE);
                    answerText.setText("Correct option i.e. 4");
                    //editTextAnswer.setText("1");
                }
                if (selectedItemText.equals("Mcq-Multiple")) {
                    testcaseLayout.setVisibility(View.GONE);
                    optionLayout.setVisibility(View.VISIBLE);
                    editTextAnswer.setVisibility(View.VISIBLE);
                    answerText.setVisibility(View.VISIBLE);
                    answerText.setText("Correct option(s) i.e 14");
                    //editTextAnswer.setText("1,2");
                }
                if (selectedItemText.equals("Output")) {
                    testcaseLayout.setVisibility(View.GONE);
                    optionLayout.setVisibility(View.GONE);
                    editTextAnswer.setVisibility(View.VISIBLE);
                    answerText.setVisibility(View.VISIBLE);
                    answerText.setText("Output");
                    //editTextAnswer.setText("");
                }
                if (selectedItemText.equals("Program")) {
                    testcaseLayout.setVisibility(View.VISIBLE);
                    optionLayout.setVisibility(View.GONE);
                    editTextAnswer.setVisibility(View.GONE);
                    answerText.setVisibility(View.GONE);
                    answerText.setText("Program i.e. 222");
                    //editTextAnswer.setText("#include<stdio.h>\nvoid main(){\n\n}");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public <ViewGroup> void levelspinnerInit() {
        String[] myResArray = getResources().getStringArray(R.array.level_array);
        levelspinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myResArray) {
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                tf = HashUtil.GetTypeface();
                TextView v = (TextView) super.getView(position, convertView, parent);
                v.setTypeface(tf);
                v.setTextColor(Color.BLACK);
                v.setTextSize(12);
                return v;
            }

            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                TextView v = (TextView) super.getView(position, convertView, parent);
                v.setTypeface(tf);
                v.setTextColor(Color.BLACK);
                v.setBackgroundResource(R.drawable.tab_backround);
                v.setHeight(20);
                v.setGravity(Gravity.CENTER);
                return v;
            }
        };

        levelspinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelSpinner.setAdapter(levelspinnerAdapter);
        levelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public int getTypePosition(String type) {
        if (spinner.getItemAtPosition(0).equals(type)) {
            return 0;
        } else if (spinner.getItemAtPosition(1).equals(type)) {
            return 1;
        } else if (spinner.getItemAtPosition(2).equals(type)) {
            return 2;
        } else if (spinner.getItemAtPosition(3).equals(type)) {
            return 3;
        } else if (spinner.getItemAtPosition(4).equals(type)) {
            return 4;
        } else {
            return 0;
        }
    }

    public int getLevelPosition(String type) {
        if (levelSpinner.getItemAtPosition(0).equals(type)) {
            return 0;
        } else if (levelSpinner.getItemAtPosition(1).equals(type)) {
            return 1;
        } else {
            return 2;
        }
    }

    private void publish() {

        if (TextUtils.isEmpty(editTextTitle.getText().toString())) {
            Toast toast = Toast.makeText(this, R.string.title, Toast.LENGTH_SHORT);
            toast.show();
            editTextTitle.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(editTextQuestion.getText().toString())) {
            Toast toast = Toast.makeText(this, R.string.question, Toast.LENGTH_SHORT);
            toast.show();
            editTextQuestion.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(editTextTag.getText().toString())) {
            Toast toast = Toast.makeText(this, R.string.tag, Toast.LENGTH_SHORT);
            toast.show();
            editTextTag.requestFocus();
            return;
        }
        if (spinner.getSelectedItem().equals("True/False") || spinner.getSelectedItem().equals("Output")) {
            if (TextUtils.isEmpty(editTextAnswer.getText().toString())) {
                Toast toast = Toast.makeText(this, R.string.answer, Toast.LENGTH_SHORT);
                toast.show();
                editTextAnswer.requestFocus();
                return;
            }
        } else if (spinner.getSelectedItem().equals("Mcq-One") || spinner.getSelectedItem().equals("Mcq-Multiple")) {
            if (TextUtils.isEmpty(editTextOption1.getText().toString())) {
                Toast toast = Toast.makeText(this, R.string.option1, Toast.LENGTH_SHORT);
                toast.show();
                editTextOption1.requestFocus();
                return;
            } else if (TextUtils.isEmpty(editTextOption2.getText().toString())) {
                Toast toast = Toast.makeText(this, R.string.option2, Toast.LENGTH_SHORT);
                toast.show();
                editTextOption2.requestFocus();
                return;
            } else if (TextUtils.isEmpty(editTextOption3.getText().toString())) {
                Toast toast = Toast.makeText(this, R.string.option3, Toast.LENGTH_SHORT);
                toast.show();
                editTextOption3.requestFocus();
                return;
            } else if (TextUtils.isEmpty(editTextOption4.getText().toString())) {
                Toast toast = Toast.makeText(this, R.string.option4, Toast.LENGTH_SHORT);
                toast.show();
                editTextOption4.requestFocus();
                return;
            } else if (TextUtils.isEmpty(editTextAnswer.getText().toString())) {
                Toast toast = Toast.makeText(this, R.string.answer, Toast.LENGTH_SHORT);
                toast.show();
                editTextAnswer.requestFocus();
                return;
            }
        } else if (spinner.getSelectedItem().equals("Program")) {
            if (TextUtils.isEmpty(editTextInput1.getText().toString())) {
                Toast toast = Toast.makeText(this, R.string.input1, Toast.LENGTH_SHORT);
                toast.show();
                editTextInput1.requestFocus();
                return;
            } else if (TextUtils.isEmpty(editTextOutput1.getText().toString())) {
                Toast toast = Toast.makeText(this, R.string.output1, Toast.LENGTH_SHORT);
                toast.show();
                editTextOutput1.requestFocus();
                return;
            } else if (TextUtils.isEmpty(editTextInput2.getText().toString())) {
                Toast toast = Toast.makeText(this, R.string.input2, Toast.LENGTH_SHORT);
                toast.show();
                editTextInput2.requestFocus();
                return;
            } else if (TextUtils.isEmpty(editTextOutput2.getText().toString())) {
                Toast toast = Toast.makeText(this, R.string.output2, Toast.LENGTH_SHORT);
                toast.show();
                editTextOutput2.requestFocus();
                return;
            }
        }
        if (mNotUpImageUri != null) {
            StorageReference photoRef = mProfilePicStorageReference.child(mNotUpImageUri.getLastPathSegment());
            try {
                // Upload file to Firebase Storage
                photoRef.putFile(mNotUpImageUri)
                        .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // When the image has successfully uploaded, we get its download URL
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                checkType(downloadUrl.toString());
                                Toast toast = Toast.makeText(AddCykActivity.this, "Posting...", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
            } catch (Exception e) {
                Toast toast = Toast.makeText(AddCykActivity.this, e.toString(), Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            if (mUpImageUri != null) {
                checkType(mUpImageUri.toString());
            } else {
                checkType(null);
            }
            Toast toast = Toast.makeText(AddCykActivity.this, "Posting...", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void checkType(String s) {
        if (spinner.getSelectedItem().equals("True/False")) {
            writeNewCheckYourKnowledge(s, editTextTitle.getText().toString(), editTextTag.getText().toString(), seekBar.getProgress(), levelSpinner.getSelectedItem().toString(), editTextQuestion.getText().toString(), spinner.getSelectedItem().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, editTextAnswer.getText().toString().trim());
        }
        if (spinner.getSelectedItem().equals("Mcq-One")) {
            writeNewCheckYourKnowledge(s, editTextTitle.getText().toString(), editTextTag.getText().toString(), seekBar.getProgress(), levelSpinner.getSelectedItem().toString(), editTextQuestion.getText().toString(), spinner.getSelectedItem().toString(), null, editTextOption1.getText().toString(), editTextOption2.getText().toString(), editTextOption3.getText().toString(), editTextOption4.getText().toString(), null, null, null, null, null, null, null, null, null, null, editTextAnswer.getText().toString().trim());
        }
        if (spinner.getSelectedItem().equals("Mcq-Multiple")) {
            writeNewCheckYourKnowledge(s, editTextTitle.getText().toString(), editTextTag.getText().toString(), seekBar.getProgress(), levelSpinner.getSelectedItem().toString(), editTextQuestion.getText().toString(), spinner.getSelectedItem().toString(), null, editTextOption1.getText().toString(), editTextOption2.getText().toString(), editTextOption3.getText().toString(), editTextOption4.getText().toString(), null, null, null, null, null, null, null, null, null, null, editTextAnswer.getText().toString().trim());
        }
        if (spinner.getSelectedItem().equals("Output")) {
            writeNewCheckYourKnowledge(s, editTextTitle.getText().toString(), editTextTag.getText().toString(), seekBar.getProgress(), levelSpinner.getSelectedItem().toString(), editTextQuestion.getText().toString(), spinner.getSelectedItem().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, editTextAnswer.getText().toString().trim());
        }
        if (spinner.getSelectedItem().equals("Program")) {
            writeNewCheckYourKnowledge(s, editTextTitle.getText().toString(), editTextTag.getText().toString(), seekBar.getProgress(), levelSpinner.getSelectedItem().toString(), editTextQuestion.getText().toString(), spinner.getSelectedItem().toString(), null, null, null, null, null, editTextInput1.getText().toString(), editTextInput2.getText().toString(), editTextInput3.getText().toString(), editTextInput4.getText().toString(), editTextInput5.getText().toString(), editTextOutput1.getText().toString(), editTextOutput2.getText().toString(), editTextOutput3.getText().toString(), editTextOutput4.getText().toString(), editTextOutput5.getText().toString(), null);
        }
    }

    private void writeNewCheckYourKnowledge(String mPicUrl, String title, String tags, int point, String level, String que, String queType, String inst, String o1, String o2, String o3, String o4, String i1, String i2, String i3, String i4, String i5, String to1, String to2, String to3, String to4, String to5, String ans) {
        String key = FirebaseUtils.GetDbRef().child("check-your-knowledge").push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        if (!extra.equals("ADD")) {
            CheckYourKnowledge checkYourKnowledge = new CheckYourKnowledge(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail()), mPicUrl, title, tags, point, level, que, queType, inst, o1, o2, o3, o4, i1, i2, i3, i4, i5, to1, to2, to3, to4, to5, ans, extra, ServerValue.TIMESTAMP);
            Map<String, Object> cykValues = checkYourKnowledge.toMap();
            childUpdates.put("/check-your-knowledge/" + extra, cykValues);
        } else {
            CheckYourKnowledge checkYourKnowledge = new CheckYourKnowledge(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail()), mPicUrl, title, tags, point, level, que, queType, inst, o1, o2, o3, o4, i1, i2, i3, i4, i5, to1, to2, to3, to4, to5, ans, key, ServerValue.TIMESTAMP);
            Map<String, Object> cykValues = checkYourKnowledge.toMap();
            childUpdates.put("/check-your-knowledge/" + key, cykValues);
        }
        FirebaseUtils.GetDbRef().updateChildren(childUpdates);
    }
}