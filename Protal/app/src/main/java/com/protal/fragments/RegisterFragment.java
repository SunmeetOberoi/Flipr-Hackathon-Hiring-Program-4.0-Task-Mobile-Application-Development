package com.protal.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.protal.activities.MainHomePageActivity;
import com.protal.R;


public class RegisterFragment extends Fragment {


    ProgressBar pbLoadingRegister;
    TextView tvRegisterError;
    Button btnRegisterSubmit;


    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_register, container, false);
        final EditText etNameRegister = view.findViewById(R.id.etNameRegister);
        final EditText etEMailRegister = view.findViewById(R.id.etEMailRegister);
        final EditText etPasswordRegister = view.findViewById(R.id.etPasswordRegister);
        final EditText etPasswordConfirmRegister = view.findViewById(R.id.etPasswordConfirmRegister);
        btnRegisterSubmit = view.findViewById(R.id.btnRegisterSubmit);
        tvRegisterError = view.findViewById(R.id.tvRegisterError);
        pbLoadingRegister = view.findViewById(R.id.pbLoadingRegister);

        btnRegisterSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // prevent resubmission of form
                btnRegisterSubmit.setClickable(false);
                // field validation
                if(etNameRegister.getText().toString().trim().isEmpty())
                    setErrorMessage(getResources().getString(R.string.empty_name_error), true);
                else if(etEMailRegister.getText().toString().trim().isEmpty())
                    setErrorMessage(getResources().getString(R.string.empty_email_error), true);
                else if(etPasswordRegister.getText().toString().isEmpty())
                    setErrorMessage(getResources().getString(R.string.empty_password_error), true);
                else if(etPasswordConfirmRegister.getText().toString().isEmpty())
                    setErrorMessage(getResources().getString(R.string.empty_confirm_password_error)
                            , true);
                else if(!etPasswordRegister.getText().toString()
                        .equals(etPasswordConfirmRegister.getText().toString()))
                    setErrorMessage(getResources().getString(R.string.password_diff_error), true);
                else{
                    register_user(etNameRegister.getText().toString().trim(),
                            etEMailRegister.getText().toString().trim(),
                            etPasswordRegister.getText().toString().trim());
                }
            }
        });
        return view;
    }

    private void register_user(final String name, String email, String password) {
        pbLoadingRegister.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            task.getResult().getUser().updateProfile(new UserProfileChangeRequest
                                    .Builder()
                            .setDisplayName(name)
                            .build()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> taskresult) {
                                    // verify email
                                    task.getResult().getUser().sendEmailVerification();
                                    setErrorMessage(getString(R.string.confirm_email_message), false);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(new Intent(getActivity(), MainHomePageActivity.class));
                                        }
                                    }, 2000);
                                    pbLoadingRegister.setVisibility(View.GONE);
                                }
                            });
                        }else{
                            setErrorMessage(task.getException().getLocalizedMessage(), true);
                            pbLoadingRegister.setVisibility(View.GONE);

                        }
                    }
                });
    }

    // handles displaying message
    private void setErrorMessage(String message, Boolean error){
        tvRegisterError.setText(message);
        if(!error) {
            tvRegisterError.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        tvRegisterError.setVisibility(View.VISIBLE);
        btnRegisterSubmit.setClickable(true);
    }
}
