package com.example.cocolor.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.cocolor.R;
import com.example.cocolor.pojo.Card;
import com.example.cocolor.util.SqliteDBHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.popup.QMUIPopups;
import com.qmuiteam.qmui.widget.popup.QMUIQuickAction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.os.Environment.getExternalStorageDirectory;

public class UpdateActivity extends AppCompatActivity {

    private int[] icon = {R.mipmap.scene,R.mipmap.sofa,R.mipmap.makeup,R.mipmap.cloth,R.mipmap.food,R.mipmap.pets,R.mipmap.smile,R.mipmap.idea};

    private FloatingActionButton back, edit, download, collection;
    private ImageView picture, category;
    private EditText title, description;
    private View view1, view2, view3, view4, view5, view6;
    private ScrollView view;

    private Card card;
    boolean flag = false;
    boolean col = false;
    int category_chosen;
    private SQLiteDatabase db;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        QMUIStatusBarHelper.translucent(UpdateActivity.this);

        //接收传递过来的参数
        final Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle == null){
//            Toast.makeText(this, "出错!", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(UpdateActivity.this, MainActivity.class);
            startActivity(intent1);
        }

        card = (Card) bundle.get("card");

        back = findViewById(R.id.back_to_main);
        edit = findViewById(R.id.edit_card);
        download = findViewById(R.id.download_card);
        picture = findViewById(R.id.card_image);
        category = findViewById(R.id.category_edit_view);
        title = findViewById(R.id.title_edit_view);
        description = findViewById(R.id.description_edit_view);
        view1 = findViewById(R.id.color_view_1);
        view2 = findViewById(R.id.color_view_2);
        view3 = findViewById(R.id.color_view_3);
        view4 = findViewById(R.id.color_view_4);
        view5 = findViewById(R.id.color_view_5);
        view6 = findViewById(R.id.color_view_6);
        view = findViewById(R.id.view);
        collection = findViewById(R.id.colloection);

        initData();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentmain = new Intent(UpdateActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentmain);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFunc();
            }
        });

        collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCol();
            }
        });



        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                viewSaveToImage(view);
                Toast.makeText(getBaseContext(), "已保存到相册", Toast.LENGTH_SHORT).show();
            }
        };
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download.setVisibility(View.GONE);
                edit.setVisibility(View.GONE);
                back.setVisibility(View.GONE);
                collection.setVisibility(View.GONE);

                new Handler().post(runnable);

                download.setVisibility(View.VISIBLE);
                edit.setVisibility(View.VISIBLE);
                back.setVisibility(View.VISIBLE);
                collection.setVisibility(View.VISIBLE);

            }
        });

        initPopup();

    }

    private void initData(){
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(UpdateActivity.this);
        db = sqliteDBHelper.getWritableDatabase();

        picture.setImageURI(Uri.fromFile(new File(card.getPicture())));
        description.setText(card.getDescription());
        title.setText(card.getTitle());
        category_chosen = card.getCategory();
        category.setImageResource(icon[category_chosen]);
        context = this;

        int vibrant, darkVibrant, lightVibrant, muted, darkMuted, lightMuted;
        vibrant = card.getVibrant();
        darkVibrant = card.getDarkVibrant();
        lightVibrant = card.getLightVibrant();
        muted = card.getMuted();
        darkMuted = card.getDarkMuted();
        lightMuted = card.getLightMuted();

        if(vibrant != -1){
            GradientDrawable drawable1 = (GradientDrawable) view1.getBackground();
            drawable1.setColor(vibrant);
        }else{
            view1.setVisibility(View.GONE);
        }

        if(darkVibrant != -1){
            GradientDrawable drawable2 = (GradientDrawable) view2.getBackground();
            drawable2.setColor(darkVibrant);
        }else{
            view2.setVisibility(View.GONE);
        }

        if(lightVibrant != -1){
            GradientDrawable drawable3 = (GradientDrawable) view3.getBackground();
            drawable3.setColor(lightVibrant);
        }else{
            view3.setVisibility(View.GONE);
        }

        if(muted != -1){
            GradientDrawable drawable4 = (GradientDrawable) view4.getBackground();
            drawable4.setColor(muted);
        }else{
            view4.setVisibility(View.GONE);
        }

        if(darkMuted != -1){
            GradientDrawable drawable5 = (GradientDrawable) view5.getBackground();
            drawable5.setColor(darkMuted);
        }else{
            view5.setVisibility(View.GONE);
        }

        if(lightMuted != -1){
            GradientDrawable drawable6 = (GradientDrawable) view6.getBackground();
            drawable6.setColor(lightMuted);
        }else{
            view6.setVisibility(View.GONE);
        }

        if(card.getCollection() == 1){
            col = true;
            collection.setImageResource(R.mipmap.collection_yes);
        }

    }

    private void initPopup(){
        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QMUIPopups.quickAction(context,
                        QMUIDisplayHelper.dp2px(context, 80),
                        QMUIDisplayHelper.dp2px(context, 80))
                        .skinManager(QMUISkinManager.defaultInstance(context))
                        .addAction(new QMUIQuickAction.Action().icon(R.mipmap.scene).text("自然").onClick(
                                new QMUIQuickAction.OnClickListener() {
                                    @Override
                                    public void onClick(QMUIQuickAction quickAction, QMUIQuickAction.Action action, int position) {
                                        quickAction.dismiss();
                                        category_chosen = 0;
                                        category.setImageResource(icon[0]);
                                    }
                                }
                        ))
                        .addAction(new QMUIQuickAction.Action().icon(R.mipmap.sofa).text("家居").onClick(
                                new QMUIQuickAction.OnClickListener() {
                                    @Override
                                    public void onClick(QMUIQuickAction quickAction, QMUIQuickAction.Action action, int position) {
                                        quickAction.dismiss();
                                        category_chosen = 1;
                                        category.setImageResource(icon[1]);
                                    }
                                }
                        ))
                        .addAction(new QMUIQuickAction.Action().icon(R.mipmap.makeup).text("妆容").onClick(
                                new QMUIQuickAction.OnClickListener() {
                                    @Override
                                    public void onClick(QMUIQuickAction quickAction, QMUIQuickAction.Action action, int position) {
                                        quickAction.dismiss();
                                        category_chosen = 2;
                                        category.setImageResource(icon[2]);
                                    }
                                }
                        ))
                        .addAction(new QMUIQuickAction.Action().icon(R.mipmap.cloth).text("服饰").onClick(
                                new QMUIQuickAction.OnClickListener() {
                                    @Override
                                    public void onClick(QMUIQuickAction quickAction, QMUIQuickAction.Action action, int position) {
                                        quickAction.dismiss();
                                        category_chosen = 3;
                                        category.setImageResource(icon[3]);
                                    }
                                }
                        ))
                        .addAction(new QMUIQuickAction.Action().icon(R.mipmap.food).text("美食").onClick(
                                new QMUIQuickAction.OnClickListener() {
                                    @Override
                                    public void onClick(QMUIQuickAction quickAction, QMUIQuickAction.Action action, int position) {
                                        quickAction.dismiss();
                                        category_chosen = 4;
                                        category.setImageResource(icon[4]);
                                    }
                                }
                        ))
                        .addAction(new QMUIQuickAction.Action().icon(R.mipmap.pets).text("宠物").onClick(
                                new QMUIQuickAction.OnClickListener() {
                                    @Override
                                    public void onClick(QMUIQuickAction quickAction, QMUIQuickAction.Action action, int position) {
                                        quickAction.dismiss();
                                        category_chosen = 5;
                                        category.setImageResource(icon[5]);
                                    }
                                }
                        ))
                        .addAction(new QMUIQuickAction.Action().icon(R.mipmap.smile).text("心情").onClick(
                                new QMUIQuickAction.OnClickListener() {
                                    @Override
                                    public void onClick(QMUIQuickAction quickAction, QMUIQuickAction.Action action, int position) {
                                        quickAction.dismiss();
                                        category_chosen = 6;
                                        category.setImageResource(icon[6]);
                                    }
                                }
                        ))
                        .addAction(new QMUIQuickAction.Action().icon(R.mipmap.idea).text("随记").onClick(
                                new QMUIQuickAction.OnClickListener() {
                                    @Override
                                    public void onClick(QMUIQuickAction quickAction, QMUIQuickAction.Action action, int position) {
                                        quickAction.dismiss();
                                        category_chosen = 7;
                                        category.setImageResource(icon[7]);
                                    }
                                }
                        ))
                        .show(v);
            }
        });
        category.setClickable(false);
    }

    private void changeFunc(){
        if(!flag){
            editCard();
            flag = true;
        }

        else{
            finishCard();
            flag = false;
        }

    }

    private void editCard(){
        edit.setImageResource(R.mipmap.finish);
        category.setClickable(true);
        title.setEnabled(true);
        description.setEnabled(true);
    }

    private void finishCard(){

        ContentValues cv = new ContentValues();//实例化一个ContentValues用来装载待插入的数据
        cv.put("title", title.getText().toString());
        cv.put("description", description.getText().toString());
        cv.put("category", category_chosen);
        String whereClause = "id=?";//修改条件
        String[] whereArgs = {String.valueOf(card.getId())};//修改条件的参数

        db.beginTransaction();
        try {
            //执行插入操作)
            if(db.update("card",cv,whereClause,whereArgs) == -1){
                Toast.makeText(this,"修改失败",Toast.LENGTH_SHORT).show();
            }
            db.setTransactionSuccessful();

        }finally{
            db.endTransaction();
        }

        edit.setImageResource(R.mipmap.edit);
        category.setClickable(false);
        title.setEnabled(false);
        description.setEnabled(false);

    }

    private void changeCol(){
        if(col){
            removeCol();
            collection.setImageResource(R.mipmap.collection);
        }
        else {
            addCol();
            collection.setImageResource(R.mipmap.collection_yes);
        }
    }

    private void removeCol() {
        ContentValues cv = new ContentValues();//实例化一个ContentValues用来装载待插入的数据
        cv.put("collection", 0);
        col = false;
        String whereClause = "id=?";//修改条件
        String[] whereArgs = {String.valueOf(card.getId())};//修改条件的参数

        db.beginTransaction();
        try {
            //执行插入操作)
            if(db.update("card",cv,whereClause,whereArgs) == -1){
                Toast.makeText(this,"修改失败",Toast.LENGTH_SHORT).show();
            }
            db.setTransactionSuccessful();

        }finally{
            db.endTransaction();
        }
    }

    private void addCol() {
        ContentValues cv = new ContentValues();//实例化一个ContentValues用来装载待插入的数据
        cv.put("collection", 1);
        col = true;
        String whereClause = "id=?";//修改条件
        String[] whereArgs = {String.valueOf(card.getId())};//修改条件的参数

        db.beginTransaction();
        try {
            //执行插入操作)
            if(db.update("card",cv,whereClause,whereArgs) == -1){
                Toast.makeText(this,"修改失败",Toast.LENGTH_SHORT).show();
            }
            db.setTransactionSuccessful();

        }finally{
            db.endTransaction();
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void viewSaveToImage(View view) {
//        view.setDrawingCacheEnabled(true);
//        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
//        view.setDrawingCacheBackgroundColor(Color.WHITE);

        // 把一个View转换成图片
        Bitmap cachebmp = loadBitmapFromView(view);
        File image;
        FileOutputStream fos;
        String imagePath = "";
        String imageFileName = "";
        try {
            // 判断手机设备是否有SD卡
            boolean isHasSDCard = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
            if (isHasSDCard) {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                imageFileName = "JPEG_" + timeStamp + "_";
                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                File storageDir = cw.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                image = File.createTempFile(
                        imageFileName,  /* prefix */
                        ".jpg",         /* suffix */
                        storageDir      /* directory */
                );

                fos = new FileOutputStream(image);
                imagePath = image.getAbsolutePath();
            } else
                throw new Exception("创建文件失败!");

            cachebmp.compress(Bitmap.CompressFormat.PNG, 90, fos);

            fos.flush();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),imagePath, imageFileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(imagePath)));

        view.destroyDrawingCache();
    }

    private Bitmap loadBitmapFromView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();

        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);

        c.drawColor(Color.WHITE);
        /** 如果不设置canvas画布为白色，则生成透明 */

        v.layout(0, 0, w, h);
        v.draw(c);

        return bmp;
    }
}
