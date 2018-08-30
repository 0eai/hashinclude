package hash.include.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import hash.include.R;
import hash.include.activity.CykActivity;
import hash.include.model.CheckYourKnowledge;
import hash.include.viewholder.CykPracticeViewHolder;

public abstract class CykPracticeListFragment extends Fragment {
    private static final String TAG = "CykListFragment";
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<CheckYourKnowledge, CykPracticeViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    public CykPracticeListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.recycler_view, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
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
        Query postsQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<CheckYourKnowledge, CykPracticeViewHolder>(CheckYourKnowledge.class, R.layout.item_cyk,
                CykPracticeViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final CykPracticeViewHolder viewHolder, final CheckYourKnowledge model, final int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String Key = postRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), CykActivity.class);
                        intent.putExtra(CykActivity.EXTRA, Key);
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
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }


    public abstract Query getQuery(DatabaseReference databaseReference);

}
