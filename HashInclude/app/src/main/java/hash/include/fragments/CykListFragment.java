package hash.include.fragments;

import android.app.ActivityOptions;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;

import hash.include.R;
import hash.include.activity.AddCykActivity;
import hash.include.activity.AddEventActivity;
import hash.include.activity.CykActivity;
import hash.include.model.CheckYourKnowledge;
import hash.include.model.CykPractice;
import hash.include.transitions.FabTransform;
import hash.include.viewholder.CykViewHolder;

import static android.content.Context.CLIPBOARD_SERVICE;

public abstract class CykListFragment extends Fragment {

    private static final String TAG = "CykListFragment";
    ClipboardManager myClipboard;
    private ClipData myClip;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<CheckYourKnowledge, CykViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;


    public CykListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.recycler_view, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRecycler = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        myClipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
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
        mAdapter = new FirebaseRecyclerAdapter<CheckYourKnowledge, CykViewHolder>(CheckYourKnowledge.class, R.layout.item_cyk_edit,
                CykViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final CykViewHolder viewHolder, final CheckYourKnowledge model, final int position) {
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
                viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        myClip = ClipData.newPlainText("text", Key);
                        myClipboard.setPrimaryClip(myClip);
                        Toast.makeText(v.getContext(), "Quid Copied",
                                Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                viewHolder.editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), AddCykActivity.class);
                        intent.putExtra(AddEventActivity.EXTRA, Key);
                        FabTransform.addExtras(intent,
                                ContextCompat.getColor(v.getContext(), R.color.tcolor), R.drawable.ic_edit_black_24dp);
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), viewHolder.editButton,
                                getString(R.string.transition_designer_news_login));
                        startActivity(intent, options.toBundle());

                    }
                });

                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String, Object> childUpdates = new HashMap<>();
                        CykPractice cykPractice = new CykPractice(model.title, model.questionType);
                        Map<String, Object> cykValues = cykPractice.toMap();
                        childUpdates.put("/practice-cyk/" + Key, cykValues);
                        mDatabase.updateChildren(childUpdates);
                        Toast.makeText(view.getContext(), "Cyk added to practice",
                                Toast.LENGTH_SHORT).show();
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
