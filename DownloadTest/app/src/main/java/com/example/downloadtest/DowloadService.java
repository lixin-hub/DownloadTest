package com.example.downloadtest;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Binder;

public class DowloadService extends Service
{

    private DownLoadTask downloadTask;

    String downloadUrl;

    private DownloadListener downloadListener=new DownloadListener(){

        @Override
        public void onSuccess()
        {

        }

        @Override
        public void onCanceled()
        {

        }

        @Override
        public void onProgress(int progress)
        {
            // TODO: Implement this method
        }

        @Override
        public void onPaused()
        {
            // TODO: Implement this method
        }

        @Override
        public void onFailed()
        {
            // TODO: Implement this method
        }
    };



    @Override
    public IBinder onBind(Intent p1)
    {

        return null;
    }


    class DownloadBinder extends Binder
    {
        public void startDownload(String url)
        {
            if (downloadTask == null)
            {
                downloadUrl = url;
                downloadTask = new DownLoadTask(downloadListener);
                downloadTask.execute(downloadUrl);
                //


            }
        }


        public void pauseDownload()
        {
            if (downloadTask != null)
            {
                downloadTask.pausedDownload();
            }
        }

        public void cancelDownload()
        {
            if (downloadTask != null)
            {
                downloadTask.cancelDownled();
            }


        }
    }



}
