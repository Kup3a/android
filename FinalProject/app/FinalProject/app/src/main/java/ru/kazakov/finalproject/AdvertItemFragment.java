package ru.kazakov.finalproject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import ru.kazakov.finalproject.dummy.AdvertContent;

public class AdvertItemFragment extends Fragment {
    public static String ADVERT_ID;
    AdvertContent.AdvertItem curAdvert = null;

    private OnItemFragmentInteractionListener mListener;

    public AdvertItemFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.advert_info, container, false);

        MainActivity.IS_USER_CHOOSEN = false;

        curAdvert = AdvertContent.ITEM_MAP.get(ADVERT_ID);
        setInfoValuesAndActions(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemFragmentInteractionListener) {
            mListener = (OnItemFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnItemFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String[] s = AdvertItemFragment.class.getName().split("\\.");
        mListener.setCurrentPosition(s[s.length - 1]);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setInfoValuesAndActions(View view) {
        TextView priceView = (TextView) view.findViewById(R.id.price_value);
        TextView depositView = (TextView) view.findViewById(R.id.deposit_value);
        TextView placeView = (TextView) view.findViewById(R.id.place_value);
        TextView timeView = (TextView) view.findViewById(R.id.time_value);
        TextView userNameView = (TextView) view.findViewById(R.id.user_name);
        TextView useTelView = (TextView) view.findViewById(R.id.user_tel);
        TextView userVkView = (TextView) view.findViewById(R.id.vk_profile);
        TextView descrView = (TextView) view.findViewById(R.id.descr_value);
        ImageView avatar = (ImageView) view.findViewById(R.id.user_av);
        Button makeDeal = (Button) view.findViewById(R.id.make_deal);

        priceView.setText(curAdvert.price);
        placeView.setText(curAdvert.place);
        timeView.setText(curAdvert.time);
        depositView.setText(curAdvert.deposit);
        descrView.setText(curAdvert.descr);
        userNameView.setText(curAdvert.user.name);
        useTelView.setText(curAdvert.user.tel);
        userVkView.setText(curAdvert.user.vk);

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onAvatar(curAdvert.user.id);
            }
        });
        makeDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onMakeDeal(curAdvert.id);
            }
        });

    }


    public interface OnItemFragmentInteractionListener extends CurrentPositionInterface {
        // TODO: Update argument type and name
        void onAvatar(String userId);
        void onMakeDeal(String advertId);
    }
}
