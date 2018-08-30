package hash.include.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import hash.include.R;
import hash.include.model.Event;
import hash.include.model.Register;
import hash.include.model.UserValues;
import hash.include.model.Winner;
import hash.include.transitions.FabTransform;
import hash.include.ui.RiseNumberTextView;
import hash.include.util.FirebaseUtils;
import hash.include.util.HashUtil;
import hash.include.viewholder.WinnerViewHolder;

public class EventDetailsActivity extends Activity {
    // View name of the header image. Used for activity scene transitions
    public static final String VIEW_NAME_HEADER_IMAGE = "detail:header:image";
    // View name of the header title. Used for activity scene transitions
    public static final String VIEW_NAME_HEADER_TITLE = "detail:header:title";
    public static final String EXTRA = "EXTRA";
    public RecyclerView winners;
    @BindView(R.id.icon_left)
    MaterialIconView back;
    @BindView(R.id.card_winners)
    CardView cardViewwinners;
    @BindView(R.id.event_details_register)
    CardView register;
    @BindView(R.id.pending_request_button)
    Button pendingRequest;
    @BindView(R.id.register_text)
    TextView registerText;

    @BindView(R.id.event_details_image)
    ImageView eventPic;
    @BindView(R.id.event_details_type)
    TextView type;
    @BindView(R.id.event_details_title)
    TextView title;
    @BindView(R.id.event_details_about)
    TextView about;
    @BindView(R.id.event_org)
    TextView org;
    @BindView(R.id.event_status)
    TextView status;
    @BindView(R.id.event_details_link)
    TextView link;
    @BindView(R.id.start_date)
    TextView startDate;
    @BindView(R.id.end_date)
    TextView endDate;
    @BindView(R.id.duration)
    TextView duration;
    @BindView(R.id.event_details_total_reg)
    RiseNumberTextView totalReg;
    @BindView(R.id.event_details_total_part)
    RiseNumberTextView totalPart;
    Event event = null;
    private String extra;
    private FirebaseRecyclerAdapter<Winner, WinnerViewHolder> mAdapter;
    private LinearLayoutManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        ViewCompat.setTransitionName(findViewById(R.id.event_details_image), VIEW_NAME_HEADER_IMAGE);
        ViewCompat.setTransitionName(findViewById(R.id.event_details_title), VIEW_NAME_HEADER_TITLE);
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
                register();
            }
        });
        checkRegistration();
        setUpMember();
        pendingRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventDetailsActivity.this, PendingUserActivity.class);
                intent.putExtra(PendingUserActivity.EXTRA, "EVENT@" + extra);
                startActivity(intent);
            }
        });
    }

    public void sync() {
        FirebaseUtils.GetDbRef().child("events").child(extra).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // ...
                try {
                    event = dataSnapshot.getValue(Event.class);
                    if (event != null) {
                        type.setText(event.eventType);
                        if (event.eventType.equals("CHALLENGE")) {
                            register.setVisibility(View.GONE);
                        }
                        title.setText(event.title);
                        about.setText(event.about);
                        org.setText(event.org);
                        status.setText("Online");
                        checkStatus(event.startDate, event.endDate);
                        link.setText(event.link);
                        String[] parts = event.startDate.split("-");
                        String sdate = getMonth(Integer.parseInt(parts[1])) + " " + parts[0] + ", " + parts[2];
                        startDate.setText(event.startDate);
                        endDate.setText(event.endDate);
                        duration.setText(event.duration);
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat df3 = new SimpleDateFormat("dd-MM-yyyy");
                        String currentDate = df3.format(c.getTime());

                        if (event.totalPart != null && event.totalReg != null) {
                            totalPart.withNumber(Integer.parseInt(event.totalPart)).setDuration(1500).start();
                            totalReg.withNumber(Integer.parseInt(event.totalReg)).setDuration(1500).start();
                        }
                        HashUtil.loadImageInImageView(eventPic, event.picUrl);

                        eventPic.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(v.getContext(), ImageViewerActivity.class);
                                intent.putExtra(ImageViewerActivity.EXTRA, event.title + "@" + event.eventType + "@" + event.picUrl);
                                ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(EventDetailsActivity.this,
                                        new Pair<View, String>(eventPic,
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
                        if (event.eventType.equals("CHALLENGE")) {
                            register.setVisibility(View.GONE);
                            pendingRequest.setVisibility(View.GONE);
                        } else {
                            pendingRequest.setVisibility(View.VISIBLE);
                            register.setVisibility(View.GONE);
                        }
                    } else if (uservalues.userType.equals("Gold")) {
                        if (event != null) {
                            if (event.uid.equals(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail()))) {
                                if (event.eventType.equals("CHALLENGE")) {
                                    register.setVisibility(View.GONE);
                                    pendingRequest.setVisibility(View.GONE);
                                } else {
                                    pendingRequest.setVisibility(View.VISIBLE);
                                    register.setVisibility(View.GONE);
                                }
                            } else {
                                if (event.eventType.equals("CHALLENGE")) {
                                    register.setVisibility(View.GONE);
                                    pendingRequest.setVisibility(View.GONE);
                                } else {
                                    register.setVisibility(View.VISIBLE);
                                    pendingRequest.setVisibility(View.GONE);
                                }
                            }
                        }
                    } else {
                        if (event.eventType.equals("CHALLENGE")) {
                            register.setVisibility(View.GONE);
                            pendingRequest.setVisibility(View.GONE);
                        } else {
                            register.setVisibility(View.VISIBLE);
                            pendingRequest.setVisibility(View.GONE);
                        }
                    }
                } else {
                    if (event.eventType.equals("CHALLENGE")) {
                        register.setVisibility(View.GONE);
                        pendingRequest.setVisibility(View.GONE);
                    } else {
                        register.setVisibility(View.VISIBLE);
                        pendingRequest.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }

    public String getMonth(int mon) {
        String[] month = {"", "Jan", "Feb", "Mar", "April", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return month[mon];
    }

    public void checkStatus(String s, String e) {
        if (cmpDate(e)) {
            register.setVisibility(View.GONE);
            status.setText("Event over");
            cardViewwinners.setVisibility(View.VISIBLE);
        }
    }

    public boolean cmpDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm a");
        Date strDate = new Date();
        try {
            strDate = sdf.parse(date);
        } catch (ParseException e) {

        }
        if (System.currentTimeMillis() > strDate.getTime()) {
            return true;
        } else {
            return false;
        }
    }

    public void setUpMember() {
        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(false);
        winners.setLayoutManager(mManager);
        winners.setItemAnimator(new DefaultItemAnimator());
        winners.setHasFixedSize(true);
        Query memberQuery = getQuery();
        mAdapter = new FirebaseRecyclerAdapter<Winner, WinnerViewHolder>(Winner.class, R.layout.item_member,
                WinnerViewHolder.class, memberQuery) {
            @Override
            protected void populateViewHolder(final WinnerViewHolder viewHolder, final Winner model, final int position) {
                final DatabaseReference memberRef = getRef(position);

                final String Key = memberRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(EventDetailsActivity.this, ProfileDetailsActivity.class);
                        intent.putExtra(ProfileDetailsActivity.EXTRA, Key);
                        FabTransform.addExtras(intent,
                                ContextCompat.getColor(EventDetailsActivity.this, R.color.colorPrimary), R.drawable.ic_add_dark);
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(EventDetailsActivity.this, viewHolder.picUrl,
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
        // All my posts
        return FirebaseUtils.GetDbRef().child("event-winners").child(extra).orderByChild("position");
    }
    public void init() {
        winners = findViewById(R.id.winners_recyclerview);
    }

    private void setFonts() {
        ((TextView) findViewById(R.id.winners_text)).setTypeface(HashUtil.typefaceLatoLight);
        ((TextView) findViewById(R.id.start_date_text)).setTypeface(HashUtil.typefaceLatoLight);
        ((TextView) findViewById(R.id.end_date_text)).setTypeface(HashUtil.typefaceLatoLight);
        ((TextView) findViewById(R.id.duration_text)).setTypeface(HashUtil.typefaceLatoLight);
        ((TextView) findViewById(R.id.total_part_text)).setTypeface(HashUtil.typefaceLatoLight);
        ((TextView) findViewById(R.id.Total_register_text)).setTypeface(HashUtil.typefaceLatoLight);
        ((TextView) findViewById(R.id.register_text)).setTypeface(HashUtil.typefaceLatoLight);
        totalReg.setTypeface(HashUtil.typefaceLatoHairline);
        totalPart.setTypeface(HashUtil.typefaceLatoHairline);
        type.setTypeface(HashUtil.typefaceLatoLight);
        title.setTypeface(HashUtil.typefaceLatoLight);
        about.setTypeface(HashUtil.typefaceLatoLight);
        org.setTypeface(HashUtil.typefaceLatoLight);
        status.setTypeface(HashUtil.typefaceLatoLight);
        link.setTypeface(HashUtil.typefaceLatoLight);
        startDate.setTypeface(HashUtil.typefaceLatoLight);
        endDate.setTypeface(HashUtil.typefaceLatoLight);
        duration.setTypeface(HashUtil.typefaceLatoLight);
        pendingRequest.setTypeface(HashUtil.typefaceLatoLight);
    }

    public void checkRegistration() {

        FirebaseUtils.GetDbRef().child("event-registration-details").child(extra).child(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Register register1 = dataSnapshot.getValue(Register.class);
                if (register1 != null) {
                    if (register1.status.equals("Pending")) {
                        registerText.setText(register1.status);
                    } else {
                        FirebaseUtils.GetDbRef().child("event-registration-details").child(extra).child(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail())).removeValue();
                        registerText.setText("Unregister");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }

    public void register() {

        FirebaseUtils.GetDbRef().child("event-registration-details").child(extra).child(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Register register1 = dataSnapshot.getValue(Register.class);
                if (register1 != null) {
                    if (register1.status.equals("Pending")) {
                        registerText.setText(register1.status);
                    } else {
                        FirebaseUtils.GetDbRef().child("event-registration-details").child(extra).child(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail())).removeValue();
                        registerText.setText("Unregister");
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
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        Register register1 = new Register(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail()), "Pending", ServerValue.TIMESTAMP);
        Map<String, Object> registerValues = register1.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        try {
            childUpdates.put("/event-registration-details/" + extra + "/" + HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail()), registerValues);
        } catch (NullPointerException e) {

        }
        mDatabaseRef.updateChildren(childUpdates);
        registerText.setText("Pending");
    }

}
