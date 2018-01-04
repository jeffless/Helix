package com.news.helix;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.bridgefy.sdk.client.Bridgefy;
import com.bridgefy.sdk.client.BridgefyClient;
import com.bridgefy.sdk.client.Message;
import com.bridgefy.sdk.client.MessageListener;
import com.bridgefy.sdk.client.RegistrationListener;
import com.bridgefy.sdk.client.StateListener;
import com.google.gson.Gson;
import com.news.helix.adapter.FeedAdapter;
import com.news.helix.adapter.OfflineFeedAdapter;
import com.news.helix.common.HTTPDataHandler;
import com.news.helix.model.RSSObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;
    RecyclerView recyclerView;
    RSSObject rssObject;
    private final String RSS_Link = "http://rss.nytimes.com/services/xml/rss/nyt/HomePage.xml";
    private final String RSS_To_JSON_API = "https://api.rss2json.com/v1/api.json?rss_url=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_bar);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (isThingsDevice(this)) {
            bluetoothAdapter.enable();
        }

        Bridgefy.initialize(getApplicationContext(), "60771531-18ed-4bc8-bac1-df3908df319c", new RegistrationListener() {
            @Override
            public void onRegistrationSuccessful(BridgefyClient bridgefyClient) {
                super.onRegistrationSuccessful(bridgefyClient);

                Log.wtf("mai_error", "successful registration");
                Bridgefy.start(messageListener, stateListener);
            }

            @Override
            public void onRegistrationFailed(int errorCode, String message) {
                super.onRegistrationFailed(errorCode, message);

                Log.wtf("mai_error", "failed registration");
            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("News");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        if (isNetworkAvailable()) loadRSS();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Bridgefy.stop();
    }

    private void loadRSS() {
        AsyncTask<String, String, String> loadRSSAsync = new AsyncTask<String, String, String>() {
            ProgressDialog mDialog = new ProgressDialog(MainActivity.this);

            @Override
            protected void onPreExecute() {
                mDialog.setMessage("Please wait...");
                mDialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                String result;
                HTTPDataHandler http = new HTTPDataHandler();
                result = http.GetHTTPData(strings[0]);
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                mDialog.dismiss();
                rssObject = new Gson().fromJson(s, RSSObject.class);
                FeedAdapter adapter = new FeedAdapter(rssObject, getBaseContext());
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                prepareMessage();
            }
        };

        StringBuilder url_get_data = new StringBuilder(RSS_To_JSON_API);
        url_get_data.append(RSS_Link);
        loadRSSAsync.execute(url_get_data.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_refresh) {
            if (isNetworkAvailable()) loadRSS();
        }
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.news) {

            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);

        } else if (id == R.id.chat) {

            Intent i = new Intent(this, MainActivityChat.class);
            startActivity(i);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean isThingsDevice(Context context) {
        final PackageManager pm = context.getPackageManager();
        return pm.hasSystemFeature("android.hardware.type.embedded");
    }

    StateListener stateListener = new StateListener() {
        @Override
        public void onStarted() {
            super.onStarted();

            if (isThingsDevice(getApplicationContext())) {

            }

            final Handler handler = new Handler();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (rssObject != null) prepareMessage();
                    handler.postDelayed(this, 1000);
                }
            });
        }

        @Override
        public void onStartError(String message, int errorCode) {
            super.onStartError(message, errorCode);

            Bridgefy.start(messageListener, stateListener);

            switch (errorCode) {
                case (StateListener.INSUFFICIENT_PERMISSIONS):
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                    break;
                case (StateListener.LOCATION_SERVICES_DISABLED):
                    break;
            }
        }

        @Override
        public void onStopped() {
            super.onStopped();
        }
    };

    MessageListener messageListener = new MessageListener() {
        @Override
        public void onBroadcastMessageReceived(Message message) {
            super.onBroadcastMessageReceived(message);

            HashMap<String, Object> messageContent = message.getContent();
            int count = (int) messageContent.get("count");

            ArrayList<String> title = new ArrayList<>();
            ArrayList<String> date = new ArrayList<>();
            ArrayList<String> content = new ArrayList<>();


            for (int i = 0; i < count; i++) {
                title.add((String) messageContent.get("title" + i));
                date.add((String) messageContent.get("date" + i));
                content.add((String) messageContent.get("content" + i));
            }

            OfflineFeedAdapter adapter = new OfflineFeedAdapter(title, date, content, count, getBaseContext());
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Bridgefy.start(messageListener, stateListener);
        }
    }

    void prepareMessage() {
        HashMap<String, Object> data = new HashMap<>();

        Bridgefy.start(messageListener, stateListener);
        
        data.put("count", rssObject.items.size());

        for (int i = 0; i < rssObject.items.size(); i++) {
            data.put("title" + i, rssObject.getItems().get(i).getTitle());
            data.put("date" + i, rssObject.getItems().get(i).getPubDate());
            data.put("content" + i, rssObject.getItems().get(i).getContent());
        }

        Bridgefy.sendBroadcastMessage(data);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
