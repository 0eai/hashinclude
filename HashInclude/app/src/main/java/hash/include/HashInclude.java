package hash.include;


import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
public class HashInclude extends Application {

    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        HashInclude.context = getApplicationContext();
        Log.e("TheApp", "application created");
    }
    public static Context getAppContext() {
        return HashInclude.context;
    }
}