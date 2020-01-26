package com.example.downloadtest;

public interface DownloadListener 
{
	public void onSuccess();
	public void onCanceled();
	public void onProgress(int progress);
	public void onPaused();
	public void onFailed();
	
}
