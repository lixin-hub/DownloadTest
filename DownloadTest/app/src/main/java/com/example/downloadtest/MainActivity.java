package com.example.downloadtest;

import android.Manifest;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

public class MainActivity extends AppCompatActivity implements OnClickListener
{


    private Button bt_start,bt_pause,bt_cancel;

    EditText ed_url;

    private DowloadService.DownloadBinder binder;

    private ServiceConnection connection=new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName p1, IBinder p2)
        {
            binder = (DowloadService.DownloadBinder) p2;
        }

        @Override
        public void onServiceDisconnected(ComponentName p1)
        {
            // TODO: Implement this method
        }


    };
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt_start = findViewById(R.id.activity_mainButton_start);
        bt_pause = findViewById(R.id.activity_mainButton_pause);
        bt_cancel = findViewById(R.id.activity_mainButton_cancel);
        ed_url = findViewById(R.id.activity_mainEditText_url);
        bt_start.setOnClickListener(this);
        bt_pause.setOnClickListener(this);
        bt_cancel.setOnClickListener(this);
        Intent intent=new Intent(this, DowloadService.class);
        startService(intent);
        bindService(intent, connection, BIND_AUTO_CREATE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat. requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }



    }


    @Override
    public void onClick(View p1)
    {if (binder == null)
        {return;}
        switch (p1.getId())
        {

            case R.id.activity_mainButton_start:
                if (!ed_url.getText().toString().equals(null))
                    binder.startDownload(ed_url.getText().toString());
                else
                    Toast.makeText(this, "请输入地址", Toast.LENGTH_SHORT).show();
                break;
            case R.id.activity_mainButton_pause:
                binder.pauseDownload();
                break;
            case R.id.activity_mainButton_cancel:
                binder.cancelDownload();
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onDestroy()
    {
        unbindService(connection);
        super.onDestroy();
    }



}

