package com.example.cocolor.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cocolor.R;
import com.example.cocolor.palette.Palette;
import com.example.cocolor.pojo.Card;
import com.example.cocolor.util.SqliteDBHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopups;
import com.qmuiteam.qmui.widget.popup.QMUIQuickAction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class EditActivity extends AppCompatActivity {

    FloatingActionButton back, add;
    ImageView picture, category;
    EditText title, description;
    Context context;

    View view1, view2, view3, view4, view5, view6;

    int category_chosen = 0;
    private int[] icon = {R.mipmap.scene,R.mipmap.sofa,R.mipmap.makeup,R.mipmap.cloth,R.mipmap.food,R.mipmap.pets,R.mipmap.smile,R.mipmap.idea};
    String currentPhotoPath;

    SQLiteDatabase db;

    int vibrant, darkVibrant, lightVibrant, muted, darkMuted, lightMuted, textColor;    // 柔和的亮色

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        QMUIStatusBarHelper.translucent(EditActivity.this);

        //接收传递过来的参数
        final Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle == null){
            Intent intentmain = new Intent(EditActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentmain);
        }else{
            currentPhotoPath = bundle.getString("currentPhotoPath");
        }

        context = this;
        picture = findViewById(R.id.card_image);
        category = findViewById(R.id.category_edit_view);
        title = findViewById(R.id.title_edit_view);
        description = findViewById(R.id.description_edit_view);
        back = findViewById(R.id.back_to_main);
        add = findViewById(R.id.create_new_card);
        view1 = findViewById(R.id.color_view_1);
        view2 = findViewById(R.id.color_view_2);
        view3 = findViewById(R.id.color_view_3);
        view4 = findViewById(R.id.color_view_4);
        view5 = findViewById(R.id.color_view_5);
        view6 = findViewById(R.id.color_view_6);



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new QMUIDialog.MessageDialogBuilder(context)
                        .setTitle("未保存")
                        .setMessage("确定要返回吗？")
                        .setSkinManager(QMUISkinManager.defaultInstance(context))
                        .addAction("取消", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                            }
                        })
                        .addAction(0, "确定", QMUIDialogAction.ACTION_PROP_POSITIVE, new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                                Intent intentmain = new Intent(EditActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intentmain);
                            }
                        })
                        .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();

            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewCard();
            }
        });

        File file = new File(currentPhotoPath);

        try {
            picture.setImageURI(Uri.fromFile(file));
        }catch (Exception e){
            Intent i = new Intent(EditActivity.this, MainActivity.class);
            startActivity(i);
        }


        FileInputStream input = null;
        try {
            input = new FileInputStream(file);
            extract(BitmapFactory.decodeStream(input));
        } catch (Exception e) {
            e.printStackTrace();
            Intent intentmain = new Intent(EditActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentmain);
        }

        initPopup();

    }

    private void extract(Bitmap bitmap) {

        // 提取颜色
        Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch v, dv, lv, m, dm, lm;
                textColor = Color.BLACK;

                v = palette.getVibrantSwatch();
                dv = palette.getDarkVibrantSwatch();
                lv = palette.getLightVibrantSwatch();
                m = palette.getMutedSwatch();
                dm = palette.getDarkMutedSwatch();
                lm = palette.getLightMutedSwatch();

                if(v != null){
                    GradientDrawable drawable1 = (GradientDrawable) view1.getBackground();
                    vibrant = palette.getVibrantSwatch().getRgb();
                    drawable1.setColor(vibrant);
                }else{
                    vibrant = -1;
                    view1.setVisibility(View.GONE);
                }

                if(lm != null){
                    GradientDrawable drawable6 = (GradientDrawable) view6.getBackground();
                    lightMuted = palette.getLightMutedSwatch().getRgb();
                    drawable6.setColor(lightMuted);
                    textColor = lm.getTitleTextColor();
                }else{
                    lightMuted = -1;
                    view6.setVisibility(View.GONE);
                }

                if(lv != null){
                    GradientDrawable drawable3 = (GradientDrawable) view3.getBackground();
                    lightVibrant = palette.getLightVibrantSwatch().getRgb();
                    drawable3.setColor(lightVibrant);
                    textColor = lv.getTitleTextColor();
                }else{
                    lightVibrant = -1;
                    view3.setVisibility(View.GONE);
                }

                if(m != null){
                    GradientDrawable drawable4 = (GradientDrawable) view4.getBackground();
                    muted = palette.getMutedSwatch().getRgb();
                    drawable4.setColor(muted);
                    textColor = m.getTitleTextColor();
                }else{
                    muted = -1;
                    view4.setVisibility(View.GONE);
                }

                if(dv != null){
                    GradientDrawable drawable2 = (GradientDrawable) view2.getBackground();
                    darkVibrant = palette.getDarkVibrantSwatch().getRgb();
                    drawable2.setColor(darkVibrant);
                    textColor = dv.getTitleTextColor();
                }else{
                    darkVibrant  = -1;
                    view2.setVisibility(View.GONE);
                }


                if(dm != null){
                    GradientDrawable drawable5 = (GradientDrawable) view5.getBackground();
                    darkMuted = palette.getDarkMutedSwatch().getRgb();
                    drawable5.setColor(darkMuted);
                }else{
                    darkMuted = -1;
                    view5.setVisibility(View.GONE);
                }



            }
        });
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
    }

    private void addNewCard(){
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(EditActivity.this);
        db = sqliteDBHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();//实例化一个ContentValues用来装载待插入的数据
        cv.put("title", title.getText().toString());
        cv.put("description", description.getText().toString());
        cv.put("picture", currentPhotoPath);
        cv.put("category", category_chosen);
        cv.put("vibrant", vibrant);
        cv.put("darkVibrant", darkVibrant);
        cv.put("lightVibrant", lightVibrant);
        cv.put("muted", muted);
        cv.put("darkMuted", darkMuted);
        cv.put("lightMuted", lightMuted);
        cv.put("textColor", textColor);
        cv.put("collection", 0);


        db.beginTransaction();
        try {
            //执行插入操作)
            if(db.insert("card", null, cv) == -1){
                Toast.makeText(this,"添加失败",Toast.LENGTH_SHORT).show();
                db.setTransactionSuccessful();
            }else{
                db.setTransactionSuccessful();
                Intent intentmain = new Intent(EditActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentmain);
            }

        }finally{

            db.endTransaction();

        }

    }

}
