package com.ssr_projects.trackpad.Helpers;

import android.util.Log;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketClass {
    private static Socket clientSocket = null;
    private static String IpAddress;

    public static void setIpAddress(String ipAddress) {
        IpAddress = ipAddress;
    }

    public static String getIpAddress() {
        return IpAddress;
    }

    public static synchronized Socket getSocketInstance() {
        if (null == clientSocket) {
            try {
                IO.Options opts = new IO.Options();
                opts.transports = new String[]{"websocket"};
                clientSocket = IO.socket(IpAddress, opts);
                Log.d(SocketClass.class.getName(), "getSocketInstance: ");
            } catch (URISyntaxException e) {
                Log.e(SocketClass.class.getName(), "instance initializer: ", e.fillInStackTrace());
            }
        }
        return clientSocket;
    }

    public static synchronized void destroySocketInstance(){
        clientSocket = null;
    }
}
