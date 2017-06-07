package com.example.rodri.portal;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

public class UserFragment extends Fragment {

    private static final String TAG = "bluetooth1";

    LayoutInflater inflater;
    private View view;
    private DatabaseReference mDatabaseRef;
    private String macAddress;
    private AlertDialog doorDialog;
    private AlertDialog.Builder doorDialogBuilder;
    private TextView macAddressTextView;
    private TextView usernameTextView;

    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // MAC-address of Bluetooth module (you must edit this line)
    private static String address = "98:D3:31:80:0C:C4";

    ListView doorListView;
    ArrayAdapter<String> doorListAdapter;
    User meUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        view = inflater.inflate(R.layout.fragment_user, container, false);
        macAddress = android.provider.Settings.Secure.getString(getContext().getContentResolver(), "bluetooth_address");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        meUser = new User();
        doorListView = (ListView) view.findViewById(R.id.door_list_view);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();

        doorListAdapter = new ArrayAdapter<String>(getContext(), R.layout.adapter_door_list);
        doorListView.setAdapter(doorListAdapter);

        macAddressTextView = (TextView) view.findViewById(R.id.user_mac_text_view);
        usernameTextView = (TextView) view.findViewById(R.id.user_username_text_view);

        macAddressTextView.setText(macAddress);
        usernameTextView.setText("Device without permissions.");
        updateUserList();
        doorControl();

        return view;
    }

    public void doorControl(){
        doorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                View doorControlView = inflater.inflate(R.layout.dialog_door_control, null);
                doorDialogBuilder = new AlertDialog.Builder(getContext());
                final TextView doorNameTextView = (TextView) doorControlView.findViewById(R.id.door_name_text_view);
                final Button doorOpenButton = (Button) doorControlView.findViewById(R.id.door_switch);
                final Button okButton = (Button) doorControlView.findViewById(R.id.ok_door_control_button);

                doorNameTextView.setText(doorListAdapter.getItem(position).toString());
                doorDialogBuilder.setView(doorControlView);
                doorDialog = doorDialogBuilder.create();
                doorDialog.show();
                doorDialog.setCancelable(false);

                doorOpenButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendData(String.valueOf(position));
                        Toast.makeText(getContext(), "Turn on LED", Toast.LENGTH_SHORT).show();
                    }
                });

                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lockDoor(doorListAdapter.getItem(position).toString());
                        doorDialog.cancel();
                    }
                });
            }
        });
    }

    public void lockDoor(String door){
        Toast.makeText(getActivity(), door +" locked.", Toast.LENGTH_SHORT).show();
    }

    public void unlockDoor(String door){
        Toast.makeText(getActivity(), door +" unlocked.", Toast.LENGTH_SHORT).show();
    }

    public void updateUserList(){
        mDatabaseRef.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()){
                    meUser = snapshot.child(macAddress).getValue(User.class);
                    if(meUser!=null){
                        System.out.println(meUser.toString());
                        macAddressTextView.setText(meUser.getMac());
                        usernameTextView.setText(meUser.getUsername());
                        if(getActivity()!=null){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    doorListAdapter.clear();
                                    if(meUser.getPortals() != null){
                                        for(String portal : meUser.getPortals()){
                                            doorListAdapter.add(portal);
                                        }
                                    }
                                }
                            });
                        }
                    } else {
                        macAddressTextView.setText(macAddress);
                        usernameTextView.setText("Device without permissions.");
                        doorListAdapter.clear();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection",e);
            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    @Override
    public void onResume() {
        super.onResume();

        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e1) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e1.getMessage() + ".");
        }

        btAdapter.cancelDiscovery();

        Log.d(TAG, "...Connecting...");
        try {
            btSocket.connect();
            Log.d(TAG, "...Connection ok...");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        Log.d(TAG, "...Create Socket...");

        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "...In onPause()...");

        if (outStream != null) {
            try {
                outStream.flush();
            } catch (IOException e) {
                errorExit("Fatal Error", "In onPause() and failed to flush output stream: " + e.getMessage() + ".");
            }
        }

        try     {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }

    private void checkBTState() {
        if(btAdapter==null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private void errorExit(String title, String message){
        Toast.makeText(getActivity().getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        getActivity().finish();
    }

    private void sendData(String message) {
        byte[] msgBuffer = message.getBytes();

        Log.d(TAG, "...Send data: " + message + "...");

        try {
            outStream.write(msgBuffer);
        } catch (IOException e) {
            String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
            if (address.equals("00:00:00:00:00:00"))
                msg = msg + ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 35 in the java code";
            msg = msg +  ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";

            errorExit("Fatal Error", msg);
        }
    }
}
