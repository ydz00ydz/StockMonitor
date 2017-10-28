package ydz.stockmonitor;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by YDZ on 2017/10/13.
 */

public class Stock {
    //整理完数据：    603999, 读者传媒,9.89,     0.82,    9.81,   9.80, 9.93,   9.73, 24152.66, 23792343, 1.05,          2.04,      0.12
    //                code  , name    ,trade,    Chg,    preclose,open, high,   low,  volume,  turnover,  turnoverratio, daychange, pe
    //                代码，  名字，   最新价格，涨跌幅， 昨收，  今开， 最高， 最低，成交量，  成交额，   换手率，      振幅，     市盈率

    private int CHG_LIMIT=50;//存储的最新涨跌信息数，太多的话读写数据库、异动判断很慢
//    private String symbol;//代码，带板块头
    private String code;//纯数字股票代码
    private String name;//名
    private float trade;//成交价
    private String chgStr="";//涨跌幅,该字段是将所有涨跌幅拼接成一个字符串存入，存入数据库
    private ArrayList<Float> chgList;//涨跌幅列，获取涨跌幅的数列
    private float chg=100f;//涨跌幅,是从json数据中直接解析出的实时涨跌幅，不存入数据库,10表示10%
//    private float buy;//买一价
//    private float sell;//卖一价
//    private float settlement;//昨收盘
    private float open;//开盘价
    private float high;//最高价
    private float low;//最低价
//    private int volume;//成交量(股)
//    private int amount;//成交额(元)
//    private double mktcap;//总市值
//    private float nmc;//流通市值(万)
    private float turnoverratio;//换手率

//    public String getSymbol() {
//        return symbol;
//    }
//
//    public void setSymbol(String symbol) {
//        this.symbol = symbol;
//    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getTrade() {
        return trade;
    }

    public void setTrade(float trade) {
        this.trade = trade;
    }

    //解析chgStr，一般用于从数据库中读取出后，返回最新changepercent
    public float getChg() {
        if(chg==100f && chgStr!=null){
            String[] tmp=chgStr.split(",");
            chg=Float.valueOf(tmp[tmp.length-1]);
        }
        return chg;
    }

    public void setChg(float chg){
        this.chg=chg;
    }

    public String getChgStr() {
        return chgStr;
    }

    public void setChgStr(String chgStr) {
        this.chgStr = chgStr;
    }

    public void appendChg(Float chg){
        this.chgStr=chgStr+chg+",";
        String[] tmp=chgStr.split(",");
        StringBuffer nStr=new StringBuffer();
        if(tmp.length > CHG_LIMIT){//将分时数据，控制在LIMIT内
            for(int i=tmp.length-CHG_LIMIT;i<tmp.length-1;i++){
                nStr.append(tmp[i]+",");
//                nStr.append(",");
            }
            chgStr=nStr.toString();
        }
    }

    /**
     * @return
     */
    //解析chgStr，返回ArrayLi
    public ArrayList<Float> getChgList(){
        if(chgList!=null && chgList.size()>0){
            //无动作
        }else if(chgStr!=null && chgStr.indexOf(",")>-1){
            chgList=new ArrayList<Float>();
            for(String str:chgStr.split(",")){
                chgList.add(Float.valueOf(str));//从数据库中取出的，不进行过滤了
            }
        }
        return chgList;
    }

//    public void appendChgList(Float chg){
//        if(chgList!=null){
//            chgList.add(chg);
//        }else if(chgList==null && chgStr!=null){
//            getChgList();
//            chgList.add(chg);
//        }else if(chgList==null && chgStr==null){
//            chgList=new ArrayList<Float>();
//            chgList.add(chg);
//        }else{
//            Log.e("Stock","appendChgList错误");
//        }
//    }

    public float getHigh() {
        return high;
    }

    public void setHigh(float high) {
        this.high = high;
    }

    public float getLow() {
        return low;
    }

    public void setLow(float low) {
        this.low = low;
    }

    public float getOpen() {
        return open;
    }

    public void setOpen(float open) {
        this.open = open;
    }

//    public float getNmc() {
//        return nmc;
//    }
//
//    public void setNmc(float nmc) {
//        this.nmc = nmc;
//    }

    public float getTurnoverratio() { return turnoverratio; }

    public void setTurnoverratio(float turnoverratio) {
        this.turnoverratio = turnoverratio;
    }

}
