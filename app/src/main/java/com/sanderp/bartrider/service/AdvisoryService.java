package com.sanderp.bartrider.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanderp.bartrider.R;
import com.sanderp.bartrider.pojo.advisory.AdvisoryPojo;
import com.sanderp.bartrider.pojo.advisory.Bsa;
import com.sanderp.bartrider.utility.Constants;
import com.sanderp.bartrider.utility.PrefContract;
import com.sanderp.bartrider.utility.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Asynchronously requests advisory data from the BART API and updates a shared preference
 * with a diff of the previous request. BAsed on the diff, posts a device notification if
 * the service was called by an AlarmManager.
 */
public class AdvisoryService extends IntentService {
    private static final String TAG = "AdvisoryService";

    public static final int NOTIFICATION_ID = 101;
    public static final String NOTIFY = "notify";

    private static final String DEFAULT_ADVISORY = "No delays reported.";

    private NotificationManager mNotificationManager;
    private SharedPreferences sharedPrefs;

    private static final ObjectMapper mapper;
    static {
        mapper = new ObjectMapper()
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
                .setDateFormat(new SimpleDateFormat("EEE MMM dd yyyy KK:mm a zzz", Locale.US));
    }

    public AdvisoryService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        sharedPrefs = getSharedPreferences(PrefContract.PREFS_NAME, 0);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            try {
                Set<String> advisorySet = advisoriesToSet(getAdvisories());
                if (intent.getBooleanExtra(NOTIFY, true)) {
                    Log.i(TAG, "Posting notification...");
                    postAdvisoryNotification(getAdvisoriesDiff(advisorySet));
                } else {
                    Log.i(TAG, "Sending broadcast...");
                    Intent localIntent = new Intent(Constants.Broadcast.ADVISORY_SERVICE);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                }
                sharedPrefs.edit()
                        .putString(PrefContract.ADVISORY, advisoriesToString(advisorySet))
                        .putStringSet(PrefContract.PREV_ADVISORY, advisorySet)
                        .apply();
            } catch (IOException e) {
                Log.e(TAG, "Input stream failed.");
                e.printStackTrace();
            }
        }
    }

    private AdvisoryPojo getAdvisories() throws IOException {
        InputStream stream = null;
        String url = Constants.Bart.API_URL + "bsa.aspx?cmd=bsa"
                + "&key=" + getResources().getString(R.string.bartApiKey)
                + "&json=y";
        try {
            Log.i(TAG, "Getting advisories...");
            stream = Utils.getUrlStream(url);
            return mapper.readValue(stream, AdvisoryPojo.class);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    private void postAdvisoryNotification(String advisories) {
        if (!advisories.isEmpty() && !advisories.equals(DEFAULT_ADVISORY)) {
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("BART Advisory")
                    .setContentText(advisories)
                    .setSmallIcon(R.drawable.ic_stat_bart_rider_img)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(advisories))
                    .build();
            mNotificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

    private Set<String> advisoriesToSet(AdvisoryPojo pojo) {
        Set<String> advisorySet = new HashSet<>();
        for (Bsa bsa : pojo.getRoot().getBsa()) {
            advisorySet.add(bsa.getDescription().getCdataSection());
        }
        return advisorySet;
    }

    private String advisoriesToString(Set<String> advisorySet) {
        StringBuilder advisories = new StringBuilder();
        for (String advisory : advisorySet) {
            advisories.append(advisory + "\n\n");
        }
        return advisories.toString().trim();
    }

    private String getAdvisoriesDiff(Set<String> advisorySet) {
        Set<String> prevAdvisorySet = sharedPrefs.getStringSet(PrefContract.PREV_ADVISORY, new HashSet<String>());
        Log.d(TAG, advisoriesToString(advisorySet) + " | " + advisoriesToString(prevAdvisorySet));
        StringBuilder advisories = new StringBuilder();
        for (String advisory : advisorySet) {
            if (!prevAdvisorySet.contains(advisory)) {
                advisories.append(advisory + "\n\n");
            }
        }
        return advisories.toString().trim();
    }
}
