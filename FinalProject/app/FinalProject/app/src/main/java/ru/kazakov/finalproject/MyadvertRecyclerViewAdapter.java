package ru.kazakov.finalproject;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.kazakov.finalproject.AdvertListFragment.OnAdvertListInteractionListener;
import ru.kazakov.finalproject.dummy.AdvertContent.AdvertItem;

/**
 * {@link RecyclerView.Adapter} that can display a {@link AdvertItem} and makes a call to the
 * specified {@link OnAdvertListInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyadvertRecyclerViewAdapter extends RecyclerView.Adapter<MyadvertRecyclerViewAdapter.ViewHolder> {

    private final List<AdvertItem> mValues;
    private final OnAdvertListInteractionListener mListener;

    public MyadvertRecyclerViewAdapter(List<AdvertItem> items, OnAdvertListInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_advert, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).name);
        holder.mPriceView.setText(mValues.get(position).price);
        holder.mPlaceView.setText(mValues.get(position).place);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onAdvertListInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mPriceView;
        public final TextView mPlaceView;
        public AdvertItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.ad_name);
            mPriceView = (TextView) view.findViewById(R.id.ad_price);
            mPlaceView = (TextView) view.findViewById(R.id.ad_place);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mPriceView.getText() + "'";
        }
    }
}
