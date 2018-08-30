package hash.include.activity;


import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import hash.include.R;
import hash.include.fragments.EventFragment;
import hash.include.fragments.GarageFragment;
import hash.include.fragments.PointerFragment;
import hash.include.fragments.ReinforceFragment;
import hash.include.model.User;
import hash.include.model.UserValues;
import hash.include.transitions.FabTransform;
import hash.include.ui.RiseNumberTextView;
import hash.include.util.AnimUtils;
import hash.include.util.FirebaseUtils;
import hash.include.util.HashUtil;


public class HashActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FloatingActionButton fab;
    RiseNumberTextView point;
    RiseNumberTextView rank;
    TextView pointText;
    TextView rankText;
    TextView userName;
    TextView userEmail;

    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;

    private ImageButton updateProfile;
    private ActionBarDrawerToggle mDrawerToggle;

    private MaterialRippleLayout leaderboard;
    private MaterialRippleLayout library;
    private MaterialRippleLayout practice;
    private MaterialRippleLayout donate;
    private MaterialRippleLayout logout;
    private MaterialRippleLayout settings;
    private MaterialRippleLayout feedback;
    private MaterialRippleLayout about;
    private CircleImageView profileImage;
    private TextView activityTitleLude;
    private TextView activityTitleInc;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hash);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        try {
            if (mAuth.getCurrentUser() != null) {

            } else {
                Intent intent = new Intent(HashActivity.this, SigninActivity.class);
                startActivity(intent);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent(HashActivity.this, SigninActivity.class);
            startActivity(intent);
            finish();
        }
        FirebaseUtils.init();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        activityTitleLude = toolbar.findViewById(R.id.activity_title_lude);
        activityTitleInc = toolbar.findViewById(R.id.activity_title_inc);
        activityTitleInc.setAlpha(0f);
        activityTitleInc.setScaleX(0.8f);
        activityTitleInc.animate()
                .alpha(1f)
                .scaleX(1f)
                .setStartDelay(300)
                .setDuration(900)
                .setInterpolator(AnimUtils.getFastOutSlowInInterpolator(this));
        //toolbar title animation
        activityTitleLude.setAlpha(0f);
        activityTitleLude.setScaleX(0.8f);

        activityTitleLude.animate()
                .alpha(1f)
                .scaleX(1f)
                .setStartDelay(300)
                .setDuration(900)
                .setInterpolator(AnimUtils.getFastOutSlowInInterpolator(this));
        //toolbar title animation end

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            final ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                VectorDrawableCompat indicator
                        = VectorDrawableCompat.create(getResources(), R.drawable.ic_logo_app, getTheme());
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setDisplayUseLogoEnabled(true);
                actionBar.setHomeButtonEnabled(true);
                actionBar.setHomeAsUpIndicator(indicator);
            }
        }


        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);
        pointText = findViewById(R.id.user_point_text);
        point = findViewById(R.id.user_point);

        rankText = findViewById(R.id.user_rank_text);
        rank = findViewById(R.id.user_rank);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tabLayout.getSelectedTabPosition() == 0) {
                    Intent intent = new Intent(HashActivity.this, AddPointerActivity.class);
                    intent.putExtra(AddPointerActivity.EXTRA, "ADD");
                    FabTransform.addExtras(intent,
                            ContextCompat.getColor(HashActivity.this, R.color.colorPrimary), R.drawable.ic_add_dark);
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(HashActivity.this, fab,
                            getString(R.string.transition_designer_news_login));
                    //startActivityForResult(intent, options.toBundle());
                    startActivity(intent, options.toBundle());
                } else if (tabLayout.getSelectedTabPosition() == 1) {
                    Intent intent = new Intent(HashActivity.this, AddReinforceActivity.class);
                    intent.putExtra(AddReinforceActivity.EXTRA, "ADD");
                    FabTransform.addExtras(intent,
                            ContextCompat.getColor(HashActivity.this, R.color.colorPrimary), R.drawable.ic_add_dark);
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(HashActivity.this, fab,
                            getString(R.string.transition_designer_news_login));
                    //startActivityForResult(intent, options.toBundle());
                    startActivity(intent, options.toBundle());
                } else if (tabLayout.getSelectedTabPosition() == 2) {
                    Intent intent = new Intent(HashActivity.this, AddEventActivity.class);
                    intent.putExtra(AddEventActivity.EXTRA, "ADD");
                    FabTransform.addExtras(intent,
                            ContextCompat.getColor(HashActivity.this, R.color.colorPrimary), R.drawable.ic_add_dark);
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(HashActivity.this, fab,
                            getString(R.string.transition_designer_news_login));
                    //startActivityForResult(intent, options.toBundle());
                    startActivity(intent, options.toBundle());
                } else if (tabLayout.getSelectedTabPosition() == 3) {
                    Intent intent = new Intent(HashActivity.this, AddGarageActivity.class);
                    intent.putExtra(AddGarageActivity.EXTRA, "ADD");
                    FabTransform.addExtras(intent,
                            ContextCompat.getColor(HashActivity.this, R.color.colorPrimary), R.drawable.ic_add_dark);
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(HashActivity.this, fab,
                            getString(R.string.transition_designer_news_login));
                    //startActivityForResult(intent, options.toBundle());
                    startActivity(intent, options.toBundle());
                }
            }
        });
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        leaderboard = (MaterialRippleLayout) mDrawer.findViewById(R.id.leader_layout);
        library = (MaterialRippleLayout) mDrawer.findViewById(R.id.library_layout);
        practice = (MaterialRippleLayout) mDrawer.findViewById(R.id.pratice_layout);
        donate = (MaterialRippleLayout) mDrawer.findViewById(R.id.donate_layout);
        settings = (MaterialRippleLayout) mDrawer.findViewById(R.id.settings_layout);
        logout = (MaterialRippleLayout) mDrawer.findViewById(R.id.logout_layout);
        feedback = (MaterialRippleLayout) mDrawer.findViewById(R.id.feedback_layout);
        about = (MaterialRippleLayout) mDrawer.findViewById(R.id.about_layout);
        updateProfile = (ImageButton) mDrawer.findViewById(R.id.update_profile_button);

        donate.setVisibility(View.GONE);
        profileImage = mDrawer.findViewById(R.id.profile_image);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HashActivity.this, ProfileDetailsActivity.class);
                intent.putExtra(ProfileDetailsActivity.EXTRA, HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail()));
                FabTransform.addExtras(intent,
                        ContextCompat.getColor(HashActivity.this, R.color.colorPrimary), android.R.drawable.ic_input_add);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(HashActivity.this, v,
                        getString(R.string.transition_fab));
                startActivity(intent, options.toBundle());
            }
        });

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        setupTabIcons();
        TabLayout.Tab tab = tabLayout.getTabAt(0);
        tab.select();
        int tabIconColor = ContextCompat.getColor(HashActivity.this, R.color.white);
        tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View tab1 = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab1.getLayoutParams();
            p.setMargins(4, 4, 4, 4);
            tab1.requestLayout();
        }
        tabLayout.addOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {

                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);
                        int tabIconColor = ContextCompat.getColor(HashActivity.this, R.color.white);
                        try {
                            tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        super.onTabUnselected(tab);
                        int tabIconColor = ContextCompat.getColor(HashActivity.this, R.color.tcolor);
                        try {
                            tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                        super.onTabReselected(tab);
                    }
                });
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, 0, 0) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawer.setDrawerListener(mDrawerToggle);
        setFonts();
        setListeners();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                } else {
                    Intent intent = new Intent(HashActivity.this, SigninActivity.class);
                    startActivity(intent);
                    finish();

                }
            }
        };
        sync();
    }

    public void sync() {

        try {
            FirebaseUtils.GetDbRef().child("users").child(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail())).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // ...
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        userName.setText(user.username);
                        userEmail.setText(user.email);
                        HashUtil.loadImageInCircularImageView(profileImage, user.picUrl);
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
                        if (uservalues.point != 0) {
                            point.withNumber(uservalues.point).setDuration(uservalues.point * 10).start();
                        } else {
                            point.withNumber(uservalues.point).setDuration(0).start();
                        }
                        if (uservalues.rank != 0) {
                            rank.withNumber(uservalues.rank).setDuration(uservalues.rank * 10).start();
                        }
                        if (uservalues.userType.equals("Super Admin") || uservalues.userType.equals("Admin") || uservalues.userType.equals("Gold")) {
                            fab.setVisibility(View.VISIBLE);
                            settings.setVisibility(View.VISIBLE);
                            library.setVisibility(View.VISIBLE);
                        } else {
                            fab.setVisibility(View.GONE);
                            settings.setVisibility(View.GONE);
                            library.setVisibility(View.GONE);
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

    @Override
    public void onStart() {
        if (mAuthListener != null) {
            FirebaseUtils.GetAuth().addAuthStateListener(mAuthListener);
        }
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseUtils.GetAuth().removeAuthStateListener(mAuthListener);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new PointerFragment(), "POINTER");
        adapter.addFrag(new ReinforceFragment(), "REINFORCE");
        adapter.addFrag(new EventFragment(), "EVENT");
        adapter.addFrag(new GarageFragment(), "GARAGE");
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        int[] tabIcons = {
                R.drawable.ic_link,
                R.drawable.ic_reinforce,
                R.drawable.ic_event,
                R.drawable.ic_light_bulb,
        };

        try {
            tabLayout.getTabAt(0).setIcon(tabIcons[0]);
            tabLayout.getTabAt(1).setIcon(tabIcons[1]);
            tabLayout.getTabAt(2).setIcon(tabIcons[2]);
            tabLayout.getTabAt(3).setIcon(tabIcons[3]);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.hash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_search) {
            View searchMenuView = toolbar.findViewById(R.id.menu_search);
            if (tabLayout.getSelectedTabPosition() == 0) {
                HashUtil.startSearchActivity(HashActivity.this, "POINTER@0000", searchMenuView);
            } else if (tabLayout.getSelectedTabPosition() == 1) {
                HashUtil.startSearchActivity(HashActivity.this, "REINFORCE@0000", searchMenuView);
            } else if (tabLayout.getSelectedTabPosition() == 2) {
                HashUtil.startSearchActivity(HashActivity.this, "EVENT@0000", searchMenuView);
            } else if (tabLayout.getSelectedTabPosition() == 3) {
                HashUtil.startSearchActivity(HashActivity.this, "GARAGE@0000", searchMenuView);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setFonts() {
        pointText.setTypeface(HashUtil.typefaceLatoLight);
        point.setTypeface(HashUtil.typefaceLatoLight);
        rankText.setTypeface(HashUtil.typefaceLatoLight);
        rank.setTypeface(HashUtil.typefaceLatoLight);
        userName.setTypeface(HashUtil.typefaceLatoLight);
        userEmail.setTypeface(HashUtil.typefaceLatoLight);
        activityTitleInc.setTypeface(HashUtil.typefaceComfortaaBold);
        activityTitleLude.setTypeface(HashUtil.typefaceComfortaaBold);
        ((TextView) findViewById(R.id.leader_text)).setTypeface(HashUtil.GetTypeface());
        ((TextView) findViewById(R.id.library_text)).setTypeface(HashUtil.GetTypeface());
        ((TextView) findViewById(R.id.pratice_text)).setTypeface(HashUtil.GetTypeface());
        ((TextView) findViewById(R.id.donate_text)).setTypeface(HashUtil.GetTypeface());
        ((TextView) findViewById(R.id.settings_text)).setTypeface(HashUtil.GetTypeface());
        ((TextView) findViewById(R.id.feedback_text)).setTypeface(HashUtil.GetTypeface());

    }

    private void setListeners() {
        leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HashActivity.this, LeaderBoardActivity.class));
            }
        });
        library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HashActivity.this, LibraryActivity.class));
            }
        });
        practice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HashActivity.this, PracticeActivity.class));
            }
        });
        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HashActivity.this, SettingsActivity.class));
            }
        });
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(HashActivity.this, FeedbackActivity.class));
                HashUtil.showFeedbackDialog(HashActivity.this);
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(HashActivity.this, FeedbackActivity.class));
                HashUtil.showAboutDialog(HashActivity.this,HashActivity.this);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUtils.signOut();
                startActivity(new Intent(HashActivity.this, SigninActivity.class));
                finish();
            }
        });
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HashActivity.this, EditProfileActivity.class);
                intent.putExtra(EditProfileActivity.EXTRA, "add");
                FabTransform.addExtras(intent,
                        ContextCompat.getColor(HashActivity.this, R.color.colorPrimary), R.drawable.ic_add_light);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(HashActivity.this, updateProfile,
                        getString(R.string.transition_fab));
                startActivity(intent, options.toBundle());
            }
        });

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // return null to display only the icon
            return null;
        }
    }

}
