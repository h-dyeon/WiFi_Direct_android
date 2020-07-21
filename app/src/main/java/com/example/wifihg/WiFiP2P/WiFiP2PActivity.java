package com.example.wifihg.WiFiP2P;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.wifihg.R;

/**
 * WiFiDirectActivity를 WiFiP2PActivity로 모양을 이쁘게 바꿀거임
 */
public class WiFiP2PActivity extends AppCompatActivity implements WifiP2pManager.ChannelListener, PeerListFragment.DeviceActionListener {

    public static final String TAG = "WiFiP2PActivity_TAG"; //log 태그를 위한 변수
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1001;

    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;
    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi_p2p);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        if (!initP2p()) {
            finish();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    WiFiP2PActivity.PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION);
            // After this point you wait for callback in
            // onRequestPermissionsResult(int, String[], int[]) overridden method
        }

        setBtnOnClickListener();
    }

    private boolean initP2p() {
        // Device capability definition check
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT)) {
            Log.e(TAG, "Wi-Fi Direct is not supported by this device.");
            return false;
        }
        // Hardware capability check
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            Log.e(TAG, "Cannot get Wi-Fi system service.");
            return false;
        }
        if (!wifiManager.isP2pSupported()) {
            Log.e(TAG, "Wi-Fi Direct is not supported by the hardware or Wi-Fi is off.");
            return false;
        }
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        if (manager == null) {
            Log.e(TAG, "Cannot get Wi-Fi Direct system service.");
            return false;
        }
        channel = manager.initialize(this, getMainLooper(), null);
        if (channel == null) {
            Log.e(TAG, "Cannot initialize Wi-Fi Direct.");
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION:
                if  (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "Fine location permission is not granted!");
                    finish();
                }
                break;
        }
    }

    public void setBtnOnClickListener() {
        Button btn_isWiFi = (Button)findViewById(R.id.btn_isWiFi);
        btn_isWiFi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (manager != null && channel != null) {
                    // Since this is the system wireless settings activity, it's
                    // not going to send us a result. We will be notified by
                    // WiFiDeviceBroadcastReceiver instead.
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                } else {
                    Log.e(TAG, "channel or manager is null");
                }
            }
        });

        Button btn_search=(Button) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isWifiP2pEnabled) {
                    Toast.makeText(WiFiP2PActivity.this, R.string.p2p_off_warning,
                            Toast.LENGTH_SHORT).show();
                }
                PeerListFragment fragment = (PeerListFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_peer_list);
//                fragment.onInitiateDiscovery(); //progress를 보여주는 과정
                manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(WiFiP2PActivity.this, "Peer 검색 성공",
                                Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(WiFiP2PActivity.this, "Peer검색 실패 : " + reasonCode,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    /**
     * Remove all peers and clear all fields. This is called on
     * BroadcastReceiver receiving a state change event.
     */
//    hdy
    public void resetData() {
        PeerListFragment fragmentList = (PeerListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_peer_list);
        PeerDetailFragment fragmentDetails = (PeerDetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_peer_detail);

        if (fragmentList != null) {
            fragmentList.clearPeers();
        }
        if (fragmentDetails != null) {
            fragmentDetails.resetViews();
        }
    }

    public void resetDetailsView(){
        PeerDetailFragment fragmentDetails = (PeerDetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_peer_detail);
        fragmentDetails.resetViews();
    }

    //Activity의 onResume() 메서드에서 Broadcast Receiver를 등록하고
    @Override
    public void onResume() {
        super.onResume();
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }
    //Activity의 onPause() 메서드에서 등록을 취소합니다.
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }


//    hdy
    @Override
    public void showDetails(WifiP2pDevice device) {
        PeerDetailFragment fragment=(PeerDetailFragment)getSupportFragmentManager().
                findFragmentById(R.id.fragment_peer_detail);
        fragment.showDetails(device);
    }



    @Override
    public void connect(final WifiP2pConfig config) {
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("helloooo", "connect() :" + config.deviceAddress);
                // wWiFiDirectBroadcastReceiver will notify us. Ignore for now.
            }
            @Override
            public void onFailure(int reason) {
                Toast.makeText(WiFiP2PActivity.this, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void disconnect() {
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
            }
            @Override
            public void onSuccess() {
                PeerDetailFragment fragment = (PeerDetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_peer_detail);
                fragment.resetViews();
                fragment.getView().setVisibility(View.GONE);

//                hdy
                PeerListFragment peerListFragment=(PeerListFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_peer_list);
                peerListFragment.notifyData();
            }
        });
    }










    @Override
    public void onChannelDisconnected() {
        Toast.makeText(this, "onChannelDisconnected", Toast.LENGTH_LONG).show();
//        // we will try once more
//        if (manager != null && !retryChannel) {
//            Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
//            resetData();
//            retryChannel = true;
//            manager.initialize(this, getMainLooper(), this);
//        } else {
//            Toast.makeText(this,
//                    "Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
//                    Toast.LENGTH_LONG).show();
//        }
    }






    //connect 시도하던 도중에 취소했을때 함수
    //사용안할거임
    @Override
    public void cancelDisconnect() {
        Toast.makeText(this, "cancelDisconnect", Toast.LENGTH_LONG).show();
//        /*
//         * A cancel abort request by user. Disconnect i.e. removeGroup if
//         * already connected. Else, request WifiP2pManager to abort the ongoing
//         * request
//         */
//        if (manager != null) {
//            final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
//                    .findFragmentById(R.id.frag_list);
//            if (fragment.getDevice() == null
//                    || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
//                disconnect();
//            } else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE
//                    || fragment.getDevice().status == WifiP2pDevice.INVITED) {
//                manager.cancelConnect(channel, new ActionListener() {
//                    @Override
//                    public void onSuccess() {
//                        Toast.makeText(WiFiDirectActivity.this, "Aborting connection",
//                                Toast.LENGTH_SHORT).show();
//                    }
//                    @Override
//                    public void onFailure(int reasonCode) {
//                        Toast.makeText(WiFiDirectActivity.this,
//                                "Connect abort request failed. Reason Code: " + reasonCode,
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        }
    }
}
