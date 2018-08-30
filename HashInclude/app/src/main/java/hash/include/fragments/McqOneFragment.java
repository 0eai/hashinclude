package hash.include.fragments;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by root on 2/4/18.
 */

public class McqOneFragment extends CykListFragment {
    public McqOneFragment() {
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        Query query = databaseReference.child("check-your-knowledge").orderByChild("questionType").equalTo("Mcq-One");
        return query;
    }
}
