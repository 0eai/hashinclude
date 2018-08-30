package hash.include.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import hash.include.R;
import hash.include.model.Pointer;
import hash.include.transitions.FabTransform;
import hash.include.ui.transitions.MorphTransform;
import hash.include.util.FirebaseUtils;
import hash.include.util.HashUtil;

public class AddPointerActivity extends AppCompatActivity implements
        View.OnClickListener {

    public static final String EXTRA = "EXTRA";
    public static final String EXTRA_KEY = "EXTRA";

    public TextView mdate;
    public Spinner spinner;
    public LinearLayout timeLayout;
    Typeface tfavv;
    @BindView(R.id.delete_button)
    Button delButton;
    @BindView(R.id.container)
    ViewGroup container;
    EditText title;
    EditText about;
    EditText link;
    private String extrakey;
    private Bitmap bitmap = null;
    private String mProfilePicUrl = null;
    private String extra;
    private TextView activityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pointer);
        ButterKnife.bind(this);
        if (!FabTransform.setup(this, container)) {
            MorphTransform.setup(this, container,
                    ContextCompat.getColor(this, R.color.background_light),
                    getResources().getDimensionPixelSize(R.dimen.dimen_2dp));
        }
        extra = getIntent().getStringExtra(EXTRA);
        if (extra == null) {
            throw new IllegalArgumentException("Must pass EXTRA");
        }else if (extra.equals("edit")){
            extrakey = getIntent().getStringExtra(EXTRA);
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
        SimpleDateFormat df3 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss a");
        String formattedDate3 = df3.format(c.getTime());
        mdate.setText(formattedDate3);

        spinnerInit();
        timeLayout.setOnClickListener(this);
        if (!extra.equals("ADD")) {
            delButton.setVisibility(View.VISIBLE);
            sync();
        }
        delButton.setOnClickListener(this);
    }

    public void init() {
        //views
        mdate = findViewById(R.id.pointer_display_date_text);
        spinner = findViewById(R.id.pointer_date_type_spinner);
        timeLayout = findViewById(R.id.pointer_date_button);

         title = findViewById(R.id.pointer_title_edittext);
         about = findViewById(R.id.pointer_about_edittext);
         link = findViewById(R.id.pointer_link_edittext);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.publish, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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

    private void publish() {

        if (TextUtils.isEmpty(title.getText().toString())) {
            Toast toast = Toast.makeText(this, R.string.title, Toast.LENGTH_SHORT);
            toast.show();
            title.requestFocus();
            return;
        } else if (TextUtils.isEmpty(about.getText().toString())) {
            Toast toast = Toast.makeText(this, R.string.about, Toast.LENGTH_SHORT);
            toast.show();
            about.requestFocus();
            return;
        } else if (TextUtils.isEmpty(link.getText().toString())) {
            Toast toast = Toast.makeText(this, R.string.link, Toast.LENGTH_SHORT);
            toast.show();
            link.requestFocus();
            return;
        }

        writeNewPointer(title.getText().toString(), about.getText().toString(), link.getText().toString(), spinner.getSelectedItem().toString(), mdate.getText().toString());

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pointer_date_button:
                chooseDate();
                break;
            case R.id.delete_button:
                Toast toast = Toast.makeText(this, "Deleting", Toast.LENGTH_SHORT);
                toast.show();
                FirebaseUtils.GetDbRef().child("pointers").child(extra).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        Toast toast = Toast.makeText(AddPointerActivity.this, "Deleted", Toast.LENGTH_SHORT);
                        toast.show();
                        finish();
                    }
                });
                break;
        }
    }

    private void setFonts() {

        ((TextView) findViewById(R.id.pointer_title_Text)).setTypeface(HashUtil.typefaceComfortaaRegular);
        ((TextView) findViewById(R.id.pointer_about_text)).setTypeface(HashUtil.typefaceComfortaaRegular);
        ((TextView) findViewById(R.id.pointer_link_text)).setTypeface(HashUtil.typefaceComfortaaRegular);
        ((TextView) findViewById(R.id.pointer_date_type_text)).setTypeface(HashUtil.typefaceComfortaaRegular);
        ((TextView) findViewById(R.id.pointer_date_text)).setTypeface(HashUtil.typefaceComfortaaRegular);

        ((TextView) findViewById(R.id.pointer_basic_date_text)).setTypeface(HashUtil.typefaceComfortaaBold);
        ((TextView) findViewById(R.id.pointer_basic_details_text)).setTypeface(HashUtil.typefaceComfortaaBold);

        ((EditText) findViewById(R.id.pointer_title_edittext)).setTypeface(HashUtil.GetTypeface());
        ((EditText) findViewById(R.id.pointer_about_edittext)).setTypeface(HashUtil.GetTypeface());
        ((EditText) findViewById(R.id.pointer_link_edittext)).setTypeface(HashUtil.GetTypeface());
        ((TextView) findViewById(R.id.pointer_display_date_text)).setTypeface(HashUtil.GetTypeface());


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

                        mdate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public <ViewGroup> void spinnerInit() {
        String[] myResArray = getResources().getStringArray(R.array.date_array);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myResArray) {
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                tfavv = HashUtil.GetTypeface();
                TextView v = (TextView) super.getView(position, convertView, parent);
                v.setTypeface(tfavv);
                v.setTextColor(Color.BLACK);
                v.setTextSize(12);
                return v;
            }

            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                TextView v = (TextView) super.getView(position, convertView, parent);
                v.setTypeface(tfavv);
                v.setTextColor(Color.BLACK);
                return v;
            }
        };

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // Notify the selected item text
                /*Toast.makeText
                        (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                        .show();*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void writeNewPointer(String title, String about, String link, String dateType, String date) {

        String key = FirebaseUtils.GetDbRef().child("pointers").push().getKey();
        Pointer pointer = new Pointer(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail()), title, about, link, dateType, date, ServerValue.TIMESTAMP);
        Map<String, Object> pointerValues = pointer.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        if (!extra.equals("ADD")) {
            childUpdates.put("/pointers/" + extra, pointerValues);
        } else {
            childUpdates.put("/pointers/" + key, pointerValues);
        }
        FirebaseUtils.GetDbRef().updateChildren(childUpdates);
    }

    public void sync() {

        FirebaseUtils.GetDbRef().child("pointers").child(extra).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // ...
                Pointer pointer = dataSnapshot.getValue(Pointer.class);
                if (pointer != null) {
                    about.setText(pointer.about);
                    title.setText(pointer.title);
                    link.setText(pointer.link);
                    mdate.setText(pointer.date);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

    }

    private static class MySpinnerAdapter extends ArrayAdapter<String> {
        // Initialise custom font, for example:
        Typeface font = HashUtil.GetTypeface();

        // (In reality I used a manager which caches the Typeface objects)
        // Typeface font = FontManager.getInstance().getFont(getContext(), BLAMBOT);

        private MySpinnerAdapter(Context context, int resource, List<String> items) {
            super(context, resource, items);
        }

        // Affects default (closed) state of the spinner
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            view.setTypeface(font);
            return view;
        }

        // Affects opened state of the spinner
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getDropDownView(position, convertView, parent);
            view.setTypeface(font);
            return view;
        }
    }

}
