package ru.kazakov.task3serverwork;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.kazakov.task3serverwork.ChatsFragment.OnChatsListInteractionListener;
import ru.kazakov.task3serverwork.dummy.ChatsContent.ChatItem;

import java.util.List;


public class MyChatsRecyclerViewAdapter extends RecyclerView.Adapter<MyChatsRecyclerViewAdapter.ViewHolder> {

    private final List<ChatItem> mValues;
    private final OnChatsListInteractionListener mListener;

    public MyChatsRecyclerViewAdapter(List<ChatItem> items, OnChatsListInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_chats, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNamrView.setText(mValues.get(position).name);
        holder.mDescrView.setText(mValues.get(position).descr);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onChannelInteraction(holder.mItem);
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
        public final TextView mNamrView;
        public final TextView mDescrView;
        public ChatItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNamrView = (TextView) view.findViewById(R.id.body);
            mDescrView = (TextView) view.findViewById(R.id.nick);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDescrView.getText() + "'";
        }
    }
}
