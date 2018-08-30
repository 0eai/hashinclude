package hash.include.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
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
import hash.include.model.UserValues;
import hash.include.util.FirebaseUtils;
import hash.include.util.HashUtil;

public class TeamViewHolder extends RecyclerView.ViewHolder {
    public TextView email;
    public TextView username;
    public ImageView picUrl;
    public TextView userType;
    ArrayAdapter<String> spinnerAdapter;

    public TeamViewHolder(View itemView) {
        super(itemView);

        picUrl = itemView.findViewById(R.id.user_pic);
        email = itemView.findViewById(R.id.user_email);
        username = itemView.findViewById(R.id.user_name);
        userType = itemView.findViewById(R.id.team_user_type);
    }

    public void bindToPost(UserValues values, View.OnClickListener ClickListener) {
        email.setTypeface(HashUtil.GetTypeface());
        username.setTypeface(HashUtil.GetTypeface());
        userType.setTypeface(HashUtil.GetTypeface());
        userType.setText(values.userType);
        FirebaseUtils.loadUserInUserViewsImg(values.uid,username,email,picUrl);
    }
}
