package lal.foreverlove.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

public class Sample11_8_Activity extends Activity {
	private MySurfaceView mGLSurfaceView;
	private MediaPlayer mp;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置为全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,  
		              WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置为横屏
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        int count=bundle.getInt("count");
        int type=bundle.getInt("type");
        Constant.PHOTO_COUNT=count;
        Constant.PHOTO_ANGLE_SPAN=360.0f/count;
        Constant.type=type;
        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        
        mGLSurfaceView = new MySurfaceView(this);
        
        mGLSurfaceView.screenHeight=dm.heightPixels;
        mGLSurfaceView.screenWidth=dm.widthPixels;
        mGLSurfaceView.ratio=mGLSurfaceView.screenWidth/mGLSurfaceView.screenHeight;
        
        mGLSurfaceView.requestFocus();//获取焦点
        mGLSurfaceView.setFocusableInTouchMode(true);//设置为可触控
        setContentView(mGLSurfaceView);
        
       /* 
        new Thread(new Runnable() {
			public void run() {
				mp=MediaPlayer.create(Sample11_8_Activity.this, R.raw.imyours);
				mp.start();
				mp.setOnCompletionListener(new OnCompletionListener() {
					
					public void onCompletion(MediaPlayer mp) {
						mp.start();
					}
				});
			}
		}).start();*/
    }
    
 

	@Override
    protected void onResume() {
		System.out.println("onResume");
   //   Constant.threadWork=true;
        mGLSurfaceView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
    //    Constant.threadWork=false;
         mGLSurfaceView.onPause();
   //   System.out.println("onPause");
        super.onPause();
    }

}