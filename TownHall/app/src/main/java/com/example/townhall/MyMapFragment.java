package com.example.townhall;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MyMapFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_map, container, false);

        // Replace FragMap with MyMapsActivity (MyMapsActivity is a Fragment)
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.MapContainer, new MyMapsActivity());
        transaction.addToBackStack(null);
        transaction.commit();

        return view;
    }


}
