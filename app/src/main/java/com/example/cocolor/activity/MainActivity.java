package com.example.cocolor.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.cocolor.R;
import com.example.cocolor.pojo.Card;
import com.example.cocolor.pojo.CardDAO;
import com.example.cocolor.pojo.CardRecyclerViewAdapter;
import com.example.cocolor.util.SqliteDBHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.pullLayout.QMUIPullLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;

    private QMUIPullLayout mPullLayout;
    private TextView blankTextView, topTitle;
    private RecyclerView recycleView;
    private SearchView searchView;
    private ImageView back;


    private CardDAO cardDAO;
    private CardRecyclerViewAdapter cardRecyclerViewAdapter;

    private int pageSize = 8;// 每页能显示的记录数
    private int pageIndex = 1;// 页数。默认第一页
    private int pageCount;// 总页数，记录完了不再加载数
    private List<Card> totalList;
    private String currentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        QMUIStatusBarHelper.translucent(MainActivity.this);
        cardDAO = new CardDAO(MainActivity.this);

        blankTextView = findViewById(R.id.blank_textview);
        FloatingActionButton addItemButton = findViewById(R.id.addItem);
        FloatingActionButton collection = findViewById(R.id.colloection);
        mPullLayout = findViewById(R.id.pull_to_refresh);
        recycleView = findViewById(R.id.display_cards_recycleview);
        searchView = findViewById(R.id.search_view);
        topTitle = findViewById(R.id.topbat_title);
        back = findViewById(R.id.back);

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSimpleBottomSheetList();
            }
        });

        collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CollectionActivity.class);
               startActivity(intent);
            }
        });


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

//    底部选择栏
    private void showSimpleBottomSheetList() {
        QMUIBottomSheet.BottomListSheetBuilder builder = new QMUIBottomSheet.BottomListSheetBuilder(this);
        builder.setGravityCenter(true)
                .setAddCancelBtn(false)
                .setAllowDrag(false)
                .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                        dialog.dismiss();
                        switch (position){
                            case 0:
//                                TODO 调用相机
                                dispatchTakePictureIntent();
                                break;
                            case 1:
//                                TODO 调用相册
                                //  动态申请WRITE_EXTERNAL_STORAGE 这个危险权限。因为相册中的照片时存储在SD卡上的，我们从SD卡中读取照片就需要申请这个权限
                                //  WRITE_EXTERNAL_STORAGE  ——  同时授权程序对SD卡读和写的能力
                                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                                }else{
                                    openAlbum();        //  用户授权了权限申请之后就会调用该方法
                                }
                                break;
                        }
                    }
                });

        builder.addItem(ContextCompat.getDrawable(this, R.mipmap.camera), "Take Photo");
        builder.addItem(ContextCompat.getDrawable(this, R.mipmap.gallery), "Choose From Gallery");

        builder.build().show();
    }


    private void onLoadMore(){
        if (pageIndex < pageCount) {
            pageIndex++;
            totalList.addAll(getSqlDataToListview());
            cardRecyclerViewAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "没有更多了！", Toast.LENGTH_LONG).show();
        }

    }

    private void initDatabase() {
        // 创建数据库对象
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(MainActivity.this);
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
                back.setVisibility(View.GONE);
                topTitle.setVisibility(View.GONE);
                searchView.setVisibility(View.VISIBLE);
                // 先计算总页数
                String sql = "select count(*) from card order by id desc";// 一行一列数据，即一个表
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
                cardRecyclerViewAdapter.setItem(totalList);
            }
        });


//      TODO 分页显示
        // 先计算总页数
        String sql = "select count(*) from card order by id desc";// 一行一列数据，即一个表
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

        cardRecyclerViewAdapter = new CardRecyclerViewAdapter(totalList);
        recycleView.setAdapter(cardRecyclerViewAdapter);




//        TODO 搜索
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            private String TAG = getClass().getSimpleName();

            /*
             * 在输入时触发的方法，当字符真正显示到searchView中才触发，像是拼音，在舒服法组词的时候不会触发
             */
            @Override
            public boolean onQueryTextChange(String queryText) {
                return true;
            }

            /*
             * 输入完成后，提交时触发的方法，一般情况是点击输入法中的搜索按钮才会触发。表示现在正式提交了
             */
            @Override
            public boolean onQueryTextSubmit(String queryText) {
                Log.d(TAG, "onQueryTextSubmit = " + queryText);

                back.setVisibility(View.VISIBLE);
                topTitle.setVisibility(View.VISIBLE);
                searchView.setVisibility(View.GONE);
                topTitle.setText(queryText);

                String searchSQL = "select * from card where title like '%" + queryText + "%' or description like '%" + queryText + "%' or category like '%" + queryText + "%' order by id desc";
                totalList = cardDAO.selectList(searchSQL, new String[]{});
                cardRecyclerViewAdapter.setItem(totalList);

                if (searchView != null) {
                    // 得到输入管理对象
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        // 这将让键盘在所有的情况下都被隐藏，但是一般我们在点击搜索按钮后，输入法都会乖乖的自动隐藏的。
                        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0); // 输入法如果是显示状态，那么就隐藏输入法                    }
                        searchView.clearFocus(); // 不获取焦点
                    }
                }
                return true;
            }
        });

    }

//    TODO 分页相关
    public List<Card> getSqlDataToListview() {
        // 分页Sql
        String sql = "select * from card order by id desc limit ?,?";
        // 每次从start开始，取pageSize个记录，将前一页去过的数据去掉
        int start = (pageIndex - 1) * pageSize;
        return cardDAO.selectList(sql, new String[] { start + "", pageSize + "" });
    }


//    相机 / 相册相关
    @SuppressLint("SimpleDateFormat")
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.cocolor.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, TAKE_PHOTO);
            }
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void openAlbum() {
        /**
         *      启动相册程序
         */
        //  action —— android.intent.action.GET_CONTENT
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        //  该函数表示要查找文件的mime类型（如*/*），这个和组件在manifest里定义的相对应，但在源代码里
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);    //打开相册,用自定义常量 —— CHOOSE_PHOTO来作为case处理图片的标识
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //  通过 Uri 和 selection 来获取真实图片的路径
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if (cursor != null){
            if (cursor.moveToNext()){
                //  MediaStore.Images.Media.insertImage —— 得到保存图片的原始路径
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)){
            //  如果返回的Uri是 document 类型的话，那就取出 document id 进行处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];        //  解析出数字格式id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            //  如果是 content 类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri,null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            //  如果是 file 类型的 Uri ，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        return imagePath;
    }

    private String handleImageBeforeKitKat(Intent data) {
        return getImagePath(data.getData(),null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode){
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    galleryAddPic();
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT  >= 19){      //  如果是在4.4及以 上 系统的手机就调用该方法来处理图片
                        currentPhotoPath = handleImageOnKitKat(data);
                    }else{
                        currentPhotoPath = handleImageBeforeKitKat(data);      //  如果是在4.4以 下 系统的手机就调用该方法来处理图片
                    }
                }
                break;
            default:
                break;
        }


        Intent intent = new Intent(MainActivity.this,EditActivity.class);
        intent.putExtra("currentPhotoPath",currentPhotoPath);

        startActivity(intent);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else {
                    Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}








