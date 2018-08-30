package hash.include.viewholder;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import hash.include.R;
import hash.include.model.User;
import hash.include.util.HashUtil;

public class SearchMemberAdapter extends RecyclerView.Adapter<SearchMemberAdapter.MyViewHolder>{

    private List<User> userList;
    private OnItemClickListener onItemClickListener;
    private OnClickListener onClickListener;
    public SearchMemberAdapter(List<User> userList) {
        this.userList = userList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_user, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        User user = userList.get(position);
        holder.username.setText(user.username);
        holder.username.setTypeface(HashUtil.typefaceLatoRegular);
        holder.email.setText(user.email);
        holder.email.setTypeface(HashUtil.GetTypeface());
        HashUtil.loadImageInImageView(holder.picUrl, user.picUrl);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(user);
            }
        };
        holder.addMember.setOnClickListener(listener);
        View.OnClickListener onClickListener1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(user);
            }
        };
        holder.itemView.setOnClickListener(onClickListener1);

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView email;
        public TextView username;
        public ImageView picUrl;
        public ImageView addMember;
        public MyViewHolder(View view) {
            super(view);
            picUrl =  itemView.findViewById(R.id.user_pic);
            email =  itemView.findViewById(R.id.user_email);
            username =  itemView.findViewById(R.id.user_name);
            addMember =  itemView.findViewById(R.id.add_member);
        }
    }

}