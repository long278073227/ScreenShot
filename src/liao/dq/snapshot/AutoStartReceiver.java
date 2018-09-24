package liao.dq.snapshot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/**
 * 类名称：AutoStartReceiver
 * 类描述：监听开机启动service
 * 创建时间：2014-01-14
 */
public class AutoStartReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		
		Intent intent = new Intent(arg0,LockService.class);
		arg0.startService(intent);
	}

}
