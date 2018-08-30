package hash.include.fragments;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class McqMultipleFragment extends CykListFragment {
    public McqMultipleFragment() {
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        Query query = databaseReference.child("check-your-knowledge").orderByChild("questionType").equalTo("Mcq-Multiple");
        return query;
    }
}
