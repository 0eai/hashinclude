package hash.include.activity;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jsibbold.zoomage.ZoomageView;

import butterknife.ButterKnife;
import hash.include.R;
import hash.include.util.HashUtil;

public class ImageViewerActivity extends AppCompatActivity {

    public static final String EXTRA = "EXTRA";
    public static final String VIEW_NAME_IMAGE = "detail:header:image";

    private TextView activityTitle;
    private TextView activitySubTitle;
    private String extra;
    private String[] string;
    private ZoomageView zoomageView;
    View decorView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        ButterKnife.bind(this);
        ViewCompat.setTransitionName(findViewById(R.id.myZoomageView), VIEW_NAME_IMAGE);
        zoomageView = (ZoomageView) findViewById(R.id.myZoomageView);
        extra = getIntent().getStringExtra(EXTRA);
        if (extra == null) {
            throw new IllegalArgumentException("Must pass KEY");
        } else {
            string = extra.split("@", 3);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (toolbar != null) {
            activityTitle = toolbar.findViewById(R.id.activity_title);
            activitySubTitle = toolbar.findViewById(R.id.activity_sub_title);
            activityTitle.setTypeface(HashUtil.typefaceLatoLight);
            activitySubTitle.setTypeface(HashUtil.typefaceLatoLight);
            setSupportActionBar(toolbar);
            final ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        activityTitle.setText(string[0]);
        activitySubTitle.setText(string[1]);
        if (string[2].contains("googleusercontent")){
            string[2] = string[2].replace("96","512");
        }
        try {
            Glide.with(zoomageView.getContext())
                    .load(string[2])
                    .into(zoomageView);
        } catch (IllegalArgumentException e) {

        }
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
