package hash.include.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import hash.include.R;
import hash.include.model.CheckYourKnowledge;
import hash.include.model.UserValues;
import hash.include.util.FirebaseUtils;
import hash.include.util.HashUtil;

public class CykViewHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public ImageButton editButton;
    public ImageButton addLibButton;

    public CykViewHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.cyk_title);
        editButton = itemView.findViewById(R.id.edit_button);
        addLibButton = itemView.findViewById(R.id.add_to_practice_button);
    }

    public void bindToPost(CheckYourKnowledge cyk, View.OnClickListener ClickListener) {
        title.setText(cyk.title);
        title.setTypeface(HashUtil.typefaceLatoLight);
        addLibButton.setOnClickListener(ClickListener);
        FirebaseUtils.GetDbRef().child("users-values").child(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserValues uservalues = dataSnapshot.getValue(UserValues.class);
                if (uservalues != null) {
                    if (uservalues.userType.equals("Super Admin") || uservalues.userType.equals("Admin")) {
                        editButton.setVisibility(View.VISIBLE);
                        addLibButton.setVisibility(View.VISIBLE);
                    } else if (uservalues.userType.equals("Gold")) {
                        if (cyk.uid.equals(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail()))) {
                            editButton.setVisibility(View.VISIBLE);
                            addLibButton.setVisibility(View.GONE);
                        } else {
                            editButton.setVisibility(View.GONE);
                            addLibButton.setVisibility(View.GONE);
                        }
                    } else {
                        editButton.setVisibility(View.GONE);
                        addLibButton.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }
}
