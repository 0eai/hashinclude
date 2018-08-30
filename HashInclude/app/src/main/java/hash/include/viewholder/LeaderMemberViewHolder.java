package hash.include.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import hash.include.R;
import hash.include.model.User;
import hash.include.model.UserValues;
import hash.include.ui.RiseNumberTextView;
import hash.include.util.FirebaseUtils;
import hash.include.util.HashUtil;

public class LeaderMemberViewHolder extends RecyclerView.ViewHolder {
    public TextView email;
    public TextView username;
    public ImageView picUrl;
    RiseNumberTextView point;


    public LeaderMemberViewHolder(View itemView) {
        super(itemView);

        picUrl = itemView.findViewById(R.id.user_pic);
        email = itemView.findViewById(R.id.user_email);
        username = itemView.findViewById(R.id.user_name);
        point = itemView.findViewById(R.id.user_point);

    }

    public void bindToPost(UserValues values, View.OnClickListener ClickListener) {
        email.setTypeface(HashUtil.GetTypeface());
        username.setTypeface(HashUtil.GetTypeface());
        point.setTypeface(HashUtil.GetTypeface());
        if (values.point != 0) {
            point.withNumber(values.point).setDuration(values.point * 10).start();
        } else {
            point.withNumber(values.point).setDuration(0).start();
        }
        FirebaseUtils.loadUserInUserViewsImg(values.uid,username,email,picUrl);
    }
}
