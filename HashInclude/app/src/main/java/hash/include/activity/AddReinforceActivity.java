/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hash.include.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import hash.include.R;
import hash.include.model.Reinforce;
import hash.include.transitions.FabTransform;
import hash.include.ui.transitions.MorphTransform;
import hash.include.util.FirebaseUtils;
import hash.include.util.HashUtil;

public class AddReinforceActivity extends AppCompatActivity implements
        View.OnClickListener{
    public static final String EXTRA = "EXTRA";
    private static final int RC_PHOTO_PICKER = 2;

    public TextView mdate;

    public EditText title;
    public EditText about;
    public ImageView imageView;
    public LinearLayout timeLayout;
    @BindView(R.id.delete_button)
    Button delButton;
    @BindView(R.id.container)
    ViewGroup container;
    Uri mNotUpImageUri = null;
    Uri mUpImageUri = null;
    private StorageReference mPicStorageReference;
    private FirebaseStorage mFirebaseStorage;
    private String extra;
    private TextView activityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reinforce);
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
        Toolbar toolbar = findViewById(R.id.toolbar);
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
        mdate.setText(formattedDate3);
        findViewById(R.id.reinforce_choose_image).setOnClickListener(this);
        timeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseDate();
            }
        });
        if (!extra.equals("ADD")) {
            delButton.setVisibility(View.VISIBLE);
            sync();
        }
        delButton.setOnClickListener(this);
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
            title.requestFocus();
            return;
        } else if (TextUtils.isEmpty(about.getText().toString())) {
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
                                writeNewReinforce(downloadUrl.toString(), title.getText().toString(), about.getText().toString(), mdate.getText().toString());
                                Toast toast = Toast.makeText(AddReinforceActivity.this, "Posting...", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
            } catch (Exception e) {
                Toast toast = Toast.makeText(AddReinforceActivity.this, e.toString(), Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            if (mUpImageUri != null) {
                writeNewReinforce(mUpImageUri.toString(), title.getText().toString(), about.getText().toString(), mdate.getText().toString());
                //HashUtil.showToast(AddProjectActivity.this, downloadUrl.toString());
                Toast toast = Toast.makeText(AddReinforceActivity.this, "Posting...", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                writeNewReinforce(null, title.getText().toString(), about.getText().toString(), mdate.getText().toString());
                //HashUtil.showToast(AddProjectActivity.this, downloadUrl.toString());
                Toast toast = Toast.makeText(AddReinforceActivity.this, "Posting...", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.reinforce_choose_image:
                //changeLogo();
                HashUtil.chooseImage(AddReinforceActivity.this);
                break;
            case R.id.delete_button:
                Toast toast = Toast.makeText(this, "Deleting", Toast.LENGTH_SHORT);
                toast.show();
                FirebaseUtils.GetDbRef().child("reinforce").child(extra).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        Toast toast = Toast.makeText(AddReinforceActivity.this, "Deleted", Toast.LENGTH_SHORT);
                        toast.show();
                        FirebaseUtils.GetDbRef().child("reinforce-registration-details").child(extra).removeValue();
                        FirebaseUtils.GetDbRef().child("session").child(extra).removeValue();
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
            //Toast toast = Toast.makeText(AddReinforceActivity.this, mNotUpImageUri.toString(), Toast.LENGTH_SHORT);
            //toast.show();
            imageView.setImageURI(selectedImageUri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void writeNewReinforce(String mProfilePicUrl, String title, String about, String date) {
        String key = FirebaseUtils.GetDbRef().child("reinforce").push().getKey();
        Reinforce reinforce = new Reinforce(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail()), mProfilePicUrl, title, about, date, ServerValue.TIMESTAMP);
        Map<String, Object> pointerValues = reinforce.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        if (!extra.equals("ADD")) {
            childUpdates.put("/reinforce/" + extra, pointerValues);
        } else {
            childUpdates.put("/reinforce/" + key, pointerValues);
        }
        FirebaseUtils.GetDbRef().updateChildren(childUpdates);
    }

    private void setFonts() {

        ((TextView) findViewById(R.id.reinforce_basic_details_text)).setTypeface(HashUtil.typefaceLatoRegular);
        ((TextView) findViewById(R.id.reinforce_title_Text)).setTypeface(HashUtil.typefaceLatoRegular);
        ((TextView) findViewById(R.id.reinforce_about_text)).setTypeface(HashUtil.typefaceLatoRegular);
        ((TextView) findViewById(R.id.reinforce_date_text)).setTypeface(HashUtil.typefaceLatoRegular);

        ((TextView) findViewById(R.id.reinforce_display_date_text)).setTypeface(HashUtil.GetTypeface());
        //((TextView) findViewById(R.id.reinforce_add_member_text)).setTypeface(HashUtil.typefaceLatoRegular);

        ((EditText) findViewById(R.id.reinforce_title_edittext)).setTypeface(HashUtil.GetTypeface());
        ((EditText) findViewById(R.id.reinforce_about_edittext)).setTypeface(HashUtil.GetTypeface());

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
        mPicStorageReference = mFirebaseStorage.getReference().child("reinforce-image");

        //views
        mdate = findViewById(R.id.reinforce_display_date_text);
        imageView = findViewById(R.id.reinforce_choosed_image);
        timeLayout = findViewById(R.id.reinforce_date_button);

        title = findViewById(R.id.reinforce_title_edittext);
        about = findViewById(R.id.reinforce_about_edittext);
    }

    public void sync() {

        FirebaseUtils.GetDbRef().child("reinforce").child(extra).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // ...
                Reinforce reinforce = dataSnapshot.getValue(Reinforce.class);
                if (reinforce != null) {
                    about.setText(reinforce.about);
                    title.setText(reinforce.title);
                    mdate.setText(reinforce.startDate);
                    if (!TextUtils.isEmpty(reinforce.picUrl)) {
                        mUpImageUri = Uri.parse(reinforce.picUrl);
                        try {
                            Glide.with(imageView.getContext())
                                    .load(reinforce.picUrl)
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
