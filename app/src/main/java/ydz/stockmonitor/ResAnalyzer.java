package ydz.stockmonitor;

import java.util.HashMap;

import okhttp3.Response;

/**
 * Created by YDZ on 2017/10/28.
 */

public class ResAnalyzer  {
    private static ResAnalyzer instance= new ResAnalyzer();

    private ResAnalyzer() {
    }

    public static ResAnalyzer getInstance() {
        return instance;
    }

    //进行第一次抓取的分析
    public void analysisRes1(Response res){
        Runnable r=new Runnable(){
            @Override
            public void run() {

            }
        };
        r.run();
    }

    //每次updateAll的内容分析
    public void analysisRes2(){

    }

}
