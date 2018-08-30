package hash.include.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import hash.include.R;
import hash.include.model.Garage;
import hash.include.model.Member;
import hash.include.transitions.FabTransform;
import hash.include.ui.transitions.MorphTransform;
import hash.include.util.FirebaseUtils;
import hash.include.util.HashUtil;
import hash.include.viewholder.MemberViewHolder;

public class ProjectDetailsActivity extends Activity implements
        View.OnClickListener {

    public static final String EXTRA_FILE_KEY = "project_key";
    // View name of the header image. Used for activity scene transitions
    public static final String VIEW_NAME_HEADER_IMAGE = "detail:header:image";

    // View name of the header title. Used for activity scene transitions
    public static final String VIEW_NAME_HEADER_TITLE = "detail:header:title";
    boolean isDismissing = false;
    @BindView(R.id.container)
    ViewGroup container;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.about)
    TextView about;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.link_button)
    Button linkButton;
    @BindView(R.id.project_poster)
    ImageView poster;
    Garage garage;
    private DatabaseReference mProjectReference;
    private ValueEventListener mProjectListener;
    private String mProjectKey;
    // [END define_database_reference]
    private FirebaseRecyclerAdapter<Member, MemberViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_details);
        ButterKnife.bind(this);
            MorphTransform.setup(this, container,
                    ContextCompat.getColor(this, R.color.background_light),
                    getResources().getDimensionPixelSize(R.dimen.dimen_2dp));
        mRecycler = (RecyclerView)findViewById(R.id.member_recyclerview);
        mRecycler.setHasFixedSize(true);

        mProjectKey = getIntent().getStringExtra(EXTRA_FILE_KEY);
        if (mProjectKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_PROJECT_KEY");
        }
        mProjectReference = FirebaseDatabase.getInstance().getReference()
                .child("projects").child(mProjectKey);
        setFonts();
        setUpMember();
        ViewCompat.setTransitionName(poster, VIEW_NAME_HEADER_IMAGE);
        ViewCompat.setTransitionName(title, VIEW_NAME_HEADER_TITLE);
        linkButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.link_button:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(garage.link)));
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        ValueEventListener projectListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                garage = dataSnapshot.getValue(Garage.class);
                title.setText(garage.title);
                about.setText(garage.about);
                date.setText(garage.startDate);
                if (garage.link.isEmpty()) {
                    linkButton.setVisibility(View.GONE);
                } else {
                    linkButton.setVisibility(View.VISIBLE);
                }
                if (TextUtils.isEmpty(garage.picUrl)) {
                    poster.setVisibility(View.GONE);
                } else {
                    poster.setVisibility(View.VISIBLE);
                    Glide.with(poster.getContext())
                            .load(garage.picUrl)
                            .into(poster);
                    poster.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(), ImageViewerActivity.class);
                            intent.putExtra(ImageViewerActivity.EXTRA, garage.title + "@" + garage.startDate + "@" + garage.picUrl);
                            ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(ProjectDetailsActivity.this,
                                    new Pair<View, String>(poster,
                                            ImageViewerActivity.VIEW_NAME_IMAGE));
                            startActivity(intent, activityOptions.toBundle());


                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ProjectDetailsActivity", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mProjectReference.addValueEventListener(projectListener);
        mProjectListener = projectListener;

    }

    public void setUpMember() {

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
        mManager.setReverseLayout(false);
        //mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query memberQuery = getQuery();
        mAdapter = new FirebaseRecyclerAdapter<Member, MemberViewHolder>(Member.class, R.layout.item_member,
                MemberViewHolder.class, memberQuery) {
            @Override
            protected void populateViewHolder(final MemberViewHolder viewHolder, final Member model, final int position) {
                final DatabaseReference memberRef = getRef(position);

                // Set click listener for the whole post view
                final String Key = memberRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), ProfileDetailsActivity.class);
                        intent.putExtra(ProfileDetailsActivity.EXTRA, Key);
                        FabTransform.addExtras(intent,
                                ContextCompat.getColor(v.getContext(), R.color.colorPrimary), R.drawable.ic_add_dark);
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ProjectDetailsActivity.this, v,
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
        mRecycler.setAdapter(mAdapter);
    }

    public Query getQuery() {
        return FirebaseUtils.GetDbRef().child("project-members").child(mProjectKey);
    }

    @Override @SuppressLint("NewApi")
    public void onEnterAnimationComplete() {

    }

    @Override
    public void onBackPressed() {
        dismiss(null);
    }



    public void dismiss(View view) {
        isDismissing = true;
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
        finishAfterTransition();
    }

    private void setFonts() {

        ((TextView) findViewById(R.id.title)).setTypeface(HashUtil.typefaceComfortaaRegular);
        ((TextView) findViewById(R.id.about)).setTypeface(HashUtil.GetTypeface());
        ((TextView) findViewById(R.id.date)).setTypeface(HashUtil.GetTypeface());
        ((TextView) findViewById(R.id.link_button)).setTypeface(HashUtil.typefaceComfortaaRegular);
    }
    public void init() {

    }

    private boolean addTransitionListener() {
        final Transition transition = getWindow().getSharedElementEnterTransition();

        if (transition != null) {
            // There is an entering shared element transition so add a listener to it
            transition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    // As the transition has ended, we can now load the full-size image
                   // loadFullSizeImage();

                    // Make sure we remove ourselves as a listener
                    transition.removeListener(this);
                }

                @Override
                public void onTransitionStart(Transition transition) {
                    // No-op
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                    // Make sure we remove ourselves as a listener
                    transition.removeListener(this);
                }

                @Override
                public void onTransitionPause(Transition transition) {
                    // No-op
                }

                @Override
                public void onTransitionResume(Transition transition) {
                    // No-op
                }
            });
            return true;
        }

        return false;
    }

}
