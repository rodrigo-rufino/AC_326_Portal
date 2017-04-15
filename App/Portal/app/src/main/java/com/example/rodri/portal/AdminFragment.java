package com.example.rodri.portal;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class AdminFragment extends Fragment{

    private DatabaseReference mDatabase;

    public static AdminFragment newInstance() {
        AdminFragment fragment = new AdminFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_admin, container, false);
        Button addButton = (Button) view.findViewById(R.id.add_button);

        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_add_user, null);

        final EditText usernameEditText = (EditText) dialogView.findViewById(R.id.username_edit_text);
        final EditText macEditText = (EditText) dialogView.findViewById(R.id.mac_edit_text);
        final Button addUserButton = (Button) dialogView.findViewById(R.id.add_user_button);
        mBuilder.setView(dialogView);
        final AlertDialog addUserDialog = mBuilder.create();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUserDialog.show();
            }
        });

        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String mac = macEditText.getText().toString();
                User user = new User(username, mac);
                mDatabase.child("users").child(username).setValue(user);
                addUserDialog.cancel();
            }
        });
        return view;
    }
}
