package hash.include.util;

import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import hash.include.model.Member;
import hash.include.model.User;
import hash.include.model.UserDetails;
import hash.include.model.UserRegister;
import hash.include.model.UserValues;
import hash.include.model.Winner;

public class FirebaseUtils {
    private static DatabaseReference mDatabase;
    private static FirebaseAuth mAuth;
    private static FirebaseUser mUser;
    public static User user;
    public static UserValues userValues;
    public static UserDetails userDetails;

    public static void init() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUser = mAuth.getCurrentUser();
        //initUser();
        //initUserValues();
        //initUserDetails();
    }

    public static DatabaseReference GetDbRef() {
        if (mDatabase == null) {
            init();
            return mDatabase;
        } else {
            return mDatabase;
        }
    }

    public static FirebaseAuth GetAuth() {
        if (mAuth == null) {
            init();
            return mAuth;
        } else {
            return mAuth;
        }
    }


    public static FirebaseUser GetCurrentUser() {
        if (mUser == null) {
            init();
            return mUser;
        } else {
            return mUser;
        }
    }

    public static String GetCurrentUserUid() {
        if (mUser == null) {
            init();
            return HashUtil.usernameFromEmail(mUser.getEmail());
        } else {
            return HashUtil.usernameFromEmail(mUser.getEmail());
        }
    }

    public static String GetCurrentUserEmail() {
        if (mUser == null) {
            init();
            return mUser.getEmail();
        } else {
            return mUser.getEmail();
        }
    }

    public static void initUserValues() {
        if (mDatabase == null) {
            init();
        }
        if (mAuth == null) {
            init();
        }
        if (mUser == null) {
            init();
        }

        try {
            mDatabase.child("users-values").child(GetCurrentUserUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userValues = dataSnapshot.getValue(UserValues.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // ...
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static void initUser() {
        if (mDatabase == null) {
            init();
        }
        if (mAuth == null) {
            init();
        }
        if (mUser == null) {
            init();
        }
        try {
            mDatabase.child("users").child(GetCurrentUserUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // ...
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static void loadUserInUserViewsImg(String id, TextView usernameView, TextView emailView, ImageView picView) {
        if (mDatabase == null) {
            init();
        }
        if (id != null) {
            try {
                mDatabase.child("users").child(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            if (usernameView != null) {
                                usernameView.setText(user.username);
                            }
                            if (emailView != null) {
                                emailView.setText(user.email);
                            }
                            if (picView != null) {
                                HashUtil.loadImageInImageView(picView, user.picUrl);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // ...
                    }
                });
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
    public static void loadUserInUserViewsCir(String id, TextView usernameView, TextView emailView, CircleImageView picView) {
        if (mDatabase == null) {
            init();
        }
        if (id != null) {
            try {
                mDatabase.child("users").child(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            if (usernameView != null) {
                                usernameView.setText(user.username);
                            }
                            if (emailView != null) {
                                emailView.setText(user.email);
                            }
                            if (picView != null) {
                                HashUtil.loadImageInCircularImageView(picView, user.picUrl);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // ...
                    }
                });

            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
    public static void setEditForUser(String id, ImageButton editButton) {
        if (mDatabase == null) {
            init();
        }
        try {
            mDatabase.child("users-values").child(HashUtil.usernameFromEmail(GetCurrentUserEmail())).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserValues uservalues = dataSnapshot.getValue(UserValues.class);
                    if (uservalues != null) {
                        if (uservalues.userType.equals("Super Admin") || uservalues.userType.equals("Admin")) {
                            editButton.setVisibility(View.VISIBLE);
                        } else if (uservalues.userType.equals("Gold")) {
                            if (id.equals(HashUtil.usernameFromEmail(GetCurrentUserEmail()))) {
                                editButton.setVisibility(View.VISIBLE);
                            } else {
                                editButton.setVisibility(View.GONE);
                            }
                        } else {
                            editButton.setVisibility(View.GONE);
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // ...
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static void initUserDetails() {
        if (mDatabase == null) {
            init();
        }
        if (mAuth == null) {
            init();
        }
        if (mUser == null) {
            init();
        }
        try {
            mDatabase.child("user-details").child(GetCurrentUserUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userDetails = dataSnapshot.getValue(UserDetails.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // ...
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static void writeNewUser(String userId, String name,String email, String picUrl) {
        if (mDatabase == null) {
            init();
        }
        User user = new User(userId,name, email, picUrl, ServerValue.TIMESTAMP);
        mDatabase.child("users").child(userId).setValue(user);
    }

    public static void writeNewUserValues(String userId, int p) {
        if (mDatabase == null) {
            init();
        }
        UserValues userValues = new UserValues(userId, p, "User");
        mDatabase.child("users-values").child(userId).setValue(userValues);
    }

    public static void writeNewMember(User user, String title, String id) {
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        Member member = new Member(HashUtil.usernameFromEmail(user.email), title, ServerValue.TIMESTAMP);
        UserRegister userRegister = new UserRegister(HashUtil.usernameFromEmail(user.email), id);
        Map<String, Object> userValues = member.toMap();
        Map<String, Object> userRegValues = userRegister.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/project-members/" + id + "/" + HashUtil.usernameFromEmail(user.email), userValues);
        childUpdates.put("/users-projects/" + HashUtil.usernameFromEmail(user.email) + "/" + id, userRegValues);
        mDatabaseRef.updateChildren(childUpdates);
    }

    public static void writeNewWinner(User user, String position, String id) {
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        Winner winner = new Winner(HashUtil.usernameFromEmail(user.email), position, ServerValue.TIMESTAMP);
        Map<String, Object> userValues = winner.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/event-winners/" + id + "/" + HashUtil.usernameFromEmail(user.email), userValues);
        mDatabaseRef.updateChildren(childUpdates);
    }

    public static void signOut() {
        if (mAuth == null) {
            init();
        }
        mAuth.signOut();
    }

    public static void subscribeToTopic( String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic);
    }
}
