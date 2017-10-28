package ydz.stockmonitor;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import java.util.ArrayList;


public class DBManager {

    //数据库名字
    private static final String DB_NAME = "stocks.db";
    private static final String TABLE_NAME = "stocks";
    private static DBManager dbm;
    private static SQLiteDatabase db;

    private static DBHelper dbHelper;
    public Context context;

    private DBManager(Context context) {
//        this.context=context;
//        dbHelper = new DBHelper(context);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
    }

    public static DBManager getInstance(){
        if(dbm==null){
            dbm=new DBManager(MyApplication.getContext());
            dbHelper = new DBHelper(MyApplication.getContext());
            db = dbHelper.getWritableDatabase();
//            SQLiteDatabase db = dbHelper.getWritableDatabase();
        }
        return dbm;
    }

    /**
     * 添加股票数据，插入前判断，不存在才插入
     *
     * @param stock
     */
    public void add(Stock stock) {
        if(!db.isOpen()){
            db=dbHelper.getWritableDatabase();
        }
        String sql = "insert into "+TABLE_NAME+"(code, name, chgStr, open,trade,high,low,turnoverratio) select ?,?,?,?,?,?,?,? " +
                " where not exists(select * from "+TABLE_NAME+" where code = ? )";
        Object[] args = {stock.getCode(), stock.getName(), stock.getChg()+",",stock.getOpen(), stock.getTrade(),
                stock.getHigh(),stock.getLow(),stock.getTurnoverratio(),stock.getCode()};
//        Log.d("DB","insert a data");
        db.execSQL(sql, args);
//        Log.d("数据库","插入数据成功："+stock.getCode());
//        db.close();
    }

    /**
     * 删除数据
     *
     * @param code
     */
    public void delete(String code) {
        if(!db.isOpen()){
            db=dbHelper.getWritableDatabase();
        }
        String sql = "delete from "+TABLE_NAME+" where code = ?";
        Object[] args = {code};
        db.execSQL(sql, args);
//        Log.d("数据库","删除数据成功："+code);
//        db.close();
    }

    /**
     * 修改数据，不存在时则新增数据，新增数据也可以用这个
     *
     * @param stock
     */
    public void replaceInto(Stock stock) {
        if(!db.isOpen()){
            db=dbHelper.getWritableDatabase();
        }
        //replace into stocks (code, name, chgStr, open,trade,high,low,turnoverratio) values ("sz002901","大博医疗","9.85",9.00,8.50,10.0,3.5,23.2)
        String sql = "replace into "+TABLE_NAME+"(code, name, chgStr, open,trade,high,low,turnoverratio) values( ?,?,?,?,?,?,?,? )";
        Object[] args = {stock.getCode(), stock.getName(), stock.getChgStr()+",",stock.getOpen(), stock.getTrade(),
                stock.getHigh(),stock.getLow(),stock.getTurnoverratio()};
        db.execSQL(sql, args);
//        Log.d("数据库","修改数据成功："+stock.getCode());
//        db.close();
    }

    /**
     * 更新所有数据，包括涨跌信息，目前采用
     *
     * @param stock
     */
    public synchronized void update(Stock stock) {
        if(!db.isOpen()){
            db=dbHelper.getWritableDatabase();
        }
        String sql = "update "+TABLE_NAME+" set name=?,chgStr=?,open=?,trade=?,high=?,low=?,turnoverratio=? where code=?";
        Object[] args = {stock.getName(), stock.getChgStr(),stock.getOpen(), stock.getTrade(),stock.getHigh(),stock.getLow(),stock.getTurnoverratio(),stock.getCode()};
        db.execSQL(sql, args);
//        Log.d("数据库","追加数据成功："+stock.getCode());
//        db.close();
    }

    /**
     * 追加涨跌信息，其他数据为更新
     *
     * @param stock
     */
    public void append(Stock stock) {
        if(!db.isOpen()){
            db=dbHelper.getWritableDatabase();
        }
        String sql = "update "+TABLE_NAME+" set name=?,chgStr=chgStr||?,open=?,trade=?,high=?,low=?,turnoverratio=? where code=?";
        Object[] args = {stock.getName(), stock.getChg()+",",stock.getOpen(), stock.getTrade(),stock.getHigh(),stock.getLow(),stock.getTurnoverratio(),stock.getCode()};
        db.execSQL(sql, args);
//        Log.d("数据库","追加数据成功："+stock.getCode());
//        db.close();
    }

    /**
     * 按code查询
     * @param code
     * @return
     */
    public Stock getStockByCode(String code) {
        if(!db.isOpen()){
            db=dbHelper.getWritableDatabase();
        }
        String sql = "select * from "+TABLE_NAME+" where code = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{code});
        Stock stock = null;
        if (cursor.moveToNext()) {
            stock = new Stock();
            stock.setCode(cursor.getString(cursor.getColumnIndexOrThrow("code")));
            stock.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            stock.setChgStr(cursor.getString(cursor.getColumnIndexOrThrow("chgStr")));
            stock.setOpen(cursor.getFloat(cursor.getColumnIndexOrThrow("open")));
            stock.setTrade(cursor.getFloat(cursor.getColumnIndexOrThrow("trade")));
            stock.setHigh(cursor.getFloat(cursor.getColumnIndexOrThrow("high")));
            stock.setLow(cursor.getFloat(cursor.getColumnIndexOrThrow("low")));
            stock.setTurnoverratio(cursor.getFloat(cursor.getColumnIndexOrThrow("turnoverratio")));
        }
        cursor.close();
//        db.close();
        return stock;
    }

    /**
     * 查询所有
     * @return
     */
    public ArrayList<Stock> getAllStocks() {
        if(!db.isOpen()){
            db=dbHelper.getWritableDatabase();
        }
        String sql = "select * from "+TABLE_NAME;
        Cursor cursor = db.rawQuery(sql,null);
        ArrayList<Stock> stocks = new ArrayList<>();
        Stock stock = null;
        while (cursor.moveToNext()) {
            stock = new Stock();
            stock.setCode(cursor.getString(cursor.getColumnIndexOrThrow("code")));
            stock.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            stock.setChgStr(cursor.getString(cursor.getColumnIndexOrThrow("chgStr")));
            stock.setOpen(cursor.getFloat(cursor.getColumnIndexOrThrow("open")));
            stock.setTrade(cursor.getFloat(cursor.getColumnIndexOrThrow("trade")));
            stock.setHigh(cursor.getFloat(cursor.getColumnIndexOrThrow("high")));
            stock.setLow(cursor.getFloat(cursor.getColumnIndexOrThrow("low")));
            stock.setTurnoverratio(cursor.getFloat(cursor.getColumnIndexOrThrow("turnoverratio")));
            stocks.add(stock);
        }
        cursor.close();
//        db.close();
        return stocks;
    }

    /**
     * 获取部分股票数据，一次性获取太多会影响内存，按code升序排列
     * @param start    数据起点
     * @param len      数据条数
     * @return
     */
    public ArrayList<Stock> getStocks(int start,int len) {
        if(!db.isOpen()){
            db=dbHelper.getWritableDatabase();
        }
        String sql = "select * from "+TABLE_NAME+" ORDER BY code limit ?,?";
        Cursor cursor = db.rawQuery(sql,new String[]{String.valueOf(start),String.valueOf(len)});
        ArrayList<Stock> stocks = new ArrayList<>();
        Stock stock = null;
        while (cursor.moveToNext()) {
            stock = new Stock();
            stock.setCode(cursor.getString(cursor.getColumnIndexOrThrow("code")));
            stock.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            stock.setChgStr(cursor.getString(cursor.getColumnIndexOrThrow("chgStr")));
            stock.setOpen(cursor.getFloat(cursor.getColumnIndexOrThrow("open")));
            stock.setTrade(cursor.getFloat(cursor.getColumnIndexOrThrow("trade")));
            stock.setHigh(cursor.getFloat(cursor.getColumnIndexOrThrow("high")));
            stock.setLow(cursor.getFloat(cursor.getColumnIndexOrThrow("low")));
            stock.setTurnoverratio(cursor.getFloat(cursor.getColumnIndexOrThrow("turnoverratio")));
            stocks.add(stock);
        }
        cursor.close();
//        db.close();
        return stocks;
    }

    //开启事务，然后进行批量读写操作速度会提升很多！
    public void beginTransaction(){
        if(!db.isOpen()){
            db=dbHelper.getWritableDatabase();
        }
        db.beginTransaction();
    }

    //设置事务成功，批量读写操作后进行设置
    public void setTransactionSuccessful(){
        if(!db.isOpen()){
            db=dbHelper.getWritableDatabase();
        }
        db.setTransactionSuccessful();
    }

    //结束事务。
    public void endTransaction(){
        if(!db.isOpen()){
            db=dbHelper.getWritableDatabase();
        }
        db.endTransaction();
    }

    public void close(){
        db.close();
    }

}