package ydz.stockmonitor;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by YDZ on 2017/10/14.
 */

public class DebugActivity extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Button test1=(Button)findViewById(R.id.test1);
        test1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("提示","新增数据");
                DBManager dbm=DBManager.getInstance();
                FetchUtil.getInstance().fetchNew();
                Log.d("提示","抓取所有股票数据");
            }
        });

        Button test2=(Button)findViewById(R.id.test2);
        test2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("提示","追加数据");
                DBManager dbm=DBManager.getInstance();
                FetchUtil.getInstance().updateAll();
                Log.d("提示","抓取所有股票数据");
//                Log.d("提示","修改数据");
//                Stock stock=new Stock();
//                DBManager dbm=DBManager.getInstance();
//                stock.setChgStr("0.05");
//                stock.setHigh(23238);
//                stock.setOpen(111);
//                stock.setTrade((float)(Math.random()*100));
//                stock.setLow(1222);
//                stock.setTurnoverratio(12.5f);
//                stock.setName("测试");
//                stock.setCode("600001");
//                dbm.replaceInto(stock);
//                dbm.close();
            }
        });

        Button test3=(Button)findViewById(R.id.test3);
        test3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("提示","按钮3点击");
                Message msg=new Message();
                msg.what=R.id.status;
                Bundle b=new Bundle();
                b.putString("status","显示信息88888");
                msg.setData(b);
                MainActivity.sendMessage(msg);
//                DBManager dbm=DBManager.getInstance();
//                Stock stock=dbm.getStockByCode("000005");
//                ArrayList<Float> stocks=stock.getChgList();
//                if(stock==null){
//                    Log.i("查询失败","查无此股票");
//                }else{
//                    for(Float f:stocks){
//                        Log.d("涨跌幅：",String.valueOf(f));
//                    }
//                    Log.d("输出Stock",stock.getName()+" "+stock.getHigh()+" "+stock.getChg());
//                }
            }
        });
    }
}
