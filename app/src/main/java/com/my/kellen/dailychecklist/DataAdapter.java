package com.my.kellen.dailychecklist;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.NumberViewHolder> {

    private Cursor mCursor;
    final private ListItemClickListener mOnClickListener;
    private final Context mContext;
    private final boolean completeView;

    public interface ListItemClickListener {
        void onListClick(int clickedItemIndex);
    }

    DataAdapter(Context context, Cursor cursor, ListItemClickListener listener) {
        mContext = context;
        mCursor = cursor;
        mOnClickListener = listener;
        SharedPreferences sP = PreferenceManager.getDefaultSharedPreferences(context);
        completeView = sP.getBoolean(mContext.getString(R.string.showCompleteKey), true);
    }

    @Override
    public NumberViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int layoutIdForListItem = R.layout.item_complete;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new NumberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NumberViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position))
            return;

        String name = mCursor.getString(mCursor.getColumnIndex(Contract.Entry.COLUMN_NAME));
        boolean complete = mCursor.getInt(mCursor.getColumnIndex(Contract.Entry.COLUMN_COMPLETE)) == 1;
        boolean repeat = mCursor.getInt(mCursor.getColumnIndex(Contract.Entry.COLUMN_REPEAT)) == 1;
        long id = mCursor.getLong(mCursor.getColumnIndex(Contract.Entry._ID));

        holder.nameText.setText(name);
        holder.itemView.setTag(id);

        if (completeView) {
            if (complete) {
                holder.checkImage.setVisibility(View.VISIBLE);
            } else {
                holder.checkImage.setVisibility(View.INVISIBLE);
            }
            if (repeat) {
                holder.repeatImage.setVisibility(View.VISIBLE);
            } else {
                holder.repeatImage.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    void swapCursor(Cursor newCursor) {
        if (mCursor != null) mCursor.close();
        mCursor = newCursor;
        if (newCursor != null) {
            this.notifyDataSetChanged();
        }
    }

    class NumberViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView nameText;
        ImageView checkImage;
        ImageView repeatImage;

        NumberViewHolder(View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.event_name_text);
            if (completeView) {
                checkImage = itemView.findViewById(R.id.imageView2);
                repeatImage = itemView.findViewById(R.id.imageView);
            }
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListClick(clickedPosition);
        }
    }
}
