package com.example.wifihg.WiFiP2P;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import com.example.wifihg.R;


public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private WiFiP2PActivity activity;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       WiFiP2PActivity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            //기기에서 Wi-Fi P2P가 활성화되었거나 비활성화되었는지 브로드캐스트합니다.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                activity.setIsWifiP2pEnabled(true);
            } else {
                activity.setIsWifiP2pEnabled(false);
                activity.resetData(); //기존에 있던 peer들을 모두 clear, 초기화,
            }
            Log.d(WiFiP2PActivity.TAG, "P2P state changed - " + state);
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            //discoverPeers()를 호출할 때 브로드캐스트합니다.
            // 일반적으로는 이 인텐트를 애플리케이션에서 처리할 경우, requestPeers()를 호출하여 피어의 업데이트된 목록을 가져올 것입니다.
            // requestPeers => callback on PeerListListener.onPeersAvailable()
            if (manager != null) {
                manager.requestPeers(channel,
                        (WifiP2pManager.PeerListListener) activity.getSupportFragmentManager().findFragmentById(R.id.fragment_peer_list));
            }
            Log.d("helloooo", "onReceive : 피어변경");
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            //기기의 Wi-Fi 연결 상태가 변경되면 브로드캐스트합니다.
            if (manager == null) {
                return;
            }
            Log.d("helloooo", "onReceive : 기기의 Wi-Fi 연결 상태가 변경");
            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                // we are connected with the other device, request connection info to find group owner IP
                //연결을 위한 정보를 얻어옴
                PeerDetailFragment fragment = (PeerDetailFragment) activity.getSupportFragmentManager().findFragmentById(R.id.fragment_peer_detail);
                manager.requestConnectionInfo(channel, fragment);
                Log.d("helloooo", "onReceive : 기기의 Wi-Fi 연결 상태가 변경, isConnected");
            } else {
                Log.d("helloooo", "onReceive : 기기의 Wi-Fi 연결 상태가 변경, disConnect");
                // It's a disconnect
//                activity.resetData();
//                if (manager != null) {
//                    manager.requestPeers(channel,
//                            (WifiP2pManager.PeerListListener) activity.getSupportFragmentManager().findFragmentById(R.id.fragment_peer_list));
//                }
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            Log.d("helloooo", "onReceive : WIFI_P2P_THIS_DEVICE_CHANGED_ACTION, 기기상세정보 변경");
            //기기의 상세 정보(예: 기기 이름)가 변경되었는지 브로드캐스트합니다.
            PeerListFragment fragment = (PeerListFragment) activity.getSupportFragmentManager().findFragmentById(R.id.fragment_peer_list);
            fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
                    WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
        }

    }
}
