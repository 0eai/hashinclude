package hash.include.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import hash.include.R;
import hash.include.model.Reinforce;
import hash.include.model.UserRegister;
import hash.include.util.HashUtil;

public class UserReinforceViewHolder extends RecyclerView.ViewHolder {

    public TextView reinforceTitle;
    public ImageView reinforcePoster;
    public DatabaseReference mDatabase;

    public UserReinforceViewHolder(View itemView) {
        super(itemView);

        reinforcePoster = itemView.findViewById(R.id.reinforce_poster);
        reinforceTitle = itemView.findViewById(R.id.title);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void bindToPost(UserRegister userRegister, View.OnClickListener shareClickListener) {
        reinforceTitle.setTypeface(HashUtil.typefaceLatoRegular);
        mDatabase.child("reinforce").child(userRegister.fuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // ...
                Reinforce reinforce = dataSnapshot.getValue(Reinforce.class);
                if (reinforce != null) {
                    reinforceTitle.setText(reinforce.title);
                    HashUtil.loadImageInImageView(reinforcePoster, reinforce.picUrl);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

    }
}
