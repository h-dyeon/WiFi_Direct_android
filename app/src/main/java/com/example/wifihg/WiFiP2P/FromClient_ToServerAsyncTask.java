package com.example.wifihg.WiFiP2P;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

//클라이언트가 서버에게 메시지를 보낸다.
public class FromClient_ToServerAsyncTask extends AsyncTask<Void, Void, String> {
    public static final String TAG = "FromClient_ToServerAsyncTask_TAG"; //log 태그를 위한 변수

    private Context context;
    private TextView statusText;
    private WifiP2pInfo info;
    private WifiP2pDevice device;
    private Socket socket;
    private ObjectOutputStream outputStream;
    /**
     * @param context
     * @param statusText
     */
    public FromClient_ToServerAsyncTask(Context context, View statusText, WifiP2pInfo info, WifiP2pDevice device) {
        this.context = context;
        this.statusText = (TextView) statusText;
        this.info=info;
        this.device=device;
    }
    @Override
    protected String doInBackground(Void... params) {
        try {
            socket=new Socket(info.groupOwnerAddress,8988);
            Log.d(FromClient_ToServerAsyncTask.TAG, "Client: Socket opened");

            outputStream=new ObjectOutputStream(socket.getOutputStream());
            String str="hhhh_"+device.deviceName+"__"+device.deviceAddress;
            outputStream.writeObject(str);
            outputStream.flush();
            return str;
        } catch (IOException e) {
            Log.e(FromClient_ToServerAsyncTask.TAG, e.getMessage());
            return null;
        }finally {
            try {
                outputStream.close();
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    //doInBackground( ) 메소드에서 작업이 끝나면 onPostExcuted( ) 로 결과 파라미터를 리턴하면서 그 리턴값을 통해 스레드 작업이 끝났을 때의 동작을 구현합니다
    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            statusText.setText("client send :"+result);
        }
    }

    @Override
    protected void onPreExecute() {
        statusText.setText("Opening a client socket");
    }

}


//
//    ServerSocket serverSocket = new ServerSocket(8988);
//            Log.d(FromClient_ToServerAsyncTask.TAG, "Server: Socket opened");
//                    Socket client = serverSocket.accept();
//                    Log.d(FromClient_ToServerAsyncTask.TAG, "Server: connection done");
//
//
//                    InputStream inputStream=client.getInputStream();
//                    OutputStream outputStream=client.getOutputStream();
//
//                    String devicename=device.deviceName;
//                    String deviceIP=device.deviceAddress;
//                    outputStream.write(devicename.getBytes());
//                    outputStream.write(deviceIP.getBytes());
//
//                    outputStream.flush();
//                    outputStream.close();
//                    inputStream.close();
//                    serverSocket.close();
//
//                    return device.deviceName+device.deviceAddress;