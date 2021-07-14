/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.wifidirect;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;

import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;


import androidx.core.content.FileProvider;


import com.example.android.wifidirect.DeviceListFragment.DeviceActionListener;
import com.example.android.wifidirect.db.MyPreferences;
import com.example.android.wifidirect.db.Person;
import com.example.android.wifidirect.db.PersonDao;
import com.example.android.wifidirect.db.PersonRespository;
import com.example.android.wifidirect.db.PersonsDataRoom;
import com.example.android.wifidirect.db.RandomString;
import com.smartregister.client.wifidirect.MainActivity;
import com.smartregister.client.wifidirect.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 */
public class DeviceDetailFragment extends Fragment implements ConnectionInfoListener {

    protected static final int CHOOSE_FILE_RESULT_CODE = 20;
    private View mContentView = null;
    private WifiP2pDevice device;
    private WifiP2pInfo info;
    ProgressDialog progressDialog = null;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContentView = inflater.inflate(R.layout.device_detail, null);
        mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
                        "Connecting to :" + device.deviceAddress, true, true
//                        new DialogInterface.OnCancelListener() {
//
//                            @Override
//                            public void onCancel(DialogInterface dialog) {
//                                ((DeviceActionListener) getActivity()).cancelDisconnect();
//                            }
//                        }
                );
                ((DeviceActionListener) getActivity()).connect(config);
            }
        });
        mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ((DeviceActionListener) getActivity()).disconnect();
                    }
                });

        mContentView.findViewById(R.id.btn_start_client).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // Allow user to pick an image from Gallery or other
                        // registered apps
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*");
                        startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);

                    }
                });

        mContentView.findViewById(R.id.dummy_data).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //We have to select dummy data and then send it
                        String host = info.groupOwnerAddress.getHostAddress();
                        int port = 8988;
                        int SOCKET_TIMEOUT = 5000;

                        Socket socket = new Socket();


                        AssetManager assetManager = getActivity().getAssets();
                        final String[] fileNames = new String[1];

                        //folder name to iterate
                        String[] folder1 = new String[0];

                        try {
                            folder1 = assetManager.list("");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //to store all file names
                        int length = folder1.length;  // + folder2.length + folder3.length;

                        //to store all files name under a
                        String[] a = new String[length];
                        for(int i = 0; i < folder1.length;i++){
                            a[i]=folder1[i];
                        }

                        final InputStream[] file = new InputStream[1];

                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Log.d(WiFiDirectActivity.TAG, "is test working");

                                        // socket.bind(null);
                                        socket.connect((new InetSocketAddress(host, port)), 10000);
                                        OutputStream outputStream = socket.getOutputStream();

                                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                                        objectOutputStream.writeLong(a.length);//no of files to the server // may break the normal working

                                        Log.d(WiFiDirectActivity.TAG,"Client jhjhghjghj");
                                        for(int j= 0;j < a.length;j++) {


                                        }
                                        for(int i=0; i< a.length; i++){
                                            AssetFileDescriptor fd =assetManager.openFd(a[i]);
                                            fileNames[0] = a[i]; //write file names    //filename , size, file
                                            objectOutputStream.writeUTF(fileNames[0]);

                                            file[0] = assetManager.open(a[i]);
                                            objectOutputStream.writeLong(fd.getLength());
                                            copyFile(file[0], objectOutputStream);
                                        }

                                        Log.d(WiFiDirectActivity.TAG,"Client file attached");


                                        Toast.makeText(getContext(), "file copying done", Toast.LENGTH_LONG);


                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            thread.start();
                        }



                }
        );
        mContentView.findViewById(R.id.dummy_database).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            InputStream file = getActivity().getAssets().open("sample.json");
                            //now call transferring function
                            //for testing purpose
                            MyPreferences mypreference = new MyPreferences(getContext());
                            Boolean value = mypreference.generatedDataTrueOrFalse();
                            Log.d(WiFiDirectActivity.TAG, value.toString());


                            PersonDao dao = PersonsDataRoom.Companion.getDatabase(getContext()).personDao();
                            PersonRespository repository = new PersonRespository(dao);
                            RandomString gen = new RandomString(8, ThreadLocalRandom.current());
                            Person person = new Person("anu",234343, "male", 343434);
                            repository.insert(person);
                            List name = repository.getAllPerson();
                            if (!value.booleanValue()){
                                //generate data for 500
                                Log.d(WiFiDirectActivity.TAG, name.toString());
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        return mContentView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // User has picked an image. Transfer it to group owner i.e peer using
        // FileTransferService.
        Uri uri = data.getData();
        TextView statusText = (TextView) mContentView.findViewById(R.id.status_text);
        statusText.setText("Sending: " + uri);
        Log.d(WiFiDirectActivity.TAG, "Intent----------- " + uri);

        String fileName = "";
       Cursor returnCursor = getContext().getContentResolver().query(uri, null, null, null, null);
        fileName = OpenableColumns.DISPLAY_NAME;
        if (returnCursor.moveToFirst()){
             fileName = returnCursor.getString(returnCursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
        }
     //   String mimeType = getActivity().getContentResolver().getType(uri);
       Log.d(WiFiDirectActivity.TAG, "filename from device " +returnCursor);


        Intent serviceIntent = new Intent(getActivity(), FileTransferService.class);
        serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
        serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                info.groupOwnerAddress.getHostAddress());
        serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_NAME, fileName);
    //    serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_TYPE, mimeType);
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
        getActivity().startService(serviceIntent);
    }

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        this.info = info;
        this.getView().setVisibility(View.VISIBLE);

        // The owner IP is now known.
        TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(getResources().getString(R.string.group_owner_text)
                + ((info.isGroupOwner == true) ? getResources().getString(R.string.yes)
                        : getResources().getString(R.string.no)));

        // InetAddress from WifiP2pInfo struct.
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText("Group Owner IP - " + info.groupOwnerAddress.getHostAddress());

        // After the group negotiation, we assign the group owner as the file
        // server. The file server is single threaded, single connection server
        // socket.
        if (info.groupFormed && info.isGroupOwner) {
            new FileServerAsyncTask(getActivity(), mContentView.findViewById(R.id.status_text))
                    .execute();
        } else if (info.groupFormed) {
            // The other device acts as the client. In this case, we enable the
            // get file button.
            mContentView.findViewById(R.id.btn_start_client).setVisibility(View.VISIBLE);
            mContentView.findViewById(R.id.dummy_data).setVisibility(View.VISIBLE);
            mContentView.findViewById(R.id.dummy_database).setVisibility(View.VISIBLE);
            ((TextView) mContentView.findViewById(R.id.status_text)).setText(getResources()
                    .getString(R.string.client_text));
        }

        // hide the connect button
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
    }

    /**
     * Updates the UI with device data
     * 
     * @param device the device to be displayed
     */
    public void showDetails(WifiP2pDevice device) {
        this.device = device;
        this.getView().setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(device.deviceAddress);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(device.toString());

    }

    /**
     * Clears the UI fields after a disconnect or direct mode disable operation.
     */
    public void resetViews() {
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.status_text);
        view.setText(R.string.empty);
        mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);
        mContentView.findViewById(R.id.dummy_data).setVisibility(View.GONE);
        mContentView.findViewById(R.id.dummy_database).setVisibility(View.GONE);
        this.getView().setVisibility(View.GONE);
    }

    /**
     * A simple server socket that accepts connection and writes some data on
     * the stream.
     */
    public static class FileServerAsyncTask extends AsyncTask<Void, Void, String> {

        private Context context;
        private TextView statusText;

        /**
         * @param context
         * @param statusText
         */
        public FileServerAsyncTask(Context context, View statusText) {
            this.context = context;
            this.statusText = (TextView) statusText;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                ServerSocket serverSocket = new ServerSocket(8988);
                Log.d(WiFiDirectActivity.TAG, "Server: Socket opened");
                Socket client = serverSocket.accept();
                Log.d(WiFiDirectActivity.TAG, "Server: connection done");
                //Writing the data from about the file to stream
                InputStream inputStream = client.getInputStream();
//                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                ObjectInputStream dis = new ObjectInputStream(inputStream);
                long number = dis.readLong();
                ArrayList<File> files = new ArrayList<File>((int) number);

//                for(int i = 0; i< number;i++){
//                    File file = new File(objectInputStream.readUTF());
//                    files.add(file);
//                }
                context.getExternalFilesDir("wifiDirect-received").mkdirs();
                for(int i = 0; i< number;i++){
                    int n = -1;
                    byte buf[] = new byte[1024];
                    String filename = dis.readUTF();
                    final File f = new File(context.getExternalFilesDir("wifiDirect-received"), filename);
//                    copyFile(dis, new FileOutputStream(f));
                    long fileSize = dis.readLong();
                    FileOutputStream fos = new FileOutputStream(f.getAbsolutePath());
                    while (fileSize > 0 && (n = dis.read(buf, 0, (int)Math.min(buf.length, fileSize))) != -1)
                    {
                        fos.write(buf,0,n);
                        fileSize -= n;
                    }
                    fos.close();
                }

                /**
                 * If this code is reached, a client has connected and transferred data
                 * Save the input stream from the client as a JPEG file
                 */
               // String fileName =  objectInputStream.readUTF();



//                for(int i = 0; i < files.size();i++){
//                    final File f = new File(context.getExternalFilesDir("received"), files.get(i).getName());
//                    copyFile(dis, new FileOutputStream(f));
//
//                }

//                    final File f = new File(context.getExternalFilesDir("received"),
//                        fileName);
//                Log.d(WiFiDirectActivity.TAG, "device detail -> value of f.getName()" + f.getName());
//                File dirs = new File(f.getParent());
//                if (!dirs.exists())
//                    dirs.mkdirs();
//                f.createNewFile();
//
//                Log.d(WiFiDirectActivity.TAG, "server: copying files " + f.toString());
//                copyFile(objectInputStream, new FileOutputStream(f));
//                //serverSocket.close();
//                return f.getAbsolutePath();
                return null; //onPostExcute depends on this
            } catch (IOException e) {
                Log.e(WiFiDirectActivity.TAG, e.getMessage());
                return null;
            }
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                statusText.setText("File copied - " + result);

                File recvFile = new File(result);
                //probably need to identify the file and then move forward
                Uri fileUri = FileProvider.getUriForFile(
                                context,
                                "com.example.android.wifidirect.fileprovider",
                                recvFile);
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(fileUri, "*/*"); //image /video /file etc
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);
            }else{
                Toast.makeText(context.getApplicationContext(), "Something received",Toast.LENGTH_SHORT).show();
            }

        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            statusText.setText("Opening a server socket");
        }

    }

    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);

            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            Log.d(WiFiDirectActivity.TAG, e.toString());
            return false;
        }
        return true;
    }

}
