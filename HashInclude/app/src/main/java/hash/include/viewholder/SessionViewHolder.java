package hash.include.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import hash.include.R;
import hash.include.model.Session;
import hash.include.util.FirebaseUtils;
import hash.include.util.HashUtil;

public class SessionViewHolder extends RecyclerView.ViewHolder {
    public TextView sessionNumber;
    public TextView sessionDate;
    public TextView sessionTitle;
    public ImageButton editButton;
    public SessionViewHolder(View itemView) {
        super(itemView);
        sessionNumber = itemView.findViewById(R.id.session_number);
        sessionDate = itemView.findViewById(R.id.session_date);
        sessionTitle = itemView.findViewById(R.id.session_title);
        editButton = itemView.findViewById(R.id.edit_button);
    }

    public void bindToPost(Session session, View.OnClickListener shareClickListener) {
        sessionNumber.setText("Session #" + session.sessionNo);
        sessionTitle.setText(session.title);
        sessionTitle.setTypeface(HashUtil.typefaceLatoRegular);
        sessionDate.setTypeface(HashUtil.typefaceLatoLight);
        sessionNumber.setTypeface(HashUtil.typefaceLatoLight);
        sessionDate.setText(session.sessionDate + " " + session.sessionTime);
        FirebaseUtils.setEditForUser(session.uid, editButton);
    }
}
