package com.example.cocolor.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.cocolor.R;
import com.example.cocolor.pojo.Card;
import com.example.cocolor.pojo.CardDAO;
import com.example.cocolor.pojo.CollectionRecyclerViewAdapter;
import com.example.cocolor.util.SqliteDBHelper;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.pullLayout.QMUIPullLayout;

import java.util.List;

public class CollectionActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private QMUIPullLayout mPullLayout;
    private TextView blankTextView;
    private RecyclerView recycleView;
    private ImageView back;


    private CardDAO cardDAO;

    private int pageSize = 8;// 每页能显示的记录数
    private int pageIndex = 1;// 页数。默认第一页
    private int pageCount;// 总页数，记录完了不再加载数
    private List<Card> totalList;

    private CollectionRecyclerViewAdapter collectionRecyclerViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        QMUIStatusBarHelper.translucent(CollectionActivity.this);
        cardDAO = new CardDAO(CollectionActivity.this);

        blankTextView = findViewById(R.id.blank_textview);
        mPullLayout = findViewById(R.id.pull_to_refresh);
        recycleView = findViewById(R.id.display_cards_recycleview);
        back = findViewById(R.id.back);


//        TODO 展示数据
        initDatabase();
        initData();

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    private void onLoadMore(){
        if (pageIndex < pageCount) {
            pageIndex++;
            totalList.addAll(getSqlDataToListview());
            collectionRecyclerViewAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "没有更多了！", Toast.LENGTH_LONG).show();
        }

    }

    private void initDatabase() {
        // 创建数据库对象
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(CollectionActivity.this);
        sqliteDBHelper.getWritableDatabase();
        System.out.println("数据库创建成功");
    }

    //    初始化数据
    private void initData() {

//        TODO 下拉加载
        mPullLayout.setActionListener(new QMUIPullLayout.ActionListener() {
            @Override
            public void onActionTriggered(@NonNull final QMUIPullLayout.PullAction pullAction) {
                mPullLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(pullAction.getPullEdge() == QMUIPullLayout.PULL_EDGE_BOTTOM){
                            onLoadMore();
                        }
                        mPullLayout.finishActionRun(pullAction);
                    }
                }, 1000);
            }
        });

        recycleView.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentmain = new Intent(CollectionActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentmain);
            }
        });


//      TODO 分页显示
        // 先计算总页数
        String sql = "select count(*) from card where collection=1 order by id desc";// 一行一列数据，即一个表
        Cursor cursor = cardDAO.selectCursor(sql, null);
        cursor.moveToFirst();// 注意一定要移动游标
        int count = cursor.getInt(0);// 总记录数，取列的总数
        cursor.close();
        // 如下：总页数算法比如23.5，返回24,Math.floor(23.5)，返回时23
        pageCount = (int) Math.ceil(count / ((double) pageSize));
        // 第一页时，要向数据库提取数据
        if (pageIndex == 1) {
            totalList = getSqlDataToListview();
            if(totalList.size() <= 0) blankTextView.setVisibility(View.VISIBLE);
        }

        System.out.println(totalList.toString());
        collectionRecyclerViewAdapter = new CollectionRecyclerViewAdapter(totalList);
        recycleView.setAdapter(collectionRecyclerViewAdapter);


    }

    //    TODO 分页相关
    public List<Card> getSqlDataToListview() {
        // 分页Sql
        String sql = "select * from card where collection=1 order by id desc limit ?,?";
        // 每次从start开始，取pageSize个记录，将前一页去过的数据去掉
        int start = (pageIndex - 1) * pageSize;
        return cardDAO.selectList(sql, new String[] { start + "", pageSize + "" });
    }


}








