package lal.foreverlove.ui;

import com.lal.Foreverlove.R;

import android.R.bool;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class showphotolist extends Activity {

	private mthread mt;
	private Intent intent;
	private boolean flag = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * // Òþ²Ø×óÉÏ½ÇÍ¼±ê getActionBar().setDisplayShowHomeEnabled(false); //
		 * ÏÔÊ¾×óÉÏ½Ç·µ»Ø°´Å¥ getActionBar().setDisplayHomeAsUpEnabled(true);
		 */
		// mt = new mthread();
		// mt.start();
		setContentView(R.layout.photolist);
		Listerner lister = new Listerner();
		LinearLayout l1 = (LinearLayout) findViewById(R.id.layout1);
		LinearLayout l2 = (LinearLayout) findViewById(R.id.layout2);
		LinearLayout l3 = (LinearLayout) findViewById(R.id.layout3);
		LinearLayout l4 = (LinearLayout) findViewById(R.id.layout4);
		LinearLayout l5 = (LinearLayout) findViewById(R.id.layout5);
		LinearLayout l6 = (LinearLayout) findViewById(R.id.layout6);
		LinearLayout l7 = (LinearLayout) findViewById(R.id.layout7);
		LinearLayout l8 = (LinearLayout) findViewById(R.id.layout8);
		l1.setOnClickListener(lister);
		l2.setOnClickListener(lister);
		l3.setOnClickListener(lister);
		l4.setOnClickListener(lister);
		l5.setOnClickListener(lister);
		l6.setOnClickListener(lister);
		l7.setOnClickListener(lister);
		l8.setOnClickListener(lister);

	}

	private class mthread extends Thread {

		@Override
		public void run() {
			MediaPlayer mPlayer = new MediaPlayer();
			mPlayer = MediaPlayer.create(showphotolist.this, R.raw.imyours);
			mPlayer.start();
			super.run();
		}

	}

	@Override
	protected void onPause() {
		intent = new Intent("lal.music.pause");
		startService(intent);
		super.onPause();
	}

	@Override
	protected void onResume() {
		intent = new Intent("lal.music.play");
		startService(intent);
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if (intent != null) {
			stopService(intent);
		}
		super.onDestroy();
	}

	private class Listerner implements OnClickListener {

		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(showphotolist.this, Sample11_8_Activity.class);
			Bundle bundle = new Bundle();
			switch (v.getId()) {
			case R.id.layout1:
				bundle.putInt("type", 1);
				bundle.putInt("count", 16);
				intent.putExtras(bundle);
				startActivity(intent);
				break;
			case R.id.layout2:
				bundle.putInt("type", 2);
				bundle.putInt("count", 11);
				intent.putExtras(bundle);
				startActivity(intent);
				break;
			case R.id.layout3:
				bundle.putInt("type", 3);
				bundle.putInt("count", 17);
				intent.putExtras(bundle);
				startActivity(intent);
				break;
			case R.id.layout4:
				bundle.putInt("type", 4);
				bundle.putInt("count", 12);
				intent.putExtras(bundle);
				startActivity(intent);
				break;
			case R.id.layout5:
				bundle.putInt("type", 5);
				bundle.putInt("count", 13);
				intent.putExtras(bundle);
				startActivity(intent);
				break;
			case R.id.layout6:
				bundle.putInt("type", 6);
				bundle.putInt("count", 14);
				intent.putExtras(bundle);
				startActivity(intent);
				break;
			case R.id.layout7:
				bundle.putInt("type", 7);
				bundle.putInt("count", 15);
				intent.putExtras(bundle);
				startActivity(intent);
				break;
			case R.id.layout8:
				bundle.putInt("type", 8);
				bundle.putInt("count", 13);
				intent.putExtras(bundle);
				startActivity(intent);
				break;
			case R.id.layoutmore:
				break;
			default:
				break;
			}

		}
	}
}
