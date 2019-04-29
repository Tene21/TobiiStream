package uk.ac.dundee.computing.tobiistream;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

public class UDPClient {
    private AsyncTask<Void, Void, Void> async_client;
    public String Message;
    private boolean keepAlive = true;

    public void sendMessage() {
        async_client = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                DatagramSocket ds = null;
                System.out.println("Sending message: " + Message);
                try {
                    ds = new DatagramSocket();
                    DatagramPacket dp;
                    dp = new DatagramPacket(Message.getBytes(), Message.length(), InetAddress.getByName("192.168.71.50"), 49152);
                    ds.setBroadcast(true);
                    while(keepAlive) {
                        ds.send(dp);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (ds != null) {
                        ds.close();
                    }
                }

                return null;
            }

            protected void onPostExecute(Void result) {
                super.onPostExecute(result);

            }
        };
        async_client.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    public void stop_UDP_Client()
    {
        keepAlive = false;
    }
}