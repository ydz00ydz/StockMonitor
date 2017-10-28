package ydz.stockmonitor;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by YDZ on 2017/10/22.
 */

public class MonitorUtil {
//    public int type=0;//异动代号，0：无异动，1：快速上涨，2：突破新高，3：突破均线

    private static MonitorUtil instance=new MonitorUtil();

    private MonitorUtil(){
    }

    public static MonitorUtil getInstance(){
        return instance;
    }


    /**
     * 对传入的股票分时数据进行分析，满足异动特征的返回type
     *
     * @param chgStr
     * @return
     */
    public int[] analysis(String chgStr){
        float warn=PreferencesUtil.getInstance().getWarnChg()/10000f*PreferencesUtil.getInstance().getFetchDealy();//转化为每10s涨跌幅
        int[] type={0,0};
        String[] ts=chgStr.split(",");
        ArrayList<Float> fs=new ArrayList<>();
        for(int i=0;i<ts.length;i++){
            if(ts[i].length()>1){
                fs.add(Float.valueOf(ts[i]));
            }
//            Log.d("MonitorUtil","ts "+i+" "+ts[i]);
        }
        if(fs.get(fs.size()-1)-fs.get(fs.size()-2)>warn){
            type[0]=1;
            type[1]=(int)(fs.get(fs.size()-1)-fs.get(fs.size()-2));
        }
        return type;
    }
}
