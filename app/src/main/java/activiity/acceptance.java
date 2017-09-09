package activiity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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

import static activiity.R.id.errorunit;

/**
 * Created by admin on 2017/8/21.
 */

public class acceptance extends AppCompatActivity {

    private Context mContext=acceptance.this;
    private Button close, save, photograph, btn1, btn2, btn3, btn4;
    private boolean isSelected1 = false, isSelected2 = false, isSelected3 = false, isSelected4 = false;
    private final int OPEN_RESULT = 1; //用来打开相机
    private TextView car, cyAear;
    private EditText danwei;
    private List<String> choiceList = new ArrayList<String>();
    private List<String> realityAearList = new ArrayList<String>();
    private List<String> errorunitList = new ArrayList<String>();
    private ImageView mImageView1 = null, mImageView2 = null, mImageView3 = null, mImageView4 = null;
    private ArrayAdapter<String> adapter;
    private Spinner choiceSpinner, realityAearSpinner, errorunitSpinner;
    TruckInformation truck = new TruckInformation();
    private String strImgPath;
    private String fileName;
    private RadioGroup radioGroupID;
    private RadioButton rbtn1, rbtn2;

    private JSONArray jsonReal;
    private String jsoncoal;
    private String username;
    private String coalbytruckid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acceptance);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /*获取Intent中的Bundle对象*/
        Bundle bundle = this.getIntent().getExtras();
        /*获取Bundle中的数据，注意类型和key*/
        String vehicleno = bundle.getString("vehicleno");
        String coalfieldid = bundle.getString("coalfieldid");
        username = bundle.getString("username");
        coalbytruckid = bundle.getString("coalbytruckid");
        car = (TextView) findViewById(R.id.car);
        car.setText(vehicleno);
        cyAear = (TextView) findViewById(R.id.coalYardAear);
        cyAear.setText(coalfieldid);
        choiceSpinner = (Spinner) findViewById(R.id.choice);
        realityAearSpinner = (Spinner) findViewById(R.id.realityAear);
        errorunitSpinner = (Spinner) findViewById(errorunit);
        danwei = (EditText) findViewById(R.id.danwei);
        radioGroupID = (RadioGroup) findViewById(R.id.radioGroupID);
        rbtn1 = (RadioButton) findViewById(R.id.femaleGroupID);
        rbtn2 = (RadioButton) findViewById(R.id.maleGroupID);
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

        radioGroupID.setOnCheckedChangeListener(new radioListener());
        save.setOnClickListener(new saveListener());
        close.setOnClickListener(new closeListener());

        btn1.setOnClickListener(new btn1Listener());
        btn2.setOnClickListener(new btn2Listener());
        btn3.setOnClickListener(new btn3Listener());
        btn4.setOnClickListener(new btn4Listener());

        photograph.setOnClickListener(new photographListener());

        init();
        save.setOnClickListener(new saveListener());

    }

    private void init() {
        choiceClick();
        errorunit();
        realityAearClick();
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
            new Thread(uploadImageRunnable).start();
            Intent i = new Intent(acceptance.this, from.class);
            i.putExtra("username", username);
            startActivity(i);
        }
    }

    private class closeListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            finish();
        }
    }

    private class photographListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (isSelected1 || isSelected2 || isSelected3 || isSelected4) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, OPEN_RESULT);
            } else {
                Looper.prepare();
                Toast.makeText(acceptance.this, "请选择拍照图片！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }
    }


    private void realityAearClick() {
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, realityAearList());
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //第四步：将适配器添加到下拉列表上
        realityAearSpinner.setAdapter(adapter);
    }

    public List<String> realityAearList() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet("http://192.168.0.200:8090/App/GetCoalFieldZone");
                // String key = "[{FIELDNAME:A,COALFIELDID:1},{FIELDNAME:B,COALFIELDID:2}]";
                try {
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    String key = EntityUtils.toString(httpResponse.getEntity());
                    JSONArray jsonArray = new JSONArray(key);
                    JSONObject jsonObject = new JSONObject();
                    jsonReal = jsonArray;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = (JSONObject) jsonArray.get(i);
                        String fieldname = (String) jsonObject.get("FIELDNAME");
                        String zonename = (String) jsonObject.get("zonename");
                        realityAearList.add(fieldname + "-" + zonename);
                    }
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Looper.loop();
            }
        }).start();

        return realityAearList;
    }

    private void errorunit() {
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                errorunitList());
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //第四步：将适配器添加到下拉列表上
        errorunitSpinner.setAdapter(adapter);
    }

    public List<String> errorunitList() {
        errorunitList.add("有");
        errorunitList.add("无");
        return errorunitList;
    }

    private void choiceClick() {
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                choiceList());
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //第四步：将适配器添加到下拉列表上
        choiceSpinner.setAdapter(adapter);

    }

    public List<String> choiceList() {
        choiceList.add("扣矸");
        choiceList.add("扣水");
        choiceList.add("扣杂");
        return choiceList;
    }

    public void setBackground() {
        if (isSelected1) {
            btn1.setBackgroundResource(R.drawable.backall);
        } else {
            btn1.setBackgroundResource(R.drawable.boder);
        }
        if (isSelected2) {
            btn2.setBackgroundResource(R.drawable.backall);
        } else {
            btn2.setBackgroundResource(R.drawable.boder);
        }
        if (isSelected3) {
            btn3.setBackgroundResource(R.drawable.backall);
        } else {
            btn3.setBackgroundResource(R.drawable.boder);
        }
        if (isSelected4) {
            btn4.setBackgroundResource(R.drawable.backall);
        } else {
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
        switch (requestCode) {
            case OPEN_RESULT:
                if (resultCode == RESULT_OK) {
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
                    fileName = file + "/" + name;
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
                    if (requestCode == 0) {
                        //获取从相册界面返回的缩略图
                        bitmap = data.getParcelableExtra("data");
                        if (bitmap == null) {//如果返回的图片不够大，就不会执行缩略图的代码，因此需要判断是否为null,如果是小图，直接显示原图即可
                            try {
                                //通过URI得到输入流
                                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                                //通过输入流得到bitmap对象
                                bitmap = BitmapFactory.decodeStream(inputStream);

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (requestCode == 1) {
                        bitmap = (Bitmap) data.getExtras().get("data");
                    }

                    // 将图片显示在ImageView里
                    if (isSelected1) {
                        File file1 = new File(fileName);
                        truck.setImagefile1(file1);
                        truck.setCheckphoto1(name);
                        mImageView1.setImageBitmap(bitmap);
                    }
                    if (isSelected2) {
                        File file2 = new File(fileName);
                        truck.setImagefile2(file2);
                        truck.setCheckphoto2(name);
                        mImageView2.setImageBitmap(bitmap);
                    }
                    if (isSelected3) {
                        File file3 = new File(fileName);
                        truck.setImagefile3(file3);
                        truck.setCheckphoto3(name);
                        mImageView3.setImageBitmap(bitmap);
                    }
                    if (isSelected4) {
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
            Looper.prepare();
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://192.168.0.200:8090/App/Update");
            httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
            JSONObject param = new JSONObject();
            truck.setVehicleno(car.getText().toString());
            truck.setCoalfieldid(cyAear.getText().toString());
            truck.setRealcoalfieldid(realityAearSpinner.getSelectedItem().toString());
            truck.setDiscount(choiceSpinner.getSelectedItem().toString());
            truck.setRemark(errorunitSpinner.getSelectedItem().toString());
            truck.setDeductweight(danwei.getText().toString());
            try {
                JSONObject jsonObject = new JSONObject();
                String s = realityAearSpinner.getSelectedItem().toString();
                for (int i = 0; i < jsonReal.length(); i++) {
                    jsonObject = (JSONObject) jsonReal.get(i);
                    String fieldname = (String) jsonObject.get("FIELDNAME");
                    String zonename = (String) jsonObject.get("zonename");
                    if ((fieldname + "-" + zonename).equals(realityAearSpinner.getSelectedItem().toString())) {
                        truck.setCoalmineid(String.valueOf(jsonObject.get("coalfieldid")));
                    }
                }
                if (truck.getRemark().equals("通过验收")) {
                    param.put("state", 5);
                } else if (truck.getRemark().equals("退车")) {
                    param.put("state", 7);
                }

                param.put("coalbytruckid", coalbytruckid);
                param.put("checkuser", username);
                param.put("vehicleno", truck.getVehicleno());
                param.put("realcoalfieldid", truck.getCoalmineid());
                if (truck.getRemark().equals("有")) {
                    param.put("illegaid", 1);
                } else if (truck.getRemark().equals("无")) {
                    param.put("illegaid", 0);
                }

                if (truck.getDiscount().equals("扣水")) {
                    param.put("waterdiscount", truck.getDeductweight());
                } else if (truck.getDiscount().equals("扣矸")) {
                    param.put("wasterockdiscount", truck.getDeductweight());
                } else if (truck.getDiscount().equals("扣杂")) {
                    param.put("impuritydiscount", truck.getDeductweight());
                }

                if (truck.getCheckphoto1() != null) {
                    param.put("checkphoto1", truck.getCheckphoto1());
                }
                if (truck.getCheckphoto2() != null) {
                    param.put("checkphoto2", truck.getCheckphoto2());
                }
                if (truck.getCheckphoto3() != null) {
                    param.put("checkphoto3", truck.getCheckphoto3());
                }
                if (truck.getCheckphoto4() != null) {
                    param.put("checkphoto4", truck.getCheckphoto4());
                }


            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                StringEntity se = new StringEntity(param.toString());
                httpPost.setEntity(se);
                HttpResponse httpResponse = httpClient.execute(httpPost);
                String key = EntityUtils.toString(httpResponse.getEntity());
                if (key.length() > 0) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {



                            if (truck.getImagefile1() != null) {
                                try {
                                    String[] params = {String.valueOf(truck.getImagefile1()),String.valueOf(truck.getImagefile2()),String.valueOf(truck.getImagefile3()),String.valueOf(truck.getImagefile4())};
                                    uploadFile("http://192.168.0.200:8090/APP/UploadImg",params);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }



                        }
                    }).start();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            Looper.loop();
        }
    };


    private class radioListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {

            if (rbtn1.getId() == i) {
                truck.setState(rbtn1.getText().toString());
            } else if (rbtn2.getId() == i) {
                truck.setState(rbtn2.getText().toString());
            }

        }
    }

    public void uploadFile(String url,String... parmas) throws Exception {
        String path =parmas[0];
        String path2 =parmas[1];
        String path3 = parmas[2];
        String path4 = parmas[3];
        File file = new File(path);
        File file2 = new File(path2);
        File file3 = new File(path3);
        File file4 = new File(path4);
        Looper.prepare();

        if (file.exists() && file.length() > 0) {
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("file1", file);
            if (file2.exists() && file2.length() > 0) params.put("file2", file2);
            if (file3.exists() && file3.length() > 0) params.put("file3", file3);
            if (file4.exists() && file4.length() > 0) params.put("file4", file4);
            // 上传文件
            client.post(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers,
                                      byte[] responseBody) {
                    // 上传成功后要做的工作
                    Toast.makeText(mContext, "上传成功", Toast.LENGTH_LONG).show();

                }

                @Override
                public void onFailure(int statusCode, Header[] headers,
                                      byte[] responseBody, Throwable error) {
                    // 上传失败后要做到工作
                    Toast.makeText(mContext, "上传失败", Toast.LENGTH_LONG).show();
                }


                @Override
                public void onRetry(int retryNo) {
                    // TODO Auto-generated method stub
                    super.onRetry(retryNo);
                    // 返回重试次数
                }

            });
        } else {
            Toast.makeText(mContext, "文件不存在", Toast.LENGTH_LONG).show();
        }
        Looper.loop();
    }

}

