package com.example.wang.materialdesign;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        registerReceiver(receiver, getFilter());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();
        setDatabase();

    }

    public void initView(){
         wifiManager= (WifiManager) getSystemService(Context.WIFI_SERVICE);
        Button button= (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!wifiManager.isWifiEnabled()){
                    wifiManager.setWifiEnabled(true);
                }else wifiManager.setWifiEnabled(false);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.one:

                List<User> list=getDaoSession().getUserDao().queryBuilder().where(UserDao.Properties.Name.eq("mini")).list();
                list.get(0).getId();
                Toast.makeText(MainActivity.this,  list.get(0).getId().toString(), Toast.LENGTH_LONG).show();
                /*Snackbar.make(getCurrentFocus(),"this",Snackbar.LENGTH_LONG).setAction("undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this,"fsdfsfds",Toast.LENGTH_LONG).show();
                    }
                }).show();*/

                break;
            case R.id.two:
                EventBus.getDefault().post(new MyEvent("ssss"));
                break;
            case R.id.there:
                break;

        }


        return true;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEventMainThread(MyEvent event) {
        Toast.makeText(MainActivity.this,event.msg+"ads",Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    /**
     * 设置greenDao
     */
    private void setDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        mHelper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
        UserDao userDao=mDaoSession.getUserDao();
        userDao.insert(new User(null,"mini"));
    }
    public DaoSession getDaoSession() {
        return mDaoSession;
    }
    public SQLiteDatabase getDb() {
        return db;
    }



    private IntentFilter getFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_BATTERY_LOW);
        filter.addAction(Intent.ACTION_BATTERY_OKAY);
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        return filter;
    }


    private BatteryChangedReceiver receiver = new BatteryChangedReceiver();
    class BatteryChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            final String action = intent.getAction();
            if (action.equalsIgnoreCase(Intent.ACTION_BATTERY_CHANGED)) {
                System.out
                        .println("BatteryChangedReceiver BATTERY_CHANGED_ACTION---");
                // 当前电池的电压
                int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE,
                        -1);
                // 电池的健康状态
                int health = intent
                        .getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
                switch (health) {
                    case BatteryManager.BATTERY_HEALTH_COLD:
                        System.out.println("BATTERY_HEALTH_COLD");
                        break;
                    case BatteryManager.BATTERY_HEALTH_DEAD:
                        System.out.println("BATTERY_HEALTH_DEAD ");
                        break;
                    case BatteryManager.BATTERY_HEALTH_GOOD:
                        System.out.println("BATTERY_HEALTH_GOOD");
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                        System.out.println("BATTERY_HEALTH_OVERHEAT");
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                        System.out.println("BATTERY_HEALTH_COLD");
                        break;
                    case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                        System.out.println("BATTERY_HEALTH_UNKNOWN");
                        break;
                    case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                        System.out.println("BATTERY_HEALTH_UNSPECIFIED_FAILURE");
                        break;
                    default:
                        break;
                }
                // 电池当前的电量, 它介于0和 EXTRA_SCALE之间
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                // 电池电量的最大值
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                // 当前手机使用的是哪里的电源
                int pluged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,
                        -1);
                switch (pluged) {
                    case BatteryManager.BATTERY_PLUGGED_AC:
                        // 电源是AC charger.[应该是指充电器]
                        System.out.println("BATTERY_PLUGGED_AC");
                        break;
                    case BatteryManager.BATTERY_PLUGGED_USB:
                        // 电源是USB port
                        System.out.println("BATTERY_PLUGGED_USB ");
                        break;
                    default:
                        break;
                }
                int status = intent
                        .getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                switch (status) {
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        // 正在充电
                        System.out.println("BATTERY_STATUS_CHARGING ");
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        System.out.println("BATTERY_STATUS_DISCHARGING  ");
                        break;
                    case BatteryManager.BATTERY_STATUS_FULL:
                        // 充满
                        System.out.println("BATTERY_STATUS_FULL ");
                        break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        // 没有充电
                        System.out.println("BATTERY_STATUS_NOT_CHARGING ");
                        break;
                    case BatteryManager.BATTERY_STATUS_UNKNOWN:
                        // 未知状态
                        System.out.println("BATTERY_STATUS_UNKNOWN ");
                        break;
                    default:
                        break;
                }
                // 电池使用的技术。比如，对于锂电池是Li-ion
                String technology = intent
                        .getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
                // 当前电池的温度
                int temperature = intent.getIntExtra(
                        BatteryManager.EXTRA_TEMPERATURE, -1);
                System.out.println("voltage = " + voltage + " technology = "
                        + technology + " temperature = " + temperature
                        + " level = " + level + " scale = " + scale);
            } else if (action.equalsIgnoreCase(Intent.ACTION_BATTERY_LOW)) {
                // 表示当前电池电量低
                System.out
                        .println("BatteryChangedReceiver ACTION_BATTERY_LOW---");
            } else if (action.equalsIgnoreCase(Intent.ACTION_BATTERY_OKAY)) {
                // 表示当前电池已经从电量低恢复为正常
                System.out
                        .println("BatteryChangedReceiver ACTION_BATTERY_OKAY ---");
            }else if(action.equals(Intent.ACTION_HEADSET_PLUG)){
                System.out
                        .println("耳机插入");
            }
        }

    }




}
