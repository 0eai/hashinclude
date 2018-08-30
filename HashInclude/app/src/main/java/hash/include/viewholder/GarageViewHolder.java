package hash.include.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import hash.include.R;
import hash.include.model.Garage;
import hash.include.model.UserValues;
import hash.include.util.FirebaseUtils;
import hash.include.util.HashUtil;

public class GarageViewHolder extends RecyclerView.ViewHolder {
    public TextView garageTitle;
    public TextView garageDesc;
    public ImageView garagePoster;
    public ImageButton editButton;

    public GarageViewHolder(View itemView) {
        super(itemView);

        garagePoster = itemView.findViewById(R.id.project_poster);
        garageTitle = itemView.findViewById(R.id.title);
        garageDesc = itemView.findViewById(R.id.desc);
        editButton = itemView.findViewById(R.id.edit_button);
    }

    public void bindToPost(final Garage garage, View.OnClickListener shareClickListener) {

        garageTitle.setText(garage.title);
        garageTitle.setTypeface(HashUtil.typefaceLatoRegular);
        garageDesc.setText(garage.about);
        garageDesc.setTypeface(HashUtil.GetTypeface());
        HashUtil.loadImageInImageView(garagePoster, garage.picUrl);
        FirebaseUtils.setEditForUser(garage.uid, editButton);
    }
}
