
package hash.include.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.content.Intent;
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
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import hash.include.R;
import hash.include.model.Garage;
import hash.include.model.Member;
import hash.include.transitions.FabTransform;
import hash.include.ui.transitions.MorphTransform;
import hash.include.util.FirebaseUtils;
import hash.include.util.HashUtil;
import hash.include.viewholder.RemoveMemberViewHolder;

public class AddGarageActivity extends AppCompatActivity implements
        View.OnClickListener {
    public static final String EXTRA = "EXTRA";
    private static final int RC_PHOTO_PICKER = 2;
    public TextView mdate;
    public ImageView imageView;
    public LinearLayout timeLayout;
    @BindView(R.id.container)
    ViewGroup container;
    @BindView(R.id.delete_button)
    Button delButton;
    boolean isDismissing = false;
    Uri mNotUpImageUri = null;
    Uri mUpImageUri = null;
    EditText title;
    EditText about;
    EditText link;
    TextView date;
    @BindView(R.id.member_recyclerview)
    RecyclerView members;
    @BindView(R.id.card_members)
    CardView cardMembers;
    private String extrakey;
    private String extra;
    private StorageReference mPicStorageReference;
    private FirebaseStorage mFirebaseStorage;
    private TextView activityTitle;
    private LinearLayoutManager mManager;

    private FirebaseRecyclerAdapter<Member, RemoveMemberViewHolder> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_garage);
        ButterKnife.bind(this);
        if (!FabTransform.setup(this, container)) {
            MorphTransform.setup(this, container,
                    ContextCompat.getColor(this, R.color.background_light),
                    getResources().getDimensionPixelSize(R.dimen.dimen_2dp));
        }
        extra = getIntent().getStringExtra(EXTRA);
        if (extra == null) {
            throw new IllegalArgumentException("Must pass EXTRA");
        }/*else if (extra.equals("EDIT")){
            extrakey = getIntent().getStringExtra(EXTRA_KEY);
        }*/
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
        findViewById(R.id.project_choose_image).setOnClickListener(this);
        timeLayout.setOnClickListener(this);
        findViewById(R.id.project_add_member_button).setOnClickListener(this);

        if (!extra.equals("ADD")) {
            cardMembers.setVisibility(View.VISIBLE);
            extrakey = extra;
            mManager = new LinearLayoutManager(this);
            mManager.setReverseLayout(false);
            members.setLayoutManager(mManager);
            members.setItemAnimator(new DefaultItemAnimator());
            members.setHasFixedSize(true);
            delButton.setVisibility(View.VISIBLE);
            sync();
            setUpMember();
        } else {
            cardMembers.setVisibility(View.GONE);
        }
        delButton.setOnClickListener(this);
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
                        FirebaseUtils.GetDbRef().child("project-members").child(extra).child(Key).removeValue();
                        FirebaseUtils.GetDbRef().child("users-projects").child(Key).child(extra).removeValue();
                    }
                });
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AddGarageActivity.this, ProfileDetailsActivity.class);
                        intent.putExtra(ProfileDetailsActivity.EXTRA, Key);
                        FabTransform.addExtras(intent,
                                ContextCompat.getColor(AddGarageActivity.this, R.color.colorPrimary), R.drawable.ic_add_dark);
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AddGarageActivity.this, viewHolder.picUrl,
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
        members.setAdapter(mAdapter);
    }

    public Query getQuery() {
        return FirebaseUtils.GetDbRef().child("project-members").child(extra);
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


    private void showLoading() {
        TransitionManager.beginDelayedTransition(container);

    }

    private void showLogin() {
        TransitionManager.beginDelayedTransition(container);

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
        }
        if (mNotUpImageUri != null) {
            StorageReference photoRef = mPicStorageReference.child(mNotUpImageUri.getLastPathSegment());
            // Upload file to Firebase Storage
            try {
                photoRef.putFile(mNotUpImageUri)
                        .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                writeNewPointer(downloadUrl.toString(), title.getText().toString(), about.getText().toString(), link.getText().toString(), date.getText().toString());
                                Toast toast = Toast.makeText(AddGarageActivity.this, "Posting...", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
            } catch (Exception e) {
                Toast toast = Toast.makeText(AddGarageActivity.this, e.toString(), Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            writeNewPointer(mUpImageUri.toString(), title.getText().toString(), about.getText().toString(), link.getText().toString(), date.getText().toString());
            //HashUtil.showToast(AddProjectActivity.this, downloadUrl.toString());
            Toast toast = Toast.makeText(AddGarageActivity.this, "Posting...", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.project_choose_image:
                //changeLogo();
                HashUtil.chooseImage(AddGarageActivity.this);
                break;
            case R.id.project_add_member_button:
                Intent intent = new Intent(this, SearchActivity.class);
                String s = "ADD-MEMBERS@" + extrakey;
                intent.putExtra(SearchActivity.SRAECH, s);
                View searchMenuView = findViewById(R.id.project_add_member_button);
                Bundle options = ActivityOptions.makeSceneTransitionAnimation(this, searchMenuView,
                        getString(R.string.transition_search_back)).toBundle();
                startActivity(intent, options);
                break;
            case R.id.project_date_button:
                chooseDate();
                break;
            case R.id.delete_button:
                Toast toast = Toast.makeText(this, "Deleting", Toast.LENGTH_SHORT);
                toast.show();
                FirebaseUtils.GetDbRef().child("projects").child(extra).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        Toast toast = Toast.makeText(AddGarageActivity.this, "Deleted", Toast.LENGTH_SHORT);
                        toast.show();
                        FirebaseUtils.GetDbRef().child("project-members").child(extra).removeValue();
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
            //Toast toast = Toast.makeText(AddGarageActivity.this, mNotUpImageUri.toString(), Toast.LENGTH_SHORT);
            //toast.show();
            imageView.setImageURI(selectedImageUri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void writeNewPointer(String mProfilePicUrl, String title, String about, String link, String date) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = FirebaseUtils.GetDbRef().child("projects").push().getKey();
        Garage garage = new Garage(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail()), mProfilePicUrl, title, about, link, date, null, ServerValue.TIMESTAMP);
        Map<String, Object> pointerValues = garage.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        if (!extra.equals("ADD")) {
            childUpdates.put("/projects/" + extra, pointerValues);
        } else {
            childUpdates.put("/projects/" + key, pointerValues);
        }
        FirebaseUtils.GetDbRef().updateChildren(childUpdates);
    }


    private void setFonts() {

        ((TextView) findViewById(R.id.project_title_Text)).setTypeface(HashUtil.typefaceComfortaaRegular);
        ((TextView) findViewById(R.id.project_about_text)).setTypeface(HashUtil.typefaceComfortaaRegular);
        ((TextView) findViewById(R.id.project_link_text)).setTypeface(HashUtil.typefaceComfortaaRegular);
        ((TextView) findViewById(R.id.project_display_date_text)).setTypeface(HashUtil.GetTypeface());

        ((TextView) findViewById(R.id.project_date_text)).setTypeface(HashUtil.typefaceComfortaaBold);
        ((TextView) findViewById(R.id.project_basic_details_text)).setTypeface(HashUtil.typefaceComfortaaBold);
        ((TextView) findViewById(R.id.project_add_member_text)).setTypeface(HashUtil.typefaceComfortaaBold);

        ((EditText) findViewById(R.id.project_title_edittext)).setTypeface(HashUtil.GetTypeface());
        ((EditText) findViewById(R.id.project_about_edittext)).setTypeface(HashUtil.GetTypeface());
        ((EditText) findViewById(R.id.project_link_edittext)).setTypeface(HashUtil.GetTypeface());

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

    public void init() {
        //firebase
        mFirebaseStorage = FirebaseStorage.getInstance();
        mPicStorageReference = mFirebaseStorage.getReference().child("garage-image");

        //views
        mdate = findViewById(R.id.project_display_date_text);
        imageView = findViewById(R.id.project_choosed_image);
        timeLayout = findViewById(R.id.project_date_button);
        title = findViewById(R.id.project_title_edittext);
        about = findViewById(R.id.project_about_edittext);
        link = findViewById(R.id.project_link_edittext);
        date = findViewById(R.id.project_display_date_text);

    }

    public void sync() {

        FirebaseUtils.GetDbRef().child("projects").child(extra).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // ...
                Garage garage = dataSnapshot.getValue(Garage.class);
                if (garage != null) {
                    about.setText(garage.about);
                    title.setText(garage.title);
                    link.setText(garage.link);
                    date.setText(garage.startDate);
                    if (!TextUtils.isEmpty(garage.picUrl)) {
                        mUpImageUri = Uri.parse(garage.picUrl);
                        try {
                            Glide.with(imageView.getContext())
                                    .load(garage.picUrl)
                                    .into(imageView);
                        } catch (IllegalArgumentException e) {

                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

    }
}
