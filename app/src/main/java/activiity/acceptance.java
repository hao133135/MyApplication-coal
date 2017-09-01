package activiity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import model.TruckInformation;

/**
 * Created by admin on 2017/8/21.
 */

public class acceptance extends AppCompatActivity {

    private Button close, save, photograph, btn1, btn2, btn3, btn4;
    private boolean isSelected1=false,isSelected2=false,isSelected3=false,isSelected4=false;
    private final int OPEN_RESULT = 1; //用来打开相机
    private TextView car;
    private TextView cyAear;
    private List<String> choiceList = new ArrayList<String>();
    private List<String> realityAearList = new ArrayList<String>();
    private List<String> errorunitList = new ArrayList<String>();
    private ImageView mImageView1 = null,mImageView2 = null,mImageView3 = null,mImageView4 = null;
    private ArrayAdapter<String> adapter;
    private Spinner choiceSpinner,realityAearSpinner,errorunitSpinner;
    TruckInformation truck = new TruckInformation();
    private String strImgPath;
    private String fileName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acceptance);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /*获取Intent中的Bundle对象*/
        Bundle bundle = this.getIntent().getExtras();
        /*获取Bundle中的数据，注意类型和key*/
        String voicedeviceid = bundle.getString("voicedeviceid");
        String coalfieldid = bundle.getString("coalfieldid");
        car = (TextView) findViewById(R.id.car);
        car.setText(voicedeviceid);
        cyAear = (TextView) findViewById(R.id.coalYardAear);
        cyAear.setText(coalfieldid);
        choiceSpinner = (Spinner) findViewById(R.id.choice);
        realityAearSpinner = (Spinner) findViewById(R.id.realityAear);
        errorunitSpinner = (Spinner) findViewById(R.id.errorunit);

        save = (Button) findViewById(R.id.save);
        close = (Button) findViewById(R.id.close);
        photograph = (Button) findViewById(R.id.photograph);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);
        mImageView1 = (ImageView) findViewById(R.id.imageview1);
        mImageView2 = (ImageView) findViewById(R.id.imageview2);
        mImageView3 = (ImageView) findViewById(R.id.imageview3);
        mImageView4 = (ImageView) findViewById(R.id.imageview4);

        save.setOnClickListener(new saveListener());
        close.setOnClickListener(new closeListener());

        btn1.setOnClickListener(new btn1Listener());
        btn2.setOnClickListener(new btn2Listener());
        btn3.setOnClickListener(new btn3Listener());
        btn4.setOnClickListener(new btn4Listener());

        photograph.setOnClickListener(new photographListener());

        init();

    }

    private void init() {
        JSONArray choice= null;
        JSONArray realityAear = null;
        JSONArray errorunit = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://192.168.0.106:8081/use1r/querycoalyard/");
            httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
            JSONObject param = new JSONObject();
            param.put("p3","p3");
            StringEntity se = new StringEntity(param.toString());
            httpPost.setEntity(se);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            String key = EntityUtils.toString(httpResponse.getEntity());
            JSONObject jsonObject = new JSONObject(key);
            choice = jsonObject.getJSONArray("errorunit");
            realityAear = jsonObject.getJSONArray("realityAear");
            choice = jsonObject.getJSONArray("choice");
            choiceClick(choice);
            realityAearClick(realityAear);
            errorunitClick(errorunit);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    private class saveListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if(car!=null&&cyAear!=null&&choiceSpinner.getSelectedItem()!=null&&realityAearSpinner.getSelectedItem()!=null&&errorunitSpinner.getSelectedItem()!=null){
                new Thread(uploadImageRunnable).start();
            }
        }
    }

    private class closeListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(acceptance.this, from.class);
            startActivity(i);
        }
    }

    private class photographListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if(isSelected1){
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i,OPEN_RESULT);
            }
            if(isSelected2){
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i,OPEN_RESULT);
            }
            if(isSelected3){
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i,OPEN_RESULT);
            }
            if(isSelected4){
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i,OPEN_RESULT);
            }
        }
    }

    private void errorunitClick(JSONArray jsonArray) {
        adapter =new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, errorunitList(jsonArray));
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //第四步：将适配器添加到下拉列表上
        errorunitSpinner.setAdapter(adapter);
    }

    public List<String> errorunitList(JSONArray jsonArray){
        errorunitList.add("无");
        errorunitList.add("有");
        return errorunitList;
    }

    private void realityAearClick(JSONArray jsonArray) {
        adapter =new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, realityAearList(jsonArray));
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //第四步：将适配器添加到下拉列表上
        realityAearSpinner.setAdapter(adapter);
    }

    public List<String> realityAearList(JSONArray jsonArray){

        realityAearList.add("A煤场-北区");
        realityAearList.add("A煤场-南区");
        realityAearList.add("B煤场-北区");
        realityAearList.add("B煤场-南区");
        return realityAearList;
    }

    private void choiceClick(JSONArray jsonArray) {
        adapter =new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,

                choiceList(jsonArray));
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //第四步：将适配器添加到下拉列表上
        choiceSpinner.setAdapter(adapter);
    }

    public List<String> choiceList(JSONArray jsonArray){
        choiceList.add("扣水");
        choiceList.add("扣杂");
        return choiceList;
    }

   public void setBackground(){
       if (isSelected1){
           btn1.setBackgroundResource(R.drawable.backall);
       }else{
           btn1.setBackgroundResource(R.drawable.boder);
       }
       if (isSelected2){
           btn2.setBackgroundResource(R.drawable.backall);
       }else{
           btn2.setBackgroundResource(R.drawable.boder);
       }
       if (isSelected3){
           btn3.setBackgroundResource(R.drawable.backall);
       }else{
           btn3.setBackgroundResource(R.drawable.boder);
       }
       if (isSelected4){
           btn4.setBackgroundResource(R.drawable.backall);
       }else{
           btn4.setBackgroundResource(R.drawable.boder);
       }

   }

    private class btn1Listener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            isSelected1 = true;
            isSelected2 = false;
            isSelected3 = false;
            isSelected4 = false;
            mImageView1.setVisibility(ViewGroup.VISIBLE);
            mImageView2.setVisibility(ViewGroup.GONE);
            mImageView3.setVisibility(ViewGroup.GONE);
            mImageView4.setVisibility(ViewGroup.GONE);

            setBackground();
        }
    }

    private class btn2Listener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            isSelected1 = false;
            isSelected2 = true;
            isSelected3 = false;
            isSelected4 = false;
            mImageView1.setVisibility(ViewGroup.GONE);
            mImageView2.setVisibility(ViewGroup.VISIBLE);
            mImageView3.setVisibility(ViewGroup.GONE);
            mImageView4.setVisibility(ViewGroup.GONE);
            setBackground();
        }
    }

    private class btn3Listener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            isSelected1 = false;
            isSelected2 = false;
            isSelected3 = true;
            isSelected4 = false;
            mImageView1.setVisibility(ViewGroup.GONE);
            mImageView2.setVisibility(ViewGroup.GONE);
            mImageView3.setVisibility(ViewGroup.VISIBLE);
            mImageView4.setVisibility(ViewGroup.GONE);
            setBackground();
        }
    }

    private class btn4Listener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            isSelected1 = false;
            isSelected2 = false;
            isSelected3 = false;
            isSelected4 = true;
            mImageView1.setVisibility(ViewGroup.GONE);
            mImageView2.setVisibility(ViewGroup.GONE);
            mImageView3.setVisibility(ViewGroup.GONE);
            mImageView4.setVisibility(ViewGroup.VISIBLE);
            setBackground();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case OPEN_RESULT:
                if(resultCode == RESULT_OK){
                    String sdStatus = Environment.getExternalStorageState();
                    if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                        Log.i("TestFile",
                                "SD card is not avaiable/writeable right now.");
                        return;
                    }

                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    new DateFormat();
                    String name = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
                    //Toast.makeText(this, name, Toast.LENGTH_LONG).show();
                    FileOutputStream b = null;
                    File file = new File("/sdcard/Image/");
                    file.mkdirs();
                    fileName = file+"/"+name;

                    try {

                        b = new FileOutputStream(fileName);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(fileName));// 把数据写入文件
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            b.flush();
                            b.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if(requestCode==0){
                        //获取从相册界面返回的缩略图
                        bitmap = data.getParcelableExtra("data");
                        if(bitmap==null){//如果返回的图片不够大，就不会执行缩略图的代码，因此需要判断是否为null,如果是小图，直接显示原图即可
                            try {
                                //通过URI得到输入流
                                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                                //通过输入流得到bitmap对象
                                bitmap = BitmapFactory.decodeStream(inputStream);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }else if(requestCode==1){
                        bitmap = (Bitmap) data.getExtras().get("data");
                    }
                    // 将图片显示在ImageView里
                    if(isSelected1){
                        File file1 = new File(fileName);
                        truck.setImagefile1(file1);
                        truck.setCheckphoto1(name);
                        mImageView1.setImageBitmap(bitmap);
                    }
                    if(isSelected2){
                        File file2 = new File(fileName);
                        truck.setImagefile2(file2);
                        truck.setCheckphoto2(name);
                        mImageView2.setImageBitmap(bitmap);
                    }
                    if(isSelected3){
                        File file3 = new File(fileName);
                        truck.setImagefile3(file3);
                        truck.setCheckphoto3(name);
                        mImageView3.setImageBitmap(bitmap);
                    }
                    if(isSelected4){
                        File file4 = new File(fileName);
                        truck.setImagefile4(file4);
                        truck.setCheckphoto4(name);
                        mImageView4.setImageBitmap(bitmap);
                    }
                }
                break;
        }
    }
    /**
     * 使用HttpUrlConnection模拟post表单进行文件
     * 上传平时很少使用，比较麻烦
     * 原理是： 分析文件上传的数据格式，然后根据格式构造相应的发送给服务器的字符串。
     */
    Runnable uploadImageRunnable = new Runnable() {
        @Override
        public void run() {

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://192.168.0.200:8090/api/APP");
            httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
            JSONObject param = new JSONObject();
            /*truck.setVehicleno();
            truck.setCoalfieldid();
            truck.setRealcoalfieldid();
            truck.setDiscount();
            truck.setRemark();
            truck.setDeductweight();
            truck.setState();*/


            try {
                param.put("truck",truck);
                StringEntity se = new StringEntity(param.toString());

                httpPost.setEntity(se);
                HttpResponse httpResponse = httpClient.execute(httpPost);
                String key = EntityUtils.toString(httpResponse.getEntity());
                JSONObject jsonObject = new JSONObject(key);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

}
