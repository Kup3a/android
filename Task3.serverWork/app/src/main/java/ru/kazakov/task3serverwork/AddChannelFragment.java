package ru.kazakov.task3serverwork;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



public class AddChannelFragment extends Fragment {


    private OnAddingInteractionListener mListener;
    private EditText name;
    private EditText descr;

    public AddChannelFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_channel, container, false);
        getActivity().setTitle(R.string.add);
        name = (EditText)view.findViewById(R.id.et_chat_name);
        descr =(EditText)view.findViewById(R.id.et_chat_descr);
        view.findViewById(R.id.bt_add_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.addChannel(name.getText().toString(), descr.getText().toString());
            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAddingInteractionListener) {
            mListener = (OnAddingInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAddingInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnAddingInteractionListener {
        // TODO: Update argument type and name
        void addChannel(String name, String description);
    }
}
