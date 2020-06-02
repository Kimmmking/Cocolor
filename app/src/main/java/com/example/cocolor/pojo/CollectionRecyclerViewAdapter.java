package com.example.cocolor.pojo;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cocolor.R;
import com.example.cocolor.activity.ColorActivity;

import java.util.List;

public class CollectionRecyclerViewAdapter extends RecyclerView.Adapter<CollectionRecyclerViewAdapter.ViewHolder> {

    private List<Card> mItems;
    private AdapterView.OnItemClickListener mOnItemClickListener;

    public CollectionRecyclerViewAdapter(List<Card> mItems){
        this.mItems = mItems;
    }

    public void addItem(int position, Card card) {
        if (position > mItems.size()) return;

        mItems.add(position, card);
        notifyItemInserted(position);
    }

    private void removeItem(int position) {
        if (position >= mItems.size()) return;

        mItems.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View root = inflater.inflate(R.layout.color_item, parent, false);
        return new ViewHolder(root, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Card card = mItems.get(position);
        holder.setData(card);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setItem(List<Card> cards) {
        mItems.clear();
        mItems.addAll(cards);

        notifyDataSetChanged();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    private void onItemHolderClick(RecyclerView.ViewHolder itemHolder) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(null, itemHolder.itemView,
                    itemHolder.getAdapterPosition(), itemHolder.getItemId());
        }
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private View view1, view2, view3, view4, view5, view6;


        private CollectionRecyclerViewAdapter mAdapter;

        ViewHolder(final View itemView, CollectionRecyclerViewAdapter adapter) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), ColorActivity.class);
                    intent.putExtra("card", mItems.get(getAdapterPosition()));
                    itemView.getContext().startActivity(intent);
                }
            });

            mAdapter = adapter;
            view1 = itemView.findViewById(R.id.view1);
            view2 = itemView.findViewById(R.id.view2);
            view3 = itemView.findViewById(R.id.view3);
            view4 = itemView.findViewById(R.id.view4);
            view5 = itemView.findViewById(R.id.view5);
            view6 = itemView.findViewById(R.id.view6);

        }

        void setData(Card card) {
            //设置数据
            int vibrant, darkVibrant, lightVibrant, muted, darkMuted, lightMuted;
            vibrant = card.getVibrant();
            darkVibrant = card.getDarkVibrant();
            lightVibrant = card.getLightVibrant();
            muted = card.getMuted();
            darkMuted = card.getDarkMuted();
            lightMuted = card.getLightMuted();

            if(vibrant != -1){
                view1.setBackgroundColor(vibrant);
            }else{
                view1.setVisibility(View.GONE);
            }

            if(darkVibrant != -1){
                view2.setBackgroundColor(darkVibrant);
            }else{
                view2.setVisibility(View.GONE);
            }

            if(lightVibrant != -1){
                view3.setBackgroundColor(lightVibrant);
            }else{
                view3.setVisibility(View.GONE);
            }

            if(muted != -1){
                view4.setBackgroundColor(muted);
            }else{
                view4.setVisibility(View.GONE);
            }

            if(darkMuted != -1){
                view5.setBackgroundColor(darkMuted);
            }else{
                view5.setVisibility(View.GONE);
            }

            if(lightMuted != -1){
                view6.setBackgroundColor(lightMuted);
            }else{
                view6.setVisibility(View.GONE);
            }
        }


        @Override
        public void onClick(View v) {
            mAdapter.onItemHolderClick(this);
        }


    }
}
