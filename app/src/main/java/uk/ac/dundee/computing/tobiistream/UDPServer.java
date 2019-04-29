package uk.ac.dundee.computing.tobiistream;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import android.os.AsyncTask;

public class UDPServer
{
    private AsyncTask<Void, Void, String> async;
    private boolean Server_active = true;

    public void runUdpServer()
    {
        async = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params)
            {
                System.out.println("Running server");
                byte[] lMsg = new byte[4096];
                DatagramPacket dp = new DatagramPacket(lMsg, lMsg.length);
                DatagramSocket ds = null;

                try
                {
                    ds = new DatagramSocket(49152);

                    while(Server_active)
                    {
                        ds.receive(dp);
                        System.out.println("Received packet: " + dp.getData().toString());
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if (ds != null)
                    {
                        ds.close();
                    }
                }

                return dp.getData().toString();
            }
        };
        async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void stop_UDP_Server()
    {
        Server_active = false;
    }
}