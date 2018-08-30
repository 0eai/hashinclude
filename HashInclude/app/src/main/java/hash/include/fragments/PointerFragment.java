package hash.include.fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import hash.include.R;
import hash.include.activity.AddPointerActivity;
import hash.include.model.Pointer;
import hash.include.transitions.FabTransform;
import hash.include.util.FirebaseUtils;
import hash.include.viewholder.PointerViewHolder;

public class PointerFragment extends Fragment {

    private static final String TAG = "PointerFragment";
    private FirebaseRecyclerAdapter<Pointer, PointerViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    public PointerFragment() {}

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
        mAdapter = new FirebaseRecyclerAdapter<Pointer, PointerViewHolder>(Pointer.class, R.layout.item_pointer,
                PointerViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final PointerViewHolder viewHolder, final Pointer pointer, final int position) {
                final DatabaseReference postRef = getRef(position);

                final String Key = postRef.getKey();

                viewHolder.editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), AddPointerActivity.class);
                        intent.putExtra(AddPointerActivity.EXTRA, Key);
                        FabTransform.addExtras(intent,
                                ContextCompat.getColor(v.getContext(), R.color.tcolor), R.drawable.ic_edit_black_24dp);
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), viewHolder.editButton,
                                getString(R.string.transition_fab));
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
        return databaseReference.child("pointers").limitToFirst(100);
    }
}
