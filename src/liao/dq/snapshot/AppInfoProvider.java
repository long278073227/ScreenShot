package liao.dq.snapshot;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
/**
 * �����ƣ�AppInfoProvider 
 * ����������ȡӦ�ó���������Ϣ
 * ����ʱ�䣺2014-01-14
 */
public class AppInfoProvider {
	
	private PackageManager packageManager;
	//��ȡһ����������
	public AppInfoProvider(Context context){
		packageManager = context.getPackageManager();
		
	}
	
	public List<AppInfo> getAllApps(){
		
		List<AppInfo> list = new ArrayList<AppInfo>();
		AppInfo myAppInfo;
		 //��ȡ�����а�װ�˵�Ӧ�ó������Ϣ��������Щж���˵ģ���û��������ݵ�Ӧ�ó��� 
		List<PackageInfo> packageInfos = packageManager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		for(PackageInfo info:packageInfos){
			myAppInfo = new AppInfo();
			//�õ�����
			String packageName = info.packageName;
			 //�õ�Ӧ�ó������Ϣ 
			ApplicationInfo appInfo = info.applicationInfo;
			//�õ�Ӧ�ó����ͼ��
			Drawable icon = appInfo.loadIcon(packageManager);
			//�õ�Ӧ�ó���Ĵ�С
			//long codesize = packageStats.codeSize;
			//Log.i("info", "-->"+codesize);
			//�õ�Ӧ�ó���ĳ�����
			String appName = appInfo.loadLabel(packageManager).toString();
			Log.e(packageName, appName);
			
			myAppInfo.setPackageName(packageName);
			myAppInfo.setAppName(appName);
			myAppInfo.setIcon(icon);
			
			if(filterApp(appInfo)){
				myAppInfo.setSystemApp(false);
			}else{
				myAppInfo.setSystemApp(true);
			}
			list.add(myAppInfo);
		}
		return list;
		
	}
	
	//�ж�ĳһ��Ӧ�ó����ǲ���ϵͳ��Ӧ�ó�������Ƿ���true�����򷵻�false 
	public boolean filterApp(ApplicationInfo info){
		//��ЩϵͳӦ���ǿ��Ը��µģ�����û��Լ�������һ��ϵͳ��Ӧ����������ԭ���ģ�������ϵͳӦ�ã���������ж����������
		if((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0){
			return true;
		}else if((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0){//�ж��ǲ���ϵͳӦ��
			return true;
		}
		return false;
	}
}
