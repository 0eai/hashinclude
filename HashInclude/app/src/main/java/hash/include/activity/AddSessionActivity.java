package hash.include.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import hash.include.R;
import hash.include.model.Session;
import hash.include.transitions.FabTransform;
import hash.include.ui.transitions.MorphTransform;
import hash.include.util.FirebaseUtils;
import hash.include.util.HashUtil;

public class AddSessionActivity extends AppCompatActivity implements
        View.OnClickListener {


    public static final String EXTRA = "EXTRA";
    LinearLayout dateLayout;
    LinearLayout timeLayout;
    @BindView(R.id.session_title_edittext)
    EditText editTextTitle;
    @BindView(R.id.session_key_point_edittext)
    EditText editTextKeyPoint;
    @BindView(R.id.session_video_id_edittext)
    EditText editTextVideoId;
    @BindView(R.id.session_references_edittext)
    EditText editTextReference;
    @BindView(R.id.session_number_edittext)
    EditText editTextNumber;
    @BindView(R.id.session_que_id_edittext)
    EditText editTextQueId;
    @BindView(R.id.container)
    ViewGroup container;
    @BindView(R.id.session_display_time_text)
    TextView textTime;
    @BindView(R.id.session_display_date_text)
    TextView textDate;
    String[] string;
    @BindView(R.id.delete_button)
    Button delButton;
    private String extra;
    private TextView activityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_session);
        ButterKnife.bind(this);
        if (!FabTransform.setup(this, container)) {
            MorphTransform.setup(this, container,
                    ContextCompat.getColor(this, R.color.background_light),
                    getResources().getDimensionPixelSize(R.dimen.dimen_2dp));
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
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df3 = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate3 = df3.format(c.getTime());
        textDate.setText(formattedDate3);
        df3 = new SimpleDateFormat("HH:mm a");
        formattedDate3 = df3.format(c.getTime());
        textTime.setText(formattedDate3);
        dateLayout.setOnClickListener(this);
        timeLayout.setOnClickListener(this);
        extra = getIntent().getStringExtra(EXTRA);
        if (extra == null) {
            throw new IllegalArgumentException("Must pass EXTRA");
        } else {
            string = extra.split("@", 2);
            //Toast.makeText(AddSessionActivity.this, extra + "\n" + string[0] + "\n" + string[1], Toast.LENGTH_LONG).show();
            if (!string[0].equals("ADD")) {
                delButton.setVisibility(View.VISIBLE);
                sync();
            }
        }
        delButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.date_button_layout:
                chooseDate();
                break;
            case R.id.time_button_layout:
                chooseTime();
                break;
            case R.id.delete_button:
                Toast toast = Toast.makeText(this, "Deleting", Toast.LENGTH_SHORT);
                toast.show();
                FirebaseUtils.GetDbRef().child("session").child(string[0]).child(string[1]).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        Toast toast = Toast.makeText(AddSessionActivity.this, "Deleted", Toast.LENGTH_SHORT);
                        toast.show();
                        finish();
                    }
                });
                break;
        }
    }

    public void sync() {
        FirebaseUtils.GetDbRef().child("session").child(string[0]).child(string[1]).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // ...
                try {
                    Session session = dataSnapshot.getValue(Session.class);
                    if (session != null) {
                        editTextTitle.setText(session.title);
                        editTextKeyPoint.setText(session.keyLearning);
                        editTextVideoId.setText(session.youtubeVideoId);
                        editTextReference.setText(session.references);
                        editTextNumber.setText(session.sessionNo);
                        editTextQueId.setText(session.queUID);

                        textDate.setText(session.sessionDate);
                        textTime.setText(session.sessionTime);

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

    public void init() {
        dateLayout = findViewById(R.id.date_button_layout);
        timeLayout = findViewById(R.id.time_button_layout);
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

    private void setFonts() {
        ((TextView) findViewById(R.id.session_title_Text)).setTypeface(HashUtil.typefaceLatoRegular);
        ((TextView) findViewById(R.id.session_key_point_text)).setTypeface(HashUtil.typefaceLatoRegular);
        ((TextView) findViewById(R.id.session_video_id_text)).setTypeface(HashUtil.typefaceLatoRegular);
        ((TextView) findViewById(R.id.session_references_Text_t)).setTypeface(HashUtil.typefaceLatoRegular);
        ((TextView) findViewById(R.id.session_number_text)).setTypeface(HashUtil.typefaceLatoRegular);
        ((TextView) findViewById(R.id.session_que_id_text)).setTypeface(HashUtil.typefaceLatoRegular);
        ((TextView) findViewById(R.id.session_basic_date_text)).setTypeface(HashUtil.typefaceLatoRegular);
        ((TextView) findViewById(R.id.session_basic_details_text)).setTypeface(HashUtil.typefaceLatoRegular);
        ((TextView) findViewById(R.id.session_date_text)).setTypeface(HashUtil.GetTypeface());
        ((TextView) findViewById(R.id.session_time_text)).setTypeface(HashUtil.GetTypeface());
        ((TextView) findViewById(R.id.session_display_time_text)).setTypeface(HashUtil.GetTypeface());
        ((TextView) findViewById(R.id.session_display_date_text)).setTypeface(HashUtil.GetTypeface());

        editTextTitle.setTypeface(HashUtil.GetTypeface());
        editTextKeyPoint.setTypeface(HashUtil.GetTypeface());
        editTextVideoId.setTypeface(HashUtil.GetTypeface());
        editTextReference.setTypeface(HashUtil.GetTypeface());
        editTextNumber.setTypeface(HashUtil.GetTypeface());
        editTextQueId.setTypeface(HashUtil.GetTypeface());
    }

    private void publish() {

        if (TextUtils.isEmpty(editTextTitle.getText().toString())) {
            Toast toast = Toast.makeText(this, R.string.title, Toast.LENGTH_SHORT);
            toast.show();
            editTextTitle.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(editTextKeyPoint.getText().toString())) {
            Toast toast = Toast.makeText(this, R.string.keylerning, Toast.LENGTH_SHORT);
            toast.show();
            editTextKeyPoint.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(editTextNumber.getText().toString())) {
            Toast toast = Toast.makeText(this, R.string.s_num, Toast.LENGTH_SHORT);
            toast.show();
            editTextNumber.requestFocus();
            return;
        } else {
            writeNewSession(editTextVideoId.getText().toString(), editTextTitle.getText().toString(), editTextKeyPoint.getText().toString(), editTextReference.getText().toString(), editTextNumber.getText().toString(), textDate.getText().toString(), textTime.getText().toString(), editTextQueId.getText().toString());
        }

    }

    private void writeNewSession(String yid, String title, String kL, String re, String no, String d, String t, String qid) {
        Session session = new Session(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail()), title, yid, kL, re, no, d, t, qid, ServerValue.TIMESTAMP);
        Map<String, Object> sessionValues = session.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        if (string[0].equals("ADD")) {
            String key = FirebaseUtils.GetDbRef().child("session").child(string[1]).push().getKey();
            childUpdates.put("/session/" + string[1] + "/" + key, sessionValues);
            Toast toast = Toast.makeText(this, "Session added", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            childUpdates.put("/session/" + string[0] + "/" + string[1], sessionValues);
            Toast toast = Toast.makeText(this, "Session updated", Toast.LENGTH_SHORT);
            toast.show();
        }
        FirebaseUtils.GetDbRef().updateChildren(childUpdates);
    }

    private void chooseDate() {
        // Get Current Date
        int mYear, mMonth, mDay;
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        textDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void chooseTime() {
        int mHour, mMinute;
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        textTime.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }
}
