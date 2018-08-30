package hash.include.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import hash.include.R;
import hash.include.model.Register;
import hash.include.model.Reinforce;
import hash.include.model.Session;
import hash.include.model.UserValues;
import hash.include.transitions.FabTransform;
import hash.include.util.FirebaseUtils;
import hash.include.util.HashUtil;
import hash.include.viewholder.SessionViewHolder;

public class ReinforceDetailActivity extends AppCompatActivity {
    // View name of the header image. Used for activity scene transitions
    public static final String VIEW_NAME_HEADER_IMAGE = "detail:header:image";
    // View name of the header title. Used for activity scene transitions
    public static final String VIEW_NAME_HEADER_TITLE = "detail:header:title";
    public static final String EXTRA = "EXTRA";
    public RecyclerView session;
    @BindView(R.id.icon_left)
    MaterialIconView back;
    @BindView(R.id.reinforce_details_register)
    CardView register;
    @BindView(R.id.register_text)
    TextView registerText;

    @BindView(R.id.reinforce_details_image)
    ImageView reinforcePic;
    @BindView(R.id.chat_button)
    ImageButton buttonChat;

    @BindView(R.id.reinforce_details_title)
    TextView title;
    @BindView(R.id.reinforce_details_about)
    TextView about;
    @BindView(R.id.pending_request_button)
    Button pendingRequest;
    Reinforce reinforce;
    private String extra;
    private FirebaseRecyclerAdapter<Session, SessionViewHolder> mAdapter;
    private LinearLayoutManager mManager;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reinforce_detail);
        ViewCompat.setTransitionName(findViewById(R.id.reinforce_details_image), VIEW_NAME_HEADER_IMAGE);
        ViewCompat.setTransitionName(findViewById(R.id.reinforce_details_title), VIEW_NAME_HEADER_TITLE);
        ButterKnife.bind(this);
        init();
        extra = getIntent().getStringExtra(EXTRA);
        if (extra == null) {
            throw new IllegalArgumentException("Must pass EXTRA");
        }
        setFonts();
        sync();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });
        checkRegistration();
        setUpSession();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReinforceDetailActivity.this, AddSessionActivity.class);
                intent.putExtra(PendingUserActivity.EXTRA, "ADD@" + extra);
                FabTransform.addExtras(intent,
                        ContextCompat.getColor(ReinforceDetailActivity.this, R.color.colorPrimary), R.drawable.ic_add_dark);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ReinforceDetailActivity.this, fab,
                        getString(R.string.transition_designer_news_login));
                startActivity(intent, options.toBundle());
            }
        });
        buttonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReinforceDetailActivity.this, ChatActivity.class);
                intent.putExtra(ChatActivity.EXTRA, extra);
                FabTransform.addExtras(intent,
                        ContextCompat.getColor(ReinforceDetailActivity.this, R.color.tcolor), R.drawable.ic_chart_bubble);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ReinforceDetailActivity.this, buttonChat,
                        getString(R.string.transition_designer_news_login));
                startActivity(intent, options.toBundle());
            }
        });
        pendingRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReinforceDetailActivity.this, PendingUserActivity.class);
                intent.putExtra(AddPointerActivity.EXTRA, "REINFORCE@" + extra);
                startActivity(intent);
            }
        });
    }

    public void sync() {
        FirebaseUtils.GetDbRef().child("reinforce").child(extra).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // ...
                try {
                    reinforce = dataSnapshot.getValue(Reinforce.class);
                    if (reinforce != null) {
                        title.setText(reinforce.title);
                        about.setText(reinforce.about);
                        HashUtil.loadImageInImageView(reinforcePic, reinforce.picUrl);
                        reinforcePic.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(v.getContext(), ImageViewerActivity.class);
                                intent.putExtra(ImageViewerActivity.EXTRA, reinforce.title + "@" + reinforce.startDate + "@" + reinforce.picUrl);
                                ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(ReinforceDetailActivity.this,
                                        new Pair<View, String>(reinforcePic,
                                                ImageViewerActivity.VIEW_NAME_IMAGE));
                                startActivity(intent, activityOptions.toBundle());
                            }
                        });
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

        FirebaseUtils.GetDbRef().child("users-values").child(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserValues uservalues = dataSnapshot.getValue(UserValues.class);
                if (uservalues != null) {
                    if (uservalues.userType.equals("Super Admin") || uservalues.userType.equals("Admin")) {
                        fab.setVisibility(View.VISIBLE);
                        buttonChat.setVisibility(View.VISIBLE);
                        register.setVisibility(View.GONE);
                        pendingRequest.setVisibility(View.VISIBLE);
                    } else if (uservalues.userType.equals("Gold")) {
                        if (reinforce != null) {
                            if (reinforce.uid.equals(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail()))) {
                                pendingRequest.setVisibility(View.VISIBLE);
                                register.setVisibility(View.GONE);
                                buttonChat.setVisibility(View.VISIBLE);
                                fab.setVisibility(View.VISIBLE);
                            } else {
                                register.setVisibility(View.VISIBLE);
                                fab.setVisibility(View.GONE);
                                buttonChat.setVisibility(View.GONE);
                                pendingRequest.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        register.setVisibility(View.VISIBLE);
                        pendingRequest.setVisibility(View.GONE);
                        buttonChat.setVisibility(View.GONE);
                        fab.setVisibility(View.GONE);
                    }
                } else {
                    fab.setVisibility(View.GONE);
                    buttonChat.setVisibility(View.GONE);
                    register.setVisibility(View.VISIBLE);
                    pendingRequest.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }

    public void setUpSession() {
        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(false);
        session.setLayoutManager(mManager);
        session.setItemAnimator(new DefaultItemAnimator());
        session.setHasFixedSize(true);
        Query memberQuery = getQuery();
        mAdapter = new FirebaseRecyclerAdapter<Session, SessionViewHolder>(Session.class, R.layout.item_session,
                SessionViewHolder.class, memberQuery) {
            @Override
            protected void populateViewHolder(final SessionViewHolder viewHolder, final Session model, final int position) {
                final DatabaseReference memberRef = getRef(position);

                final String Key = memberRef.getKey();

                FirebaseUtils.GetDbRef().child("users-values").child(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail())).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserValues uservalues = dataSnapshot.getValue(UserValues.class);
                        if (uservalues != null) {
                            if (uservalues.userType.equals("Super Admin") || uservalues.userType.equals("Admin") || uservalues.userType.equals("Gold")) {
                                viewHolder.editButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(v.getContext(), AddSessionActivity.class);
                                        intent.putExtra(AddSessionActivity.EXTRA, extra + "@" + Key);
                                        FabTransform.addExtras(intent,
                                                ContextCompat.getColor(v.getContext(), R.color.tcolor), R.drawable.ic_edit_black_24dp);
                                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ReinforceDetailActivity.this, viewHolder.editButton,
                                                getString(R.string.transition_designer_news_login));
                                        startActivity(intent, options.toBundle());
                                    }
                                });
                                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(ReinforceDetailActivity.this, SessionDetailsActivity.class);
                                        intent.putExtra(SessionDetailsActivity.EXTRA, extra + "@" + Key);
                                        startActivity(intent);
                                    }
                                });
                            } else if (uservalues.userType.equals("Elite")) {
                                FirebaseUtils.GetDbRef().child("reinforce-registration-details").child(extra).child(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail())).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Register register1 = dataSnapshot.getValue(Register.class);
                                        if (register1 != null) {
                                            if (register1.status.equals("Accepted")) {
                                                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(ReinforceDetailActivity.this, SessionDetailsActivity.class);
                                                        intent.putExtra(SessionDetailsActivity.EXTRA, extra + "@" + Key);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else {
                                                viewHolder.itemView.setOnClickListener(null);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // ...
                                    }
                                });
                            } else {
                                viewHolder.itemView.setOnClickListener(null);
                                viewHolder.editButton.setOnClickListener(null);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // ...
                    }
                });

                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {

                    }
                });

            }
        };
        session.setAdapter(mAdapter);
    }

    public Query getQuery() {
        return FirebaseUtils.GetDbRef().child("session").child(extra).orderByChild("sessionNo");
    }

    public void init() {
        session = findViewById(R.id.session_recyclerview);
    }

    private void setFonts() {
        ((TextView) findViewById(R.id.session_text)).setTypeface(HashUtil.typefaceLatoLight);
        ((TextView) findViewById(R.id.register_text)).setTypeface(HashUtil.typefaceLatoLight);
        title.setTypeface(HashUtil.typefaceLatoRegular);
        about.setTypeface(HashUtil.typefaceLatoLight);
        pendingRequest.setTypeface(HashUtil.typefaceLatoLight);
    }

    public void checkRegistration() {
        FirebaseUtils.GetDbRef().child("reinforce-registration-details").child(extra).child(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Register register1 = dataSnapshot.getValue(Register.class);
                if (register1 != null) {
                    if (register1.status.equals("Pending")) {
                        registerText.setText(register1.status);
                        buttonChat.setVisibility(View.GONE);
                    } else {
                        registerText.setText("Unregister");
                        buttonChat.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }

    public void Register() {
        FirebaseUtils.GetDbRef().child("reinforce-registration-details").child(extra).child(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Register register1 = dataSnapshot.getValue(Register.class);
                if (register1 != null) {
                    if (register1.status.equals("Pending")) {
                        registerText.setText(register1.status);
                    } else {
                        FirebaseUtils.GetDbRef().child("reinforce-registration-details").child(extra).child(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail())).removeValue();
                        registerText.setText("Register");
                        buttonChat.setVisibility(View.GONE);
                    }
                } else {
                    registration();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }

    public void registration() {
        Register register1 = new Register(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail()), "Pending", ServerValue.TIMESTAMP);
        Map<String, Object> registerValues = register1.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        try {
            childUpdates.put("/reinforce-registration-details/" + extra + "/" + HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail()), registerValues);
        } catch (NullPointerException e) {

        }
        FirebaseUtils.GetDbRef().updateChildren(childUpdates);
        registerText.setText("Pending");
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
}
