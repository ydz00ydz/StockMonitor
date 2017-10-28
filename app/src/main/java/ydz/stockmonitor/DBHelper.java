package ydz.stockmonitor;

        import android.content.Context;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.util.Log;
        import android.widget.Toast;


public class DBHelper extends SQLiteOpenHelper {

    //数据库名字
    private static final String DB_NAME = "stocks.db";
    private static final String TABLE_NAME = "stocks";

    //本版号
    private static final int VERSION = 1;

    //创建表
    private static final  String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" (symbol varchar(8) primary key,"+
            "name varchar(12), cpStr text, open float,trade float, high float, low float, nmc int, turnoverratio float)";
//    private static final  String CREATE_TABLE = "CREATE TABLE stocks(_id integer primary key autoincrement,"+
//            "title text, content text, createDate text, updateDate text)";

    //删除表
    private static final String DROP_TABLE = "drop table if exists "+TABLE_NAME;


    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //SQLiteDatabase 用于操作数据库的工具类
//        db=SQLiteDatabase.openOrCreateDatabase(DB_NAME,null);
        db.execSQL(CREATE_TABLE);
        Log.d("提示","创建数据库");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        db.execSQL(CREATE_TABLE);
    }
}