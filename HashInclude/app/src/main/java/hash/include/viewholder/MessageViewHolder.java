package hash.include.viewholder;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import hash.include.R;
import hash.include.model.GetMessage;
import hash.include.model.User;
import hash.include.util.FirebaseUtils;
import hash.include.util.HashUtil;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    public CircleImageView senderPic;
    public CircleImageView messageImage;
    public View leftMargin;
    public View rightMargin;
    public RelativeLayout imageLayout;
    public LinearLayout textLayout;
    public ProgressBar imageProgress;
    public TextView messageText;
    public ImageView sendStatus;
    public TextView textTime;
    public TextView imageTime;
    public CardView textCard;
    public ImageView uploadImage;

    public MessageViewHolder(View itemView) {
        super(itemView);

        senderPic = itemView.findViewById(R.id.chat_message_sender_pic);
        messageImage = itemView.findViewById(R.id.chat_message_image);
        leftMargin = itemView.findViewById(R.id.chat_message_left_margin);
        rightMargin = itemView.findViewById(R.id.chat_message_right_margin);
        imageLayout = itemView.findViewById(R.id.chat_message_image_layout);
        textLayout = itemView.findViewById(R.id.chat_message_text_layout);
        imageProgress = itemView.findViewById(R.id.chat_message_image_progress);
        messageText = itemView.findViewById(R.id.chat_message_text);
        sendStatus = itemView.findViewById(R.id.chat_send_status);
        textTime = itemView.findViewById(R.id.chat_message_date_time_text);
        imageTime = itemView.findViewById(R.id.chat_message_date_time_image);
        textCard = itemView.findViewById(R.id.chat_message_text_card);
        uploadImage = itemView.findViewById(R.id.chat_message_image_upload);

    }

    public void bindToPost(GetMessage message, View.OnClickListener ClickListener) {
        messageText.setTypeface(HashUtil.GetTypeface());
        textTime.setTypeface(HashUtil.GetTypeface());
        imageTime.setTypeface(HashUtil.GetTypeface());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        try {
            if (message.getUId().equals(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail()))) {
                if (message.mType.equals("TEXT")) {
                    textLayout.setVisibility(View.VISIBLE);
                    leftMargin.setVisibility(View.VISIBLE);
                    imageLayout.setVisibility(View.GONE);
                    imageProgress.setVisibility(View.GONE);
                    senderPic.setVisibility(View.GONE);
                    messageImage.setVisibility(View.GONE);
                    messageText.setVisibility(View.VISIBLE);

                    params.gravity = Gravity.END;
                    textCard.setLayoutParams(params);
                    textTime.setLayoutParams(params);
                    int color = Color.parseColor("#7B8D8E");
                    textCard.setCardBackgroundColor(color);
                    messageText.setText(message.getMText());
                    SimpleDateFormat sfd = new SimpleDateFormat();
                    textTime.setText(sfd.format(new Date(message.getTimeStamp())));

                } else {
                    textLayout.setVisibility(View.GONE);
                    leftMargin.setVisibility(View.VISIBLE);
                    imageLayout.setVisibility(View.VISIBLE);
                    messageImage.setVisibility(View.VISIBLE);
                    messageText.setVisibility(View.GONE);

                    int color = Color.parseColor("#7B8D8E");
                    messageImage.setBorderColor(color);
                    try {
                        Glide.with(messageImage.getContext())
                                .load(message.getmPicUrl())
                                .into(messageImage);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                    SimpleDateFormat sfd = new SimpleDateFormat();
                    imageTime.setText(sfd.format(new Date(message.getTimeStamp())));
                }
                sendStatus.setVisibility(View.VISIBLE);
                rightMargin.setVisibility(View.GONE);
                imageProgress.setVisibility(View.GONE);
                senderPic.setVisibility(View.GONE);
                textTime.setVisibility(View.GONE);
                imageTime.setVisibility(View.GONE);
                uploadImage.setVisibility(View.GONE);
                if (message.getSendStatus()) {
                    sendStatus.setImageResource(R.drawable.ic_check);
                    imageProgress.setVisibility(View.GONE);
                    uploadImage.setVisibility(View.GONE);
                } else {
                    sendStatus.setImageResource(R.drawable.ic_clock);
                    imageProgress.setVisibility(View.VISIBLE);
                    if (!message.getmType().equals("TEXT") && message.mError != null) {
                        imageProgress.setVisibility(View.GONE);
                        uploadImage.setVisibility(View.VISIBLE);
                        uploadImage.setOnClickListener(ClickListener);
                    }
                /*if (!message.getmType().equals("TEXT") && message.getError() == null && !message.getSendStatus()){
                    imageProgress.setVisibility(View.VISIBLE);
                    uploadImage.setVisibility(View.GONE);
                }*/
                }

            } else {
                if (message.getmType().equals("TEXT")) {
                    textLayout.setVisibility(View.VISIBLE);
                    sendStatus.setVisibility(View.GONE);
                    leftMargin.setVisibility(View.GONE);
                    rightMargin.setVisibility(View.VISIBLE);
                    imageLayout.setVisibility(View.GONE);
                    imageProgress.setVisibility(View.GONE);
                    senderPic.setVisibility(View.VISIBLE);
                    messageImage.setVisibility(View.GONE);
                    messageText.setVisibility(View.VISIBLE);
                    textTime.setVisibility(View.GONE);
                    imageTime.setVisibility(View.GONE);

                    params.gravity = Gravity.START;

                    textCard.setLayoutParams(params);
                    textTime.setLayoutParams(params);
                    int color = Color.parseColor("#4099ff");
                    textCard.setCardBackgroundColor(color);
                    messageText.setText(message.getMText());
                    SimpleDateFormat sfd = new SimpleDateFormat();
                    textTime.setText(sfd.format(new Date(message.getTimeStamp())));

                    FirebaseUtils.loadUserInUserViewsCir(message.getUId(),null,null,senderPic);

                } else {
                    textLayout.setVisibility(View.GONE);
                    sendStatus.setVisibility(View.GONE);
                    leftMargin.setVisibility(View.GONE);
                    rightMargin.setVisibility(View.VISIBLE);
                    imageLayout.setVisibility(View.VISIBLE);
                    imageProgress.setVisibility(View.GONE);
                    senderPic.setVisibility(View.VISIBLE);
                    messageImage.setVisibility(View.VISIBLE);
                    messageText.setVisibility(View.GONE);
                    textTime.setVisibility(View.GONE);
                    imageTime.setVisibility(View.GONE);

                    int color = Color.parseColor("#4099ff");
                    messageImage.setBorderColor(color);
                    HashUtil.loadImageInCircularImageView(messageImage, message.getmPicUrl());
                    SimpleDateFormat sfd = new SimpleDateFormat();
                    imageTime.setText(sfd.format(new Date(message.getTimeStamp())));

                    FirebaseUtils.loadUserInUserViewsCir(message.getUId(),null,null,senderPic);
                }
                uploadImage.setVisibility(View.GONE);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
