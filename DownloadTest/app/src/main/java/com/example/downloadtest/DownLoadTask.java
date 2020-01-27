 package com.example.downloadtest;
import android.os.AsyncTask;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import android.os.Environment;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class DownLoadTask extends AsyncTask<String,Integer,Integer>
{
	public static final int TYPE_SUCCESS=0;
	public static final int TYPE_FAILED=1;
	public static final int TYPE_PAUSE=2;
	public static final int TYPE_CANCELLED=3;


    private DownloadListener listener;

    public boolean isCancelled;

    private boolean isPaused;

    private int lastProgress;

    public DownLoadTask(DownloadListener listener)
    {
        this.listener = listener;
    }


	@Override
	protected Integer doInBackground(String[] p1)
	{
		InputStream is=null;
        RandomAccessFile savedFile=null;
        File file=null;
        try
        {
            long downloadedLength=0;
            String downloadUrl=p1[0];
            String fileName=downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            String directory=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            file = new File(directory + fileName);

            long contentLength=getContLength(downloadUrl);
            if (file.exists())
            {
                downloadedLength = file.length();
            }
            else
            if (downloadedLength == contentLength)
            {
                return TYPE_SUCCESS;
            }

            OkHttpClient client=new OkHttpClient();
            Request request=new Request.Builder().url(downloadUrl)
                .addHeader("RANGE", "bytes=" + downloadedLength + "-").
                build();
            Response response=client.newCall(request).execute();
            if (response != null)
            {
                is = response.body().byteStream();
                savedFile = new RandomAccessFile(file, "rw");
                savedFile.seek(downloadedLength);
                byte[] b=new byte[1024];
                int toatle=0;
                int len;
                while ((len = is.read(b)) != -1)
                {
                    if (isCancelled())
                    {
                        return TYPE_CANCELLED;
                    }
                    else if (isPaused)
                    {
                        return TYPE_PAUSE;
                    }
                    else
                    {
                        toatle += len;
                        savedFile.write(b, 0, len);
                        int progress=(int)((toatle + downloadedLength) * 100 / contentLength);
                        publishProgress(progress);
                    }
                }
                response.body().close();
                return TYPE_SUCCESS;
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (savedFile != null)
                    savedFile.close();
                if (is != null)
                    is.close();
                if (isCancelled && file != null)
                    file.delete();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
		return TYPE_FAILED;
	}

    @Override
    protected void onProgressUpdate(Integer[] values)
    {
        int progress=values[0];
        if (progress > lastProgress)
        {
            listener.onProgress(progress);
            lastProgress = progress;}
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Integer result)
    {
        switch (result)
        {
            case TYPE_FAILED:
                listener.onFailed();
                break;
            case TYPE_PAUSE:
                listener.onPaused();
                break;
            case TYPE_CANCELLED:
                listener.onCanceled();
                break;
            case TYPE_SUCCESS:
                listener.onSuccess();
                break;

        }
        super.onPostExecute(result);
    }


    public void pausedDownload()
    {
        this.isPaused = true;
    }
    
    public void cancelDownled()
    {
        this.isCancelled = true;
    }


    private long getContLength(String downloadUrl) throws IOException
    {

        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder()
            .url(downloadUrl)
            .build();
        Response response=client.newCall(request).execute();
        if(response != null)
        { 
            response.close();
            long contentLength=response.body().contentLength();
            return contentLength;}
        else
        {
            return 0;    
        }
    }

}
