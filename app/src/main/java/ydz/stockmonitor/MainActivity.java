package ydz.stockmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

public class MainActivity extends AppCompatActivity {

    private static Handler handler=new Handler();//UI操作类
    private static TextView msgTxt;
    private static EditText statusTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SQLiteStudioService.instance().start(this);//用于启用SQLiteStudio
        msgTxt=(TextView) findViewById(R.id.message);
        statusTxt=(EditText) findViewById(R.id.status);
        final Button fetchButton=(Button)findViewById(R.id.fetchButton);
        Button settingButton=(Button)findViewById(R.id.settingButton);


        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent);
            }
        });

        fetchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FetchUtil fu= FetchUtil.getInstance();
                fu.setMain(MainActivity.this);
                if(fu.getFetchStatus()==fu.FETCH_STOP){
                    fu.start();
                    fetchButton.setText(R.string.fetchButtonSuspend);
                }else if(fu.getFetchStatus()==fu.FETCH_ON){
                    fu.suspend();
                    fetchButton.setText(R.string.fetchButtonResume);
                }else if(fu.getFetchStatus()==fu.FETCH_SUSPEND){
                    fu.resume();
                    fetchButton.setText(R.string.fetchButtonSuspend);
                }else{
                    Log.e("抓取线程status","未知状态 "+fu.getFetchStatus());
                }
            }
        });
    }

    //通过该方法，在其他类中可直接调用进行UI更新
    public static void sendMessage(final Message msg){
        new Thread() {
            @Override
            public void run() {//在run()方法实现业务逻辑；
                //更新UI操作；
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(msg.what==R.id.message){//更新message
                            msgTxt.setText(msg.getData().getString("message")+"\r\n"+msgTxt.getText());
                        }else if(msg.what==R.id.status){//更新status
                            statusTxt.setText(msg.getData().getString("status"));
                        }
                    }
                });
            }
        }.start();
    }
}
