package com.example.cocolor.pojo;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.cocolor.util.SqliteDBHelper;

import java.util.ArrayList;
import java.util.List;

public class CardDAO {
    private SQLiteDatabase db;

     // 重写构造方法
     public CardDAO(Context context) {
         SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(context);
         db = sqliteDBHelper.getWritableDatabase();
    }


    /**
     * 查询方法一：返回的是游标
     *
     * @param sql
     * @param selectionArgs
     * @return
     */
    public Cursor selectCursor(String sql, String[] selectionArgs) {
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        return cursor;
    }

    /**
     * 查询方法二：返回的是list集合
     *
     * @param sql
     * @param selectionArgs
     * @return
     */
    public List<Card> selectList(String sql, String[] selectionArgs) {
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        return cursorToList(cursor);
    }

    /**
     * 封装游标数据到List
     *
     * @param cursor
     * @return
     */
    private List<Card> cursorToList(Cursor cursor) {
        List<Card> list = new ArrayList<>();
        while (cursor.moveToNext()) {// 循环的是每一行数据

            Card card = new Card();
            card.setId(cursor.getInt(0));
            card.setTitle(cursor.getString(1));
            card.setDescription(cursor.getString(2));
            card.setPicture(cursor.getString(3));
            card.setCategory(cursor.getInt(4));
            card.setVibrant(cursor.getInt(5));
            card.setDarkVibrant(cursor.getInt(6));
            card.setLightVibrant(cursor.getInt(7));
            card.setMuted(cursor.getInt(8));
            card.setDarkMuted(cursor.getInt(9));
            card.setLightMuted(cursor.getInt(10));
            card.setTextColor(cursor.getInt(11));
            card.setCollection(cursor.getInt(12));

            list.add(card);
        }
        cursor.close();
        return list;
    }

    /**
     * 增删查的方法
     *
     * @param sql
     * @param bindArgs
     *            ：是 sql语句中要绑定(占位符)的参数值
     * @return
     */
    public boolean executeData(String sql, Object[] bindArgs) {
        try {
            if (bindArgs == null) {
                db.execSQL(sql);
            } else {
                db.execSQL(sql, bindArgs);
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("MainActivity", "数据错误！！");
            return false;
        }
    }

    // 关闭时销毁db
    public void destroy() {
        if (db != null) {
            db.close();
        }
    }
}
