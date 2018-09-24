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
 * �����ƣ�LockService ������������Ӧ�ó���ĺ�̨���񣬲���������Ӧ�ó�����в��� ����ʱ�䣺2014-01-14
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
						// �õ���ǰ���е�����ջ���������ǵõ����ٸ�����ջ��1����ֻ��һ������ջ
						// ��Ӧ��Ҳ�����������е�����ջ�� ,ע����������嵥�ļ�����ӻ�ȡ��Ȩ��
						List<RunningTaskInfo> runTaskInfos = activityManager
								.getRunningTasks(1);
						// �õ���ǰ���е�����ջ
						RunningTaskInfo runningTaskInfo = runTaskInfos.get(0);
						// �õ�Ҫ���е�Activity�İ���
						String runningpackageName = runningTaskInfo.topActivity
								.getPackageName();
						Log.d(TAG, runningpackageName);
						flag = false;
						if (infoList != null && !infoList.isEmpty()) {
							for (String name : infoList) {
								// �����ǰ���е�����ջ���ڲ����Ƶ� ��flag=true
								if (name.equals(runningpackageName)) {
									flag = true;
								}
							}
						} else {
							getInfo();
						// infoListΪ���������ƣ�flag=true
							flag = true;
						}
						// flag=false�򷵻�ϵͳ����
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
		int height = metrics.heightPixels; //��Ļ��
		int width = metrics.widthPixels;    //��Ļ�Ŀ�
		
//			��ȡ��ʾ��ʽ
		int pixelformat = display.getPixelFormat();
		PixelFormat localPixelFormat1 =new PixelFormat();
		PixelFormat.getPixelFormatInfo(pixelformat, localPixelFormat1);
		int deepth = localPixelFormat1.bytesPerPixel;//λ��
			    
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
				
				//long [] pattern = {100,400,100,400};   // ֹͣ ���� ֹͣ ����   
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
	 * ��ȡ�����Ƶĳ��������Ϣ
	 */
	private void getInfo() {
		AppInfoProvider provider=new AppInfoProvider(this);
		appInfo=provider.getAllApps();//���а�װ�˵ĳ������Ϣ������ж���˵�û��������ݵ�
		infoList = new ArrayList<String>();
		infoList.add("com.wz.nurse");//��������
		infoList.add("com.android.launcher");//��������
		infoList.add("com.android.settings");//��������
	}
}
