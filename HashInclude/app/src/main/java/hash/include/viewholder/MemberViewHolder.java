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
import hash.include.model.Member;
import hash.include.model.User;
import hash.include.util.FirebaseUtils;
import hash.include.util.HashUtil;

public class MemberViewHolder extends RecyclerView.ViewHolder{
    public TextView title;
    public TextView username;
    public ImageView picUrl;


    public MemberViewHolder(View itemView) {
        super(itemView);

        picUrl =  itemView.findViewById(R.id.user_pic);
        title =  itemView.findViewById(R.id.title);
        username =  itemView.findViewById(R.id.user_name);

    }

    public void bindToPost(Member member, View.OnClickListener ClickListener) {
        title.setText(member.title);
        title.setTypeface(HashUtil.typefaceLatoRegular);
        username.setTypeface(HashUtil.GetTypeface());
        FirebaseUtils.loadUserInUserViewsImg(member.uid,username,null,picUrl);
    }
}
