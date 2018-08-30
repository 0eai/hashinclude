package hash.include.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.steamcrafted.materialiconlib.MaterialIconView;

import butterknife.BindView;
import butterknife.ButterKnife;
import hash.include.R;
import hash.include.model.Register;
import hash.include.model.User;
import hash.include.model.UserDetails;
import hash.include.model.UserRegister;
import hash.include.model.UserValues;
import hash.include.util.FirebaseUtils;
import hash.include.util.HashUtil;
import hash.include.viewholder.UserEventViewHolder;
import hash.include.viewholder.UserGarageViewHolder;
import hash.include.viewholder.UserReinforceViewHolder;


public class ProfileDetailsActivity extends Activity implements
        View.OnClickListener {

    public static final String EXTRA = "EXTRA";

    @BindView(R.id.user_profile_github)
    ImageButton github;
    @BindView(R.id.user_profile_hackerearth)
    ImageButton hackerearth;
    @BindView(R.id.user_profile_facebook)
    ImageButton facebook;
    @BindView(R.id.user_profile_twitter)
    ImageButton twitter;

    @BindView(R.id.user_profile_pic)
    ImageView profilePic;

    @BindView(R.id.user_profile_name)
    TextView name;
    @BindView(R.id.user_profile_email)
    TextView email;
    @BindView(R.id.user_profile_points)
    TextView point;
    @BindView(R.id.user_profile_about)
    TextView about;

    @BindView(R.id.user_profile_event_recycler)
    RecyclerView recyclerViewEvent;
    @BindView(R.id.user_profile_project_recycler)
    RecyclerView recyclerViewProject;
    @BindView(R.id.user_profile_reinforce_recycler)
    RecyclerView recyclerViewReinforce;
    @BindView(R.id.icon_left)
    MaterialIconView materialIconView;
    UserDetails userDetails;
    private FirebaseRecyclerAdapter<UserRegister, UserGarageViewHolder> mGarageAdapter;
    private FirebaseRecyclerAdapter<UserRegister, UserEventViewHolder> mEventAdapter;
    private FirebaseRecyclerAdapter<UserRegister, UserReinforceViewHolder> mReinforceAdapter;
    private LinearLayoutManager mProjectManager;
    private LinearLayoutManager mReinforceManager;
    private LinearLayoutManager mEventManager;
    private String extra;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_details_activity);
        ButterKnife.bind(this);
        extra = getIntent().getStringExtra(EXTRA);
        if (extra == null) {
            throw new IllegalArgumentException("Must pass EXTRA");
        }
        setFonts();
        sync();
        twitter.setOnClickListener(this);
        facebook.setOnClickListener(this);
        github.setOnClickListener(this);
        hackerearth.setOnClickListener(this);
        //profilePic.setOnClickListener(this);
        materialIconView.setOnClickListener(this);

        recyclerViewEvent.setHasFixedSize(true);
        recyclerViewProject.setHasFixedSize(true);
        recyclerViewReinforce.setHasFixedSize(true);
        mEventManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
        mEventManager.setReverseLayout(false);
        mProjectManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
        mProjectManager.setReverseLayout(false);
        mReinforceManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
        mReinforceManager.setReverseLayout(false);
        recyclerViewReinforce.setLayoutManager(mReinforceManager);
        recyclerViewProject.setLayoutManager(mProjectManager);
        recyclerViewEvent.setLayoutManager(mEventManager);
        initForGarage();
        initForEvent();
        initForReinforce();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_profile_github:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(userDetails.githubLink)));
                break;
            case R.id.user_profile_hackerearth:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(userDetails.hackerearthLink)));
                break;
            case R.id.user_profile_facebook:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(userDetails.facebookLink)));
                break;
            case R.id.user_profile_twitter:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(userDetails.twitterLink)));
                break;
            case R.id.icon_left:
                onBackPressed();
                break;
        }
    }

    public void sync() {
        FirebaseUtils.loadUserInUserViewsImg(extra, name, email, profilePic);
        FirebaseUtils.GetDbRef().child("users").child(extra).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    profilePic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(), ImageViewerActivity.class);
                            intent.putExtra(ImageViewerActivity.EXTRA, user.username + "@" + user.email.replace('@', ' ') + "@" + user.picUrl);
                            ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(ProfileDetailsActivity.this,
                                    new Pair<View, String>(profilePic,
                                            ImageViewerActivity.VIEW_NAME_IMAGE));
                            startActivity(intent, activityOptions.toBundle());


                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseUtils.GetDbRef().child("user-details").child(extra).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userDetails = dataSnapshot.getValue(UserDetails.class);
                if (userDetails != null) {
                    if (userDetails.about.isEmpty()) {
                        findViewById(R.id.user_profile_about_text).setVisibility(View.GONE);
                        about.setVisibility(View.GONE);
                    } else {
                        about.setText(userDetails.about);
                        findViewById(R.id.user_profile_about_text).setVisibility(View.VISIBLE);
                        about.setVisibility(View.VISIBLE);
                    }
                    if (userDetails.facebookLink.isEmpty()) {
                        facebook.setVisibility(View.GONE);
                    } else {
                        facebook.setVisibility(View.VISIBLE);
                    }
                    if (userDetails.hackerearthLink.isEmpty()) {
                        hackerearth.setVisibility(View.GONE);
                    } else {
                        hackerearth.setVisibility(View.VISIBLE);
                    }
                    if (userDetails.twitterLink.isEmpty()) {
                        twitter.setVisibility(View.GONE);
                    } else {
                        twitter.setVisibility(View.VISIBLE);
                    }
                    if (userDetails.githubLink.isEmpty()) {
                        github.setVisibility(View.GONE);
                    } else {
                        github.setVisibility(View.VISIBLE);
                    }
                } else {
                    hackerearth.setVisibility(View.GONE);
                    twitter.setVisibility(View.GONE);
                    github.setVisibility(View.GONE);
                    facebook.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
        FirebaseUtils.GetDbRef().child("users-values").child(extra).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserValues uservalues = dataSnapshot.getValue(UserValues.class);
                if (uservalues != null) {
                    point.setText(uservalues.point + "");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }

    private void setFonts() {

        ((TextView) findViewById(R.id.user_profile_about_text)).setTypeface(HashUtil.typefaceLatoLight);
        ((TextView) findViewById(R.id.user_profile_reinforce_text)).setTypeface(HashUtil.typefaceLatoLight);
        ((TextView) findViewById(R.id.user_profile_project_text)).setTypeface(HashUtil.typefaceLatoLight);
        ((TextView) findViewById(R.id.user_profile_event_text)).setTypeface(HashUtil.typefaceLatoLight);
        email.setTypeface(HashUtil.typefaceLatoLight);
        name.setTypeface(HashUtil.typefaceLatoLight);
        about.setTypeface(HashUtil.typefaceLatoLight);
        point.setTypeface(HashUtil.typefaceLatoLight);
    }

    public void initForGarage() {
        Query postsQuery = searchQueryGarage();
        mGarageAdapter = new FirebaseRecyclerAdapter<UserRegister, UserGarageViewHolder>(UserRegister.class, R.layout.item_user_project,
                UserGarageViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final UserGarageViewHolder viewHolder, final UserRegister model, final int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String key = postRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), ProjectDetailsActivity.class);
                        intent.putExtra(ProjectDetailsActivity.EXTRA_FILE_KEY, key);

                        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(ProfileDetailsActivity.this,

                                new Pair<View, String>(v.findViewById(R.id.project_poster),
                                        ProjectDetailsActivity.VIEW_NAME_HEADER_IMAGE),
                                new Pair<View, String>(v.findViewById(R.id.title),
                                        ProjectDetailsActivity.VIEW_NAME_HEADER_TITLE));
                        startActivity(intent, activityOptions.toBundle());

                    }
                });
                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {

                    }
                });
            }
        };
        recyclerViewProject.setAdapter(mGarageAdapter);
    }

    public void initForReinforce() {
        Query postsQuery = searchQueryReinforce();
        mReinforceAdapter = new FirebaseRecyclerAdapter<UserRegister, UserReinforceViewHolder>(UserRegister.class, R.layout.item_user_reinforce,
                UserReinforceViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final UserReinforceViewHolder viewHolder, final UserRegister model, final int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String Key = postRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), ReinforceDetailActivity.class);
                        intent.putExtra(ReinforceDetailActivity.EXTRA, Key);
                        startActivity(intent);

                    }
                });

                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {

                    }
                });
            }
        };
        recyclerViewReinforce.setAdapter(mReinforceAdapter);
    }

    public void initForEvent() {
        Query postsQuery = searchQueryEvent();
        mEventAdapter = new FirebaseRecyclerAdapter<UserRegister, UserEventViewHolder>(UserRegister.class, R.layout.item_user_event,
                UserEventViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final UserEventViewHolder viewHolder, final UserRegister model, final int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String Key = postRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), EventDetailsActivity.class);
                        intent.putExtra(EventDetailsActivity.EXTRA, Key);
                        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(ProfileDetailsActivity.this,
                                new Pair<View, String>(v,
                                        EventDetailsActivity.VIEW_NAME_HEADER_IMAGE),
                                new Pair<View, String>(v,
                                        EventDetailsActivity.VIEW_NAME_HEADER_TITLE));
                        startActivity(intent, activityOptions.toBundle());

                    }
                });

                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {

                    }
                });
            }
        };
        recyclerViewEvent.setAdapter(mEventAdapter);
    }

    public Query searchQueryReinforce() {
        Query query1 = FirebaseUtils.GetDbRef().child("users-reinforces").child(extra).orderByPriority();
        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    recyclerViewReinforce.setVisibility(View.GONE);
                    findViewById(R.id.user_profile_reinforce_text).setVisibility(View.GONE);
                } else {
                    recyclerViewReinforce.setVisibility(View.VISIBLE);
                    findViewById(R.id.user_profile_reinforce_text).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
        return query1;
    }

    public Query searchQueryEvent() {
        Query query1 = FirebaseUtils.GetDbRef().child("users-events").child(extra).orderByPriority();
        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    recyclerViewEvent.setVisibility(View.GONE);
                    findViewById(R.id.user_profile_event_text).setVisibility(View.GONE);
                } else {
                    recyclerViewEvent.setVisibility(View.VISIBLE);
                    findViewById(R.id.user_profile_event_text).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
        return query1;
    }

    public Query searchQueryGarage() {
        Query query1 = FirebaseUtils.GetDbRef().child("users-projects").child(extra).orderByPriority();
        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    recyclerViewProject.setVisibility(View.GONE);
                    findViewById(R.id.user_profile_project_text).setVisibility(View.GONE);
                } else {
                    recyclerViewProject.setVisibility(View.VISIBLE);
                    findViewById(R.id.user_profile_project_text).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
        return query1;
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
