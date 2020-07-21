package com.example.wifihg.WiFiP2P;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

//서버는 클라이언트에게서 메시지를 받고 화면에 그 결과를 뿌린다.
public class FromServer_ToClientAsyncTask extends AsyncTask<Void, Void, String> {
    public static final String TAG = "FromServer_ToClientAsyncTask_TAG"; //log 태그를 위한 변수

    private Context context;
    private TextView statusText;
    private WifiP2pInfo info;
    private WifiP2pDevice device;
    private ServerSocket serverSocket;
    private Socket client;
    private ObjectInputStream inputStream;
    /**
     * @param context
     * @param statusText
     */
    public FromServer_ToClientAsyncTask(Context context, View statusText, WifiP2pInfo info, WifiP2pDevice device) {
        this.context = context;
        this.statusText = (TextView) statusText;
        this.info=info;
        this.device=device;
    }
    @Override
    protected String doInBackground(Void... params) {
        try {
            serverSocket = new ServerSocket(8988);
            Log.d(FromServer_ToClientAsyncTask.TAG, "Server: Socket opened");
            client = serverSocket.accept(); //클라이언트로부터 데이터가 오는것을 감지
            Log.d(FromServer_ToClientAsyncTask.TAG, "Server: connection done");
            String myData="server get :";

            inputStream=new ObjectInputStream(client.getInputStream());
            Object input=inputStream.readObject();
            String str=input.toString();

            return str;
        } catch (IOException e) {
            Log.e(FromClient_ToServerAsyncTask.TAG, e.getMessage());
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
                client.close();
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "error";
    }

    //doInBackground( ) 메소드에서 작업이 끝나면 onPostExcuted( ) 로 결과 파라미터를 리턴하면서 그 리턴값을 통해 스레드 작업이 끝났을 때의 동작을 구현합니다
    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            statusText.setText("server get:"+result);
        }
    }

    @Override
    protected void onPreExecute() {
        statusText.setText("Opening a server socket");
    }
}