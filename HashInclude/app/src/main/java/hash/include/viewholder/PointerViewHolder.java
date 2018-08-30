package hash.include.viewholder;

import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import hash.include.R;
import hash.include.model.Pointer;
import hash.include.model.UserValues;
import hash.include.util.FirebaseUtils;
import hash.include.util.HashUtil;

public class PointerViewHolder extends RecyclerView.ViewHolder {
    public TextView pointerLink;
    public ImageButton editButton;
    private TextView pointerTitle;
    private TextView pointerDesc;
    private TextView pointerTime;
    public PointerViewHolder(View itemView) {
        super(itemView);
        pointerTitle = itemView.findViewById(R.id.title);
        pointerDesc = itemView.findViewById(R.id.desc);
        pointerTime = itemView.findViewById(R.id.pointer_time);
        pointerLink = itemView.findViewById(R.id.link_text);
        editButton = itemView.findViewById(R.id.edit_button);
    }

    public void bindToPost(final Pointer pointer, View.OnClickListener ClickListener) {
        pointerTitle.setText(pointer.title);
        pointerTitle.setTypeface(HashUtil.typefaceLatoLight);
        pointerDesc.setText(pointer.about);
        pointerDesc.setTypeface(HashUtil.GetTypeface());
        pointerLink.setTypeface(HashUtil.typefaceLatoLight);
        pointerLink.setText(pointer.link);
        String date = pointer.dateType + " : " + pointer.date;
        pointerTime.setText(date);
        pointerTime.setTypeface(HashUtil.typefaceLatoLight);
        FirebaseUtils.setEditForUser(pointer.uid, editButton);
    }
}
