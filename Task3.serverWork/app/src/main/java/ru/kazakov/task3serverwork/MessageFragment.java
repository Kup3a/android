package ru.kazakov.task3serverwork;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import ru.kazakov.task3serverwork.dummy.MessageContent;
import ru.kazakov.task3serverwork.dummy.MessageContent.MessageItem;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnMessageInteractionListener}
 * interface.
 */
public class MessageFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnMessageInteractionListener mListener;

    private EditText editText;
    public static String channel_id = "3d067bedfe2f4677470dd6ccf64d05ed";
    public static MyMessageRecyclerViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MessageFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static MessageFragment newInstance(int columnCount) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_list, container, false);
        final View myV = view.findViewById(R.id.list);

        getActivity().setTitle(R.string.chat);

        // Set the adapter
        if (myV instanceof RecyclerView) {
            Context context = myV.getContext();
            final RecyclerView recyclerView = (RecyclerView) myV;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            adapter = new MyMessageRecyclerViewAdapter(MessageContent.ITEMS, mListener);

            //to scroll the list view to bottom on data change
            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                }
            });


            recyclerView.setAdapter(adapter);
        }

        editText = (EditText)view.findViewById(R.id.et_message);
        // назначаем лисенера на кнопку отправки сообщения
        Button button = (Button)view.findViewById(R.id.bt_send_mes);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonSendInteraction(channel_id, editText.getText().toString());
                editText.setText(null);
            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMessageInteractionListener) {
            mListener = (OnMessageInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMessageInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof OnMessageInteractionListener) {
            mListener = (OnMessageInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMessageInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnMessageInteractionListener {
        /**
         * Вызывается при взаимодействии с сообщением в списке. Пока пустой.
         * @param item
         */
        void onAvatarInteraction(MessageItem item);
        void onButtonSendInteraction(String channelId, String body);
    }
}
