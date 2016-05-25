package ru.kazakov.task3serverwork;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class UserInfoFragment extends Fragment {
    public static String userNick;
    public static String status;


    private OnUserInteractionListener mListener;

    public UserInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);
        getActivity().setTitle(R.string.user_info);
        ((TextView) view.findViewById(R.id.tv_info_nick)).setText(userNick);
        ((TextView) view.findViewById(R.id.tv_info_status)).setText(status);
        // Inflate the layout for this fragment
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onUserInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUserInteractionListener) {
            mListener = (OnUserInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnUserInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnUserInteractionListener {
        // TODO: Update argument type and name
        void onUserInteraction(Uri uri);
    }
}
