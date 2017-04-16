package com.example.rodri.portal;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.FirebaseError;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class AdminFragment extends Fragment{

    private DatabaseReference mDatabaseRef;
    private ArrayAdapter<String> listAdapter;
    private ArrayList<User> users = new ArrayList<>();
    private List<String> portals;


    public static AdminFragment newInstance() {
        AdminFragment fragment = new AdminFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_admin, container, false);
        Button addButton = (Button) view.findViewById(R.id.add_button);
        portals = new ArrayList<>();
        portals.add("a");
        portals.add("b");
        portals.add("c");

        final ListView listView = (ListView) view.findViewById(R.id.user_list);

        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_add_user, null);


        final EditText usernameEditText = (EditText) dialogView.findViewById(R.id.username_edit_text);
        final EditText macEditText = (EditText) dialogView.findViewById(R.id.mac_edit_text);
        final Button addUserButton = (Button) dialogView.findViewById(R.id.add_user_button);

        mBuilder.setView(dialogView);
        final AlertDialog addUserDialog = mBuilder.create();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUserDialog.show();
                usernameEditText.setText("");
                macEditText.setText("");
            }
        });

        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String mac = macEditText.getText().toString();
                if(username.compareTo("")!= 0){
                    User user = new User(username, mac);
                    mDatabaseRef.child("users").child(username).child("mac").setValue(mac);
                    mDatabaseRef.child("users").child(username).child("portals").setValue(portals);
                    mDatabaseRef.child("users").orderByValue();
                    addUserDialog.cancel();
                } else {
                    Toast.makeText(getActivity(), "Enter with a valid Username", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mDatabaseRef.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                users.clear();
                System.out.println(snapshot.getValue());  //prints "Do you have data? You'll love Firebase."
                for (DataSnapshot userSnapshot: snapshot.getChildren()) {
                    //User user = userSnapshot.getValue(User.class);
                    User user = new User();
                    user.setUsername(userSnapshot.getKey());
                    user.setMac(userSnapshot.child("mac").getValue().toString());
                    System.out.println(user.getUsername());
                    users.add(user);
                }
                if(getActivity()!=null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final CustomAdapter customAdapter = new CustomAdapter(getContext(), users);
                            listView.setAdapter(customAdapter);
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
        return view;
    }
}
