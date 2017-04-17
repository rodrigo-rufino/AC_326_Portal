package com.example.rodri.portal;

import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.zip.Inflater;


/**
 * Created by rodri on 15-Apr-17.
 */

public class AdminLoginFragment extends Fragment {
    private DatabaseReference mDatabaseRef;
    private String correctLogin;
    private String correctPassword;
    String login;
    String password;
    EditText loginEditText;
    EditText passwordEditText;
    Button loginButton;
    Button changePasswordButton;
    private AlertDialog changePasswordDialog;
    private AlertDialog.Builder changePasswordDialogBuilder;
    LayoutInflater inflater;


    public AdminLoginFragment() {
    }

    public static AdminLoginFragment newInstance() {
        AdminLoginFragment fragment = new AdminLoginFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        View rootView = inflater.inflate(R.layout.fragment_admin_login, container, false);

        loginEditText = (EditText) rootView.findViewById(R.id.admin_login_edittext);
        passwordEditText = (EditText) rootView.findViewById(R.id.admin_password_edittext);

        loginButton = (Button) rootView.findViewById(R.id.button);
        changePasswordButton = (Button) rootView.findViewById(R.id.change_password_button);
        loginButton.setEnabled(false);
        changePasswordButton.setEnabled(false);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        login = loginEditText.getText().toString();
        password = passwordEditText.getText().toString();

        updateAdmin();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login = loginEditText.getText().toString();
                password = passwordEditText.getText().toString();
                if (login.compareTo(correctLogin)==0 && password.compareTo(correctPassword)==0){
                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.container_admin, new AdminFragment());
                    ft.commit();
                    Toast.makeText(getActivity(), "Successful Login.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Login and/or password are incorrect.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login = loginEditText.getText().toString();
                password = passwordEditText.getText().toString();
                if (login.compareTo(correctLogin)==0 && password.compareTo(correctPassword)==0) {
                    changePassword();
                } else {
                    Toast.makeText(getActivity(), "Login and/or password are incorrect.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    public void changePassword(){
        View changePasswordView = inflater.inflate(R.layout.dialog_change_password, null);
        changePasswordDialogBuilder = new AlertDialog.Builder(getContext());

        final EditText newLoginEditText = (EditText) changePasswordView.findViewById(R.id.change_login_edittext);
        final EditText newPasswordEditText = (EditText) changePasswordView.findViewById(R.id.change_login_edittext);
        final Button confirmChangeButton = (Button) changePasswordView.findViewById(R.id.confirm_changebutton);

        changePasswordDialogBuilder.setView(changePasswordView);
        changePasswordDialog = changePasswordDialogBuilder.create();
        changePasswordDialog.show();

        confirmChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newLogin = newLoginEditText.getText().toString();
                String newPassword = newPasswordEditText.getText().toString();

                mDatabaseRef.child("admin").child("login").setValue(newLogin);
                mDatabaseRef.child("admin").child("password").setValue(newPassword);
                loginEditText.setText("");
                passwordEditText.setText("");
                changePasswordDialog.cancel();
            }
        });

        changePasswordDialog.show();
    }

    public void updateAdmin(){
        mDatabaseRef.child("admin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()){
                    loginButton.setEnabled(true);
                    changePasswordButton.setEnabled(true);
                    correctLogin = snapshot.child("login").getValue().toString();
                    correctPassword = snapshot.child("password").getValue().toString();
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }
}