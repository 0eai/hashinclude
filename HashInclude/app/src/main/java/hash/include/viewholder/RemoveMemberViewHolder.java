package hash.include.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import hash.include.R;
import hash.include.model.Member;
import hash.include.util.FirebaseUtils;
import hash.include.util.HashUtil;

public class RemoveMemberViewHolder extends RecyclerView.ViewHolder{
    public TextView email;
    public TextView username;
    public ImageView picUrl;
    public ImageView removeMember;


    public RemoveMemberViewHolder(View itemView) {
        super(itemView);

        picUrl =  itemView.findViewById(R.id.user_pic);
        email =  itemView.findViewById(R.id.user_email);
        username =  itemView.findViewById(R.id.user_name);
        removeMember =  itemView.findViewById(R.id.remove_member);
    }

    public void bindToPost(Member member, View.OnClickListener ClickListener) {
        email.setTypeface(HashUtil.GetTypeface());
        username.setTypeface(HashUtil.GetTypeface());
        FirebaseUtils.loadUserInUserViewsImg(member.uid,username,email,picUrl);
    }
}
