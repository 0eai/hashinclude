package hash.include.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import hash.include.R;
import hash.include.model.User;
import hash.include.model.UserValues;
import hash.include.util.FirebaseUtils;
import hash.include.util.HashUtil;

public class SigninActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    //google sign in
    private static final String TAG = "SigninActivity";
    private static final int RC_SIGN_IN = 9001;
    FirebaseUser user;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // Button listeners
        findViewById(R.id.google_sign_in_button).setOnClickListener(this);
        findViewById(R.id.google_sign_in_button).setVisibility(View.GONE);
        findViewById(R.id.google_sign_in_button).setElevation(0);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                findViewById(R.id.google_sign_in_button).setEnabled(true);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            user = mAuth.getCurrentUser();
                            try {
                            mDatabase.child("users").child(HashUtil.usernameFromEmail(user.getEmail())).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User u = dataSnapshot.getValue(User.class);
                                    if (u != null) {
                                        FirebaseUtils.subscribeToTopic("pointer");
                                        FirebaseUtils.subscribeToTopic("event");
                                        FirebaseUtils.subscribeToTopic("message");
                                        FirebaseUtils.subscribeToTopic("session");

                                        presentActivity();
                                    } else {
                                        FirebaseUtils.writeNewUser(HashUtil.usernameFromEmail(user.getEmail()), user.getDisplayName(), user.getEmail(), user.getPhotoUrl().toString());
                                        FirebaseUtils.writeNewUserValues(HashUtil.usernameFromEmail(user.getEmail()), 0);
                                        FirebaseUtils.subscribeToTopic("pointer");
                                        FirebaseUtils.subscribeToTopic("event");
                                        FirebaseUtils.subscribeToTopic("message");
                                        FirebaseUtils.subscribeToTopic("session");
                                        presentActivity();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // ...
                                    findViewById(R.id.google_sign_in_button).setEnabled(true);
                                }
                            });
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SigninActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            findViewById(R.id.google_sign_in_button).setEnabled(true);
                        }
                    }
                });
    }
    private void signIn() {
        findViewById(R.id.google_sign_in_button).setEnabled(false);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        findViewById(R.id.google_sign_in_button).setEnabled(false);
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.google_sign_in_button) {
            signIn();
        }
    }

    public void presentActivity() {
        Intent intent = new Intent(this, HashActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            presentActivity();
        } else {
            findViewById(R.id.google_sign_in_button).setVisibility(View.VISIBLE);
            FirebaseUtils.init();
        }
        super.onStart();
    }
}
