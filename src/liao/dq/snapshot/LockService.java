package liao.dq.snapshot;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;




import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

/**
 * 类名称：LockService 类描述：监听应用程序的后台服务，并对启动的应用程序进行操作 创建时间：2014-01-14
 */
public class LockService extends Service {
	public static List<AppInfo> appInfo=null;
	public static List<String> infoList = null;
	private boolean flag = false;
	private static String TAG = "LockService";
	public static Context context;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		context = this;
		super.onCreate();

		final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

		new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						// 得到当前运行的任务栈，参数就是得到多少个任务栈，1就是只拿一个任务栈
						// 对应的也就是正在运行的任务栈啦 ,注意别忘了在清单文件中添加获取的权限
						List<RunningTaskInfo> runTaskInfos = activityManager
								.getRunningTasks(1);
						// 拿到当前运行的任务栈
						RunningTaskInfo runningTaskInfo = runTaskInfos.get(0);
						// 拿到要运行的Activity的包名
						String runningpackageName = runningTaskInfo.topActivity
								.getPackageName();
						Log.d(TAG, runningpackageName);
						flag = false;
						if (infoList != null && !infoList.isEmpty()) {
							for (String name : infoList) {
								// 如果当前运行的任务栈属于不限制的 则flag=true
								if (name.equals(runningpackageName)) {
									flag = true;
								}
							}
						} else {
							getInfo();
						// infoList为空则不做限制，flag=true
							flag = true;
						}
						// flag=false则返回系统桌面
						if (!flag) {
							Log.d(TAG, "close");
							
							LockService.ScreenCapture(LockService.context);
							sleep(60000);
						}

						sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	public static void ScreenCapture(Context context) throws IOException {
		Log.i(TAG,"ScreenCapture");
		// TODO Auto-generated method stub
		
		DisplayMetrics metrics =new DisplayMetrics();
		WindowManager WM = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		Display display = WM.getDefaultDisplay();
		display.getMetrics(metrics);
		int height = metrics.heightPixels; //屏幕高
		int width = metrics.widthPixels;    //屏幕的宽
		
//			获取显示方式
		int pixelformat = display.getPixelFormat();
		PixelFormat localPixelFormat1 =new PixelFormat();
		PixelFormat.getPixelFormatInfo(pixelformat, localPixelFormat1);
		int deepth = localPixelFormat1.bytesPerPixel;//位深
			    
		File mydir= new File("/dev/graphics/"); 
		
		File fbfile = new File(mydir, "fb0");  
		FileInputStream inStream = new FileInputStream(fbfile);
		
		byte[] piex = new byte[height * width * deepth];
		DataInputStream dStream = new DataInputStream(inStream);
		int[] colors = new int[height * width];
		
		if (dStream.read(piex, 0, height * width * deepth) != -1) {
			for(int m=0;m<piex.length;m++){
				if(m%4 == 0){
					int r = (piex[m] & 0xFF);
					int g = (piex[m+1] & 0xFF);
					int b = (piex[m+2] & 0xFF);
					int a = (piex[m+3] & 0xFF);
					colors[m/4]= (a << 24) + (b <<16) + (g <<8) + r;
				}
			}
			Bitmap bitmap = Bitmap.createBitmap(colors, width, height, Bitmap.Config.ARGB_8888);
			
			Date nowDate = new Date();
			
			FileOutputStream out;
			File savedir= new File("/mnt/sdcard/MScreen/");
			try {
				if(!savedir.exists()){	savedir.mkdir(); }
				String fileName = "/mnt/sdcard/MScreen/test"+nowDate.getTime()+".jpg";
				out = new FileOutputStream(fileName);
				//bitmap.compress(Bitmap.CompressFormat.PNG, 100,out);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100,out);
				
				//long [] pattern = {100,400,100,400};   // 停止 开启 停止 开启   
				//vibrator.vibrate(pattern,-1); 
				//vibrator.cancel();
				//vibrator.cancel();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		}

	/**
	 * 获取不限制的程序包名信息
	 */
	private void getInfo() {
		AppInfoProvider provider=new AppInfoProvider(this);
		appInfo=provider.getAllApps();//所有安装了的程序的信息，包括卸载了但没有清除数据的
		infoList = new ArrayList<String>();
		infoList.add("com.wz.nurse");//测试数据
		infoList.add("com.android.launcher");//测试数据
		infoList.add("com.android.settings");//测试数据
	}
}
