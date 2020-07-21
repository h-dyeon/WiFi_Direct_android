package com.example.wifihg.WiFiP2P;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.ListFragment;

import com.example.wifihg.R;

import java.util.ArrayList;
import java.util.List;


public class PeerListFragment extends ListFragment implements WifiP2pManager.PeerListListener{

    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    //  ProgressDialog progressDialog = null;
    View mContentView = null;
    private WifiP2pDevice device;


    public PeerListFragment() {
        // Required empty public constructor
    }
    public int getPeersSize(){
        return peers.size();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.setListAdapter(new WiFiPeerListAdapter(getActivity(), R.layout.row_devices, peers));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_peer_list, null);
        return mContentView;
    }

    //WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action))에서
    //manager.requestPeers(channel, myPeerListListener);의 결과를 이 함수에서 받는다.
    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
//        if (progressDialog != null && progressDialog.isShowing()) {
//            progressDialog.dismiss();
//        }
        String before="";
        for(WifiP2pDevice d:peers)
            before=before+d.deviceName+":"+d.status+"\n";
        String after="";
        for(WifiP2pDevice d: peerList.getDeviceList())
            after=after+d.deviceName+":"+d.status+"\n";
        Log.d("helloooo", "hdy : "+before+"==>>>"+after);

        if(peerList.getDeviceList().size()==0)
            ((PeerListFragment.DeviceActionListener) getActivity()).resetDetailsView();

        peers.clear();
        peers.addAll(peerList.getDeviceList());

        Toast.makeText(getActivity(), "onPeersAvailable",
                Toast.LENGTH_SHORT).show();
//        String str="";
//        for(WifiP2pDevice p : peers)
//            str=str+p.deviceName+",,";
//        Toast.makeText(getActivity(), "찾은 peer="+peers.size()+":"+str,
//                Toast.LENGTH_SHORT).show();
        notifyData();
        if (peers.size() == 0) {
            Log.d(WiFiP2PActivity.TAG, "No devices found");
            return;
        }
    }

    public void notifyData(){
        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
    }

    private class WiFiPeerListAdapter extends ArrayAdapter<WifiP2pDevice>{
        private List<WifiP2pDevice> items;
        ViewHolder viewHolder=null;

        public WiFiPeerListAdapter(@NonNull Context context, int textViewResourceId,
                                   @NonNull List<WifiP2pDevice> objects) {
            super(context, textViewResourceId, objects);
            items=objects;
        }
        @Override
        public int getCount(){
            return super.getCount();
        }
        @Override
        public WifiP2pDevice getItem(int position){
            return super.getItem(position);
        }
        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {

                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row_devices, parent,false);

                viewHolder=new ViewHolder();
                viewHolder.device_name=(TextView)v.findViewById(R.id.device_name);
                viewHolder.device_details=(TextView)v.findViewById(R.id.device_details);
                v.setTag(viewHolder);
            }else{
                viewHolder=(ViewHolder)v.getTag();
            }

            WifiP2pDevice device=items.get(position);
            if(device!=null){
                if(viewHolder.device_name!=null)
                    viewHolder.device_name.setText(device.deviceName);
                if(viewHolder.device_details!=null) {
                    String deviceStatus=getDeviceStatus(device.status,false);
                    viewHolder.device_details.setText(deviceStatus);
                    if(deviceStatus.contains("Connected")){
                        viewHolder.device_details.setTextColor(ContextCompat.getColor(getContext(),R.color.color_red));
                    }else{
                        viewHolder.device_details.setTextColor(ContextCompat.getColor(getContext(),R.color.color_black));
                    }
                }
            }
            return v;
        }

        class ViewHolder{
            public TextView device_name=null;
            public TextView device_details=null;

        }
        @Override
        protected void finalize() throws Throwable {
            free();
            super.finalize();
        }
        private void free(){
             viewHolder = null;
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        WifiP2pDevice device = (WifiP2pDevice) getListAdapter().getItem(position);

        Toast.makeText(getActivity(),device.deviceName+"을 클릭함",Toast.LENGTH_SHORT).show();
        ((PeerListFragment.DeviceActionListener) getActivity()).showDetails(device);
    }

    // An interface-callback for the activity to listen to fragment interaction events.
    public interface DeviceActionListener {
        void showDetails(WifiP2pDevice device);
        void resetDetailsView();
        void cancelDisconnect();
        void connect(WifiP2pConfig config);
        void disconnect();
    }

    public void clearPeers() {
        peers.clear();
        notifyData();
    }





//    /**
//     * @return this device
//     */
//    public WifiP2pDevice getDevice() {
//        return device;
//    }

    public void updateThisDevice(WifiP2pDevice device) {
        this.device = device;
        TextView view = (TextView) mContentView.findViewById(R.id.my_name);
        view.setText(device.deviceName);
        view = (TextView) mContentView.findViewById(R.id.my_status);
        String color=getDeviceStatus(device.status,true);
        view.setText(getDeviceStatus(device.status,true));
        if(color.contains("Connected")){
            view.setTextColor(ContextCompat.getColor(getContext(),R.color.color_red));
        }else{
            view.setTextColor(ContextCompat.getColor(getContext(),R.color.color_black));
        }
    }

    private String getDeviceStatus(int deviceStatus,boolean isMyDevice) {
        Log.d(WiFiP2PActivity.TAG, "Peer status :" + deviceStatus);
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";
        }
    }










//     프로그레스 다이얼로그 띄우는 과정 근데 progressDialog는 api26에서 decepreted됨
//     public void onInitiateDiscovery() {
//        if (progressDialog != null && progressDialog.isShowing()) {
//            progressDialog.dismiss();
//        }
//        progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel", "finding peers", true,
//                true, new DialogInterface.OnCancelListener() {
//                    @Override
//                    public void onCancel(DialogInterface dialog) {
//
//                    }
//                });
//    }
}
