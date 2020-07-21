package com.example.wifihg;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.wifihg.WiFiP2P.FromClient_ToServerAsyncTask;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientAsyncTask extends AsyncTask<Void, Void, String> {
    public static final String TAG = "socket_test"; //log 태그를 위한 변수

    private Context context;
    private String ipaddress;
    private Socket socket;
    private ObjectOutputStream outputStream;
    /**
     * @param context
     */
    public ClientAsyncTask(Context context,String ipaddress) {
        this.context = context;
        this.ipaddress=ipaddress;
    }
    @Override
    protected String doInBackground(Void... params) {
        try {
            socket=new Socket(ipaddress,8988);
            Log.d(ClientAsyncTask.TAG, "Client: Socket opened");


            outputStream=new ObjectOutputStream(socket.getOutputStream());
            String str="dfghjkljhgfdfghjkl";


            outputStream.writeObject(str);
            outputStream.flush();
            return str;
        } catch (IOException e) {
            Log.e(ClientAsyncTask.TAG, e.getMessage());
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
        Log.d(ClientAsyncTask.TAG, "send="+result);
    }

    @Override
    protected void onPreExecute() {
        Log.d(ClientAsyncTask.TAG, "Client: Socket opened");
    }

}