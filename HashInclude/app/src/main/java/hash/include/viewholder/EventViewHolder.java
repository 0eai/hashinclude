package hash.include.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import hash.include.R;
import hash.include.model.Event;
import hash.include.model.UserValues;
import hash.include.util.FirebaseUtils;
import hash.include.util.HashUtil;

public class EventViewHolder extends RecyclerView.ViewHolder{
    public TextView eventTitle;
    public TextView eventDesc;
    public ImageView eventPoster;
    public ImageButton editButton;

    public EventViewHolder(View itemView) {
        super(itemView);

        eventPoster = itemView.findViewById(R.id.project_poster);
        eventTitle = itemView.findViewById(R.id.title);
        eventDesc = itemView.findViewById(R.id.desc);
        editButton = itemView.findViewById(R.id.edit_button);
    }

    public void bindToPost(final Event event, View.OnClickListener clickListener) {
        eventTitle.setText(event.eventType);
        eventTitle.setTypeface(HashUtil.typefaceLatoRegular);
        eventDesc.setText(event.title);
        eventDesc.setTypeface(HashUtil.GetTypeface());
        HashUtil.loadImageInImageView(eventPoster, event.picUrl);
        FirebaseUtils.setEditForUser(event.uid, editButton);
    }
}
