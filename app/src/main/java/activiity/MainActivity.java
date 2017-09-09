package activiity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //v7包下去除标题栏代码：
        //getSupportActionBar().hide();
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        username = (EditText) findViewById(R.id.activity_login_user);
        password = (EditText) findViewById(R.id.activity_login_password);
        button = (Button) findViewById(R.id.activity_login_btn);
        button.setOnClickListener(new buttonListener());
    }
    private class buttonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
           new Thread(new Runnable() {
                @Override
                public void run() {
                    String user = username.getText().toString();
                    String pwd = password.getText().toString();
                    //http://39.108.73.207
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://192.168.0.200:8090/APP/Validate");
                    httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
                   try {
                       JSONObject param = new JSONObject();
                       param.put("LoginName",user);
                       param.put("LoginPwd",pwd);
                       StringEntity se = new StringEntity(param.toString());
                       se.setContentType("application/json;charset=utf-8");
                       httpPost.setEntity(se);
                       HttpResponse httpResponse = httpClient.execute(httpPost);
                       String key = EntityUtils.toString(httpResponse.getEntity());
                       String stats = key.toString();
                      // String stats ="1";
                       if(user.isEmpty()||"".equals(user)||pwd.isEmpty()||"".equals(pwd)){
                           Looper.prepare();
                           Toast.makeText(MainActivity.this,"请输入用户名和密码！",Toast.LENGTH_SHORT).show();
                           Looper.loop();
                       }else if(stats.equals("1")){
                            Intent i = new Intent(MainActivity.this, from.class);
                            i.putExtra("user1",user);
                            startActivity(i);
                        }else {
                            Looper.prepare();
                            Toast.makeText(MainActivity.this,"用户名密码错误！",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
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
}
