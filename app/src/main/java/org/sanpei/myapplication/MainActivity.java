package org.sanpei.myapplication;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Build;
import android.preference.ListPreference;
import android.provider.Settings;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;

public class MainActivity extends ActionBarActivity {

    private boolean mDebug = false;
    ListView lv;
    private final int expectedScreenSaverTime = 60 * 1000;
    private boolean mBluetoothStatus;
    private boolean mWiFiStatus;

    private void toastWithDebugOption(String str) {
        if (mDebug) {
            toastWithoutDebugOption(str);
        }
    }

    private void toastWithoutDebugOption(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    public void checkSystemStatus(View view) {
        checkSystemStatus();
    }

    public boolean getWifiStatus() {
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }

    public boolean getNfcStatus() {
        NfcManager manager = (NfcManager) getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        if (adapter != null && adapter.isEnabled()) {
            return true;
        } else {
            return false;
        }
    }

    public String getLocationStatus() {

        LocationManager locationManager = (LocationManager) this.getSystemService(Service.LOCATION_SERVICE);
        String result = "";

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            result = result +"GPS";
        }
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            if (result != "") {
                result = result + ",";
            }
            result = result + "Wi-Fi";
        }
        return result;

    }

    public boolean getBluetoothStatus() {
    BluetoothAdapter Bt = BluetoothAdapter.getDefaultAdapter();

        return Bt.isEnabled();

    }

    public boolean getTetheringStatus() {
        try {
            WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            Method method = wifi.getClass().getMethod("isWifiApEnabled");
            return  ("true".equals(method.invoke(wifi).toString()));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean getMobileDataConnectionStatus() {
        try {
            ConnectivityManager cm = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo.State state = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
            if (state == NetworkInfo.State.DISCONNECTED || state == NetworkInfo.State.UNKNOWN) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean getBatterySaver() {
        return false;
    }

    public int getScreenSaverStatus() {
        ContentResolver contentresolver = getContentResolver();
        try {
            return Settings.System.getInt(contentresolver,
                    Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (Settings.SettingNotFoundException e) {
            return 0;
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    public void checkSystemStatus() {
        /*
         *  Check Wi-Fi Status
         */
        toastWifiStatus();
        /*
         *  Check NFC Status
         */
        if (getNfcStatus()) {
            toastWithoutDebugOption("NFC Enabled");
        } else {
            toastWithDebugOption("NFC Disabled");
        }
        /*
         *  Check Location Status
         */
        String locationStatus = getLocationStatus();
        if (locationStatus != "Wi-Fi") {
            toastWithoutDebugOption("Location("+locationStatus+") Enabled");
        } else {
            toastWithDebugOption("Location Disabled");
        }
        /*
         *  Check bluetooth Status
         */
        if (getBluetoothStatus()) {
            toastWithoutDebugOption("Bluetooth Enabled");
        } else {
            toastWithDebugOption("Bluetooth Disabled");
        }
        /*
         *  Check Tethering Status
         */

        if (getTetheringStatus()) {
                toastWithoutDebugOption("Tethering Enabled");
        } else {
            toastWithDebugOption("Tethering Disabled");
        }

        /*
         * Check Screen Saver Timeout
         *  http://relog.xii.jp/mt5r/2011/04/android-10.html
         */
        int millis = getScreenSaverStatus();
        if (millis != expectedScreenSaverTime) {
            toastWithoutDebugOption("Screen Saver Timeout:" + String.valueOf(millis / 1000 / 60) + "min");
        }
        /*
         * Check Mobile Data connection status
         *
         */
        if (getMobileDataConnectionStatus()) {
            toastWithoutDebugOption("Mobile Data Connection: enabled");
        } else {
            toastWithDebugOption("Mobile Data Connection: disabled");
        }
    }

    private void setBackgroundColorEnable(View view) {
        view.setBackgroundColor(Color.rgb(255,128,0));
    }
    private void setBackgroundColorDisbable(View view) {
        view.setBackgroundColor(Color.LTGRAY);
    }

    private ArrayAdapter<String> setAdapter() {
        String locationStatus = getLocationStatus();
        String locationString;
        if (locationStatus != "") {
            locationString = "Location("+locationStatus+") Enabled";
        } else {
            locationString = "Location: Disabled";
        }

        String wifiString;
        if (getWifiStatus()) {
            WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            wifiString = "Wi-Fi: Enabled" + wifiInfo.getSSID();
        } else {
            wifiString = "Wi-Fi: Disabled";
        }

        String screenSaverTimeoutString;
        screenSaverTimeoutString = "Screen Saver Timeout: "+ String.valueOf(getScreenSaverStatus()/60/1000) + "min";

        String[] members = {wifiString, "NFC", locationString, "Bluetooth",
                "Tethering", screenSaverTimeoutString, "Mobile Data connection", "Battery Saver"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1, members) {
            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (position == 0) {
                    if (mWiFiStatus) {
                        setBackgroundColorEnable(view);

                    } else {
                        setBackgroundColorDisbable(view);
                    }
                } else if (position == 1) {
                    if (getNfcStatus()) {
                        setBackgroundColorEnable(view);
                    } else {
                        setBackgroundColorDisbable(view);
                    }
                } else if (position == 2) {
                    if (!getLocationStatus().equals("Wi-Fi")) {
                        setBackgroundColorEnable(view);
                    } else {
                        setBackgroundColorDisbable(view);
                    }
                } else if (position == 3) {
                    if (mBluetoothStatus) {
                        setBackgroundColorEnable(view);
                    } else {
                        setBackgroundColorDisbable(view);
                    }
                } else if (position == 4) {
                    if (getTetheringStatus()) {
                        setBackgroundColorEnable(view);
                    } else {
                        setBackgroundColorDisbable(view);
                    }
                } else if (position == 5) {
                    if (getScreenSaverStatus() != expectedScreenSaverTime) {
                        setBackgroundColorEnable(view);
                    } else {
                        setBackgroundColorDisbable(view);
                    }
                } else if (position == 6) {
                    if (getMobileDataConnectionStatus()) {
                        setBackgroundColorEnable(view);
                    } else {
                        setBackgroundColorDisbable(view);
                    }
                } else if (position == 7) {
                    if (getBatterySaver()) {
                        setBackgroundColorEnable(view);
                    } else {
                        setBackgroundColorDisbable(view);
                    }
                }

                return view;
            }
        };
        return adapter;
    }

    private void getDeviceStatus() {
        mBluetoothStatus = getBluetoothStatus();
        mWiFiStatus = getWifiStatus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        getDeviceStatus();

        setContentView(R.layout.activity_main);

        lv = (ListView) findViewById(R.id.listView1);

        lv.setAdapter(setAdapter());

        //リスト項目がクリックされた時の処理
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;

                String item = (String) listView.getItemAtPosition(position);
                if (position == 1) { // NFC
                    // http://stackoverflow.com/questions/5945100/android-changing-nfc-settings-on-off-programmatically
//                        Toast.makeText(getApplicationContext(), "Please activate NFC and press Back to return to the application!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                } else if (position == 5) { // Screen Saver TImeout
                    startActivity(new Intent(Settings.ACTION_DISPLAY_SETTINGS));
                } else if (position == 4) { // Tethering
                    Intent i = new Intent(Intent.ACTION_MAIN);
                    i.setClassName(
                            "com.android.settings",
                            "com.android.settings.TetherSettings");
                    try {
                        startActivity(i);
                    } catch (Exception e) {
                    }
                } else if (position == 3) { // Bluetooth
                    BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
                    if (getBluetoothStatus()) {
                        ba.disable();
                        mBluetoothStatus = false;
                    } else {
                        ba.enable();
                        mBluetoothStatus = true;

                    }
                    lv.setAdapter(setAdapter());
                } else if (position == 2) { // Location
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                } else if (position == 6) { // Mobile Data Connection
                    startActivity(new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS));
                } else if (position == 0) { // Wi-Fi
                    WifiManager wifi;
                    wifi = (WifiManager)getSystemService(WIFI_SERVICE);
                    if (getWifiStatus()) {
                        wifi.setWifiEnabled(false);
                        mWiFiStatus = false;
                    } else {
                        wifi.setWifiEnabled(true);
                        mWiFiStatus = true;
                    }
                    lv.setAdapter(setAdapter());
                } else if (position == 7) { // Battery Saver
                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                }
            }
        });
    }

    private void toastWifiStatus() {
        if (getWifiStatus()) {
            toastWithoutDebugOption("Wi-Fi Enabled");
        } else {
            toastWithDebugOption("Wi-Fi Disabled");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (false) {
            if (getNfcStatus() == false
                    && getLocationStatus().equals("")
                    && getBluetoothStatus() == false
                    && getTetheringStatus() == false
                    && getScreenSaverStatus() == expectedScreenSaverTime
                    && getMobileDataConnectionStatus() == false
                    ) {
                toastWifiStatus();
                finish();
            }
        }
        getDeviceStatus();
        lv.setAdapter(setAdapter());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
