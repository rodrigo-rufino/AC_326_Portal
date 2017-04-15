package com.example.rodri.portal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by rodri on 15-Apr-17.
 */

public class UserContainerFragment extends Fragment {

    public UserContainerFragment() {
    }

    public static UserContainerFragment newInstance() {
        UserContainerFragment fragment = new UserContainerFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        TextView textView = (TextView) view.findViewById(R.id.section_label);

        return view;
    }
}