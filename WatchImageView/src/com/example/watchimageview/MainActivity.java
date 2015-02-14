package com.example.watchimageview;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private EditText et;
	private ImageView iv;
	public static final int UPDATE_UI = 1;
	public static final int ERROR = 2;
	public Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what==UPDATE_UI){
				Bitmap bitmap=(Bitmap)msg.obj;
				iv.setImageBitmap(bitmap);
			}else if(msg.what==ERROR){
				Toast.makeText(MainActivity.this, "图片获取失败！", 0).show();
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		et=(EditText)this.findViewById(R.id.et);
		iv=(ImageView)this.findViewById(R.id.iv);
	}
	public void click(View view){
		final String path=et.getText().toString().trim();//获取输入的路径
		if(TextUtils.isEmpty(path)){//检测是否为空
			Toast.makeText(this, "路径不能为空", 0).show();
		}
		else{
			new Thread(new Runnable() {//在子线程中处理耗时的任务
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						URL url=new URL(path);
						HttpURLConnection con=(HttpURLConnection)url.openConnection();
						// 设置请求方式
						con.setRequestMethod("GET");
						// 设置超时时间
						con.setConnectTimeout(10000);
						int code=con.getResponseCode();//获得响应的请求码
						if(code==200){//200为正常响应码
							InputStream is =con.getInputStream();//得到输入流
							Bitmap bitmap=BitmapFactory.decodeStream(is);//将输入流转换成位图
							Message msg=new Message();
							msg.what=UPDATE_UI;
							msg.obj=bitmap;
							handler.sendMessage(msg);
						}
						else{
							Message msg = new Message();
							msg.what = ERROR;
							handler.sendMessage(msg);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		}
	}



}
