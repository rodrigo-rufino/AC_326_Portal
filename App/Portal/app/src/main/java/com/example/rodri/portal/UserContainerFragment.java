package com.example.rodri.portal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        View rootView = inflater.inflate(R.layout.container_user, container, false);
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container_user, new UserFragment());
        ft.commit();
        return rootView;
    }
}