package hash.include.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import hash.include.R;
import hash.include.model.Reinforce;
import hash.include.util.FirebaseUtils;
import hash.include.util.HashUtil;

public class ReinforceViewHolder extends RecyclerView.ViewHolder {
    public TextView reinforceTitle;
    public ImageView reinforcePoster;
    public ImageButton editButton;

    public ReinforceViewHolder(View itemView) {
        super(itemView);

        reinforcePoster = itemView.findViewById(R.id.reinforce_poster);
        reinforceTitle = itemView.findViewById(R.id.title);
        editButton = itemView.findViewById(R.id.edit_button);
    }

    public void bindToPost(final Reinforce reinforce, View.OnClickListener shareClickListener) {

        reinforceTitle.setText(reinforce.title);
        reinforceTitle.setTypeface(HashUtil.typefaceLatoRegular);
        HashUtil.loadImageInImageView(reinforcePoster, reinforce.picUrl);
        FirebaseUtils.setEditForUser(reinforce.uid, editButton);
    }
}
