package hash.include.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.SearchManager;
import android.app.SharedElementCallback;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.TransitionRes;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hash.include.R;
import hash.include.model.Event;
import hash.include.model.Garage;
import hash.include.model.Pointer;
import hash.include.model.Reinforce;
import hash.include.model.User;
import hash.include.transitions.FabTransform;
import hash.include.ui.transitions.CircularReveal;
import hash.include.util.FirebaseUtils;
import hash.include.util.ImeUtils;
import hash.include.util.TransitionUtils;
import hash.include.viewholder.EventViewHolder;
import hash.include.viewholder.GarageViewHolder;
import hash.include.viewholder.OnClickListener;
import hash.include.viewholder.OnItemClickListener;
import hash.include.viewholder.PointerViewHolder;
import hash.include.viewholder.ReinforceViewHolder;
import hash.include.viewholder.SearchMemberAdapter;

public class SearchActivity extends Activity{

    public static final String SRAECH = "SEARCH_QUERY";

    @BindView(R.id.searchback)
    ImageButton searchBack;
    @BindView(R.id.searchback_container)
    ViewGroup searchBackContainer;
    @BindView(R.id.search_view)
    SearchView searchView;
    @BindView(R.id.search_background)
    View searchBackground;
    @BindView(R.id.search_results)
    RecyclerView results;
    @BindView(R.id.container)
    ViewGroup container;
    @BindView(R.id.search_toolbar)
    ViewGroup searchToolbar;
    @BindView(R.id.results_container)
    ViewGroup resultsContainer;


    @BindView(R.id.scrim)
    View scrim;
    @BindView(R.id.results_scrim)
    View resultsScrim;
    //@BindInt(R.integer.num_columns) int columns;
    //@BindDimen(R.dimen.dimen_4dp) float appBarElevation;
    String[] string;
    private SparseArray<Transition> transitions = new SparseArray<>();
    private boolean focusQuery = true;
    private String key;
    private List<User> userList = new ArrayList<>();
    private SearchMemberAdapter mAdapter;
    private FirebaseRecyclerAdapter<Garage, GarageViewHolder> mGarageAdapter;
    private FirebaseRecyclerAdapter<Event, EventViewHolder> mEventAdapter;
    private FirebaseRecyclerAdapter<Reinforce, ReinforceViewHolder> mReinforceAdapter;
    private FirebaseRecyclerAdapter<Pointer, PointerViewHolder> mPointerAdapter;
    private LinearLayoutManager mManager;
    private DatabaseReference mDatabase;
    private String rankInput = "";
    private String typeInput = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setupSearchView();
        setupTransitions();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(false);
        results.setLayoutManager(mManager);
        results.setItemAnimator(new DefaultItemAnimator());
        results.setHasFixedSize(true);
        key = getIntent().getStringExtra(SRAECH);
        if (key == null) {
            throw new IllegalArgumentException("Must pass KEY");
        } else {
            string = key.split("@", 2);
            //Toast.makeText(SearchActivity.this, key + "\n" + string[0] + "\n" + string[1], Toast.LENGTH_LONG).show();
            if (string[0].equals("ADD-MEMBERS")) {
                initForAddMember();
            } else if (string[0].equals("ADD-WINNERS")) {
                initForAddMember();
            }
        }
    }

    public void initForAddMember() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mAdapter = new SearchMemberAdapter(userList);
        results.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(User item) {
                if (string[0].equals("ADD-MEMBERS")) {
                    typeInputDialog(item, "Member Title", "Set developer type", "", "Developer");
                } else if (string[0].equals("ADD-WINNERS")) {
                    inputDialog(item, "Winner Position", "Set position of the winner", "Position", "");
                }
                mAdapter.notifyDataSetChanged();
                Toast.makeText(SearchActivity.this, item.username, Toast.LENGTH_LONG).show();

            }
        });
        mAdapter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(User item) {
                Intent intent = new Intent(SearchActivity.this, ProfileDetailsActivity.class);
                intent.putExtra(ProfileDetailsActivity.EXTRA, item.uid);
                startActivity(intent);
            }
        });
    }

    public void initForGarage(String query) {
        Query postsQuery = searchQueryGarage(query);
        mGarageAdapter = new FirebaseRecyclerAdapter<Garage, GarageViewHolder>(Garage.class, R.layout.item_garage,
                GarageViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final GarageViewHolder viewHolder, final Garage garage, final int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String key = postRef.getKey();
               viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), ProjectDetailsActivity.class);
                        intent.putExtra(ProjectDetailsActivity.EXTRA_FILE_KEY, key);

                        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(SearchActivity.this,

                                new Pair<View, String>(v.findViewById(R.id.project_poster),
                                        ProjectDetailsActivity.VIEW_NAME_HEADER_IMAGE),
                                new Pair<View, String>(v.findViewById(R.id.title),
                                        ProjectDetailsActivity.VIEW_NAME_HEADER_TITLE));
                        startActivity(intent, activityOptions.toBundle());

                    }
                });
                viewHolder.editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), AddGarageActivity.class);
                        intent.putExtra(AddGarageActivity.EXTRA, key);
                        FabTransform.addExtras(intent,
                                ContextCompat.getColor(v.getContext(), R.color.tcolor), R.drawable.ic_edit_black_24dp);
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SearchActivity.this, viewHolder.editButton,
                                getString(R.string.transition_designer_news_login));
                        startActivity(intent, options.toBundle());
                    }
                });

                viewHolder.bindToPost(garage, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {

                    }
                });
            }
        };
        results.setAdapter(mGarageAdapter);
    }

    public void initForPointer(String query) {
        Query postsQuery = searchQueryPointer(query);
        mPointerAdapter = new FirebaseRecyclerAdapter<Pointer, PointerViewHolder>(Pointer.class, R.layout.item_pointer,
                PointerViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final PointerViewHolder viewHolder, final Pointer pointer, final int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String Key = postRef.getKey();

                viewHolder.editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), AddPointerActivity.class);
                        intent.putExtra(AddPointerActivity.EXTRA, Key);
                        FabTransform.addExtras(intent,
                                ContextCompat.getColor(v.getContext(), R.color.tcolor), R.drawable.ic_edit_black_24dp);
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SearchActivity.this, viewHolder.editButton,
                                getString(R.string.transition_designer_news_login));
                        startActivity(intent, options.toBundle());
                    }
                });

                viewHolder.bindToPost(pointer, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
            }
        };
        results.setAdapter(mPointerAdapter);
    }

    public void initForReinforce(String query) {
        Query postsQuery = searchQueryReinforce(query);
        mReinforceAdapter = new FirebaseRecyclerAdapter<Reinforce, ReinforceViewHolder>(Reinforce.class, R.layout.item_reinforce,
                ReinforceViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final ReinforceViewHolder viewHolder, final Reinforce reinforce, final int position) {
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
                viewHolder.editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), AddReinforceActivity.class);
                        intent.putExtra(AddReinforceActivity.EXTRA, Key);
                        FabTransform.addExtras(intent,
                                ContextCompat.getColor(v.getContext(), R.color.tcolor), R.drawable.ic_edit_black_24dp);
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SearchActivity.this, viewHolder.editButton,
                                getString(R.string.transition_designer_news_login));
                        startActivity(intent, options.toBundle());

                    }
                });

                viewHolder.bindToPost(reinforce, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {

                    }
                });
            }
        };
        results.setAdapter(mReinforceAdapter);
    }

    public void initForEvent(String query) {
        Query postsQuery = searchQueryEvent(query);
        mEventAdapter = new FirebaseRecyclerAdapter<Event, EventViewHolder>(Event.class, R.layout.item_event,
                EventViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final EventViewHolder viewHolder, final Event event, final int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String Key = postRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), EventDetailsActivity.class);
                        intent.putExtra(EventDetailsActivity.EXTRA, Key);
                        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(SearchActivity.this,
                                new Pair<View, String>(v,
                                        EventDetailsActivity.VIEW_NAME_HEADER_IMAGE),
                                new Pair<View, String>(v,
                                        EventDetailsActivity.VIEW_NAME_HEADER_TITLE));
                        startActivity(intent, activityOptions.toBundle());

                    }
                });
                viewHolder.editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), AddEventActivity.class);
                        intent.putExtra(AddEventActivity.EXTRA, Key);
                        FabTransform.addExtras(intent,
                                ContextCompat.getColor(v.getContext(), R.color.tcolor), R.drawable.ic_edit_black_24dp);
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SearchActivity.this, viewHolder.editButton,
                                getString(R.string.transition_fab));
                        startActivity(intent, options.toBundle());

                    }
                });

                viewHolder.bindToPost(event, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {

                    }
                });
            }
        };
        results.setAdapter(mEventAdapter);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
            dismiss();
    }

    @Override
    protected void onPause() {
        // needed to suppress the default window animation when closing the activity
        overridePendingTransition(0, 0);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
       // dataManager.cancelLoading();
        super.onDestroy();
    }

    @Override
    public void onEnterAnimationComplete() {
        if (focusQuery) {
            // focus the search view once the enter transition finishes
            searchView.requestFocus();
            ImeUtils.showIme(searchView);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

        }
    }

    @OnClick({ R.id.scrim, R.id.searchback })
    protected void dismiss() {
        // clear the background else the touch ripple moves with the translation which looks bad
        searchBack.setBackground(null);
        finishAfterTransition();
    }

    void clearResults() {
        TransitionManager.beginDelayedTransition(container, getTransition(R.transition.auto));
        //if(mAdapter != null){}
        if(mAdapter != null){userList.clear();mAdapter.notifyDataSetChanged();}
        results.setVisibility(View.GONE);
        //progress.setVisibility(View.GONE);
        //fab.setVisibility(View.GONE);
        resultsScrim.setVisibility(View.GONE);
        //setNoResultsVisibility(View.GONE);
    }

    /*void setNoResultsVisibility(int visibility) {
        if (visibility == View.VISIBLE) {
            if (noResults == null) {
                noResults = (TextView) ((ViewStub)
                        findViewById(R.id.stub_no_search_results)).inflate();
                noResults.setOnClickListener(v -> {
                    searchView.setQuery("", false);
                    searchView.requestFocus();
                    ImeUtils.showIme(searchView);
                });
            }
            String message = String.format(
                    getString(R.string.no_search_results), searchView.getQuery().toString());
            SpannableStringBuilder ssb = new SpannableStringBuilder(message);
            ssb.setSpan(new StyleSpan(Typeface.ITALIC),
                    message.indexOf('“') + 1,
                    message.length() - 1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            noResults.setText(ssb);
        }
        if (noResults != null) {
            noResults.setVisibility(visibility);
        }
    }*/

    void searchFor(String query) {
        clearResults();
        //progress.setVisibility(View.VISIBLE);
        ImeUtils.hideIme(searchView);
        searchView.clearFocus();
        if (string[0].equals("ADD-MEMBERS")) {
            searchQuery(query);
        } else if (string[0].equals("ADD-WINNERS")) {
            searchQuery(query);
        } else if (string[0].equals("GARAGE")) {
            initForGarage(query);
        } else if (string[0].equals("EVENT")) {
            initForEvent(query);
        } else if (string[0].equals("POINTER")) {
            initForPointer(query);
        } else if (string[0].equals("REINFORCE")) {
            initForReinforce(query);
        } else if (string[0].equals("ARTICLE")) {

        }
    }

    Transition getTransition(@TransitionRes int transitionId) {
        Transition transition = transitions.get(transitionId);
        if (transition == null) {
            transition = TransitionInflater.from(this).inflateTransition(transitionId);
            transitions.put(transitionId, transition);
        }
        return transition;
    }

    private void setupSearchView() {
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        try {
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        // hint, inputType & ime options seem to be ignored from XML! Set in code
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        searchView.setImeOptions(searchView.getImeOptions() | EditorInfo.IME_ACTION_SEARCH |
                EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_FLAG_NO_FULLSCREEN);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchFor(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (TextUtils.isEmpty(query)) {
                    clearResults();
                }
                return true;
            }
        });
        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {

        });
    }

    private void setupTransitions() {
        // grab the position that the search icon transitions in *from*
        // & use it to configure the return transition
        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onSharedElementStart(
                    List<String> sharedElementNames,
                    List<View> sharedElements,
                    List<View> sharedElementSnapshots) {
                if (sharedElements != null && !sharedElements.isEmpty()) {
                    View searchIcon = sharedElements.get(0);
                    if (searchIcon.getId() != R.id.searchback) return;
                    int centerX = (searchIcon.getLeft() + searchIcon.getRight()) / 2;
                    CircularReveal hideResults = (CircularReveal) TransitionUtils.findTransition(
                            (TransitionSet) getWindow().getReturnTransition(),
                            CircularReveal.class, R.id.results_container);
                    if (hideResults != null) {
                        hideResults.setCenter(new Point(centerX, 0));
                    }
                }
            }
        });
    }

    public void searchQuery(String query) {
        mDatabase.orderByChild("username").startAt(query).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // ...
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    userList.add(snapshot.getValue(User.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (userList != null) {
            if (results.getVisibility() != View.VISIBLE) {
                TransitionManager.beginDelayedTransition(container,
                        getTransition(R.transition.search_show_results));
                //progress.setVisibility(View.GONE);
                results.setVisibility(View.VISIBLE);
                //fab.setVisibility(View.GONE);
                mAdapter.notifyDataSetChanged();
            }
        } else {
            TransitionManager.beginDelayedTransition(
                    container, getTransition(R.transition.auto));
            //progress.setVisibility(View.GONE);
            //setNoResultsVisibility(View.VISIBLE);
        }
    }

    public Query searchQueryPointer(String query) {
        Query query1 = mDatabase.child("pointers").orderByChild("title").startAt(query);
        if (query1 != null) {
            if (results.getVisibility() != View.VISIBLE) {
                TransitionManager.beginDelayedTransition(container,
                        getTransition(R.transition.search_show_results));
                //progress.setVisibility(View.GONE);
                results.setVisibility(View.VISIBLE);
                //fab.setVisibility(View.GONE);
                //mAdapter.notifyDataSetChanged();
            }
        } else {
            TransitionManager.beginDelayedTransition(
                    container, getTransition(R.transition.auto));
            //progress.setVisibility(View.GONE);
            //setNoResultsVisibility(View.VISIBLE);
        }
        return query1;
    }

    public Query searchQueryReinforce(String query) {
        Query query1 = mDatabase.child("reinforce").orderByChild("title").startAt(query);
        if (query1 != null) {
            if (results.getVisibility() != View.VISIBLE) {
                TransitionManager.beginDelayedTransition(container,
                        getTransition(R.transition.search_show_results));
                //progress.setVisibility(View.GONE);
                results.setVisibility(View.VISIBLE);
                //fab.setVisibility(View.GONE);
                //mAdapter.notifyDataSetChanged();
            }
        } else {
            TransitionManager.beginDelayedTransition(
                    container, getTransition(R.transition.auto));
            //progress.setVisibility(View.GONE);
            //setNoResultsVisibility(View.VISIBLE);
        }
        return query1;
    }

    public Query searchQueryEvent(String query) {
        Query query1 = mDatabase.child("events").orderByChild("title").startAt(query);
        if (query1 != null) {
            if (results.getVisibility() != View.VISIBLE) {
                TransitionManager.beginDelayedTransition(container,
                        getTransition(R.transition.search_show_results));
                //progress.setVisibility(View.GONE);
                results.setVisibility(View.VISIBLE);
                //fab.setVisibility(View.GONE);
                //mAdapter.notifyDataSetChanged();
            }
        } else {
            TransitionManager.beginDelayedTransition(
                    container, getTransition(R.transition.auto));
            //progress.setVisibility(View.GONE);
            //setNoResultsVisibility(View.VISIBLE);
        }
        return query1;
    }

    public Query searchQueryGarage(String query) {
        Query query1 = mDatabase.child("projects").orderByChild("title").startAt(query);
        if (query1 != null) {
            if (results.getVisibility() != View.VISIBLE) {
                TransitionManager.beginDelayedTransition(container,
                        getTransition(R.transition.search_show_results));
                //progress.setVisibility(View.GONE);
                results.setVisibility(View.VISIBLE);
                //fab.setVisibility(View.GONE);
                //mAdapter.notifyDataSetChanged();
            }
        } else {
            TransitionManager.beginDelayedTransition(
                    container, getTransition(R.transition.auto));
            //progress.setVisibility(View.GONE);
            //setNoResultsVisibility(View.VISIBLE);
        }
        return query1;
    }


    public void inputDialog(User user, String title, String content, String hint, String prefill) {
        new MaterialDialog.Builder(SearchActivity.this)
                .title(title)
                .content(content)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input(hint, prefill, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        try {
                            rankInput = input.toString();
                        } catch (Exception n) {
                            rankInput = "";
                        }
                        if (input == "")
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                        else dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                    }
                })
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (which == DialogAction.POSITIVE) {
                            Toast.makeText(SearchActivity.this, rankInput, Toast.LENGTH_LONG).show();
                            if (rankInput != null) {
                                FirebaseUtils.writeNewWinner(user, rankInput, string[1]);
                                rankInput = null;
                                userList.remove(user);
                            }
                        }
                    }
                })
                .alwaysCallInputCallback()
                .show();
    }

    public void typeInputDialog(User user, String title, String content, String hint, String prefill) {
        new MaterialDialog.Builder(SearchActivity.this)
                .title(title)
                .content(content)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .input(hint, prefill, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        try {
                            typeInput = input.toString();
                        } catch (Exception n) {
                            typeInput = "";
                        }
                        if (input == "")
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                        else dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                    }
                })
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (which == DialogAction.POSITIVE) {
                            Toast.makeText(SearchActivity.this, typeInput, Toast.LENGTH_LONG).show();
                            if (typeInput != null) {
                                FirebaseUtils.writeNewMember(user, typeInput, string[1]);
                                typeInput = null;
                                userList.remove(user);
                            }
                        }
                    }
                })
                .inputType(InputType.TYPE_TEXT_FLAG_AUTO_CORRECT)
                .alwaysCallInputCallback()
                .show();
    }

}
