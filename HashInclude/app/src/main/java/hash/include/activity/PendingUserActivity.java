package hash.include.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import butterknife.BindView;
import butterknife.ButterKnife;
import hash.include.R;
import hash.include.model.Register;
import hash.include.model.UserRegister;
import hash.include.transitions.FabTransform;
import hash.include.util.FirebaseUtils;
import hash.include.util.HashUtil;
import hash.include.viewholder.PendingUsersViewHolder;


public class PendingUserActivity extends AppCompatActivity {
    public static final String EXTRA = "EXTRA";
    @BindView(R.id.user_recyclerview)
    RecyclerView members;
    String[] string;
    private TextView activityTitle;
    private LinearLayoutManager mManager;
    private String extra;
    private FirebaseRecyclerAdapter<Register, PendingUsersViewHolder> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_user);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        extra = getIntent().getStringExtra(EXTRA);
        if (extra == null) {
            throw new IllegalArgumentException("Must pass EXTRA");
        } else {
            string = extra.split("@", 2);
            //Toast.makeText(PendingUserActivity.this, extra + "\n" + string[0] + "\n" + string[1], Toast.LENGTH_LONG).show();

            if (!string[0].equals("EVENT")) {

            } else if (!string[0].equals("REINFORCE")) {

            }
        }
        if (toolbar != null) {
            activityTitle = toolbar.findViewById(R.id.activity_title);
            activityTitle.setTypeface(HashUtil.getInstance().typefaceComfortaaBold);
            setSupportActionBar(toolbar);

            final ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        members.setLayoutManager(mManager);
        members.setItemAnimator(new DefaultItemAnimator());
        members.setHasFixedSize(true);
        setUpMember();
    }

    public void setUpMember() {
        Query memberQuery = getQuery();
        if (memberQuery == null) {
            Toast.makeText(PendingUserActivity.this, "No pending request", Toast.LENGTH_LONG).show();
        }
        mAdapter = new FirebaseRecyclerAdapter<Register, PendingUsersViewHolder>(Register.class, R.layout.item_pending_user,
                PendingUsersViewHolder.class, memberQuery) {
            @Override
            protected void populateViewHolder(final PendingUsersViewHolder viewHolder, final Register model, final int position) {
                final DatabaseReference memberRef = getRef(position);

                final String Key = memberRef.getKey();
                viewHolder.acceptButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (string[0].equals("EVENT")) {
                            FirebaseUtils.GetDbRef().child("event-registration-details").child(string[1]).child(Key).child("status").setValue("Accepted");
                            UserRegister userRegister = new UserRegister(Key, string[1]);
                            FirebaseUtils.GetDbRef().child("users-events").child(Key).child(string[1]).setValue(userRegister);
                        } else {
                            FirebaseUtils.GetDbRef().child("reinforce-registration-details").child(string[1]).child(Key).child("status").setValue("Accepted");
                            UserRegister userRegister = new UserRegister(Key, string[1]);
                            FirebaseUtils.GetDbRef().child("users-reinforces").child(Key).child(string[1]).setValue(userRegister);
                        }
                    }
                });
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(PendingUserActivity.this, ProfileDetailsActivity.class);
                        intent.putExtra(ProfileDetailsActivity.EXTRA, Key);
                        FabTransform.addExtras(intent,
                                ContextCompat.getColor(PendingUserActivity.this, R.color.colorPrimary), R.drawable.ic_add_dark);
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(PendingUserActivity.this, viewHolder.picUrl,
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
        Query query;
        if (string[0].equals("EVENT")) {
            query = FirebaseUtils.GetDbRef().child("event-registration-details").child(string[1]).orderByChild("status").equalTo("Pending");
        } else {
            query = FirebaseUtils.GetDbRef().child("reinforce-registration-details").child(string[1]).orderByChild("status").equalTo("Pending");
        }
        return query;
    }

    @Override
    public void onBackPressed() {
        dismiss(null);
    }

    public void dismiss(View view) {
        setResult(Activity.RESULT_CANCELED);
        finishAfterTransition();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
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
