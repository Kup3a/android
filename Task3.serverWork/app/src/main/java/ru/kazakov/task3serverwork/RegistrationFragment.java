package ru.kazakov.task3serverwork;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



public class RegistrationFragment extends Fragment {
    private static final String FRAG_REG_TAG = "frag_reg_tag";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RegistrationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_registration, container, false);
        getActivity().setTitle(R.string.registration);
        Button button = (Button)view.findViewById(R.id.but_reg);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etLogin = (EditText)view.findViewById(R.id.et_login);
                EditText etPass = (EditText)view.findViewById(R.id.et_password);
                EditText etNick = (EditText)view.findViewById(R.id.et_nick);
                if (mListener != null) {
                    Log.d(FRAG_REG_TAG, etLogin.getText().toString() + " " + etPass.getText().toString());
                    mListener.onButRegistrPress(etLogin.getText().toString(),
                            etPass.getText().toString(), etNick.getText().toString());
                }

            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnChatInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onButRegistrPress(String login, String password, String nck);
    }
}
