package com.protal.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.protal.R;


public class ContainerFragment extends Fragment {

    public ContainerFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_container, container, false);
        Button btnLogin = view.findViewById(R.id.btnLogin);
        Button btnRegister = view.findViewById(R.id.btnRegister);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .hide(ContainerFragment.this)
                        .add(R.id.rlFragmentHome, LoginFragment.newInstance())
                        .commit();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .hide(ContainerFragment.this)
                        .add(R.id.rlFragmentHome, RegisterFragment.newInstance())
                        .commit();
            }
        });
        return view;
    }
}
