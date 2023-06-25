package com.example.myapplication;

import static com.example.myapplication.RegisterActivity.onResetPasswordFragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SignInFragment extends Fragment {

    public SignInFragment() {
        // Required empty public constructor
    }

    private TextView dontHaveAnAccount;

    private TextView forgotPassword;
    private FrameLayout parentFrameLayout;

    private EditText email;
    private EditText password;

    private ImageView closeBtn;

    private Button signInBtn;

    private ProgressBar progressBar;

    private final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
    private FirebaseAuth firebaseAuth;

    public static boolean disableCloseBtn = false;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in_fragment, container, false);
        dontHaveAnAccount = view.findViewById(R.id.tv_dont_have_an_account);

        parentFrameLayout = view.findViewById(R.id.register_frame_layout);

        email = view.findViewById(R.id.sign_in_email);

        password = view.findViewById(R.id.sign_in_password);

        closeBtn = view.findViewById(R.id.sign_in_close_btn);

        signInBtn = view.findViewById(R.id.sign_in_btn);

        forgotPassword = view.findViewById(R.id.sign_in_forgot_password);

        progressBar = view.findViewById(R.id.sign_in_progressbar);

        firebaseAuth = FirebaseAuth.getInstance();

        if (disableCloseBtn){
            closeBtn.setVisibility(View.GONE);
        }else {
            closeBtn.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dontHaveAnAccount.setOnClickListener(v -> setFragment(new SignUpFragment()));

        forgotPassword.setOnClickListener(v -> {
            onResetPasswordFragment = true;
            setFragment(new ResetPasswordFragment());
        });

        dontHaveAnAccount.setOnClickListener(v -> setFragment(new SignUpFragment()));
//
        closeBtn.setOnClickListener(v -> mainIntent());

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                   checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        signInBtn.setOnClickListener(v -> checkEmailAndPassword());
    }

    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slideout_from_left);

//        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);    -->   This line giving null pointer exception. Replacing with the line below

        fragmentTransaction.replace(R.id.register_frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void checkInputs(){
        if (! TextUtils.isEmpty(email.getText())){
            if (! TextUtils.isEmpty(password.getText())){
                signInBtn.setEnabled(true);
                signInBtn.setTextColor(Color.rgb(255,255,255));
            }else {
                signInBtn.setEnabled(false);
                signInBtn.setTextColor(Color.argb(50,255,255,255));
            }
        }else {
            signInBtn.setEnabled(false);
            signInBtn.setTextColor(Color.argb(50,255,255,255));
        }
    }

    private void checkEmailAndPassword(){
        if (email.getText().toString().matches(EMAIL_PATTERN)){
            if (password.length() >= 8){

                progressBar.setVisibility(View.VISIBLE);
                signInBtn.setEnabled(false);
                signInBtn.setTextColor(Color.argb(50,255,255,255));

                firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(task -> {
                           if (task.isSuccessful()){
                               mainIntent();
                           }else {
                               progressBar.setVisibility(View.INVISIBLE);
                               signInBtn.setEnabled(true);
                               signInBtn.setTextColor(Color.rgb(255,255,255));

                               String error = Objects.requireNonNull(task.getException()).getMessage();
                               Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                           }
                        });
            }else {
                Toast.makeText(getActivity(), "Incorrect Email or Password!", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getActivity(), "Incorrect Email or Password!", Toast.LENGTH_SHORT).show();
        }
    }

    private void mainIntent(){
        if (disableCloseBtn){
            disableCloseBtn = false;
        } else {
            Intent mainIntent = new Intent(getActivity(), MainActivity.class);
            startActivity(mainIntent);
        }
        getActivity().finish();
    }

}