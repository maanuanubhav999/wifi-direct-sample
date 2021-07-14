//package com.example.android.wifidirect;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.Socket;
//
//public class ClientForDummyData {
//    public static void main(String[] args) throws IOException {
//        Socket socket = null;
//        String host = "127.0.0.1";
//
//        socket = new Socket(host, 4444);
//
//        File file = new File("M:\\test.xml");
//        // Get the size of the file
//        long length = file.length();
//        byte[] bytes = new byte[1024];
//        InputStream in = new FileInputStream(file);
//        OutputStream out = socket.getOutputStream();
//
//        int count;
//        while ((count = in.read(bytes)) > 0) {
//            out.write(bytes, 0, count);
//        }
//
//        out.close();
//        in.close();
//        socket.close();
//    }
//}
