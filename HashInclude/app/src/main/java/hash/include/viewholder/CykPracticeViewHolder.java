package hash.include.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import hash.include.R;
import hash.include.model.CheckYourKnowledge;
import hash.include.util.HashUtil;

public class CykPracticeViewHolder extends RecyclerView.ViewHolder {
    public TextView title;

    public CykPracticeViewHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.cyk_title);
    }

    public void bindToPost(CheckYourKnowledge cyk, View.OnClickListener ClickListener) {
        title.setText(cyk.title);
        title.setTypeface(HashUtil.typefaceLatoLight);
    }
}
