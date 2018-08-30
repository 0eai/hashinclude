package hash.include.fragments;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class ProgramFragment extends CykListFragment {
    public ProgramFragment() {
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        Query query = databaseReference.child("check-your-knowledge").orderByChild("questionType").equalTo("Program");
        return query;
    }

}
