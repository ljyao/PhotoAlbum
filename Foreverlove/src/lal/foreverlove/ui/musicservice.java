package lal.foreverlove.ui;

import java.io.IOException;

import com.lal.Foreverlove.R;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class musicservice extends Service {

	private MediaPlayer mediaPlayer;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		if (mediaPlayer == null) {
			mediaPlayer = MediaPlayer.create(this, R.raw.imyours);
			mediaPlayer.setLooping(true);
		}
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		switch (intent.getAction()) {
		case "lal.music.play":
			if (!mediaPlayer.isPlaying()) {
				mediaPlayer.start();
			}
			break;
		case "lal.music.stop":
			if (mediaPlayer != null) {
				mediaPlayer.stop();
				try {
					mediaPlayer.prepare(); // 在调用stop后如果需要再次通过start进行播放,需要之前调用prepare函数
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			break;
		case "lal.music.pause":
			if (mediaPlayer != null && mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
			}
			break;
		}
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
