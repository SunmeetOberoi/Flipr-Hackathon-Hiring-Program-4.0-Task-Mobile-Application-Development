package com.protal;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.Executor;

public class LoginFragment extends Fragment {

    private TextView tvLoginError;
    ProgressBar pbLoadingLogin;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.login_fragment, container, false);
        final EditText etEMailLogin = view.findViewById(R.id.etEMailLogin);
        final EditText etPasswordLogin = view.findViewById(R.id.etPasswordLogin);
        Button btnLoginSubmit = view.findViewById(R.id.btnLoginSubmit);
        Button btnForgotPassword = view.findViewById(R.id.btnForgotPassword);
        tvLoginError = view.findViewById(R.id.tvLoginError);
        pbLoadingLogin = view.findViewById(R.id.pbLoadingLogin);

        btnLoginSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etEMailLogin.getText().toString().trim().isEmpty()) {
                    tvLoginError.setText(R.string.empty_email_error);
                    tvLoginError.setVisibility(View.VISIBLE);
                }
                else if(etPasswordLogin.getText().toString().trim().isEmpty()) {
                    tvLoginError.setText(R.string.empty_password_error);
                    tvLoginError.setVisibility(View.VISIBLE);
                }
                else
                    login(etEMailLogin.getText().toString().trim(),
                            etPasswordLogin.getText().toString().trim());
            }
        });

        // TODO: Forgot Password
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return view;
    }

    private void login(String email, String password) {
        pbLoadingLogin.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener( getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //TODO: Login
                            Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                            pbLoadingLogin.setVisibility(View.GONE);
                        }
                        else{
                            tvLoginError.setText(task.getException().getLocalizedMessage());
                            tvLoginError.setVisibility(View.VISIBLE);
                            pbLoadingLogin.setVisibility(View.GONE);
                        }
                    }
                });
    }


}
