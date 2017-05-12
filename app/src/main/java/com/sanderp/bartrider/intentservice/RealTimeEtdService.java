package com.sanderp.bartrider.intentservice;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.sanderp.bartrider.pojo.realtimeetd.RealTimeEtdPojo;
import com.sanderp.bartrider.utility.Constants;
import com.sanderp.bartrider.utility.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class RealTimeEtdService extends IntentService {
    private static final String TAG = "RealTimeEtdService";

    public static final String OBJECT_LIST = "objectList";
    public static final String RESULT = "result";

    private static DynamoDBMapper mapper;

    public RealTimeEtdService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mapper = new DynamoDBMapper(Utils.getDynamoDbClient(this));
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Intent localIntent = new Intent(Constants.Broadcast.REAL_TIME_ETD_SERVICE)
                    .putExtra(RESULT, getRealTimeEtd(intent.getBundleExtra(OBJECT_LIST)));
            Log.d(TAG, "Sending broadcast from service.");
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
        }
    }

    private HashMap<String, RealTimeEtdPojo> getRealTimeEtd(Bundle bundle) {
        Log.i(TAG, "Getting real-time etds...");
        Map<String, List<Object>> pojoMap = mapper.batchLoad((List<Object>) bundle.getSerializable(OBJECT_LIST));
        HashMap<String, RealTimeEtdPojo> headMap = new HashMap<>();
        for (Object object : pojoMap.get("REAL_TIME_ETD")) {
            RealTimeEtdPojo pojo = (RealTimeEtdPojo) object;
            if (!headMap.containsKey(pojo.getHeadAbbr())) headMap.put(pojo.getHeadAbbr(), pojo);
        }
        return headMap;
    }
}
