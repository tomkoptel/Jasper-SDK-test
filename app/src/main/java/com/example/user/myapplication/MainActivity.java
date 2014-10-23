package com.example.user.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.jaspersoft.android.sdk.client.JsRestClient;
import com.jaspersoft.android.sdk.client.JsServerProfile;
import com.jaspersoft.android.sdk.client.async.JsXmlSpiceService;
import com.jaspersoft.android.sdk.client.async.request.cacheable.GetServerInfoRequest;
import com.jaspersoft.android.sdk.client.oxm.server.ServerInfo;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;


public class MainActivity extends Activity {

    private static final String KEY_ALIAS = "ALIAS";
    private static final String KEY_SERVER_URL = "SERVER_URL";
    private static final String KEY_ORGANIZATION = "ORGANIZATION";
    private static final String KEY_USERNAME = "USERNAME";
    private static final String KEY_PASSWORD = "PASSWORD";

    private final SpiceManager jsSpiceManager = new SpiceManager(JsXmlSpiceService.class);
    private final JsRestClient mJsRestClient = new JsRestClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize(getCurrentProfile());
        GetServerInfoRequest request = new GetServerInfoRequest(mJsRestClient);
        GetServerInfoListener listener = new GetServerInfoListener(this);
        long cacheExpiryDuration = -1; // SettingsActivity.getRepoCacheExpirationValue(this);
        getSpiceManager().execute(request, request.createCacheKey(), cacheExpiryDuration, listener);

    }

    public void initialize (ContentValues profile) {
        long profileId = -1;
        JsServerProfile serverProfile = new JsServerProfile(profileId, profile.getAsString(KEY_ALIAS), profile.getAsString(KEY_SERVER_URL),
                profile.getAsString(KEY_ORGANIZATION), profile.getAsString(KEY_USERNAME), profile.getAsString(KEY_PASSWORD));
        mJsRestClient.setServerProfile(serverProfile);
    }

    public ContentValues getCurrentProfile() {
        ContentValues values = new ContentValues();

        values.put(KEY_ALIAS, "Mobile Demo");
        values.put(KEY_SERVER_URL, "http://mobiledemo.jaspersoft.com/jasperserver-pro");
        values.put(KEY_ORGANIZATION, "organization_1");
        values.put(KEY_USERNAME, "phoneuser");
        values.put(KEY_PASSWORD, "phoneuser");

        return values;
    }

    @Override
    protected void onStart() {
        if (!jsSpiceManager.isStarted())
            jsSpiceManager.start(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (jsSpiceManager.isStarted())
            jsSpiceManager.shouldStop();
        super.onStop();
    }

    public SpiceManager getSpiceManager() {
        return jsSpiceManager;
    }

    private class GetServerInfoListener implements RequestListener<ServerInfo> {
        private final Context mContext;

        public GetServerInfoListener(Context context) {
            mContext = context;
        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(mContext, "ServerInfo failed: " + spiceException.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(ServerInfo serverInfo) {
            Toast.makeText(mContext, "ServerInfo received", Toast.LENGTH_SHORT).show();
        }
    }
}
