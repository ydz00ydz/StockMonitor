package ydz.stockmonitor;

import android.app.Activity;
import android.app.Notification;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by YDZ on 2017/10/13.
 * 新浪接口已经限制单条抓取数据长度，并且限制抓取频率
 */

public class FetchUtil {
    private MainActivity act;
    public int fetch_delay = 10000;
    public int FETCH_STOP = 0;//抓取状态，0：停止，1：运行，2：暂停
    public int FETCH_ON = 1;//抓取状态，0：停止，1：运行，2：暂停
    public int FETCH_SUSPEND = 2;//抓取状态，0：停止，1：运行，2：暂停
    private int fetchStatus = FETCH_STOP;
    public static int ACTION_ADD = 1;//添加动作，每天开始前抓取两次，把新股添加进去。
    public static int ACTION_APPEND = 2;//追加动作，主要的抓取动作，抓取完把chg追加入数据库chgStr。
    //    private int page=1;//抓取的页数,下面链接一次只能获取100个股票信息，
    public static String URL = "http://quote.tool.hexun.com/hqzx/quote.aspx?type=2&market=0&sorttype=0&updown=down&page=1&count=10000&time=";//和讯网数据
    private static FetchUtil instance= new FetchUtil();
    private HashMap<String, Stock> stocks;

    private FetchUtil() {

    }

    public static FetchUtil getInstance() {
//        instance.act=act;
        return instance;
    }

    public void setMain(MainActivity act) {
        instance.act = act;
    }


    /**
     * 开启抓取线程，先添加新股票，之后就是更新股票数据
     */
    public void start() {
        Toast.makeText(MyApplication.getContext(), "开始抓取数据", Toast.LENGTH_SHORT).show();
        this.fetchStatus = FETCH_ON;
        final Handler handler = new Handler();
        Runnable r1 = new Runnable() {//开启分线程，进行抓取、读取、监控任务
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Log.d("FetchUtil", "R1线程开始");
                appendMessage("开始监控...");
                fetchNew();//开始先抓取增添新股票
                fetch_delay=PreferencesUtil.getInstance().getFetchDealy();

                Runnable r2 = new Runnable() {//再开启分线程，进行更新监控任务
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        fetch_delay=PreferencesUtil.getInstance().getFetchDealy();
                        try {
                            Log.d("FetchUtil", "R2线程...");
                            if(fetchStatus==FETCH_ON){
                                setStatus("抓取数据...");
                                updateAll();
                            }else{
                                setStatus("暂停抓取");
                            }
                            handler.postDelayed(this, fetch_delay);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                handler.postDelayed(r2, fetch_delay);
            }
        };
        handler.post(r1);
    }

    /**
     * 暂停抓取线程
     */
    public void suspend() {
        Toast.makeText(MyApplication.getContext(), "暂停抓取数据", Toast.LENGTH_SHORT).show();
        setStatus("暂停中");
        this.fetchStatus = FETCH_SUSPEND;
    }

    public void resume() {
        Toast.makeText(MyApplication.getContext(), "继续抓取数据", Toast.LENGTH_SHORT).show();
        setStatus("继续抓取数据...");
        this.fetchStatus = FETCH_ON;
    }


    /**
     * 刚开始监控任务时，先fetchNew，抓取新股，该方法结束时把数据库初始化到stocks
     */
    public void fetchNew() {
        if (this.fetchStatus != FETCH_ON) {
            return;
        }
        final OkHttpClient mHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(URL).build();
        try {
            mHttpClient.newCall(request).enqueue(new Callback() {//主线程中不能进行网络请求，该请求为异步
                public void onFailure(Call call, IOException e) {
                }

                public void onResponse(Call call, Response response) throws IOException {
                    StringBuffer sb = new StringBuffer(response.body().string());
                    if(sb.length()==0){
                        Log.d("FetchUtil", "fetchNew 抓取为空");
                        setStatus("fetchNew 抓取为空");
                        return;
                    }
                    sb = new StringBuffer(sb.substring(sb.indexOf("[ [") + 2, sb.indexOf("]]")));
                    String[] tmp = sb.toString().split("\r\n");
                    ArrayList<Stock> stocksList = new ArrayList<Stock>();
                    for (String str : tmp) {
                        //整理完数据：  603999,读者传媒,9.89,     0.82,    9.81,   9.80, 9.93,   9.73, 24152.66, 23792343, 1.05,          2.04,      0.12
                        //              code  ,name    ,trade,    Chg,    preclose,open, high,   low,  volume,  turnover,  turnoverratio, daychange, pe
                        //              代码， 名字，   最新价格，涨跌幅， 昨收，  今开， 最高， 最低，成交量，  成交额，   换手率，      振幅，     市盈率
                        str = str.replace("[", "").replace("'", "").replace("],", "").replace(" ", "");
                        String[] strs = str.split(",");
                        if (strs != null && strs.length == 13 && strs[0].length() == 6) {
                            Stock stock = new Stock();
//                            Log.d("FetchUtil",stock.getName()+" "+stock.getChgList().size());
                            stock.setCode(strs[0].trim());
                            stock.setName(strs[1].trim());
                            stock.setTrade(Float.valueOf(strs[2]));
                            stock.setChg(Float.valueOf(strs[3]));
                            stock.setOpen(Float.valueOf(strs[5]));
                            stock.setHigh(Float.valueOf(strs[6]));
                            stock.setLow(Float.valueOf(strs[7]));
                            stock.setTurnoverratio(Float.valueOf(strs[10]));
                            stock.appendChg(Float.valueOf(strs[3]));
                            stocksList.add(stock);
                        }
                    }
                    appendToDB(stocksList, ACTION_ADD);//存入数据库
                    initStocks();//抓取新股后就初始化stocks
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新股票数据，在进入监控主体任务后启用该方法
     */
    public void updateAll() {
        final OkHttpClient mHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(URL).build();
        try {
            mHttpClient.newCall(request).enqueue(new Callback() {//主线程中不能进行网络请求，该请求为异步
                public void onFailure(Call call, IOException e) {
                }

                public void onResponse(Call call, Response response) throws IOException {
                    Log.d("FetchUtil", "updateAll 抓取到数据");
                    setStatus("抓取到数据，分析中...");
                    long t = System.currentTimeMillis();
                    StringBuffer sb = new StringBuffer(response.body().string());
                    sb = new StringBuffer(sb.substring(sb.indexOf("[ [") + 2, sb.indexOf("]]")));
                    String[] tmp = sb.toString().split("\r\n");
                    ArrayList<Stock> stocksList = new ArrayList<Stock>();
                    int count = 0;
                    DBManager dbm = DBManager.getInstance();
                    dbm.beginTransaction();
                    for (String str : tmp) {
                        //整理完数据：  603999,读者传媒,9.89,     0.82,    9.81,   9.80, 9.93,   9.73, 24152.66, 23792343, 1.05,          2.04,      0.12
                        //              code  ,name    ,trade,    Chg,    preclose,open, high,   low,  volume,  turnover,  turnoverratio, daychange, pe
                        //              代码， 名字，   最新价格，涨跌幅， 昨收，  今开， 最高， 最低，成交量，  成交额，   换手率，      振幅，     市盈率
                        str = str.replace("[", "").replace("'", "").replace("],", "").replace(" ", "");
                        String[] strs = str.split(",");
                        if (strs != null && strs.length == 13 && strs[0].length() == 6) {
                            Stock stock = stocks.get(strs[0].trim());
                            stock.setName(strs[1].trim());
                            stock.setTrade(Float.valueOf(strs[2]));
                            stock.setChg(Float.valueOf(strs[3]));
                            stock.setOpen(Float.valueOf(strs[5]));
                            stock.setHigh(Float.valueOf(strs[6]));
                            stock.setLow(Float.valueOf(strs[7]));
                            stock.setTurnoverratio(Float.valueOf(strs[10]));
                            stock.appendChg(Float.valueOf(strs[3]));
                            stocks.put(stock.getCode(),stock);

                            int[] type = MonitorUtil.getInstance().analysis(stock.getChgStr());
                            if (type[0] == 1) {
//                                Log.d("FetchUtil", "数据异动" + stock.getName() + " " + stock.getChg());
                                String after="";
                                for(int i=0;i<type[1];i++){
                                    after=after+"*";
                                }
                                appendMessage(stock.getName()+" "+after);
                                Log.d("FetchUtil",stock.getName()+" "+stock.getChgList().size()+" "+stock.getChgStr());
                            }
                            dbm.update(stock);//修改数据库
                            count++;
//                            Log.d("FetchUtil","更新了第 "+count+" 条数据");
                        }
                    }
//                    appendMessage("结束一轮");
                    dbm.setTransactionSuccessful();
                    dbm.endTransaction();
//                    dbm.close();
                    Log.d("FetchUtil", "更新了 " + count + " 条数据，耗时 " + (System.currentTimeMillis() - t) + " ms");
                    setStatus(count+" 条数据耗时 " + (System.currentTimeMillis() - t) + " ms，等待下次抓取...");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将抓取到的股票数据新增或追加入数据库
     *
     * @param stocks
     * @param action
     */
    private synchronized void appendToDB(ArrayList<Stock> stocks, int action) {
        if (stocks != null && stocks.size() > 0) {
            DBManager dbm = DBManager.getInstance();
            dbm.beginTransaction();//开启事务
            int count = 0;
            for (Stock stock : stocks) {
                if (action == ACTION_ADD) {
                    dbm.add(stock);//新增
                } else if (action == ACTION_APPEND) {
                    dbm.append(stock);//追加
                }
                count++;
            }
            dbm.setTransactionSuccessful();//提交事务
            dbm.endTransaction();//结束事务
            dbm.close();
            if (action == ACTION_ADD) {
                Log.d("股票初始抓取", "共 " + count + " 条");
                appendMessage("数据初始抓取共 " + count + " 条");
            } else if (action == ACTION_APPEND) {
                Log.d("股票更新", "共 " + count + " 条");
//                setStatus("数据更新共 " + count + " 条");
            }
        }
    }


    /**
     * 从数据库读取出数据
     */
    public void initStocks() {
        if (stocks == null) {
            DBManager dbm = DBManager.getInstance();
            dbm.beginTransaction();//开启事务
            this.stocks = new HashMap<String, Stock>();
            int i = 0;
            int len = 100;
            ArrayList<Stock> tmp = new ArrayList<>();
            do {
                tmp = dbm.getStocks(i, len);
                if (tmp.size() > 0) {
                    for (Stock t : tmp) {
                        stocks.put(t.getCode(), t);
                    }
                }
                i = i + len;
            } while (tmp.size() > 0);
            dbm.setTransactionSuccessful();//提交事务
            dbm.endTransaction();//结束事务
            dbm.close();
            Log.d("数据库", "已读取" + stocks.size() + "条股票数据");
//            appendMessage("已读取" + stocks.size() + "条股票数据");
            appendMessage("已初始化数据库");
        }
    }

    //在msgTxt中添加信息
    public void appendMessage(final String str) {
        Message msg=new Message();
        msg.what=R.id.message;
        Bundle b=new Bundle();
        b.putString("message",str);
        msg.setData(b);
        MainActivity.sendMessage(msg);
    }

    //在statusTxt中显示运行状态
    public void setStatus(final String str){
        Message msg=new Message();
        msg.what=R.id.status;
        Bundle b=new Bundle();
        b.putString("status",str);
        msg.setData(b);
        MainActivity.sendMessage(msg);
    }

    public int getFetchStatus() {
        return fetchStatus;
    }

    public void setFetchStatus(int fetchStatus) {
        this.fetchStatus = fetchStatus;
    }

}

