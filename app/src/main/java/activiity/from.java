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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Coalbytruckbean;
import model.Coalfieldzonebean;
import utils.myadapter;

import static activiity.R.id.close;
import static activiity.R.id.spinner;

/**
 * Created by admin on 2017/8/21.
 */

public class from extends AppCompatActivity {

    private List<String > slist = new ArrayList<>();
    private List<String > sslist = new ArrayList<>();
    private List<Coalbytruckbean> llist = new ArrayList<Coalbytruckbean>();
    private Spinner mySpinner;
    private ArrayAdapter<String> adapter;
    private myadapter<Coalfieldzonebean> myArrayAdapter;
    private Button buttonCheck;
    private Button buttonClose;
    private ListView mylistview;
    private TextView myTextView;
    private Map map = new HashMap();
    private JSONArray jsonreal,jsonzone;
    private Coalbytruckbean cya = new Coalbytruckbean();
    private String username;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.from);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
         /*获取Intent中的Bundle对象*/
        Bundle bundle = this.getIntent().getExtras();
        /*获取Bundle中的数据，注意类型和key*/
        username = bundle.getString("username");
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
                try {
                    final JSONObject jsonObject = (JSONObject) jsonzone.get(0);
                    for(int i=0;i<parent.getCount();i++){
                        if (position == i) {
                            buttonCheck.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent i = new Intent(from.this, acceptance.class);
                                    i.putExtra("vehicleno",llist.get(position).getVehicleno());
                                    i.putExtra("coalfieldid",llist.get(position).getCoalfieldid());
                                    i.putExtra("username",username);
                                    try {
                                        i.putExtra("coalbytruckid",(String)jsonObject.get("COALBYTRUCKID"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    startActivity(i);
                                }
                            });
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
           new Thread(new Runnable() {
               @Override
               public void run() {
                   Looper.prepare();

                   String fieldname = mySpinner.getSelectedItem().toString();
                   try {
                       HttpClient httpClient = new DefaultHttpClient();
                       HttpPost httpPost = new HttpPost("http://192.168.0.200:8090/App/GetCoalByTruck");
                       httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
                       JSONObject param = new JSONObject();
                       if (fieldname != null) {
                           param.put("coalId", map.get(fieldname));
                       }
                       StringEntity se = new StringEntity(param.toString());
                       se.setContentType("application/json;charset=utf-8");
                       httpPost.setEntity(se);
                       HttpResponse httpResponse = httpClient.execute(httpPost);
                       String key = EntityUtils.toString(httpResponse.getEntity());
                       JSONArray jsonArray = new JSONArray(key);
                       jsonzone = jsonArray;
                       if(jsonArray.length()!=0) {
                           JSONObject jsonObject = jsonArray.getJSONObject(0);
                           cya.setVehicleno((String) jsonObject.get("vehicleno"));
                           cya.setCoalfieldname((String) jsonObject.get("zonename"));
                           cya.setCoalfieldid(fieldname + "-" + jsonObject.get("zonename"));
                           llist.add(cya);
                       }
                   } catch (Exception e) {
                       e.printStackTrace();
                   }
                   Looper.loop();
               }
           }).start();
       return llist;
   }
   public List<String> myslist() {
       if(slist.size()<1) {
           new Thread(new Runnable() {
               @Override
               public void run() {
                   Looper.prepare();
                   HttpClient httpClient = new DefaultHttpClient();
                   HttpGet httpGet = new HttpGet("http://192.168.0.200:8090/App/GetCoalField");
                   // String key = "[{FIELDNAME:A,COALFIELDID:1},{FIELDNAME:B,COALFIELDID:2}]";
                   try {
                       HttpResponse httpResponse = httpClient.execute(httpGet);
                       String key = EntityUtils.toString(httpResponse.getEntity());
                       JSONArray jsonArray = new JSONArray(key);
                       jsonreal = jsonArray;
                       for (int i = 0; i < jsonArray.length(); i++) {
                           JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                           String name = (String) jsonObject.get("FIELDNAME");
                           Integer id = (Integer) jsonObject.get("COALFIELDID");
                           String iid = String.valueOf(id);
                           sslist.add(name);
                           sslist.add(iid);

                       }
                       for (int i = 0; i < sslist.size(); ) {
                           String s1 = null;
                           String s2 = null;
                           for (int j = i; j < i + 2; j++) {
                               if (j % 2 == 0) {
                                   s1 = sslist.get(j);
                                   slist.add(s1);
                               } else {
                                   s2 = sslist.get(j);
                               }

                           }
                           map.put(s1, s2);
                           i += 2;
                       }
                       adapter.notifyDataSetChanged();
                   } catch (Exception e) {
                       e.printStackTrace();
                   }
                   Looper.loop();
               }
           }).start();
       }
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
