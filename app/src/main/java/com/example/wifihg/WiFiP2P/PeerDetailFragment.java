package com.example.wifihg.WiFiP2P;

import android.content.Intent;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.wifihg.R;

import static android.app.Activity.RESULT_OK;


public class PeerDetailFragment extends Fragment implements WifiP2pManager.ConnectionInfoListener {
    protected static final int CHOOSE_FILE_RESULT_CODE = 20;
    protected static final int CHOOSE_STRING_RESULT_CODE = 100;
    private View mContentView = null;
    private WifiP2pDevice device;
    private WifiP2pInfo info;
    //    ProgressDialog progressDialog = null;

    public PeerDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_peer_detail, container, false);

        mContentView.findViewById(R.id.detail_btn_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(device.status==WifiP2pDevice.AVAILABLE){
                    Log.d("helloooo", "connect버튼누름 : avaiable");
                    WifiP2pConfig config = new WifiP2pConfig();
                    config.deviceAddress = device.deviceAddress;
                    config.wps.setup = WpsInfo.PBC;
                    config.groupOwnerIntent=15;//to force a device to connect as a Group Owner
                    //config.groupOwnerIntent=0;//to force a device to connect as a Client.
//                if (progressDialog != null && progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
//                progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
//                        "Connecting to :" + device.deviceAddress, true, true
//                        new DialogInterface.OnCancelListener() {
//
//                            @Override
//                            public void onCancel(DialogInterface dialog) {
//                                ((DeviceActionListener) getActivity()).cancelDisconnect();
//                            }
//                        }
//                );
                    ((PeerListFragment.DeviceActionListener) getActivity()).connect(config);
                }else if(device.status==WifiP2pDevice.CONNECTED){
                    Log.d("helloooo", "connect버튼누름 : connected");
                    Toast.makeText(getActivity(), "이미 연결된 peer입니다.",
                            Toast.LENGTH_SHORT).show();
                }else{
                    Log.d("helloooo", "connect버튼누름 : else");
                    Toast.makeText(getActivity(), "연결할 수 없는 peer입니다.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        mContentView.findViewById(R.id.detail_btn_disconnect).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((PeerListFragment.DeviceActionListener) getActivity()).disconnect();
                    }
                });
//        mContentView.findViewById(R.id.btn_start_client).setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent=new Intent(getActivity(),FileServerAsyncTask.class);
//                        startActivityForResult(intent,CHOOSE_STRING_RESULT_CODE);
//                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                        intent.setType("image/*");
//                        //Activity의 결과를 받기 위해 startActivityForResult사용
//                        startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);
//                    }
//                });
        return mContentView;
    }


    public void showDetails(WifiP2pDevice device) {
        Toast.makeText(getActivity(), "showDetails",
                Toast.LENGTH_SHORT).show();
        this.device = device;
        this.getView().setVisibility(View.VISIBLE);
        TextView view=(TextView) mContentView.findViewById(R.id.detail_device_name);
        view.setText(device.deviceName);
        view=(TextView) mContentView.findViewById(R.id.detail_device_address);
        view.setText(device.deviceAddress);

        if(device.status==WifiP2pDevice.CONNECTED){
            //info.isGroupOwner에서 null point error발생
//            view = (TextView) mContentView.findViewById(R.id.detail_group_owner);
//            view.setText(getResources().getString(R.string.group_owner_text)
//                    + ((info.isGroupOwner == true) ? getResources().getString(R.string.yes)
//                    : getResources().getString(R.string.no)));
//            view = (TextView) mContentView.findViewById(R.id.detail_group_ip);
//            view.setText("Group Owner IP - " + info.groupOwnerAddress.getHostAddress());
        }else {
            view = (TextView) mContentView.findViewById(R.id.detail_group_owner);
            view.setText(R.string.empty);
            view = (TextView) mContentView.findViewById(R.id.detail_group_ip);
            view.setText(R.string.empty);
        }
    }

    /**
     * Clears the UI fields after a disconnect or direct mode disable operation.
     */
    public void resetViews() {
        TextView view=(TextView) mContentView.findViewById(R.id.detail_device_name);
        view.setText(R.string.empty);
        view=(TextView) mContentView.findViewById(R.id.detail_device_address);
        view.setText(R.string.empty);

        view = (TextView) mContentView.findViewById(R.id.detail_group_owner);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.detail_group_ip);
        view.setText(R.string.empty);

//        mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
//        mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);
        this.getView().setVisibility(View.GONE);
    }


    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
//        if (progressDialog != null && progressDialog.isShowing()) {
//            progressDialog.dismiss();
//        }
        PeerListFragment fragment = (PeerListFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_peer_list);
        Toast.makeText(getActivity(), "onConnectionInfoAvailable"+fragment.getPeersSize(),Toast.LENGTH_SHORT).show();

        if(fragment.getPeersSize()!=0){
            this.info = info;
            this.getView().setVisibility(View.VISIBLE);
            // The owner IP is now known.
            TextView view = (TextView) mContentView.findViewById(R.id.detail_group_owner);
            view.setText(getResources().getString(R.string.group_owner_text)
                    + ((info.isGroupOwner == true) ? getResources().getString(R.string.yes)
                    : getResources().getString(R.string.no)));

            // InetAddress from WifiP2pInfo struct.
            view = (TextView) mContentView.findViewById(R.id.detail_group_ip);
            view.setText("Group Owner IP - " + info.groupOwnerAddress.getHostAddress());

            //그룹오너가 서버
            //The file server is single threaded, single connection server socket.
            if (info.groupFormed && info.isGroupOwner) {
                Toast.makeText(getActivity(), "그룹오너O",Toast.LENGTH_SHORT).show();
                //new FromServer_ToClientAsyncTask(getActivity(),mContentView.findViewById(R.id.status_text),info,this.device).execute();

            } else if (info.groupFormed) {
                Toast.makeText(getActivity(), "그룹오너X",Toast.LENGTH_SHORT).show();
                //new FromClient_ToServerAsyncTask(getActivity(),mContentView.findViewById(R.id.status_text),info,this.device).execute();

                //                ((TextView) mContentView.findViewById(R.id.status_text)).setText(getResources()
//                        .getString(R.string.client_text));
            }
        }
    }


    //startActivityForResult로 설정한 후
    // 상대방앱에서 setResult(), finish()를 해서 데이터를 전송하면
    // onActivityResult로 결과값을 받음
    /*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==CHOOSE_STRING_RESULT_CODE){
            if(resultCode==RESULT_OK){
                String devicename=data.getStringExtra("devicename");
                String deviceIP=data.getStringExtra("deviceIP");
                String devicePort=data.getStringExtra("devicePort");

                Toast.makeText(getActivity(), "onActivityResult:" + data.getStringExtra("result"), Toast.LENGTH_SHORT).show();
                TextView statusText = (TextView) mContentView.findViewById(R.id.status_text);
                statusText.setText(devicename+"값돌아옴!");
            } else {   // RESULT_CANCEL
                Toast.makeText(getActivity(), "onActivityResult:실패", Toast.LENGTH_SHORT).show();
            }
        }
    }
*/
}
