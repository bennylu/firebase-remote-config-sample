package com.bstudio.firebase_remote_config;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "FirebaseRemoteConfig";

    public static final long REMOTE_CONFIG_CACHE_EXPIRATION = 3600; // one hour

    public static final String REMOTE_CONFIG_DEBUG_ENABLED = "debug_enabled";

    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get remote config instance
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        /*
        Enable dev mode.
        Fetching from the server is limited to 5 requests per hour when dev mode is off
        */
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG).build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        // Default remote config values
        mFirebaseRemoteConfig.setDefaults(R.xml.default_remote_config);

        // Set cache expiration value to 0 for dev mode
        long cacheExpiration = REMOTE_CONFIG_CACHE_EXPIRATION;
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        // Fetch values. The default cache expiration is 12 hours
        mFirebaseRemoteConfig.fetch(cacheExpiration).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Fetch Succeeded");

                            // Activated fetched values
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            Log.d(TAG, "Fetch failed");
                        }

                        // handle values
                        handleRemoteConfig();
                    }
                });
    }

    private void handleRemoteConfig() {
        Log.d(TAG, "debug=" +
                mFirebaseRemoteConfig.getString(REMOTE_CONFIG_DEBUG_ENABLED));
    }

}
