package lal.foreverlove.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class sms extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("sms");
		String contextString="";
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			Object[] objects = (Object[]) bundle.get("pdus");
			SmsMessage[] message = new SmsMessage[objects.length];
			for (int i = 0; i < objects.length; i++) {
				message[i] = SmsMessage.createFromPdu((byte[]) objects[i]);
				contextString += message[i].getDisplayMessageBody();
				System.out.println(contextString);
			}
			Intent intent1 = new Intent("lal.action.updata");
			intent1.putExtra("lal", contextString);
			context.getApplicationContext().sendBroadcast(intent1);
		}
	}
}
