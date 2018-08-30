package hash.include.fragments;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by root on 2/5/18.
 */

public class PracticeOutputFragment extends CykPracticeListFragment {
    public PracticeOutputFragment() {
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        Query query = databaseReference.child("practice-cyk").orderByChild("questionType").equalTo("Output");
        return query;
    }
}
