package com.example.cocolor.pojo;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.example.cocolor.R;
import com.example.cocolor.activity.MainActivity;
import com.example.cocolor.activity.UpdateActivity;
import com.example.cocolor.util.SqliteDBHelper;
import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CardRecyclerViewAdapter extends RecyclerView.Adapter<CardRecyclerViewAdapter.ViewHolder> {

    private List<Card> mItems;
    private AdapterView.OnItemClickListener mOnItemClickListener;

    public CardRecyclerViewAdapter(List<Card> mItems){
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
        View root = inflater.inflate(R.layout.card_item, parent, false);
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

        private int[] categories = new int[]{R.mipmap.scene, R.mipmap.sofa, R.mipmap.makeup, R.mipmap.cloth
                , R.mipmap.food, R.mipmap.pets, R.mipmap.smile, R.mipmap.idea};

        private ImageView picture, category;
        private TextView title;
        private CardRecyclerViewAdapter mAdapter;
        private LinearLayout intro;

        ViewHolder(final View itemView, CardRecyclerViewAdapter adapter) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), UpdateActivity.class);
                    intent.putExtra("card", mItems.get(getAdapterPosition()));
                    itemView.getContext().startActivity(intent);
                }
            });



            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new QMUIDialog.MessageDialogBuilder(itemView.getContext())
                            .setTitle("删除操作")
                            .setMessage("确定要删除该卡片吗？")
                            .setSkinManager(QMUISkinManager.defaultInstance(itemView.getContext()))
                            .addAction("取消", new QMUIDialogAction.ActionListener() {
                                @Override
                                public void onClick(QMUIDialog dialog, int index) {
                                    dialog.dismiss();
                                }
                            })
                            .addAction(0, "删除", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                                @Override
                                public void onClick(QMUIDialog dialog, int index) {
                                    Toast.makeText(itemView.getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                                    SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(itemView.getContext());
                                    SQLiteDatabase db = sqliteDBHelper.getWritableDatabase();

                                    String id = String.valueOf(mItems.get(getAdapterPosition()).getId());
                                    String whereClause = "id=?";//删除的条件
                                    String[] whereArgs = {id};//删除的条件参数
                                    db.delete("card", whereClause, whereArgs);//执行删除

                                    removeItem(getAdapterPosition());

                                    dialog.dismiss();
                                }
                            })
                            .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
                    return true;
                }
            });

            mAdapter = adapter;
            title = itemView.findViewById(R.id.card_item_title);
            picture = itemView.findViewById(R.id.card_item_image);
            category = itemView.findViewById(R.id.category_image_view);
            intro = itemView.findViewById(R.id.intro_area);
        }

        void setData(Card card) {
            //设置数据
            category.setImageResource(categories[card != null ? card.getCategory() : 0]);
            if(card.getTitle().length() <= 0){
                title.setVisibility(View.GONE);
            }else{
                title.setText(card.getTitle());
                title.setTextColor(card.getTextColor());
            }
            picture.setImageURI(Uri.fromFile(new File(card.getPicture())));
            if(card.getDarkVibrant() != -1){
                intro.setBackgroundColor(card.getDarkVibrant());
            } else if (card.getMuted() != -1) {
                intro.setBackgroundColor(card.getMuted());
            } else if(card.getLightVibrant() != -1) {
                intro.setBackgroundColor(card.getLightVibrant());
            }else{
                intro.setBackgroundColor(Color.WHITE);
            }
        }


        @Override
        public void onClick(View v) {
            mAdapter.onItemHolderClick(this);
        }


    }
}
