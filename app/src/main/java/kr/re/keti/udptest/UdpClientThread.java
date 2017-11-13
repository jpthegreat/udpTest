package kr.re.keti.udptest;

import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;



public class UdpClientThread extends Thread{

    String dstAddress;
    int dstPort;
    private boolean running = false;
    MainActivity.UdpClientHandler handler;

    //DatagramSocket socket;
    MulticastSocket socket;
    InetAddress address;

    // added by JP - 171109
    private int PORT = 8888;    // should be bigger than 1024 on Linux based OS (Permission denied)
    private String ADDR = "224.0.0.2";
    private int PACK_LEN = 100;

    public UdpClientThread(String addr, int port, MainActivity.UdpClientHandler handler) {
        super();
        dstAddress = addr;
        dstPort = port;
        this.handler = handler;
    }

    public void setRunning(boolean running){
        this.running = running;
    }

    private void sendState(String state){
        handler.sendMessage( Message.obtain(handler, MainActivity.UdpClientHandler.UPDATE_STATE, state) );
    }

    @Override
    public void run() {
        //sendState("connecting...");
        Log.d("UTRD", "connecting....");
        running = true;
        try {
            while(true)
            {
                /* create multicast socket */
                //socket = new DatagramSocket(1000);
                socket = new MulticastSocket(PORT);
                Log.d("UTRD", "create socket");

                /* retrieve server address */
                address = InetAddress.getByName(ADDR);
                Log.d("UTRD", "retrieve server address: "+address.toString());
                socket.joinGroup(address);

                /* prepare UDP Packet */
                byte[] buf = new byte[PACK_LEN];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                Log.d("UTRD", "packet created");

                /* receive packet from Rasp */
                socket.receive(packet);
                Log.d("UTRD", "Receive packet");

                //String line = new String(packet.getData(), 0, packet.getLength());
                Log.d("UTRD", "packet.getLength(): "+ packet.getLength());
                Log.d("UTRD", "packet.getData().length: "+ packet.getData().length);

                byte[] dataBuf = packet.getData();

                /* Log data buffer */
                for(int i=0; i<dataBuf.length; i++)
                    Log.d("UTRD", "packet["+i+"]: " +dataBuf[i]);

                /*handler.sendMessage(
                        Message.obtain(handler, MainActivity.UdpClientHandler.UPDATE_MSG, line) );*/
                handler.sendMessage(
                        Message.obtain(handler, MainActivity.UdpClientHandler.UPDATE_MSG, dataBuf) );
            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(socket != null){
                running = false;
                socket.close();
                handler.sendEmptyMessage(MainActivity.UdpClientHandler.UPDATE_END);
                Log.d("UTRD", "state - UPDATE_END");
            }
        }

    }
}
