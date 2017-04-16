package com.example.rodri.portal;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class AdminFragment extends Fragment{

    private DatabaseReference mDatabaseRef;
    private ArrayAdapter<String> listAdapter;
    private ArrayList<User> users = new ArrayList<>();
    private List<String> portals;
    private ListView listView;
    private CustomAdapter customAdapter;

    LayoutInflater inflater;


    private View view;

    private AlertDialog editUserDialog;
    private AlertDialog.Builder editDialogBuilder;
    private EditText usernameEditEditText;
    private EditText macEditEditText;
    private Button editUserButton;
    private Button deleteUserButton;
    private CheckBox door0EditCheckBox;
    private CheckBox door1EditCheckBox;
    private CheckBox door2EditCheckBox;

    private AlertDialog addUserDialog;
    private AlertDialog.Builder addDialogBuilder;
    private EditText usernameAddEditText;
    private EditText macAddEditText;
    private Button addUserButton;
    private CheckBox door0AddCheckBox;
    private CheckBox door1AddCheckBox;
    private CheckBox door2AddCheckBox;

    public static AdminFragment newInstance() {
        AdminFragment fragment = new AdminFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        this.inflater = inflater;
        view = inflater.inflate(R.layout.fragment_admin, container, false);
        Button addButton = (Button) view.findViewById(R.id.add_button);

        listView = (ListView) view.findViewById(R.id.user_list);
        portals = new ArrayList<>();

        addUser();
        updateListView();
        updateItem();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUserDialog.show();
                usernameAddEditText.setText("");
                macAddEditText.setText("");
            }
        });

        return view;
    }

    public void addUser(){
        addDialogBuilder = new AlertDialog.Builder(getContext());

        View addDialogView = inflater.inflate(R.layout.dialog_add_user, null);

        usernameAddEditText = (EditText) addDialogView.findViewById(R.id.username_edit_text);
        macAddEditText = (EditText) addDialogView.findViewById(R.id.mac_edit_text);
        addUserButton = (Button) addDialogView.findViewById(R.id.add_user_button);
        door0AddCheckBox = (CheckBox) addDialogView.findViewById(R.id.door0_check_box);
        door1AddCheckBox = (CheckBox) addDialogView.findViewById(R.id.door1_check_box);
        door2AddCheckBox = (CheckBox) addDialogView.findViewById(R.id.door2_check_box);

        portals.clear();
        portals.add("0");
        portals.add("0");
        portals.add("0");

        addDialogBuilder.setView(addDialogView);
        addUserDialog = addDialogBuilder.create();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameAddEditText.getText().toString();
                String mac = macAddEditText.getText().toString();
                if (door0AddCheckBox.isChecked()) portals.set(0, "1");
                if (door1AddCheckBox.isChecked()) portals.set(1, "1");
                if (door2AddCheckBox.isChecked()) portals.set(2, "1");

                if(username.compareTo("")!= 0){
                    User user = new User(username, mac);
                    user.setUsername(username);
                    user.setMac(mac);
                    user.setPortals(portals);
                    mDatabaseRef.child("users").child(mac).setValue(user);
                    addUserDialog.cancel();
                } else {
                    Toast.makeText(getActivity(), "Enter with a valid Username", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void updateListView(){
        mDatabaseRef.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                users.clear();
                System.out.println(snapshot.getValue());  //prints "Do you have data? You'll love Firebase."
                for (DataSnapshot userSnapshot: snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    System.out.println(user.getUsername());
                    users.add(user);
                }
                if(getActivity()!=null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            customAdapter = new CustomAdapter(getContext(), users);
                            listView.setAdapter(customAdapter);
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    public void updateItem(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                View editDialogView = inflater.inflate(R.layout.dialog_edit_user, null);;
                final User user = customAdapter.getItem(position);

                editDialogBuilder = new AlertDialog.Builder(getContext());

                usernameEditEditText = (EditText) editDialogView.findViewById(R.id.edit_username_edit_text);
                macEditEditText = (EditText) editDialogView.findViewById(R.id.edit_mac_edit_text);
                editUserButton = (Button) editDialogView.findViewById(R.id.edit_ok_user_button);
                deleteUserButton = (Button) editDialogView.findViewById(R.id.edit_delete_user_button);
                door0EditCheckBox = (CheckBox) editDialogView.findViewById(R.id.edit_door0_check_box);
                door1EditCheckBox = (CheckBox) editDialogView.findViewById(R.id.edit_door1_check_box);
                door2EditCheckBox = (CheckBox) editDialogView.findViewById(R.id.edit_door2_check_box);

                usernameEditEditText.setText(user.getUsername());
                macEditEditText.setText(user.getMac());

                if(user.getPortals().get(0) == "1") door0EditCheckBox.setChecked(true);
                if(user.getPortals().get(1) == "1") door1EditCheckBox.setChecked(true);
                if(user.getPortals().get(2) == "1") door2EditCheckBox.setChecked(true);

                editDialogBuilder.setView(editDialogView);
                editUserDialog = editDialogBuilder.create();
                editUserDialog.show();

                mDatabaseRef = FirebaseDatabase.getInstance().getReference();

                editUserButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newUsername = usernameEditEditText.getText().toString();
                        String newMac = macEditEditText.getText().toString();
                        List<String> newPortals = new ArrayList<String>();
                        newPortals.add("0");
                        newPortals.add("0");
                        newPortals.add("0");

                        if (door0EditCheckBox.isChecked()) newPortals.set(0, "1");
                        if (door1EditCheckBox.isChecked()) newPortals.set(1, "1");
                        if (door2EditCheckBox.isChecked()) newPortals.set(2, "1");

                        if(newUsername.compareTo("")!= 0){
                            mDatabaseRef.child("users").child(user.getMac()).removeValue();
                            user.setUsername(newUsername);
                            user.setMac(newMac);
                            user.setPortals(newPortals);
                            mDatabaseRef.child("users").child(newMac).setValue(user);
                            editUserDialog.cancel();
                        } else {
                            Toast.makeText(getActivity(), "Enter with a valid Username", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                deleteUserButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDatabaseRef.child("users").child(user.getMac()).removeValue();
                        editUserDialog.cancel();
                    }
                });
            }
        });
    }
}
