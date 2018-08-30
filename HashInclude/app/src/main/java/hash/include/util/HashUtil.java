package hash.include.util;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import hash.include.HashInclude;
import hash.include.R;
import hash.include.activity.ProfileDetailsActivity;
import hash.include.activity.SearchActivity;
import hash.include.model.Feedback;
import hash.include.transitions.FabTransform;

public class HashUtil {

    public static int MY_BLUE;
    private static final int RC_PHOTO_PICKER = 2;
    public static Typeface typefaceLatoRegular = null;
    public static Typeface typefaceLatoHairline = null;
    public static Typeface typefaceLatoLight = null;
    public static Typeface typefaceComfortaaBold = null;
    public static Typeface typefaceComfortaaRegular = null;
    public static Typeface typefaceComfortaaThin = null;
    public static final String DEVELOPER_KEY = "AIzaSyAlqIHDR45LEaGeLrKgbhwlKilskFD3MOk";
    private static ClipboardManager myClipboard;
    private static ClipData myClip;
    private static HashUtil ourInstance = new HashUtil();
    private static boolean exceed = false;
    private HashUtil() {
    }

    public static void init(Context context) {

        typefaceLatoRegular = Typeface.createFromAsset(
                context.getAssets(), "fonts/Lato-Regular.ttf");
        typefaceLatoHairline = Typeface.createFromAsset(
                context.getAssets(), "fonts/Lato-Hairline.ttf");
        typefaceLatoLight = Typeface.createFromAsset(
                context.getAssets(), "fonts/LatoLatin-Light.ttf");
        typefaceComfortaaBold = Typeface.createFromAsset(
                context.getAssets(), "fonts/Comfortaa_Bold.ttf");
        typefaceComfortaaRegular = Typeface.createFromAsset(
                context.getAssets(), "fonts/Comfortaa_Regular.ttf");
        typefaceComfortaaThin = Typeface.createFromAsset(
                context.getAssets(), "fonts/Comfortaa_Thin.ttf");

    }

    public static Typeface GetTypeface() {
        if (typefaceLatoLight == null) init(HashInclude.getAppContext());
        if ("en".equals(Locale.getDefault().getLanguage()))
            return typefaceLatoLight;
        if ("zh".equals(Locale.getDefault().getLanguage()))
            return Typeface.DEFAULT;
        return typefaceLatoLight;
    }

    public static void copyToClipboard(String content, Context context) {
        myClip = ClipData.newPlainText("text", content);
        myClipboard.setPrimaryClip(myClip);
        Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show();
    }

    public static HashUtil getInstance() {
        if (ourInstance == null || typefaceLatoLight == null || typefaceLatoHairline == null) {
            ourInstance = new HashUtil();
            init(HashInclude.getAppContext());
        }
        return ourInstance;
    }

    public static void loadImageInImageView(ImageView view, String picUrl) {
        try {
            Glide.with(view.getContext())
                    .load(picUrl)
                    .into(view);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static void loadImageInCircularImageView(CircleImageView view, String picUrl) {
        try {
            Glide.with(view.getContext())
                    .load(picUrl)
                    .into(view);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static void startSearchActivity(Activity activity, String search, View searchMenuView) {
        Intent intent = new Intent(activity, SearchActivity.class);
        intent.putExtra(SearchActivity.SRAECH, search);
        Bundle options = ActivityOptions.makeSceneTransitionAnimation(activity, searchMenuView,
                activity.getString(R.string.transition_search_back)).toBundle();
        activity.startActivity(intent, options);
    }

    public static void showUserTypeDialog(Context context, String Key) {
        Dialog userTypeDialog;userTypeDialog = new Dialog(context, R.style.ThemeDialogCustom);
        userTypeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        userTypeDialog.setContentView(R.layout.user_type_dialog);
        userTypeDialog.setCancelable(true);
        TextView title = userTypeDialog.findViewById(R.id.dialog_card_title);
        title.setTypeface(HashUtil.GetTypeface());
        final RadioButton r1 = userTypeDialog.findViewById(R.id.super_admin_radio);
        r1.setTypeface(HashUtil.GetTypeface());
        final RadioButton r2 = userTypeDialog.findViewById(R.id.admin_radio);
        r2.setTypeface(HashUtil.GetTypeface());
        final RadioButton r3 = userTypeDialog.findViewById(R.id.gold_radio);
        r3.setTypeface(HashUtil.GetTypeface());
        final RadioButton r4 = userTypeDialog.findViewById(R.id.elite_radio);
        r4.setTypeface(HashUtil.GetTypeface());
        final RadioButton r5 = userTypeDialog.findViewById(R.id.user_radio);
        r5.setTypeface(HashUtil.GetTypeface());
        Button cancelButton = userTypeDialog.findViewById(R.id.dialog_cancel);
        cancelButton.setTypeface(HashUtil.GetTypeface());
        Button okButton = userTypeDialog.findViewById(R.id.dialog_ok);
        okButton.setTypeface(HashUtil.GetTypeface());
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userTypeDialog.dismiss();
            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (r1.isChecked()) {
                    FirebaseUtils.GetDbRef().child("users-values").child(Key).child("userType").setValue("Super Admin");
                    Toast.makeText(v.getContext(), "Super Admin", Toast.LENGTH_SHORT).show();
                } else if (r2.isChecked()) {
                    FirebaseUtils.GetDbRef().child("users-values").child(Key).child("userType").setValue("Admin");
                    Toast.makeText(v.getContext(), "Admin", Toast.LENGTH_SHORT).show();
                } else if (r3.isChecked()) {
                    FirebaseUtils.GetDbRef().child("users-values").child(Key).child("userType").setValue("Gold");
                    Toast.makeText(v.getContext(), "Gold", Toast.LENGTH_SHORT).show();
                } else if (r4.isChecked()) {
                    FirebaseUtils.GetDbRef().child("users-values").child(Key).child("userType").setValue("Elite");
                    Toast.makeText(v.getContext(), "Elite", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseUtils.GetDbRef().child("users-values").child(Key).child("userType").setValue("User");
                    Toast.makeText(v.getContext(), "User", Toast.LENGTH_SHORT).show();
                }
                userTypeDialog.dismiss();
            }
        });
        userTypeDialog.show();
    }

    public static void showFeedbackDialog(Context context) {
        final int min = 1;
        final int max = 400;
        Dialog feedbackDialog = new Dialog(context, R.style.ThemeDialogCustom);
        feedbackDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        feedbackDialog.setContentView(R.layout.dialog_feedback);
        feedbackDialog.setCancelable(true);
        TextView title = feedbackDialog.findViewById(R.id.dialog_card_title);
        title.setTypeface(HashUtil.GetTypeface());
        TextView desc = feedbackDialog.findViewById(R.id.feedback_desc);
        desc.setTypeface(HashUtil.GetTypeface());
        TextView helper = feedbackDialog.findViewById(R.id.helper);
        helper.setTypeface(HashUtil.GetTypeface());
        TextView number = feedbackDialog.findViewById(R.id.number);
        number.setTypeface(HashUtil.GetTypeface());
        final EditText input = feedbackDialog.findViewById(R.id.edittext);
        input.setTypeface(HashUtil.GetTypeface());
        Button cancelButton = feedbackDialog.findViewById(R.id.dialog_cancel);
        cancelButton.setTypeface(HashUtil.GetTypeface());
        Button sendButton = feedbackDialog.findViewById(R.id.dialog_send);
        sendButton.setTypeface(HashUtil.GetTypeface());
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedbackDialog.dismiss();
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exceed) {
                    new MaterialDialog.Builder(v.getContext())
                            .title("Words Invalid")
                            .content("The number of word is invalid (1-400).")
                            .positiveText("ok")
                            .show();
                } else {
                    Toast.makeText(v.getContext(), "Posting...", Toast.LENGTH_SHORT).show();
                    String uid = FirebaseUtils.GetCurrentUserUid();
                    String key = FirebaseUtils.GetDbRef().child("feedback").child(uid).push().getKey();
                    Feedback feedback = new Feedback(uid, key, input.getText().toString(), ServerValue.TIMESTAMP);
                    Map<String, Object> feedbackValues = feedback.toMap();

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/feedback/" + uid + "/" + key, feedbackValues);
                    FirebaseUtils.GetDbRef().updateChildren(childUpdates);
                    Toast.makeText(v.getContext(), "Posted", Toast.LENGTH_SHORT).show();
                    input.setText("");
                    feedbackDialog.dismiss();
                }
            }
        });
        input.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                count =input.getText().toString().length();
                number.setText(count + "/" + min + "-" + max);
                if (min <= count && count <= max) {
                    number.setTextColor(ContextCompat.getColor(feedbackDialog.getContext(), R.color.button_grey));
                    exceed = false;
                } else {
                    number.setTextColor(ContextCompat.getColor(feedbackDialog.getContext(), R.color.red));
                    exceed = true;
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        feedbackDialog.show();
    }

    public static void showAboutDialog(Activity activity,Context context) {
        Dialog aboutDialog = new Dialog(context, R.style.ThemeDialogCustom);
        aboutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        aboutDialog.setContentView(R.layout.dialog_about);
        aboutDialog.setCancelable(true);
        TextView c00 = aboutDialog.findViewById(R.id.content_00);
        c00.setTypeface(HashUtil.GetTypeface());
        TextView c11 = aboutDialog.findViewById(R.id.content_11);
        c11.setTypeface(HashUtil.GetTypeface());
        TextView c33 = aboutDialog.findViewById(R.id.content_33);
        c33.setTypeface(HashUtil.GetTypeface());
        TextView c44 = aboutDialog.findViewById(R.id.content_44);
        c44.setTypeface(HashUtil.GetTypeface());
        TextView c0 = aboutDialog.findViewById(R.id.content_0);
        c0.setTypeface(HashUtil.GetTypeface());
        TextView c1 = aboutDialog.findViewById(R.id.content_1);
        c1.setTypeface(HashUtil.GetTypeface());
        TextView c2 = aboutDialog.findViewById(R.id.content_2);
        c2.setTypeface(HashUtil.GetTypeface());
        TextView c3 = aboutDialog.findViewById(R.id.content_3);
        c3.setTypeface(HashUtil.GetTypeface());
        TextView c4 = aboutDialog.findViewById(R.id.content_4);
        c4.setTypeface(HashUtil.GetTypeface());
        ((MaterialRippleLayout) aboutDialog.findViewById(R.id.layout_1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ProfileDetailsActivity.class);
                intent.putExtra(ProfileDetailsActivity.EXTRA, "iamanspire");
                FabTransform.addExtras(intent,
                        ContextCompat.getColor(v.getContext(), R.color.colorPrimary), R.drawable.ic_add_dark);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity, v,
                        activity.getString(R.string.transition_designer_news_login));
                activity.startActivity(intent, options.toBundle());
               aboutDialog.dismiss();
            }
        });
        ((MaterialRippleLayout)aboutDialog.findViewById(R.id.layout_2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutDialog.dismiss();
                v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Anspire")));
            }
        });
        ((MaterialRippleLayout)aboutDialog.findViewById(R.id.layout_3)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutDialog.dismiss();
                v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://ankit-s.firebaseapp.com/")));
            }
        });
        ((MaterialRippleLayout)aboutDialog.findViewById(R.id.layout_4)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutDialog.dismiss();
                copyToClipboard("iamanspire@gmail.com", v.getContext());
            }
        });

        ((MaterialRippleLayout)aboutDialog.findViewById(R.id.layout_33)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutDialog.dismiss();
                v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://hash-include-club.firebaseapp.com/")));
            }
        });
        ((MaterialRippleLayout)aboutDialog.findViewById(R.id.layout_44)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutDialog.dismiss();
                copyToClipboard("hashinclude17@gmail.com", v.getContext());
            }
        });
        Button cancelButton = aboutDialog.findViewById(R.id.dialog_cancel);
        cancelButton.setTypeface(HashUtil.GetTypeface());
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutDialog.dismiss();
            }
        });

        aboutDialog.show();
    }
    public static String usernameFromEmail(String email) {
        if (email.contains("@")) {
            email = email.split("@")[0];
        }
        if (email.contains(".")){
            return email.replace(".", "-");
        } else {
            return email;
        }
    }

    public static String emailFromUsername(String username) {

        if (username.contains(".")){
            return username.replace(".", "-") + ".gmail.com";
        } else {
            return username + ".gmail.com";
        }
    }

    public static void chooseImage(Activity activity) {
        new MaterialDialog.Builder(activity)
                .iconRes(R.drawable.ic_light_bulb)
                .typeface(HashUtil.GetTypeface(), HashUtil.GetTypeface())
                .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                .title(R.string.change_logo_title)
                .content(R.string.change_logo_content)
                .positiveText(R.string.from_gallery)
                .neutralText(R.string.cancel)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog,
                                        @NonNull DialogAction which) {
                        if (which == DialogAction.POSITIVE) {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/jpeg");
                            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                            activity.startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
                        }
                    }
                }).show();

    }
}
