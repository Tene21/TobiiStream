package uk.ac.dundee.computing.tobiistream;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class GazeData extends AsyncTask<String, Void, String> {
        static boolean running = true;
    static Timer timer  = new Timer();
    static int port = 49150;
    static DatagramSocket dataSocket;
    static byte[] buffer = new byte[1000];
    static String dataKeepAliveMessage = "{\"type\": \"live.data.unicast\", \"key\": \"some_GUID\", \"op\": \"start\"}";
    InetAddress glassesIP;



    protected String doInBackground(String... target) {
        if(target.equals("stop"))
        {
            stopSending();
        }
        try {
            glassesIP = InetAddress.getByName("192.168.71.50");
            dataSocket = new DatagramSocket(port);
            //dataSocket.setSoTimeout(timeout);
            dataSocket.connect(glassesIP,port);
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    sendKeepAlive(dataSocket, dataKeepAliveMessage, glassesIP);
                }
            },0,1000);
            DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
            String response;
            dataSocket.receive(receivedPacket);
            response = receivedPacket.toString();
            while(running) {
                return response;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            stopSending();
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    protected void onPostExecute(String result){
        super.onPostExecute(result);
    }

     void stopSending() {
        running = false;
    }



    void sendKeepAlive(DatagramSocket socket, String message, InetAddress IP){
        while(running){
            //System.out.println("Sending " + message + " to target " + IP);
            //System.out.println("Sent keep-alive for data");
            DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.length());
            try {
                socket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
