package com.example.rodri.portal;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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

public class UserFragment extends Fragment {

    LayoutInflater inflater;
    private View view;
    private DatabaseReference mDatabaseRef;
    private String macAddress;
    private AlertDialog doorDialog;
    private AlertDialog.Builder doorDialogBuilder;
    private TextView macAddressTextView;
    private TextView usernameTextView;
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
                final Switch doorStateSwitch = (Switch) doorControlView.findViewById(R.id.door_switch);
                final Button okButton = (Button) doorControlView.findViewById(R.id.ok_door_control_button);

                doorNameTextView.setText(doorListAdapter.getItem(position).toString());
                doorDialogBuilder.setView(doorControlView);
                doorDialog = doorDialogBuilder.create();
                doorDialog.show();
                doorDialog.setCancelable(false);

                doorStateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            unlockDoor(doorListAdapter.getItem(position).toString());
                        } else if(!isChecked){
                            lockDoor(doorListAdapter.getItem(position).toString());
                        }
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
                    }
                } else {
                    macAddressTextView.setText(macAddress);
                    usernameTextView.setText("Device without permissions.");
                    doorListAdapter.clear();
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }
}
