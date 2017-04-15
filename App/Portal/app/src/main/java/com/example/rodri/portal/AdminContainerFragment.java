package com.example.rodri.portal;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by rodri on 15-Apr-17.
 */

public class AdminContainerFragment extends Fragment {

    public AdminContainerFragment() {
    }

    public static AdminContainerFragment newInstance() {
        AdminContainerFragment fragment = new AdminContainerFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.container_admin, container, false);
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container_admin, new AdminLoginFragment());
        ft.commit();
        return rootView;
    }
}