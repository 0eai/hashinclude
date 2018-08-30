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
import hash.include.model.User;
import hash.include.model.Winner;
import hash.include.util.FirebaseUtils;
import hash.include.util.HashUtil;

public class WinnerViewHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public TextView username;
    public ImageView picUrl;
    public DatabaseReference mDatabase;


    public WinnerViewHolder(View itemView) {
        super(itemView);

        picUrl = itemView.findViewById(R.id.user_pic);
        title = itemView.findViewById(R.id.title);
        username = itemView.findViewById(R.id.user_name);
        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    public void bindToPost(Winner winner, View.OnClickListener ClickListener) {
        title.setText("Position #" + winner.position);
        title.setTypeface(HashUtil.typefaceLatoRegular);
        username.setTypeface(HashUtil.GetTypeface());
        FirebaseUtils.loadUserInUserViewsImg(winner.uid,username,null,picUrl);
    }
}
