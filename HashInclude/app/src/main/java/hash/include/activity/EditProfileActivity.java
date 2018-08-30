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
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.TransitionManager;
import android.util.Patterns;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import hash.include.R;
import hash.include.model.User;
import hash.include.model.UserDetails;
import hash.include.transitions.FabTransform;
import hash.include.ui.transitions.MorphTransform;
import hash.include.util.FirebaseUtils;
import hash.include.util.HashUtil;

public class EditProfileActivity extends AppCompatActivity implements
        View.OnClickListener {
    public static final String EXTRA = "EXTRA";
    private static final int RC_PHOTO_PICKER = 2;

    public ImageView imageView;
    public LinearLayout dobLayout;
    boolean isDismissing = false;
    @BindView(R.id.container)
    ViewGroup container;
    Uri mCropImageUri;
    EditText name;
    EditText email;
    EditText phone;
    EditText about;
    EditText regno;
    EditText github;
    EditText hackerearth;
    EditText twitter;
    EditText facebook;
    TextView dob;
    TextView lastUpdate;
    Button deleteAccount;
    private StorageReference mPicStorageReference;
    private FirebaseStorage mFirebaseStorage;
    private TextView activityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
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
        SimpleDateFormat df3 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss a");
        String formattedDate3 = df3.format(c.getTime());
        dob.setText(formattedDate3);
        findViewById(R.id.project_choose_image).setOnClickListener(this);
        dobLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseDate();
            }
        });

        sync();
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteUser(FirebaseUtils.GetAuth().getCurrentUser());
            }
        });

    }

    public void sync() {

        FirebaseUtils.loadUserInUserViewsImg(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail()), name,email,imageView);

        FirebaseUtils.GetDbRef().child("user-details").child(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // ...
                UserDetails user = dataSnapshot.getValue(UserDetails.class);
                if (user != null) {
                    phone.setText(user.phone);
                    about.setText(user.about);
                    regno.setText(user.regNo);
                    dob.setText(user.dob);
                    github.setText(user.githubLink);
                    hackerearth.setText(user.hackerearthLink);
                    twitter.setText(user.twitterLink);
                    facebook.setText(user.facebookLink);
                     //Map<String, String> timeStamp =user.timeStamp;
                    //SimpleDateFormat sfd = new SimpleDateFormat();
                    //lastUpdate.setText(sfd.format(new Date(user.timeStamp)));

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_publish) {
            updateProfile();
            Toast toast = Toast.makeText(this, "Publishing", Toast.LENGTH_SHORT);
            toast.show();
            return true;
        }
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateProfile() {
        final FirebaseUser user = FirebaseUtils.GetCurrentUser();
        if (validateForm()) {
            if (!user.getDisplayName().equals(name.getText().toString())) {
                updateName(user);
            }
            if (!user.getEmail().equals(email.getText().toString())) {
                updateEmail(user);
            }
            if (mCropImageUri != null) {
                if (!user.getPhotoUrl().equals(mCropImageUri)) {
                    updatePhoto(user);
                }
            }
            updateUserDetails(phone.getText().toString(), about.getText().toString(), regno.getText().toString(), dob.getText().toString(), github.getText().toString(), hackerearth.getText().toString(), twitter.getText().toString(), facebook.getText().toString());
        }
    }

    private boolean validateForm() {
        if (TextUtils.isEmpty(name.getText().toString())) {
            name.setError("Required.");
            return false;
        } else if (TextUtils.isEmpty(email.getText().toString())) {
            email.setError("Required.");
            return false;
        }else if (!TextUtils.isEmpty(github.getText().toString()) && !isValidUrl(github.getText().toString())) {
            github.setError("Not valid URL.");
            github.requestFocus();
            return false;
        }else if (!TextUtils.isEmpty(hackerearth.getText().toString()) && !isValidUrl(hackerearth.getText().toString())) {
            hackerearth.setError("Not valid URL.");
            hackerearth.requestFocus();
            return false;
        }else if (!TextUtils.isEmpty(twitter.getText().toString()) && !isValidUrl(twitter.getText().toString())) {
            twitter.setError("Not valid URL.");
            twitter.requestFocus();
            return false;
        }else if (!TextUtils.isEmpty(facebook.getText().toString()) && !isValidUrl(facebook.getText().toString())) {
            facebook.setError("Not valid URL.");
            facebook.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.project_choose_image:
                //changeLogo();
                HashUtil.chooseImage(EditProfileActivity.this);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            mCropImageUri = selectedImageUri;
            imageView.setImageURI(selectedImageUri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void updateName(FirebaseUser user) {

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name.getText().toString())
                .build();
        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    updateUser( user.getDisplayName(), user.getEmail(), user.getPhotoUrl().toString());
                } else {
                    //mTextViewProfile.setTextColor(Color.RED);
                    //mTextViewProfile.setText(task.getException().getMessage());
                }
            }
        });
    }

    private void updatePhoto(FirebaseUser user) {
        StorageReference photoRef = mPicStorageReference.child(mCropImageUri.getLastPathSegment());
        photoRef.putFile(mCropImageUri)
                .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(downloadUrl)
                                .build();
                        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    updateUser(user.getDisplayName(), user.getEmail(), user.getPhotoUrl().toString());
                                    //mTextViewProfile.setTextColor(Color.DKGRAY);
                                    //mTextViewProfile.setText(getString(R.string.updated, "User profile"));
                                } else {
                                    //mTextViewProfile.setTextColor(Color.RED);
                                    //mTextViewProfile.setText(task.getException().getMessage());
                                }
                            }
                        });
                    }
                });

    }

    private void updateEmail(FirebaseUser user) {

        user.updateEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    updateUser( user.getDisplayName(), user.getEmail(), user.getPhotoUrl().toString());
                } else {
                    //mTextViewProfile.setTextColor(Color.RED);
                    //mTextViewProfile.setText(task.getException().getMessage());
                }
            }
        });
    }

    private void deleteUser(FirebaseUser user) {

        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //mTextViewProfile.setTextColor(Color.DKGRAY);
                    //mTextViewProfile.setText("User account deleted.");
                    Intent intent = new Intent(EditProfileActivity.this, SigninActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    //mTextViewProfile.setTextColor(Color.RED);
                    //mTextViewProfile.setText(task.getException().getMessage());
                }
            }
        });
    }

    private void updateUser( String name, String email, String picUrl) {
        User user = new User(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail()),name, email, picUrl, ServerValue.TIMESTAMP);
        FirebaseUtils.GetDbRef().child("users").child(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail())).setValue(user);
    }

    private void updateUserDetails(String phone, String about, String regNo, String dob, String githubLink, String hackerearthLink, String twitterLink, String facebookLink) {

        UserDetails userDetails = new UserDetails(phone, about, regNo, dob, githubLink, hackerearthLink, twitterLink, facebookLink, ServerValue.TIMESTAMP);
        Map<String, Object> userDetailsValues = userDetails.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/user-details/" + HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail()), userDetailsValues);
        FirebaseUtils.GetDbRef().updateChildren(childUpdates);
    }


    private void setFonts() {

        ((TextView) findViewById(R.id.activity_title)).setTypeface(HashUtil.typefaceLatoLight);
        ((TextView) findViewById(R.id.profile_basic_details_text)).setTypeface(HashUtil.typefaceLatoRegular);
        ((TextView) findViewById(R.id.social_profile_details_text)).setTypeface(HashUtil.typefaceLatoRegular);

        ((TextView) findViewById(R.id.profile_name_Text)).setTypeface(HashUtil.GetTypeface());
        ((TextView) findViewById(R.id.profile_email_text)).setTypeface(HashUtil.typefaceLatoLight);
        ((TextView) findViewById(R.id.profile_phone_text)).setTypeface(HashUtil.typefaceLatoLight);
        ((TextView) findViewById(R.id.profile_about_text)).setTypeface(HashUtil.typefaceLatoLight);
        ((TextView) findViewById(R.id.profile_regno_text)).setTypeface(HashUtil.typefaceLatoLight);
        ((TextView) findViewById(R.id.dob_text)).setTypeface(HashUtil.typefaceLatoLight);
        ((TextView) findViewById(R.id.dob_display_date_text)).setTypeface(HashUtil.typefaceLatoLight);
        ((TextView) findViewById(R.id.github_link_Text)).setTypeface(HashUtil.typefaceLatoLight);
        ((TextView) findViewById(R.id.hackerearth_link_text)).setTypeface(HashUtil.typefaceLatoLight);
        ((TextView) findViewById(R.id.twitter_link_text)).setTypeface(HashUtil.typefaceLatoLight);
        ((TextView) findViewById(R.id.facebook_link_text)).setTypeface(HashUtil.typefaceLatoLight);
        ((TextView) findViewById(R.id.last_update_date_text)).setTypeface(HashUtil.typefaceLatoLight);


        ((EditText) findViewById(R.id.profile_name_edittext)).setTypeface(HashUtil.GetTypeface());
        ((EditText) findViewById(R.id.profile_email_edittext)).setTypeface(HashUtil.GetTypeface());
        ((EditText) findViewById(R.id.profile_phone_edittext)).setTypeface(HashUtil.GetTypeface());
        ((EditText) findViewById(R.id.profile_about_edittext)).setTypeface(HashUtil.GetTypeface());
        ((EditText) findViewById(R.id.profile_regno_edittext)).setTypeface(HashUtil.GetTypeface());
        ((EditText) findViewById(R.id.github_link_edittext)).setTypeface(HashUtil.GetTypeface());
        ((EditText) findViewById(R.id.hackerearth_link_edittext)).setTypeface(HashUtil.GetTypeface());
        ((EditText) findViewById(R.id.twitter_link_edittext)).setTypeface(HashUtil.GetTypeface());
        ((EditText) findViewById(R.id.facebook_link_edittext)).setTypeface(HashUtil.GetTypeface());

        ((Button) findViewById(R.id.delete_account_button)).setTypeface(HashUtil.GetTypeface());
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

                        dob.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    public void init() {
        //firebase
        mFirebaseStorage = FirebaseStorage.getInstance();
        mPicStorageReference = mFirebaseStorage.getReference().child("users-image");

        //views

        imageView = findViewById(R.id.project_choosed_image);
        dobLayout = findViewById(R.id.profile_date_button);

        name = findViewById(R.id.profile_name_edittext);
        email = findViewById(R.id.profile_email_edittext);
        phone = findViewById(R.id.profile_phone_edittext);
        about = findViewById(R.id.profile_about_edittext);
        regno = findViewById(R.id.profile_regno_edittext);
        github = findViewById(R.id.github_link_edittext);
        hackerearth = findViewById(R.id.hackerearth_link_edittext);
        twitter = findViewById(R.id.twitter_link_edittext);
        facebook = findViewById(R.id.facebook_link_edittext);

        dob = findViewById(R.id.dob_display_date_text);

        lastUpdate = findViewById(R.id.last_update_date_text);
        deleteAccount = findViewById(R.id.delete_account_button);

    }

    private boolean isValidUrl(String url) {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        if(m.matches())
            return true;
        else
            return false;
    }
}
