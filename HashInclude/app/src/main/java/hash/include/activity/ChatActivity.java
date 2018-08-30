package hash.include.activity;

import android.app.ActivityOptions;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import hash.include.R;
import hash.include.model.GetMessage;
import hash.include.model.Message;
import hash.include.model.Reinforce;
import hash.include.model.User;
import hash.include.transitions.FabTransform;
import hash.include.util.FirebaseUtils;
import hash.include.util.HashUtil;
import hash.include.viewholder.MessageViewHolder;


public class ChatActivity extends AppCompatActivity implements
        View.OnClickListener {

    public static final String EXTRA = "EXTRA";
    public static final String MESSAGES_CHILD = "messages";
    private static final String TAG = "ChatActivity";
    private static final int REQUEST_IMAGE = 1;

    @BindView(R.id.messageRecyclerView)
    RecyclerView messages;
    @BindView(R.id.activity_title)
    TextView chatTitle;
    @BindView(R.id.send_text_edittext)
    EditText messageText;
    @BindView(R.id.send_text_button)
    CardView buttonSendText;
    @BindView(R.id.send_image_button)
    CardView buttonSendImage;
    private TextView activityTitle;
    private CircleImageView userPic;

    private String extra;

    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<GetMessage, MessageViewHolder> mAdapter;

    private ActionMode mActionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        extra = getIntent().getStringExtra(EXTRA);
        if (extra == null) {
            throw new IllegalArgumentException("Must pass EXTRA");
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (toolbar != null) {
            activityTitle = toolbar.findViewById(R.id.activity_title);
            userPic = toolbar.findViewById(R.id.user_profile_pic);
            activityTitle.setTypeface(HashUtil.typefaceLatoLight);
            setSupportActionBar(toolbar);
            final ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        messageText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    buttonSendImage.setVisibility(View.GONE);
                    buttonSendText.setVisibility(View.VISIBLE);
                } else {
                    buttonSendText.setVisibility(View.GONE);
                    buttonSendImage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        setFonts();
        buttonSendImage.setOnClickListener(this);
        buttonSendText.setOnClickListener(this);
        sync();
        setUpMessages();

    }

    public void sync() {
        FirebaseUtils.GetDbRef().child("reinforce").child(extra).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // ...
                Reinforce reinforce = dataSnapshot.getValue(Reinforce.class);
                if (reinforce != null) {
                    activityTitle.setText(reinforce.title);
                    if (!TextUtils.isEmpty(reinforce.picUrl)) {
                        HashUtil.loadImageInCircularImageView(userPic,reinforce.picUrl);
                        userPic.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(v.getContext(), ImageViewerActivity.class);
                                intent.putExtra(ImageViewerActivity.EXTRA, reinforce.title + "@" + reinforce.startDate + "@" + reinforce.picUrl);
                                ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(ChatActivity.this,
                                        new Pair<View, String>(userPic,
                                                ImageViewerActivity.VIEW_NAME_IMAGE));
                                startActivity(intent, activityOptions.toBundle());


                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_text_button:
                sendTextMessage(messageText.getText().toString());
                messageText.setText("");
                break;
            case R.id.send_image_button:
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE);
                break;

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setFonts() {
        messageText.setTypeface(HashUtil.typefaceLatoLight);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final Uri uri = data.getData();
                    Log.d(TAG, "Uri: " + uri.toString());
                    String key = FirebaseUtils.GetDbRef().child(MESSAGES_CHILD).child(extra).push().getKey();
                    Message tempMessage = new Message(null, HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail()), null,
                            uri.toString(), "IMAGE", "error", ServerValue.TIMESTAMP);
                    FirebaseUtils.GetDbRef().child(MESSAGES_CHILD).child(extra).child(key).setValue(tempMessage);
                    StorageReference storageReference = FirebaseStorage.getInstance()
                            .getReference(MESSAGES_CHILD)
                            .child(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail()))
                            .child(key)
                            .child(uri.getLastPathSegment());

                    putImageInStorage(storageReference, uri, key);

                }
            }
        }
    }

    private void putImageInStorage(StorageReference storageReference, Uri uri,
                                   final String key) {

        UploadTask uploadTask = storageReference.putFile(uri);

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                System.out.println("Upload is " + progress + "% done");
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("Upload is paused");
                uploadTask.resume();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Message message = new Message(key,HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail()), null, uri.toString(), "IMAGE", "error", ServerValue.TIMESTAMP);
                FirebaseUtils.GetDbRef().child(MESSAGES_CHILD).child(extra).child(key)
                        .setValue(message);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                try {
                    Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                    Message message =
                            new Message(key, HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail()), null,
                                    downloadUrl.toString(), "IMAGE", null, ServerValue.TIMESTAMP);
                    FirebaseUtils.GetDbRef().child(MESSAGES_CHILD).child(extra).child(key)
                            .setValue(message);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Message message = new Message(key,HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail()), null, uri.toString(), "IMAGE", "error", ServerValue.TIMESTAMP);
                    FirebaseUtils.GetDbRef().child(MESSAGES_CHILD).child(extra).child(key)
                            .setValue(message);
                }
            }
        });
    }

    private void sendTextMessage(String text) {
        String key = FirebaseUtils.GetDbRef().child(MESSAGES_CHILD).child(extra).push().getKey();
        Message message = new Message(key, HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail()), text, null, "TEXT", null, ServerValue.TIMESTAMP);
        //mDatabase.child(MESSAGES_CHILD).push().setValue(message);
        FirebaseUtils.GetDbRef().child(MESSAGES_CHILD).child(extra).child(key).setValue(message, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Data could not be saved " + databaseError.getMessage());
                    Message message = new Message(key, HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail()), text, null, "TEXT", "error", ServerValue.TIMESTAMP);
                } else {
                    System.out.println("Data saved successfully.");
                }
            }
        });
    }

    public void setUpMessages() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        messages.setLayoutManager(mLinearLayoutManager);
        messages.setItemAnimator(new DefaultItemAnimator());
        messages.setHasFixedSize(true);
        Query memberQuery = getQuery();
        Log.e("message", memberQuery.toString());
        mAdapter = new FirebaseRecyclerAdapter<GetMessage, MessageViewHolder>(GetMessage.class, R.layout.item_message,
                MessageViewHolder.class, memberQuery) {
            @Override
            protected void populateViewHolder(final MessageViewHolder viewHolder, final GetMessage message, final int position) {
                final DatabaseReference memberRef = getRef(position);

                final String Key = memberRef.getKey();

                viewHolder.textLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mActionMode != null) {
                            mActionMode.finish();
                        }
                        if (viewHolder.textTime.getVisibility() == View.VISIBLE) {
                            viewHolder.textTime.setVisibility(View.GONE);
                        } else {
                            viewHolder.textTime.setVisibility(View.VISIBLE);
                        }
                    }
                });

                viewHolder.senderPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mActionMode != null) {
                            mActionMode.finish();
                        }
                        FirebaseUtils.GetDbRef().child(MESSAGES_CHILD).child(extra).child(Key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // ...
                                GetMessage message = dataSnapshot.getValue(GetMessage.class);
                                if (message != null) {
                                    Intent intent = new Intent(v.getContext(), ProfileDetailsActivity.class);
                                    intent.putExtra(ProfileDetailsActivity.EXTRA, message.getUId());
                                    FabTransform.addExtras(intent,
                                            ContextCompat.getColor(v.getContext(), R.color.colorPrimary), R.drawable.ic_account);
                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ChatActivity.this, v,
                                            getString(R.string.transition_designer_news_login));
                                    startActivity(intent, options.toBundle());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // ...
                            }
                        });
                    }
                });
                viewHolder.imageLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mActionMode != null) {
                            mActionMode.finish();
                        }
                        FirebaseUtils.GetDbRef().child(MESSAGES_CHILD).child(extra).child(Key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // ...
                                GetMessage message = dataSnapshot.getValue(GetMessage.class);
                                if (message != null) {
                                    FirebaseUtils.GetDbRef().child("users").child(message.getUId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            // ...
                                            User user = dataSnapshot.getValue(User.class);
                                            SimpleDateFormat sfd = new SimpleDateFormat();
                                            if (user != null) {
                                                Intent intent = new Intent(v.getContext(), ImageViewerActivity.class);
                                                intent.putExtra(ImageViewerActivity.EXTRA, user.username + "@" + sfd.format(new Date(message.getTimeStamp())) + "@" + message.getmPicUrl());
                                                ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(ChatActivity.this,
                                                        new Pair<View, String>(viewHolder.messageImage,
                                                                ImageViewerActivity.VIEW_NAME_IMAGE));
                                                startActivity(intent, activityOptions.toBundle());
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            // ...
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // ...
                            }
                        });

                    }
                });

                viewHolder.textLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    // Called when the user long-clicks on someView
                    public boolean onLongClick(View view) {
                        if (mActionMode != null) {
                            return false;
                        }
                        // Start the CAB using the ActionMode.Callback defined above
                        GetMessageModel(Key, viewHolder.itemView);
                        viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
                        return true;
                    }
                });
                viewHolder.imageLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    // Called when the user long-clicks on someView
                    public boolean onLongClick(View view) {
                        if (mActionMode != null) {
                            return false;
                        }
                        // Start the CAB using the ActionMode.Callback defined above

                        GetMessageModel(Key, viewHolder.itemView);
                        viewHolder.itemView.setBackgroundColor(Color.LTGRAY);

                        return true;
                    }
                });
                viewHolder.bindToPost(message, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mActionMode != null) {
                            mActionMode.finish();
                        }
                        viewHolder.uploadImage.setVisibility(View.GONE);
                        viewHolder.imageProgress.setVisibility(View.VISIBLE);
                        Uri uri = Uri.parse(message.getmPicUrl());
                        StorageReference storageReference =
                                FirebaseStorage.getInstance()
                                        .getReference(MESSAGES_CHILD)
                                        .child(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail()))
                                        .child(Key)
                                        .child(uri.getLastPathSegment());

                        putImageInStorage(storageReference, uri, Key);

                    }
                });

            }
        };
        messages.setAdapter(mAdapter);

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                // to the bottom of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    messages.scrollToPosition(positionStart);
                }
            }
        });
    }

    public Query getQuery() {
        return FirebaseUtils.GetDbRef().child(MESSAGES_CHILD).child(extra).limitToLast(100);
    }

    private class Toolbar_ActionMode_Callback implements ActionMode.Callback {

        // Called when the action mode is created; startActionMode() was called
        private Context context;
        private GetMessage message;
        private View view;

        public Toolbar_ActionMode_Callback(Context context, GetMessage message, View view) {
            this.context = context;
            this.message = message;
            this.view = view;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_text, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            if (message.getUId().equals(HashUtil.usernameFromEmail(FirebaseUtils.GetCurrentUserEmail()))) {
                if (!message.getmType().equals("TEXT")) {
                    menu.removeItem(R.id.menu_copy);
                }
            } else {
                if (message.getmType().equals("TEXT")) {
                    menu.removeItem(R.id.menu_delete);
                } else {
                    menu.removeItem(R.id.menu_copy);
                    menu.removeItem(R.id.menu_delete);
                }
            }
            return true; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            try {
                switch (item.getItemId()) {
                    case R.id.menu_copy:
                        HashUtil.copyToClipboard(message.getMText(), ChatActivity.this);
                        return true;
                    case R.id.menu_delete:

                        FirebaseUtils.GetDbRef().child(MESSAGES_CHILD).child(extra).child(message.getMId()).removeValue();
                        mode.finish(); // Action picked, so close the CAB
                        return true;
                    case R.id.menu_share:
                        if (message.getmType().equals("TEXT")) {
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, message.getMText());
                            sendIntent.setType("text/plain");
                            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
                        } else {
                            Uri webpage = Uri.parse(message.getmPicUrl());
                            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                startActivity(intent);
                            }
                        }
                        mode.finish(); // Action picked, so close the CAB
                        return true;
                    default:
                        return false;
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
                return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            view.setBackgroundColor(Color.TRANSPARENT);
            mActionMode = null;
        }
    }

    ;

    public void GetMessageModel(String id, View view) {
        try {
            FirebaseUtils.GetDbRef().child(MESSAGES_CHILD).child(extra).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // ...
                    GetMessage message = dataSnapshot.getValue(GetMessage.class);
                    if (message != null) {
                        mActionMode = startActionMode(new Toolbar_ActionMode_Callback(ChatActivity.this, message, view));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // ...
                }
            });

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
