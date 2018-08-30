package hash.include.viewholder;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import hash.include.R;
import hash.include.model.Register;
import hash.include.model.User;
import hash.include.util.FirebaseUtils;
import hash.include.util.HashUtil;

public class PendingUsersViewHolder extends RecyclerView.ViewHolder {
    public TextView email;
    public TextView username;
    public ImageView picUrl;
    public Button acceptButton;


    public PendingUsersViewHolder(View itemView) {
        super(itemView);

        picUrl = itemView.findViewById(R.id.user_pic);
        email = itemView.findViewById(R.id.user_email);
        username = itemView.findViewById(R.id.user_name);
        acceptButton = itemView.findViewById(R.id.accept_button);

    }

    public void bindToPost(Register register, View.OnClickListener ClickListener) {
        email.setTypeface(HashUtil.GetTypeface());
        username.setTypeface(HashUtil.GetTypeface());
        acceptButton.setTypeface(HashUtil.GetTypeface());
        Log.d("user search", register.status + "  " + register.uid);
        Log.e("user search", register.status + "  " + register.uid);
        FirebaseUtils.loadUserInUserViewsImg(register.uid,username,email,picUrl);
    }
}
