package com.example.rodri.portal;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * Created by rodri on 15-Apr-17.
 */

public class AdminLoginFragment extends Fragment {
    public AdminLoginFragment() {
    }

    public static AdminLoginFragment newInstance() {
        AdminLoginFragment fragment = new AdminLoginFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admin_login, container, false);
        final EditText loginEditText = (EditText) rootView.findViewById(R.id.admin_login_edittext);
        final EditText passwordEditText = (EditText) rootView.findViewById(R.id.admin_password_edittext);
        Button loginButton = (Button) rootView.findViewById(R.id.button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = loginEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (login.compareTo("admin")==0 && password.compareTo("admin")==0){
                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.container_admin, new AdminFragment());
                    ft.commit();
                    Toast.makeText(getActivity(), "Successful Login.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Fail.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return rootView;
    }
}