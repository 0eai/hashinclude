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
import hash.include.model.Garage;
import hash.include.model.UserRegister;
import hash.include.util.HashUtil;

public class UserGarageViewHolder extends RecyclerView.ViewHolder {

    public TextView garageTitle;
    public TextView garageDesc;
    public ImageView garagePoster;
    public DatabaseReference mDatabase;
    public UserGarageViewHolder(View itemView) {
        super(itemView);

        garagePoster = itemView.findViewById(R.id.project_poster);
        garageTitle = itemView.findViewById(R.id.title);
        garageDesc = itemView.findViewById(R.id.desc);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void bindToPost(UserRegister userRegister, View.OnClickListener shareClickListener) {
        garageTitle.setTypeface(HashUtil.typefaceLatoRegular);
        garageDesc.setTypeface(HashUtil.GetTypeface());
        mDatabase.child("projects").child(userRegister.fuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // ...
                Garage garage = dataSnapshot.getValue(Garage.class);
                if (garage != null) {
                    garageTitle.setText(garage.title);
                    garageDesc.setText(garage.about);
                    HashUtil.loadImageInImageView(garagePoster, garage.picUrl);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

    }
}
