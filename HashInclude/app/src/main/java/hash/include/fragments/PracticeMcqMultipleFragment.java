package hash.include.fragments;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class PracticeMcqMultipleFragment extends CykPracticeListFragment {
    public PracticeMcqMultipleFragment() {
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        Query query = databaseReference.child("practice-cyk").orderByChild("questionType").equalTo("Mcq-Multiple");
        return query;
    }
}
