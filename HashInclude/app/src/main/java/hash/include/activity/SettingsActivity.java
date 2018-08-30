package hash.include.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
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
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import butterknife.ButterKnife;
import hash.include.R;
import hash.include.model.UserValues;
import hash.include.transitions.FabTransform;
import hash.include.util.FirebaseUtils;
import hash.include.util.HashUtil;
import hash.include.viewholder.TeamViewHolder;

public class SettingsActivity extends AppCompatActivity {

    RecyclerView teamMembers;
    TextView textTeamMember;
    ImageButton buttonAddTeamMember;
    private TextView activityTitle;
    private LinearLayoutManager mManager;
    private Dialog userTypeDialog;

    private FirebaseRecyclerAdapter<UserValues, TeamViewHolder> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (toolbar != null) {
            activityTitle = toolbar.findViewById(R.id.activity_title);
            activityTitle.setTypeface(HashUtil.getInstance().typefaceComfortaaBold);
            setSupportActionBar(toolbar);

            final ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        teamMembers = findViewById(R.id.settings_member_recyclerview);
        textTeamMember = findViewById(R.id.settings_team_text);
        buttonAddTeamMember = findViewById(R.id.settings_team_button);

        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        teamMembers.setLayoutManager(mManager);
        teamMembers.setItemAnimator(new DefaultItemAnimator());
        teamMembers.setHasFixedSize(true);
        setUpMember();
    }

    public void setUpMember() {
        Query memberQuery = getQuery();
        mAdapter = new FirebaseRecyclerAdapter<UserValues, TeamViewHolder>(UserValues.class, R.layout.item_team,
                TeamViewHolder.class, memberQuery) {
            @Override
            protected void populateViewHolder(final TeamViewHolder viewHolder, final UserValues model, final int position) {
                final DatabaseReference memberRef = getRef(position);

                final String Key = memberRef.getKey();

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SettingsActivity.this, ProfileDetailsActivity.class);
                        intent.putExtra(ProfileDetailsActivity.EXTRA, Key);
                        FabTransform.addExtras(intent,
                                ContextCompat.getColor(SettingsActivity.this, R.color.colorPrimary), R.drawable.ic_add_dark);
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SettingsActivity.this, viewHolder.picUrl,
                                getString(R.string.transition_designer_news_login));
                        startActivity(intent, options.toBundle());
                    }
                });
                viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Context context = v.getContext();
                        userTypeDialog = new Dialog(context, R.style.ThemeDialogCustom);
                        userTypeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        userTypeDialog.setContentView(R.layout.user_type_dialog);
                        userTypeDialog.setCancelable(true);
                        TextView title = userTypeDialog.findViewById(R.id.dialog_card_title);
                        title.setTypeface(HashUtil.GetTypeface());
                        final RadioButton r1 = userTypeDialog.findViewById(R.id.super_admin_radio);
                        r1.setTypeface(HashUtil.GetTypeface());
                        final RadioButton r2 = userTypeDialog.findViewById(R.id.admin_radio);
                        r2.setTypeface(HashUtil.GetTypeface());
                        final RadioButton r3 = userTypeDialog.findViewById(R.id.gold_radio);
                        r3.setTypeface(HashUtil.GetTypeface());
                        final RadioButton r4 = userTypeDialog.findViewById(R.id.elite_radio);
                        r4.setTypeface(HashUtil.GetTypeface());
                        final RadioButton r5 = userTypeDialog.findViewById(R.id.user_radio);
                        r5.setTypeface(HashUtil.GetTypeface());
                        Button cancelButton = userTypeDialog.findViewById(R.id.dialog_cancel);
                        cancelButton.setTypeface(HashUtil.GetTypeface());
                        Button okButton = userTypeDialog.findViewById(R.id.dialog_ok);
                        okButton.setTypeface(HashUtil.GetTypeface());
                        cancelButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                userTypeDialog.dismiss();
                            }
                        });
                        okButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (r1.isChecked()) {
                                    FirebaseUtils.GetDbRef().child("users-values").child(Key).child("userType").setValue("Super Admin");
                                    Toast.makeText(v.getContext(), "Super Admin", Toast.LENGTH_SHORT).show();
                                } else if (r2.isChecked()) {
                                    FirebaseUtils.GetDbRef().child("users-values").child(Key).child("userType").setValue("Admin");
                                    Toast.makeText(v.getContext(), "Admin", Toast.LENGTH_SHORT).show();
                                } else if (r3.isChecked()) {
                                    FirebaseUtils.GetDbRef().child("users-values").child(Key).child("userType").setValue("Gold");
                                    Toast.makeText(v.getContext(), "Gold", Toast.LENGTH_SHORT).show();
                                } else if (r4.isChecked()) {
                                    FirebaseUtils.GetDbRef().child("users-values").child(Key).child("userType").setValue("Elite");
                                    Toast.makeText(v.getContext(), "Elite", Toast.LENGTH_SHORT).show();
                                } else {
                                    FirebaseUtils.GetDbRef().child("users-values").child(Key).child("userType").setValue("User");
                                    Toast.makeText(v.getContext(), "User", Toast.LENGTH_SHORT).show();
                                }
                                userTypeDialog.dismiss();
                            }
                        });
                        userTypeDialog.show();
                        //Toast.makeText(v.getContext(), "Quid Copied", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {

                    }
                });

            }
        };
        teamMembers.setAdapter(mAdapter);
    }

    public Query getQuery() {
       return FirebaseUtils.GetDbRef().child("users-values").orderByChild("userType").limitToFirst(50);
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
