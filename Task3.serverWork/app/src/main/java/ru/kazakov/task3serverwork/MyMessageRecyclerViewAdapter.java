package ru.kazakov.task3serverwork;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.kazakov.task3serverwork.MessageFragment.OnMessageInteractionListener;
import ru.kazakov.task3serverwork.dummy.MessageContent.MessageItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link MessageItem} and makes a call to the
 * specified {@link OnMessageInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyMessageRecyclerViewAdapter extends RecyclerView.Adapter<MyMessageRecyclerViewAdapter.ViewHolder> {

    private final List<MessageItem> mValues;
    private final OnMessageInteractionListener mListener;

    public MyMessageRecyclerViewAdapter(List<MessageItem> items, OnMessageInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    private RelativeLayout.LayoutParams idParams;
    private RelativeLayout.LayoutParams contParams;
    private RelativeLayout.LayoutParams aidParams;
    private RelativeLayout.LayoutParams acontParams;
    private boolean notReady = true;



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_message, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        // получаем параметры для двух типов вьюшек:
        // 1) левое сообщение (не мое)
        // 2) правое (мое)
        // Для каждого создан свой layout, чтобы удобно получиь все параметры.
        if (notReady) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_message_left, parent, false);
            ViewHolder vviewHolder = new ViewHolder(view);

            idParams = (RelativeLayout.LayoutParams) viewHolder.mBodyView.getLayoutParams();
            contParams = (RelativeLayout.LayoutParams) viewHolder.mNickView.getLayoutParams();
            aidParams = (RelativeLayout.LayoutParams) vviewHolder.mBodyView.getLayoutParams();
            acontParams = (RelativeLayout.LayoutParams) vviewHolder.mNickView.getLayoutParams();

            notReady = false;
        }
        return viewHolder;
    }

    /**
     * Каждая вьюшка наполняется не только данными, но и получает параметры, соответствующие отправителю
     * сообщения.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mBodyView.setText(mValues.get(position).body);
        holder.mNickView.setText(mValues.get(position).nick);
        if (MainActivity.LOGIN.equals(holder.mItem.from)) {
            holder.mNickView.setLayoutParams(contParams);
            holder.mBodyView.setLayoutParams(idParams);
        } else {
            holder.mNickView.setLayoutParams(acontParams);
            holder.mBodyView.setLayoutParams(aidParams);
        }

        holder.mImView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onAvatarInteraction(holder.mItem);
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
        public final TextView mBodyView;
        public final TextView mNickView;
        public final ImageView mImView;
        public MessageItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mBodyView = (TextView) view.findViewById(R.id.body);
            mNickView = (TextView) view.findViewById(R.id.nick);
            mImView = (ImageView) view.findViewById(R.id.ava);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNickView.getText() + "'";
        }
    }
}
