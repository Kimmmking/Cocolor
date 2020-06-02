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
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cocolor.R;
import com.example.cocolor.pojo.Card;
import com.example.cocolor.util.SqliteDBHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

public class ColorActivity extends AppCompatActivity {

    private View view1, view2, view3, view4, view5, view6;
    private TextView textView1, textView2, textView3, textView4, textView5, textView6;
    private LinearLayout linearLayout1, linearLayout2, linearLayout3, linearLayout4, linearLayout5, linearLayout6;
    private Card card;
    private FloatingActionButton back, download;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color);

        QMUIStatusBarHelper.translucent(ColorActivity.this);

        //接收传递过来的参数
        final Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle == null){
            Intent intent1 = new Intent(ColorActivity.this, CollectionActivity.class);
            startActivity(intent1);
        }

        card = (Card) bundle.get("card");
        context = this;

        back = findViewById(R.id.back_to_main);
        download = findViewById(R.id.download);

        view1 = findViewById(R.id.color_view_1);
        view2 = findViewById(R.id.color_view_2);
        view3 = findViewById(R.id.color_view_3);
        view4 = findViewById(R.id.color_view_4);
        view5 = findViewById(R.id.color_view_5);
        view6 = findViewById(R.id.color_view_6);

        textView1 = findViewById(R.id.text_view_1);
        textView2 = findViewById(R.id.text_view_2);
        textView3 = findViewById(R.id.text_view_3);
        textView4 = findViewById(R.id.text_view_4);
        textView5 = findViewById(R.id.text_view_5);
        textView6 = findViewById(R.id.text_view_6);

        linearLayout1 = findViewById(R.id.linear1);
        linearLayout2 = findViewById(R.id.linear2);
        linearLayout3 = findViewById(R.id.linear3);
        linearLayout4 = findViewById(R.id.linear4);
        linearLayout5 = findViewById(R.id.linear5);
        linearLayout6 = findViewById(R.id.linear6);

        final ScrollView view = findViewById(R.id.view);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentmain = new Intent(ColorActivity.this, CollectionActivity.class);
                startActivity(intentmain);
            }
        });

        initView();


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
                back.setVisibility(View.GONE);

                new Handler().post(runnable);

                download.setVisibility(View.VISIBLE);
                back.setVisibility(View.VISIBLE);

            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void initView() {

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
            textView1.setText("RGB(" + Color.red(vibrant) + "," + Color.green(vibrant) + "," + Color.blue(vibrant) + ")");
        }else{
            linearLayout1.setVisibility(View.GONE);
        }

        if(darkVibrant != -1){
            GradientDrawable drawable2 = (GradientDrawable) view2.getBackground();
            drawable2.setColor(darkVibrant);
            textView2.setText("RGB(" + Color.red(darkVibrant) + "," + Color.green(darkVibrant) + "," + Color.blue(darkVibrant) + ")");
        }else{
            linearLayout2.setVisibility(View.GONE);
        }

        if(lightVibrant != -1){
            GradientDrawable drawable3 = (GradientDrawable) view3.getBackground();
            drawable3.setColor(lightVibrant);
            textView3.setText("RGB(" + Color.red(lightVibrant) + "," + Color.green(lightVibrant) + "," + Color.blue(lightVibrant) + ")");
        }else{
            linearLayout3.setVisibility(View.GONE);
        }

        if(muted != -1){
            GradientDrawable drawable4 = (GradientDrawable) view4.getBackground();
            drawable4.setColor(muted);
            textView4.setText("RGB(" + Color.red(muted) + "," + Color.green(muted) + "," + Color.blue(muted) + ")");
        }else{
            linearLayout4.setVisibility(View.GONE);
        }

        if(darkMuted != -1){
            GradientDrawable drawable5 = (GradientDrawable) view5.getBackground();
            drawable5.setColor(darkMuted);
            textView5.setText("RGB(" + Color.red(darkMuted) + "," + Color.green(darkMuted) + "," + Color.blue(darkMuted) + ")");
        }else{
            linearLayout5.setVisibility(View.GONE);
        }

        if(lightMuted != -1){
            GradientDrawable drawable6 = (GradientDrawable) view6.getBackground();
            drawable6.setColor(lightMuted);
            textView6.setText("RGB(" + Color.red(lightMuted) + "," + Color.green(lightMuted) + "," + Color.blue(lightMuted) + ")");
        }else{
            linearLayout6.setVisibility(View.GONE);
        }

    }

    @SuppressLint("SimpleDateFormat")
    private void viewSaveToImage(View view) {

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

