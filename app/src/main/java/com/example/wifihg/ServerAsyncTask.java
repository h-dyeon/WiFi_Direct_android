package com.example.wifihg;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.wifihg.WiFiP2P.FromClient_ToServerAsyncTask;
import com.example.wifihg.WiFiP2P.FromServer_ToClientAsyncTask;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerAsyncTask extends AsyncTask<Void, Void, String> {
    public static final String TAG = "ServerAsyncTask_TAG"; //log 태그를 위한 변수

    private Context context;
    private ServerSocket serverSocket;
    private Socket client;
    private ObjectInputStream inputStream;
    /**
     * @param context
     */
    public ServerAsyncTask(Context context) {
        this.context = context;
    }
    @Override
    protected String doInBackground(Void... params) {
        try {
            serverSocket = new ServerSocket(8988);
            Log.d(ServerAsyncTask.TAG, "Server: Socket opened");
            client = serverSocket.accept(); //클라이언트로부터 데이터가 오는것을 감지
            Log.d(ServerAsyncTask.TAG, "Server: connection done");

            String str="";
            Boolean b=true;
            while(b){
                inputStream=new ObjectInputStream(client.getInputStream());
                Object input=inputStream.readObject();
                str=input.toString();///////////////////////
                Log.d(ServerAsyncTask.TAG, "Server : recieve : "+str);
            }

            return str;
        } catch (IOException e) {
            Log.e(ServerAsyncTask.TAG, e.getMessage());
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
        Log.d(ClientAsyncTask.TAG, "server : receive="+result);
    }

    @Override
    protected void onPreExecute() {
        Log.d(ClientAsyncTask.TAG, "server: Socket opened");
    }
}