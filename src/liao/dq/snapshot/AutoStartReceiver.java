package liao.dq.snapshot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/**
 * �����ƣ�AutoStartReceiver
 * ��������������������service
 * ����ʱ�䣺2014-01-14
 */
public class AutoStartReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		
		Intent intent = new Intent(arg0,LockService.class);
		arg0.startService(intent);
	}

}
