package hash.include.fragments;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by root on 2/5/18.
 */

public class PracticeProgramFragment extends CykPracticeListFragment {
    public PracticeProgramFragment() {
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        Query query = databaseReference.child("practice-cyk").orderByChild("questionType").equalTo("Program");
        return query;
    }
}
