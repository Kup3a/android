package ru.kazakov.finalproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.kazakov.finalproject.dummy.AdvertContent;
import ru.kazakov.finalproject.dummy.AdvertContent.AdvertItem;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnAdvertListInteractionListener}
 * interface.
 */
public class AdvertListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnAdvertListInteractionListener mListener;

    private static final String AD_FRAG_LOG = "ad_frag_log";
    public static final String SET_VISITED = "visited";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AdvertListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static AdvertListFragment newInstance(int columnCount) {
        AdvertListFragment fragment = new AdvertListFragment();
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

        /* если пользователь дошёл до этого экрана, то сохраняем запись об этом, чтобы потом не
        *  показывать онбоардинг в следующий раз*/
        SharedPreferences mSettings = getContext().getSharedPreferences(MainActivity.prefName, Context.MODE_PRIVATE);
        if (!mSettings.contains("visited")) {
            Log.d(AD_FRAG_LOG, "first visit");
                SharedPreferences.Editor editor = mSettings.edit();
            editor.putString(SET_VISITED, "true");
            editor.apply();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_advert_list, container, false);

        MainActivity.IS_ADVERT_CHOOSEN = false;

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyadvertRecyclerViewAdapter(AdvertContent.ITEMS, mListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAdvertListInteractionListener) {
            mListener = (OnAdvertListInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMyDealInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String[] s = AdvertListFragment.class.getName().split("\\.");
        mListener.setCurrentPosition(s[s.length - 1]);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnAdvertListInteractionListener extends CurrentPositionInterface {
        // TODO: Update argument type and name
        void onAdvertListInteraction(AdvertItem item);
    }
}
