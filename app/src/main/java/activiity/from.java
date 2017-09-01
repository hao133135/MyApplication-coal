package activiity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import model.Coalbytruckbean;
import model.Coalfieldzonebean;
import utils.myadapter;

import static activiity.R.id.close;
import static activiity.R.id.spinner;

/**
 * Created by admin on 2017/8/21.
 */

public class from extends AppCompatActivity {

    private List<String> slist = new ArrayList<String>();
    private List<Coalbytruckbean> llist = new ArrayList<Coalbytruckbean>();
    private TextView myTextView;
    private Spinner mySpinner;
    private ArrayAdapter<String> adapter;
    private Button buttonCheck;
    private Button buttonClose;
    private boolean isSelected = false;

    private ListView mylistview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.from);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mySpinner = (Spinner) findViewById(spinner);
        buttonCheck = (Button) findViewById(R.id.Check);
        buttonClose = (Button) findViewById(close);
        mylistview = (ListView) findViewById(R.id.listview);
        buttonClose.setOnClickListener(new closeListener());
        buttonCheck.setOnClickListener(new clickListener());
        spinnerClick();
    }


    private void listViewClick() {
        final myadapter<Coalfieldzonebean> myArrayAdapter = new myadapter<Coalfieldzonebean>
                (this,mylistView(),R.layout.items);
        mylistview.setAdapter(myArrayAdapter);
        mylistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
            myArrayAdapter.setSelectedItem(position);
            myArrayAdapter.notifyDataSetChanged();

                for(int i=0;i<parent.getCount();i++){
                    if (position == i) {
                        buttonCheck.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(from.this, acceptance.class);
                                i.putExtra("vehicleno",(String)llist.get(position).getVehicleno());
                                i.putExtra("coalfieldid",llist.get(position).getCoalfieldid());
                                startActivity(i);
                            }
                        });
                    }
                }
            }
        });
    }


    private class clickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                        Looper.prepare();
                        Toast.makeText(from.this, "请选择验收车辆！", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                }
            }).start();

        }
    }

    private class closeListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(from.this, MainActivity.class);
            startActivity(i);
        }
    }

    private void spinnerClick() {
        //第一步：添加一个下拉列表项的list，这里添加的项就是下拉列表的菜单项

        //第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list。
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, myslist());
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //第四步：将适配器添加到下拉列表上
        mySpinner.setAdapter(adapter);
        //第五步：为下拉列表设置各种事件的响应，这个事响应菜单被选中
        mySpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                /* 将所选mySpinner 的值带入myTextView 中*/
                adapter.getItem(arg2).toString();
                /* 将mySpinner 显示*/
                arg0.setVisibility(View.VISIBLE);
                cleanlist();
                listViewClick();
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                myTextView.setText("NONE");
                arg0.setVisibility(View.VISIBLE);
            }
        });
        /*下拉菜单弹出的内容选项触屏事件处理*/
        mySpinner.setOnTouchListener(new Spinner.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub

                return false;
            }
        });
        /*下拉菜单弹出的内容选项焦点改变事件处理*/
        mySpinner.setOnFocusChangeListener(new Spinner.OnFocusChangeListener(){
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub

            }
        });
    }

   public List<Coalbytruckbean> mylistView(){
       Coalbytruckbean cya = null;

       String fieldname = mySpinner.getSelectedItem().toString();
       System.out.print(fieldname);
       try {
           HttpClient httpClient = new DefaultHttpClient();
           HttpPost httpPost = new HttpPost("http://192.168.0.106:8081/1user/querycoalyard/");
           httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
           JSONObject param = new JSONObject();
           param.put("fieldname",fieldname);
           StringEntity se = new StringEntity(param.toString());
           httpPost.setEntity(se);
           HttpResponse httpResponse = httpClient.execute(httpPost);
           String key = EntityUtils.toString(httpResponse.getEntity());
           JSONObject jsonObject = new JSONObject(key);
           JSONArray jsonvehicleno = jsonObject.getJSONArray("vehicleno");
           JSONArray jsoncoalfieldid = jsonObject.getJSONArray("coalfieldid");
           for (int i = 0; i < jsonvehicleno.length(); i++) {
               cya = new Coalbytruckbean();
               Object vehicleno = jsonvehicleno.get(i);
               Object coalfieldid = jsoncoalfieldid.get(i);
               cya.setVehicleno(vehicleno);
               cya.setCoalfieldid((BigDecimal) coalfieldid);
               llist.add(cya);
           }
       }catch (Exception e){
           e.printStackTrace();
       }

       return llist;
   }
   public List<String> myslist(){

       try {
           HttpClient httpClient = new DefaultHttpClient();
           HttpPost httpPost = new HttpPost("http://192.168.0.200:8090/home/GetProduct");
           httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
           JSONObject param = new JSONObject();
           param.put("p2", "p2");
           StringEntity se = new StringEntity(param.toString());
           httpPost.setEntity(se);
           HttpResponse httpResponse = httpClient.execute(httpPost);
           String key = EntityUtils.toString(httpResponse.getEntity());

           JSONObject jsonObject = new JSONObject(key);
           JSONArray jsonArray = jsonObject.getJSONArray("fieldname");
           for (int i = 0; i < jsonArray.length(); i++) {
               Object o = jsonArray.get(i);
               slist.add(o.toString());
           }
       } catch (Exception e) {
           e.printStackTrace();
       }

       slist.add("请选择");
       slist.add("A煤场");
       slist.add("B煤场");
       slist.add("C煤场");

       return slist;
   }
    //清除处理
    private void cleanlist(){
        int size=llist.size();
        if(size>0){
            System.out.println(size);
            llist.removeAll(llist);
            adapter.notifyDataSetChanged();
            mylistview.setAdapter(adapter);
        }
    }



}
