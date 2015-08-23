package lal.foreverlove.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

import android.R.integer;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.lal.Foreverlove.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import static lal.foreverlove.ui.Constant.*;

class MySurfaceView extends GLSurfaceView {
	public float screenWidth;// ��Ļ���
	public float screenHeight;// ��Ļ�߶�
	public float ratio;// ��Ļ��߱�
	private final float TOUCH_SCALE_FACTOR = 180.0f / 320;// �Ƕ����ű���
	private SceneRenderer mRenderer;// ������Ⱦ��

	float mPreviousX;// �ϴΰ���λ��x����
	float mPreviousY;// �ϴΰ���λ��y����
	long previousTime;// �ϴΰ��µ�ʱ��
	boolean isCheck = false;// �Ƿ����鿴ͼƬ
	boolean isMove = false;

	int[] textureIds = new int[PHOTO_COUNT];// n����Ƭ����id����
	float yAngle = 0;// �ܳ�����ת�Ƕ�
	int currIndex = 0;// ��ǰѡ�е�����
	float yAngleV = 0;// �ܳ����Ƕȱ仯�ٶ�

	public MySurfaceView(Context context) {
		super(context);
		mRenderer = new SceneRenderer(); // ����������Ⱦ��
		setRenderer(mRenderer); // ������Ⱦ��
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);// ������ȾģʽΪ������Ⱦ

		// ����һ���̸߳��ݵ�ǰ�Ľ��ٶ���ת����
		threadWork = true;
		new Thread() {
			public void run() {
				while (threadWork) {
					if (Float.isNaN(yAngle) || Float.isNaN(yAngleV)) {
						throw new RuntimeException("yangle " + yAngle
								+ "yAngleV=" + yAngleV);
					}

					// ���ݽ��ٶȼ����µĳ�����ת�Ƕ�
					yAngle += yAngleV;
					if (Float.isNaN(yAngle)) {
						throw new RuntimeException("yangle" + yAngle);
					}
					// ���Ƕȹ�񻯵�0��360֮��
					yAngle = (yAngle + 360) % 360;
					if (Float.isNaN(yAngle) || Float.isNaN(yAngleV)) {
						throw new RuntimeException("yangle " + yAngle
								+ "yAngleV=" + yAngleV);
					}
					// ����ǰ��ָ�Ѿ�̧������ٶ�˥��
					if (!isMove) {
						yAngleV = yAngleV * 0.7f;
					}
					// �� ���ٶ�С����ֵ���0
					if (Math.abs(yAngleV) < 0.05) {
						yAngleV = 0;
					}

					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent e) {
		if (keyCode == 4) {// �����µ��Ƿ��ؼ�
			if (isCheck) {// ����detail���������������
				isCheck = false;
			} else {// ���������������˳�����
				System.exit(0);
			}
		}
		return true;
	}

	// �����¼��ص�����
	@Override
	public boolean onTouchEvent(MotionEvent e) {

		if (isCheck) {// ����detail�в��������¼�
			return true;
		}

		float x = e.getX();// ��ȡ���ص�X����
		float y = e.getY();// ��ȡ���ص�Y����
		float dx = x - mPreviousX;// ����X�򴥿�λ��
		float dy = y - mPreviousY;// ����Y�򴥿�λ��
		long currTime = System.currentTimeMillis();// ��ȡ��ǰʱ���
		long timeSpan = (currTime - previousTime) / 10;// �������δ����¼�֮���ʱ���

		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isMove = false;
			break;
		case MotionEvent.ACTION_MOVE:
			if (Math.abs(dx) > 5 || Math.abs(dy) > 5) {// ����λ�ƴ�����ֵ������ƶ�״̬
				isMove = true;
			}
			if (isMove) {// �����ƶ�״̬�����Ƕȱ仯�ٶ�
				if (timeSpan != 0) {
					yAngleV = dx * TOUCH_SCALE_FACTOR / timeSpan;
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			// ���ڷ��ƶ�״̬�ҽǶ��ٶ�Ϊ0��ѡ�е����ķ���Ƭ����ʾ
			if (!isMove && yAngleV == 0) {
				// ��������ص���NEAR���ϵ�λ��
				float nearX = (x - screenWidth / 2) * ratio / (screenWidth / 2);
				float nearY = (screenHeight / 2 - y) / (screenHeight / 2);

				// ���жϵ���ȥ������߻����ұ�
				if (x > screenWidth / 2) {// �ұ�
					ArrayList<CandidateDis> al = new ArrayList<CandidateDis>();
					for (int i = 0; i < PHOTO_COUNT; i++) {
						// ����˷���Ƭ�ĽǶ�
						float tempAngle = (i * PHOTO_ANGLE_SPAN + yAngle) % 360;
						// ���Ƕ���270��360��Χ�������ұߵ�ǰ��
						if (tempAngle > 270 && tempAngle < 360) {
							al.add(new CandidateDis(tempAngle - 270, tempAngle,
									i));
						}
					}
					// ������270�ȵļн����򣬼н�С������ǰ��
					Collections.sort(al);
					// ������ѡ�б�˭��X��Χ��˭������ʾ
					currIndex = -1;
					for (CandidateDis cd : al) {
						if (cd.isInXRange(nearX, nearY)) {
							currIndex = cd.index;
							break;
						}
					}
				} else {
					ArrayList<CandidateDis> al = new ArrayList<CandidateDis>();
					for (int i = 0; i < PHOTO_COUNT; i++) {
						// ����˷���Ƭ�ĽǶ�
						float tempAngle = (i * PHOTO_ANGLE_SPAN + yAngle) % 360;
						// ���Ƕ���180��270��Χ��������ߵ�ǰ��
						if (tempAngle > 180 && tempAngle < 270) {
							al.add(new CandidateDis(270 - tempAngle, tempAngle,
									i));
						}
					}
					// ������270�ȵļн����򣬼н�С������ǰ��
					Collections.sort(al);
					// ������ѡ�б�˭��X��Χ��˭������ʾ
					currIndex = -1;
					for (CandidateDis cd : al) {
						if (cd.isInXRange(nearX, nearY)) {
							currIndex = cd.index;
							break;
						}
					}
				}
				// ����ѡ����Ƭ�������ý���detail��ʾ״̬
				if (currIndex != -1) {
					isCheck = true;
				}
			}
			isMove = false;
			break;
		}
		mPreviousX = x;// ��¼���ر�λ��
		mPreviousY = y;// ��¼���ر�λ��
		previousTime = currTime;// ��¼�˴�ʱ��
		return true;
	}

	private class SceneRenderer implements GLSurfaceView.Renderer {
		Board tp = new Board(); // ������ʾ��Ƭ���������

		public void onDrawFrame(GL10 gl) {
			// �����ɫ��������Ȼ���
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
			// ���õ�ǰ����Ϊģʽ����
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			// ���õ�ǰ����Ϊ��λ����
			gl.glLoadIdentity();

			if (isCheck) {
				// ��ʾĳһ����Ƭ��detail״̬
				gl.glPushMatrix();
				gl.glTranslatef(0, 0f, -52f);
				gl.glTranslatef(-Board.length - Board.width * 0.5f, 0, 0);
				tp.drawSelf(gl, textureIds[currIndex]);
				gl.glPopMatrix();
			} else {
				// ��ʾ��Ƭ�飬�ɴ�����תѡ��
				gl.glPushMatrix();
				gl.glTranslatef(0, 0f, CENTER_Z);
				yAngle = yAngle % 360;
				gl.glRotatef(yAngle, 0, 1, 0);
				for (int i = 0; i < textureIds.length; i++) {// �����������飬��ʾÿ����Ƭ
					gl.glPushMatrix();
					gl.glRotatef(i * PHOTO_ANGLE_SPAN, 0, 1, 0);
					tp.drawSelf(gl, textureIds[i]);
					gl.glPopMatrix();
				}
				gl.glPopMatrix();
			}
		}

		public void onSurfaceChanged(GL10 gl, int width, int height) {
			// �����Ӵ���С��λ��
			gl.glViewport(0, 0, width, height);
			// ���õ�ǰ����ΪͶӰ����
			gl.glMatrixMode(GL10.GL_PROJECTION);
			// ���õ�ǰ����Ϊ��λ����
			gl.glLoadIdentity();
			// ���ô˷����������͸��ͶӰ����
			gl.glFrustumf(-ratio, ratio, -1, 1, NEAR, 100);
			// ����Ϊ�رձ������
			gl.glDisable(GL10.GL_CULL_FACE);
		}

		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			
			gl.glEnable(GL10.GL_TEXTURE_2D);
			// �رտ�����
			gl.glDisable(GL10.GL_DITHER);
			// �����ض�Hint��Ŀ��ģʽ������Ϊ����Ϊʹ�ÿ���ģʽ
			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
			// ������Ļ����ɫ��ɫRGBA
			gl.glClearColor(0, 0, 0, 0);
			// ������Ȳ���
			gl.glEnable(GL10.GL_DEPTH_TEST);
			// ����n������ͼ
			int a[] = new int[50];
			switch (Constant.type) {
			case 1:
				a[0] = R.drawable.a1;
				a[1] = R.drawable.a2;
				a[2] = R.drawable.a3;
				a[3] = R.drawable.a4;
				a[4] = R.drawable.a5;
				a[5] = R.drawable.a6;
				a[6] = R.drawable.a7;
				a[7] = R.drawable.a8;
				a[8] = R.drawable.a9;
				a[9] = R.drawable.a10;
				a[10] = R.drawable.a11;
				a[11] = R.drawable.a12;
				a[12] = R.drawable.a13;
				a[13] = R.drawable.a14;
				a[14] = R.drawable.a15;
				a[15] = R.drawable.a16;
				break;
			case 2:
				a[0] = R.drawable.b1;
				a[1] = R.drawable.b2;
				a[2] = R.drawable.b3;
				a[3] = R.drawable.b4;
				a[4] = R.drawable.b5;
				a[5] = R.drawable.b6;
				a[6] = R.drawable.b7;
				a[7] = R.drawable.b8;
				a[8] = R.drawable.b9;
				a[9] = R.drawable.b10;
				a[10] = R.drawable.b11;
				break;
			case 3:
				a[0] = R.drawable.c1;
				a[1] = R.drawable.c2;
				a[2] = R.drawable.c3;
				a[3] = R.drawable.c4;
				a[4] = R.drawable.c5;
				a[5] = R.drawable.c6;
				a[6] = R.drawable.c7;
				a[7] = R.drawable.c8;
				a[8] = R.drawable.c9;
				a[9] = R.drawable.c10;
				a[10] = R.drawable.c11;
				a[11] = R.drawable.c12;
				a[12] = R.drawable.c13;
				a[13] = R.drawable.c14;
				a[14] = R.drawable.c15;
				a[15] = R.drawable.c16;
				a[16] = R.drawable.c17;
				break;
			case 4:
				a[0] = R.drawable.d1;
				a[1] = R.drawable.d2;
				a[2] = R.drawable.d3;
				a[3] = R.drawable.d4;
				a[4] = R.drawable.d5;
				a[5] = R.drawable.d6;
				a[6] = R.drawable.d7;
				a[7] = R.drawable.d8;
				a[8] = R.drawable.d9;
				a[9] = R.drawable.d10;
				a[10] = R.drawable.d11;
				a[11] = R.drawable.d12;
				break;
			case 5:
				a[0] = R.drawable.e1;
				a[1] = R.drawable.e2;
				a[2] = R.drawable.e3;
				a[3] = R.drawable.e4;
				a[4] = R.drawable.e5;
				a[5] = R.drawable.e6;
				a[6] = R.drawable.e7;
				a[7] = R.drawable.e8;
				a[8] = R.drawable.e9;
				a[9] = R.drawable.e10;
				a[10] = R.drawable.e11;
				a[11] = R.drawable.e12;
				a[12] = R.drawable.e13;
				break;
			case 6:
				a[0] = R.drawable.f1;
				a[1] = R.drawable.f2;
				a[2] = R.drawable.f3;
				a[3] = R.drawable.f4;
				a[4] = R.drawable.f5;
				a[5] = R.drawable.f6;
				a[6] = R.drawable.f7;
				a[7] = R.drawable.f8;
				a[8] = R.drawable.f9;
				a[9] = R.drawable.f10;
				a[10] = R.drawable.f11;
				a[11] = R.drawable.f12;
				a[12] = R.drawable.f13;
				a[13] = R.drawable.f14;
				break;
			case 7:
				a[0] = R.drawable.h1;
				a[1] = R.drawable.h2;
				a[2] = R.drawable.h3;
				a[3] = R.drawable.h4;
				a[4] = R.drawable.h5;
				a[5] = R.drawable.h6;
				a[6] = R.drawable.h7;
				a[7] = R.drawable.h8;
				a[8] = R.drawable.h9;
				a[9] = R.drawable.h10;
				a[10] = R.drawable.h11;
				a[11] = R.drawable.h12;
				a[12] = R.drawable.h13;
				a[13] = R.drawable.h14;
				a[14] = R.drawable.h15;
				break;
			case 8:
				a[0] = R.drawable.i1;
				a[1] = R.drawable.i2;
				a[2] = R.drawable.i3;
				a[3] = R.drawable.i4;
				a[4] = R.drawable.i5;
				a[5] = R.drawable.i6;
				a[6] = R.drawable.i7;
				a[7] = R.drawable.i8;
				a[8] = R.drawable.i9;
				a[9] = R.drawable.i10;
				a[10] = R.drawable.i11;
				a[11] = R.drawable.i12;
				a[12] = R.drawable.i13;
				break;
			default:
				break;
			}
			for (int i = 0; i < Constant.PHOTO_COUNT; i++) {
				textureIds[i] = initTexture(gl, a[i]);
			}

		}
	}

	// ��ʼ������
	public int initTexture(GL10 gl, int drawableId)// textureId
	{
		// ��������ID
		int[] textures = new int[1];
		gl.glGenTextures(1, textures, 0);
		int currTextureId = textures[0];
		gl.glBindTexture(GL10.GL_TEXTURE_2D, currTextureId);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
				GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
				GL10.GL_CLAMP_TO_EDGE);

		InputStream is = this.getResources().openRawResource(drawableId);
		Bitmap bitmapTmp;
		try {
			bitmapTmp = BitmapFactory.decodeStream(is);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmapTmp, 0);
		bitmapTmp.recycle();

		return currTextureId;
	}
}
