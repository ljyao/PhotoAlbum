package lal.foreverlove.ui;

import com.lal.Foreverlove.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class welcome extends Activity {
	private boolean isshow = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.welcome);
		  
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {

			final EditText et = new EditText(this);
			new AlertDialog.Builder(this).setTitle("����������")
					.setIcon(android.R.drawable.ic_dialog_info).setView(et)
					.setPositiveButton("ȷ��", new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							String input = et.getText().toString();
							if (input.equals("1234")) {
								Intent intent=new Intent();
								intent.setClass(welcome.this, showphotolist.class);
								startActivity(intent);
								finish();
							} else {
								Toast.makeText(getApplicationContext(),
										"�������" , Toast.LENGTH_LONG)
										.show();
							}

						}
					}).setNegativeButton("ȡ��", null).show();
		}
		return super.onTouchEvent(event);
	}

}
