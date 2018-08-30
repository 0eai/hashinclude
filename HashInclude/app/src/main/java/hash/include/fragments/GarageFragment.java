package hash.include.fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import hash.include.R;
import hash.include.activity.AddGarageActivity;
import hash.include.activity.ProjectDetailsActivity;
import hash.include.model.Garage;
import hash.include.transitions.FabTransform;
import hash.include.util.FirebaseUtils;
import hash.include.viewholder.GarageViewHolder;

public class GarageFragment extends Fragment {

    private static final String TAG = "GarageFragment";
    private FirebaseRecyclerAdapter<Garage, GarageViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    public GarageFragment() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.recycler_view, container, false);
        mRecycler = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(FirebaseUtils.GetDbRef());
        mAdapter = new FirebaseRecyclerAdapter<Garage, GarageViewHolder>(Garage.class, R.layout.item_garage,
                GarageViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final GarageViewHolder viewHolder, final Garage garage, final int position) {
                final DatabaseReference postRef = getRef(position);

                final String key = postRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), ProjectDetailsActivity.class);
                        intent.putExtra(ProjectDetailsActivity.EXTRA_FILE_KEY, key);

                        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),

                                new Pair<View, String>(v.findViewById(R.id.project_poster),
                                        ProjectDetailsActivity.VIEW_NAME_HEADER_IMAGE),
                                new Pair<View, String>(v.findViewById(R.id.title),
                                        ProjectDetailsActivity.VIEW_NAME_HEADER_TITLE));
                        startActivity(intent,activityOptions.toBundle());

                    }
                });
                viewHolder.editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), AddGarageActivity.class);
                        intent.putExtra(AddGarageActivity.EXTRA, key);
                        FabTransform.addExtras(intent,
                                ContextCompat.getColor(v.getContext(), R.color.tcolor), R.drawable.ic_edit_black_24dp);
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), viewHolder.editButton,
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
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("projects").limitToFirst(100);
    }

}
