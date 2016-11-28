package ru.kazakov.finalproject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.kazakov.finalproject.dummy.MyDealContent;
import ru.kazakov.finalproject.dummy.MyDealContent.MyDeal;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnMyDealInteractionListener}
 * interface.
 */
public class MyDealsFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnMyDealInteractionListener mListener;


    public MyDealsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_mydeal_list, container, false);

        // Set the adapter

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.my_deals_list);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        recyclerView.setAdapter(new MyDealsRecyclerViewAdapter(MyDealContent.ITEMS, mListener));

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMyDealInteractionListener) {
            mListener = (OnMyDealInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMyDealInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String[] s = MyDealsFragment.class.getName().split("\\.");
        mListener.setCurrentPosition(s[s.length - 1]);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnMyDealInteractionListener extends CurrentPositionInterface {
        void onDealsListInteraction(MyDeal item);
    }
}
