package tw.org.iii.appps.h_28_contentresolver_sqllite;
//contentprovider類似搬運工,傳遞資訊,真正儲存資訊得的是資料庫,文件,xml,網路等
//可以寫成我的資料庫變成app跑到別人的系統上,作用相當大,類似三個app,共用一個資料庫
//這隻目標是讀取手機的資料庫
//URI外界进程通过 URI 找到对应的ContentProvider & 其中的数据，再进行数据操作；
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private ContentResolver cr; //內容解析器
    private  Uri uriSettings = Settings.System.CONTENT_URI; //在Setting裡面的uri
    private Cursor cursor;
    // content://database/table
    // content://ContactsContract 手機上的聯絡人
    // content://CallLog 通話紀錄
    // content://MediaStore 媒體資料
    // content://Settings 設定資料
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //3.要讀取手機聯絡人要開權限跟詢問
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALL_LOG},
                    123);
        }
            cr = getContentResolver(); //取得內容解析器物件實體( ContentResolver)


    }
    //查詢手機上面的所有Setting
    public void test1(View view) {
        //(回傳Cursor) query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder)
     cursor  = cr.query(uriSettings,null,null,null,null);

      int count = cursor.getColumnCount(); //取得這個表裡的表格數量
      while(cursor.moveToNext()){
          for(int i=0; i<count; i++){
              Log.v("brad",cursor.getColumnName(i) +":" + cursor.getString(i));
          }
      }
      cursor.close();
    }

    public void test2(View view) {
    //針對單一欄位去查詢並取得他的值
//     cursor =  cr.query(uriSettings,
//             null,
//             "name = ?" ,//查詢條件
//             new String[]{Settings.System.SCREEN_BRIGHTNESS},//查詢項目要帶的參數
//             null);
//
//     //抓指定欄位方法1
//     cursor.moveToNext();
//     String v = cursor.getString(cursor.getColumnIndexOrThrow("value"));
//     Log.v("brad",v);
//
//        //抓指定欄位方式二用getInt//(1.cr,2.欄位字串)
////        String getString(ContentResolver resolver, String name):抓取值(1.cr,2.欄位字串)
//        try{
//          int v2 = Settings.System.getInt(cr,Settings.System.SCREEN_BRIGHTNESS);
//          Log.v("brad","" +v2);
//        }catch (Exception e){
//            Log.v("brad","test2抓欄位出問題:" + e.toString());
//        }
        //用寫好的方法,輸入要查詢的項目,直接回報
        Log.v("brad","值是:"+getSettingSystem(Settings.System.SCREEN_BRIGHTNESS));;
    }

    //抓設定系統api(自己寫的),由按鈕二推算而來
    private String getSettingSystem(String settingName){
        String ret = null;
        cursor =  cr.query(uriSettings,
                null,
                "name = ?" ,//查詢條件
                new String[]{settingName},//查詢項目要帶的參數
                null);
        try{
            ret = Settings.System.getString(cr,settingName);

        }catch (Exception e){
            Log.v("brad","test2抓欄位出問題:" + e.toString());
        }
        cursor.close();
        return ret;
    }
    //3.查詢聯絡人資訊
    public void test3(View view) {
//        ContactsContract.CommonDataKinds.Phone; //找聯絡人裡面,的電話
//        ContactsContract.CommonDataKinds.Email; //找聯絡人的email
//        ContactsContract.CommonDataKinds.Photo; //找聯絡人的照片
//        ContactsContract.Contacts.CONTENT_URI 全部欄位資料

        // ContactsContract.Contacts._ID => key => phone
//      cursor =  cr.query(
//                ContactsContract.Contacts.CONTENT_URI,//控制要查詢的表
//                null,null,null,null
//        );
//        int colCount = cursor.getColumnCount(); //取得總比數
//          while(cursor.moveToNext()){
//                for(int i=0; i<colCount; i++){//當i小於比數時
//                   Log.v("brad", cursor.getColumnName(i) +">" + cursor.getString(i));
//                }
//                Log.v("brad","================");
//          }
        Log.v("brad","號碼:" + getPhoneNumber("300"));

    }
    //3.輸入電話id給你電話號碼方法
    private  String getPhoneNumber(String id){
        String ret = null;
        cursor =  cr.query(
               ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +"=?",
               new String[]{id} ,null
        );

        int count = cursor.getCount();//取得數

        while(cursor.moveToNext()){
            for (int i=0; i<count; i++){
                int col = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);//取得這個電話欄位
                ret = cursor.getString(col);
            }
        }
        return ret;
    }
    //查詢電話內部資訊
    public void test4(View view) {
        // CallLog.Calls.CACHED_NAME //來電姓名
        // CallLog.Calls.NUMBER //電話號碼
        // CallLog.Calls.TYPE => CallLog.Calls.INCOMING_TYPE(來電), OUTGOING_TYPE(去電), MISSED_TYPE(來電位接)
        // CallLog.Calls.DATE //來電時間
        // CallLog.Calls.DURATION (second)  //來電講多久

      cursor =  cr.query(CallLog.Calls.CONTENT_URI,null,null,null,null);

      while(cursor.moveToNext()){
        String name =  cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
        String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
        String duration = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION));
        Log.v("brad","姓名 :" + name + " ,電話:" + number + ",通話時間:" + duration);
      }
    }
}
