
package hash.include.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import hash.include.R;
import hash.include.model.Event;
import hash.include.model.Member;
import hash.include.transitions.FabTransform;
import hash.include.ui.transitions.MorphTransform;
import hash.include.util.FirebaseUtils;
import hash.include.util.HashUtil;
import hash.include.viewholder.RemoveMemberViewHolder;

public class AddEventActivity extends AppCompatActivity implements
        View.OnClickListener {
    public static final String EXTRA = "EXTRA";
    private static final int RC_PHOTO_PICKER = 2;
    public Spinner spinner;
    public TextView sdate;
    public TextView edate;
    public EditText title;
    public EditText about;
    public EditText org;
    public EditText link;
    public EditText duration;
    public EditText totalReg;
    public EditText totalPart;
    public CardView cardWinners;
    public CardView cardStat;
    public ImageButton addWinners;
    public RecyclerView winners;
    public ImageView imageView;
    public LinearLayout startDateLayout;
    public LinearLayout endDateLayout;
    @BindView(R.id.delete_button)
    Button delButton;
    @BindView(R.id.container)
    ViewGroup container;
    Typeface tfavv;
    boolean isDismissing = false;
    Uri mNotUpImageUri = null;
    Uri mUpImageUri = null;
    private Bitmap bitmap = null;
    private StorageReference mPicStorageReference;
    private FirebaseStorage mFirebaseStorage;
    private String extra;
    private TextView activityTitle;
    private LinearLayoutManager mManager;

    private FirebaseRecyclerAdapter<Member, RemoveMemberViewHolder> mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
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
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df3 = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate3 = df3.format(c.getTime());
        sdate.setText(formattedDate3);
        edate.setText(formattedDate3);
        findViewById(R.id.event_choose_image).setOnClickListener(this);
        startDateLayout.setOnClickListener(this);
        endDateLayout.setOnClickListener(this);

        if (!extra.equals("ADD")) {
            cardWinners.setVisibility(View.VISIBLE);
            cardStat.setVisibility(View.VISIBLE);
            mManager = new LinearLayoutManager(this);
            mManager.setReverseLayout(false);
            winners.setLayoutManager(mManager);
            winners.setItemAnimator(new DefaultItemAnimator());
            winners.setHasFixedSize(true);
            delButton.setVisibility(View.VISIBLE);
            sync();
            setUpMember();
        } else {
            cardWinners.setVisibility(View.GONE);
            cardStat.setVisibility(View.GONE);
        }
        delButton.setOnClickListener(this);
    }

    public void sync() {
        FirebaseUtils.GetDbRef().child("events").child(extra).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // ...
                try {
                    Event event = dataSnapshot.getValue(Event.class);
                    if (event != null) {
                        title.setText(event.title);
                        about.setText(event.about);
                        org.setText(event.org);
                        link.setText(event.link);
                        sdate.setText(event.startDate);
                        edate.setText(event.endDate);
                        duration.setText(event.duration);
                        totalPart.setText(event.totalPart);
                        totalReg.setText(event.totalReg);

                        if (!TextUtils.isEmpty(event.picUrl)) {
                            mUpImageUri = Uri.parse(event.picUrl);
                            try {
                                Glide.with(imageView.getContext())
                                        .load(event.picUrl)
                                        .into(imageView);
                            } catch (IllegalArgumentException e) {

                            }
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

    public void setUpMember() {
        Query memberQuery = getQuery();
        mAdapter = new FirebaseRecyclerAdapter<Member, RemoveMemberViewHolder>(Member.class, R.layout.item_remove_user,
                RemoveMemberViewHolder.class, memberQuery) {
            @Override
            protected void populateViewHolder(final RemoveMemberViewHolder viewHolder, final Member model, final int position) {
                final DatabaseReference memberRef = getRef(position);

                final String Key = memberRef.getKey();
                viewHolder.removeMember.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseUtils.GetDbRef().child("event-winners").child(extra).child(Key).removeValue();
                    }
                });
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AddEventActivity.this, ProfileDetailsActivity.class);
                        intent.putExtra(ProfileDetailsActivity.EXTRA, Key);
                        FabTransform.addExtras(intent,
                                ContextCompat.getColor(AddEventActivity.this, R.color.colorPrimary), R.drawable.ic_add_dark);
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AddEventActivity.this, viewHolder.picUrl,
                                getString(R.string.transition_designer_news_login));
                        startActivity(intent, options.toBundle());
                    }
                });
                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {

                    }
                });

            }
        };
        winners.setAdapter(mAdapter);
    }

    public Query getQuery() {
        return FirebaseUtils.GetDbRef().child("event-winners").child(extra);
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
        isDismissing = true;
        setResult(Activity.RESULT_CANCELED);
        finishAfterTransition();
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

        final EditText title = findViewById(R.id.event_title_edittext);
        final EditText about = findViewById(R.id.event_about_edittext);
        final EditText link = findViewById(R.id.event_link_edittext);

        final TextView sdate = findViewById(R.id.event_display_start_date_text);
        final TextView edate = findViewById(R.id.event_display_end_date_text);
        final EditText duration = findViewById(R.id.event__duration_edittext);

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
        } else if (TextUtils.isEmpty(org.getText().toString())) {
            Toast toast = Toast.makeText(this, "Add Org", Toast.LENGTH_SHORT);
            toast.show();
            org.requestFocus();
            return;
        } else if (TextUtils.isEmpty(link.getText().toString())) {
            Toast toast = Toast.makeText(this, R.string.link, Toast.LENGTH_SHORT);
            toast.show();
            link.requestFocus();
            return;
        }
        if (mNotUpImageUri != null) {
            StorageReference photoRef = mPicStorageReference.child(mNotUpImageUri.getLastPathSegment());
            try {
        // Upload file to Firebase Storage
                photoRef.putFile(mNotUpImageUri)
                .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // When the image has successfully uploaded, we get its download URL
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        //mProfilePicUrl = downloadUrl.toString();
                        writeNewPointer(downloadUrl.toString(), title.getText().toString(), about.getText().toString(), org.getText().toString(), link.getText().toString(), sdate.getText().toString(), edate.getText().toString(), duration.getText().toString());
                        //HashUtil.showToast(AddProjectActivity.this, downloadUrl.toString());
                        Toast toast = Toast.makeText(AddEventActivity.this, "Posting...", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            } catch (Exception e) {
                Toast toast = Toast.makeText(AddEventActivity.this, e.toString(), Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            if (mUpImageUri != null) {
                writeNewPointer(mUpImageUri.toString(), title.getText().toString(), about.getText().toString(), org.getText().toString(), link.getText().toString(), sdate.getText().toString(), edate.getText().toString(), duration.getText().toString());
                Toast toast = Toast.makeText(AddEventActivity.this, "Posting...", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                writeNewPointer(null, title.getText().toString(), about.getText().toString(), org.getText().toString(), link.getText().toString(), sdate.getText().toString(), edate.getText().toString(), duration.getText().toString());
                Toast toast = Toast.makeText(AddEventActivity.this, "Posting...", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.event_choose_image:
                //changeLogo();
                HashUtil.chooseImage(AddEventActivity.this);
                break;
            case R.id.event_start_date_button:
                chooseDate("sdate");
                break;
            case R.id.event_end_date_button:
                chooseDate("edate");
                break;
            case R.id.event_add_winners_button:
                Intent intent = new Intent(this, SearchActivity.class);
                String s = "ADD-WINNERS@" + extra;
                intent.putExtra(SearchActivity.SRAECH, s);
                View searchMenuView = findViewById(R.id.event_add_winners_button);
                Bundle options = ActivityOptions.makeSceneTransitionAnimation(this, searchMenuView,
                        getString(R.string.transition_search_back)).toBundle();
                startActivity(intent, options);
                break;
            case R.id.delete_button:
                Toast toast = Toast.makeText(this, "Deleting", Toast.LENGTH_SHORT);
                toast.show();
                FirebaseUtils.GetDbRef().child("events").child(extra).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        Toast toast = Toast.makeText(AddEventActivity.this, "Deleted", Toast.LENGTH_SHORT);
                        toast.show();
                        FirebaseUtils.GetDbRef().child("event-registration-details").child(extra).removeValue();
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
            //cropPhoto(selectedImageUri);
            mNotUpImageUri = selectedImageUri;
            imageView.setImageURI(selectedImageUri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void writeNewPointer(String mProfilePicUrl, String title, String about, String orga, String link, String sdate, String edate, String duration) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = FirebaseUtils.GetDbRef().child("events").push().getKey();
        Event event = new Event(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail()), mProfilePicUrl, title, about, orga, link, sdate, edate, duration, spinner.getSelectedItem().toString().toUpperCase(), ServerValue.TIMESTAMP, totalReg.getText().toString(), totalPart.getText().toString());
        Map<String, Object> eventValues = event.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        if (!extra.equals("ADD")) {
            childUpdates.put("/events/" + extra, eventValues);
        } else {
            childUpdates.put("/events/" + key, eventValues);
        }
        FirebaseUtils.GetDbRef().updateChildren(childUpdates);
    }


    private void setFonts() {

        ((TextView) findViewById(R.id.event_title_Text)).setTypeface(HashUtil.typefaceComfortaaRegular);
        ((TextView) findViewById(R.id.event_about_text)).setTypeface(HashUtil.typefaceComfortaaRegular);
        ((TextView) findViewById(R.id.event_org_text)).setTypeface(HashUtil.typefaceComfortaaRegular);
        ((TextView) findViewById(R.id.event_link_Text)).setTypeface(HashUtil.typefaceComfortaaRegular);
        ((TextView) findViewById(R.id.event_total_reg_Text)).setTypeface(HashUtil.typefaceComfortaaRegular);
        ((TextView) findViewById(R.id.event_total_part_text)).setTypeface(HashUtil.typefaceComfortaaRegular);

        ((TextView) findViewById(R.id.event_start_date_text)).setTypeface(HashUtil.typefaceComfortaaRegular);
        ((TextView) findViewById(R.id.event_end_date_text)).setTypeface(HashUtil.typefaceComfortaaRegular);
        ((TextView) findViewById(R.id.event_display_start_date_text)).setTypeface(HashUtil.GetTypeface());
        ((TextView) findViewById(R.id.event_display_end_date_text)).setTypeface(HashUtil.GetTypeface());

        ((TextView) findViewById(R.id.event_basic_date_text)).setTypeface(HashUtil.typefaceComfortaaBold);
        ((TextView) findViewById(R.id.event_basic_details_text)).setTypeface(HashUtil.typefaceComfortaaBold);
        ((TextView) findViewById(R.id.event_type_text)).setTypeface(HashUtil.typefaceComfortaaBold);
        ((TextView) findViewById(R.id.event_detail_text)).setTypeface(HashUtil.typefaceComfortaaBold);
        ((TextView) findViewById(R.id.event_add_winners_text)).setTypeface(HashUtil.typefaceComfortaaBold);

        ((EditText) findViewById(R.id.event_title_edittext)).setTypeface(HashUtil.GetTypeface());
        ((EditText) findViewById(R.id.event_about_edittext)).setTypeface(HashUtil.GetTypeface());
        ((EditText) findViewById(R.id.event_org_edittext)).setTypeface(HashUtil.GetTypeface());
        ((EditText) findViewById(R.id.event_link_edittext)).setTypeface(HashUtil.GetTypeface());
        ((EditText) findViewById(R.id.event_total_reg_edittext)).setTypeface(HashUtil.GetTypeface());
        ((EditText) findViewById(R.id.event_total_part_edittext)).setTypeface(HashUtil.GetTypeface());
        ((EditText) findViewById(R.id.event__duration_edittext)).setTypeface(HashUtil.GetTypeface());
    }

    private void chooseDate(String date) {
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
                        if (date.equals("sdate")) {
                            sdate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            chooseTime("sdate");
                        } else {
                            edate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            chooseTime("edate");
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void chooseTime(String date) {
        int mHour, mMinute;
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        String AM_PM;
                        if (hourOfDay < 12) {
                            AM_PM = "am";
                        } else {
                            AM_PM = "pm";
                        }

                        if (hourOfDay > 12) {
                            hourOfDay = hourOfDay - 12;
                        }
                        if (date.equals("sdate")) {
                            String d = sdate.getText().toString() + " " + hourOfDay + ":" + minute + " " + AM_PM;
                            sdate.setText(d);
                        } else {
                            String d = edate.getText().toString() + " " + hourOfDay + ":" + minute + " " + AM_PM;
                            edate.setText(d);
                        }
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    public void init() {
        //firebase
        mFirebaseStorage = FirebaseStorage.getInstance();
        mPicStorageReference = mFirebaseStorage.getReference().child("events-image");

        //views
        spinner = findViewById(R.id.event_type_spinner);
        sdate = findViewById(R.id.event_display_start_date_text);
        edate = findViewById(R.id.event_display_end_date_text);
        duration = findViewById(R.id.event__duration_edittext);
        imageView = findViewById(R.id.event_choosed_image);
        startDateLayout = findViewById(R.id.event_start_date_button);
        endDateLayout = findViewById(R.id.event_end_date_button);

        title = findViewById(R.id.event_title_edittext);
        about = findViewById(R.id.event_about_edittext);
        org = findViewById(R.id.event_org_edittext);
        link = findViewById(R.id.event_link_edittext);
        totalReg = findViewById(R.id.event_total_reg_edittext);
        totalReg.setText("0");
        totalPart = findViewById(R.id.event_total_part_edittext);
        totalPart.setText("0");
        cardWinners = findViewById(R.id.card_winners);
        cardStat = findViewById(R.id.event_card_register_stat);
        addWinners = findViewById(R.id.event_add_winners_button);
        addWinners.setOnClickListener(this);
        winners = findViewById(R.id.winners_recyclerview);
    }

    public <ViewGroup> void spinnerInit() {
        String[] myResArray = getResources().getStringArray(R.array.event_array);
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
                v.setBackgroundResource(R.drawable.tab_backround);
                v.setHeight(20);
                v.setGravity(Gravity.CENTER);
                return v;
            }
        };

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private static class MySpinnerAdapter extends ArrayAdapter<String> {
        // Initialise custom font, for example:
        Typeface font = HashUtil.GetTypeface();
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
