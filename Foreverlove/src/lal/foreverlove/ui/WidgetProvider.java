package lal.foreverlove.ui;

import com.lal.Foreverlove.R;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {
	RemoteViews rv;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (rv == null) {
			// 创建RemoteViews
			rv = new RemoteViews(context.getPackageName(), R.layout.wmain);
		}
		if (intent.getAction().equals("lal.action.updata")) {
			rv.setTextViewText(R.id.TextView01, intent.getStringExtra("lal"));
			// 真正更新Widget
			AppWidgetManager appWidgetManger = AppWidgetManager
					.getInstance(context);
			int[] appIds = appWidgetManger.getAppWidgetIds(new ComponentName(
					context, WidgetProvider.class));
			appWidgetManger.updateAppWidget(appIds, rv);
		}
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		rv = new RemoteViews(context.getPackageName(), R.layout.wmain);
		Intent intent = new Intent(context, welcome.class);
		// 创建包裹此Intent的PendingIntent
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 3,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		// 设置按下Widget中文本框发送此PendingIntent
		rv.setOnClickPendingIntent(R.id.TextView01, pendingIntent);
		appWidgetManager.updateAppWidget(appWidgetIds, rv);
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);
	}

	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		super.onDisabled(context);
	}

}
