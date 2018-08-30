package hash.include.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import hash.include.R;
import hash.include.model.Event;
import hash.include.model.Register;
import hash.include.model.UserRegister;
import hash.include.util.HashUtil;

public class UserEventViewHolder extends RecyclerView.ViewHolder {
    public TextView eventTitle;
    public TextView eventDesc;
    public ImageView eventPoster;
    public ImageButton editButton;
    public DatabaseReference mDatabase;

    public UserEventViewHolder(View itemView) {
        super(itemView);

        eventPoster = itemView.findViewById(R.id.project_poster);
        eventTitle = itemView.findViewById(R.id.title);
        eventDesc = itemView.findViewById(R.id.desc);
        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    public void bindToPost(UserRegister userRegister, View.OnClickListener clickListener) {
        eventTitle.setTypeface(HashUtil.typefaceLatoRegular);
        eventDesc.setTypeface(HashUtil.GetTypeface());
        mDatabase.child("events").child(userRegister.fuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // ...
                Event event = dataSnapshot.getValue(Event.class);
                if (event != null) {
                    eventTitle.setText(event.title);
                    eventDesc.setText(event.about);
                    HashUtil.loadImageInImageView(eventPoster, event.picUrl);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });


    }
}
