package com.example.tamuneke.uiautomatortest;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends Activity {
    private String TAG = this.getClass().getSimpleName();
    private ServerSocket server;
    private Socket client;
    private BufferedReader mInputReader;
    private PrintWriter mOutputWriter;
    private String mSocketResponse;
    private EditText text;
    private TextView textView;
    private LinearLayout linearLayout;
    private ShutdownReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter(Intent.ACTION_SHUTDOWN);
        mReceiver = new ShutdownReceiver();
        registerReceiver(mReceiver, filter);

        text = (EditText)findViewById(R.id.editText);
        linearLayout = (LinearLayout)findViewById(R.id.linearLayout);



        startServer();

    }

    public void startServer() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                acceptConnectionFromHost();
            }
        }).start();

    }

    private void acceptConnectionFromHost() {
        try {
            server = new ServerSocket(3007);

            server.setSoTimeout(500 * 10000);
            client = server.accept();
            if (client.isConnected()) {
                mInputReader = new BufferedReader(new InputStreamReader(
                        client.getInputStream()));
                mOutputWriter = new PrintWriter(client.getOutputStream(), true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean keepRunning = true;
                        while(keepRunning){
                                if(mReceiver.getAbortMsg()!=null) {
                                    sendMsgToHost(mReceiver.getAbortMsg());
                                    keepRunning=false;
                                }
                            }
                        }
                }).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            Thread.sleep(200);

                            getMsgFromHost();
                        } catch (InterruptedException e) {
                            Log.e(TAG,
                                    "InterruptedException " + e.getStackTrace());
                        }

                    }

                }).start();
            }
        } catch (IOException e) {
            Log.e(TAG,
                    "Error while making connection with Host " + e.toString());
        }
    }

    private void getMsgFromHost() {
       try {
            this.mSocketResponse = "";
            if (client != null) {

                while ((mSocketResponse = mInputReader.readLine()) != null) {
                    if (mSocketResponse != null && !mSocketResponse.isEmpty()) {
                        //Toast.makeText(getApplicationContext(), "Req-" + mSocketResponse, Toast.LENGTH_LONG).show();
                        final String req = mSocketResponse;

                        processAPI(req);
                        if (mSocketResponse.contains("CloseSocketConnection")) {
                            Log.i(TAG, mSocketResponse);
                            mOutputWriter.println("SocketConClosed");

                            break;
                        }


                    }
                }
            }

        } catch (IOException e) {
            Log.e(TAG,
                    "IOException while reading Ack from Host "
                            + e.getStackTrace());
        }

    }

    private void processAPI(String req) {
        final String text = req;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                TextView textView = new TextView(MainActivity.this);
                textView.setText("IntelliJ: "+text);
                linearLayout.addView(textView);

            }
        });

    }

    public void sendMessage(View view){
        final String msg = text.getText().toString();
        sendMsgToHost(msg);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                TextView textView = new TextView(MainActivity.this);
                textView.setText("Android: "+msg);
                linearLayout.addView(textView);
            }
        });
        text.setText("");

    }

    private void sendMsgToHost(String msg) {
        mOutputWriter.println(msg);
        mOutputWriter.flush();
        Log.i(TAG, "message " + msg + " send to Host");

    }
    public void deleteMessage(View view){
        linearLayout.removeAllViews();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "inside onDestroy() API");

    }

}