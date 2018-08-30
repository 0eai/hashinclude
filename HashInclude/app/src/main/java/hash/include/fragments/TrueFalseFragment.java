package hash.include.fragments;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class TrueFalseFragment extends CykListFragment {
    public TrueFalseFragment() {
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        Query query = databaseReference.child("check-your-knowledge").orderByChild("questionType").equalTo("True/False");
        return query;
    }
}
