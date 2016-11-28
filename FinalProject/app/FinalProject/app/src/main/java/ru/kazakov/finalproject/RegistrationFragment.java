package ru.kazakov.finalproject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class RegistrationFragment extends Fragment {

    private OnRegistrationInteractionListener mListener;
    private Button button;
    private EditText nameEV, sirnameEV, emailEV, phoneEV, passwordEV;

    public RegistrationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.registration, container, false);
        findViews(view);

        return view;
    }

    private void findViews(View view) {
        button = (Button) view.findViewById(R.id.reg_but);
        nameEV = (EditText) view.findViewById(R.id.reg_name_ev);
        sirnameEV = (EditText) view.findViewById(R.id.reg_sirname_ev);
        emailEV = (EditText) view.findViewById(R.id.reg_email_ev);
        phoneEV = (EditText) view.findViewById(R.id.reg_tel_ev);
        passwordEV = (EditText) view.findViewById(R.id.reg_pas_ev);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRegistationtInteraction(nameEV.getText().toString(), sirnameEV.getText().toString(),
                        emailEV.getText().toString(), phoneEV.getText().toString(),
                        passwordEV.getText().toString());
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRegistrationInteractionListener) {
            mListener = (OnRegistrationInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRegistrationInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnRegistrationInteractionListener {
        void onRegistationtInteraction(String name, String sirname, String email, String phone, String password);
    }
}
