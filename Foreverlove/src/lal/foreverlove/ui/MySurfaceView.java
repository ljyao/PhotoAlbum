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
	public float screenWidth;// 屏幕宽带
	public float screenHeight;// 屏幕高度
	public float ratio;// 屏幕宽高比
	private final float TOUCH_SCALE_FACTOR = 180.0f / 320;// 角度缩放比例
	private SceneRenderer mRenderer;// 场景渲染器

	float mPreviousX;// 上次按下位置x坐标
	float mPreviousY;// 上次按下位置y坐标
	long previousTime;// 上次按下的时间
	boolean isCheck = false;// 是否点击查看图片
	boolean isMove = false;

	int[] textureIds = new int[PHOTO_COUNT];// n张照片纹理id数组
	float yAngle = 0;// 总场景旋转角度
	int currIndex = 0;// 当前选中的索引
	float yAngleV = 0;// 总场景角度变化速度

	public MySurfaceView(Context context) {
		super(context);
		mRenderer = new SceneRenderer(); // 创建场景渲染器
		setRenderer(mRenderer); // 设置渲染器
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);// 设置渲染模式为主动渲染

		// 启动一个线程根据当前的角速度旋转场景
		threadWork = true;
		new Thread() {
			public void run() {
				while (threadWork) {
					if (Float.isNaN(yAngle) || Float.isNaN(yAngleV)) {
						throw new RuntimeException("yangle " + yAngle
								+ "yAngleV=" + yAngleV);
					}

					// 根据角速度计算新的场景旋转角度
					yAngle += yAngleV;
					if (Float.isNaN(yAngle)) {
						throw new RuntimeException("yangle" + yAngle);
					}
					// 将角度规格化到0～360之间
					yAngle = (yAngle + 360) % 360;
					if (Float.isNaN(yAngle) || Float.isNaN(yAngleV)) {
						throw new RuntimeException("yangle " + yAngle
								+ "yAngleV=" + yAngleV);
					}
					// 若当前手指已经抬起则角速度衰减
					if (!isMove) {
						yAngleV = yAngleV * 0.7f;
					}
					// 若 角速度小于阈值则归0
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
		if (keyCode == 4) {// 若按下的是返回键
			if (isCheck) {// 若在detail场景中则回主场景
				isCheck = false;
			} else {// 若在主场景中则退出程序
				System.exit(0);
			}
		}
		return true;
	}

	// 触摸事件回调方法
	@Override
	public boolean onTouchEvent(MotionEvent e) {

		if (isCheck) {// 若在detail中不处理触控事件
			return true;
		}

		float x = e.getX();// 获取触控点X坐标
		float y = e.getY();// 获取触控点Y坐标
		float dx = x - mPreviousX;// 计算X向触控位移
		float dy = y - mPreviousY;// 计算Y向触控位移
		long currTime = System.currentTimeMillis();// 获取当前时间戳
		long timeSpan = (currTime - previousTime) / 10;// 计算两次触控事件之间的时间差

		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isMove = false;
			break;
		case MotionEvent.ACTION_MOVE:
			if (Math.abs(dx) > 5 || Math.abs(dy) > 5) {// 触控位移大于阈值则进入移动状态
				isMove = true;
			}
			if (isMove) {// 若在移动状态则计算角度变化速度
				if (timeSpan != 0) {
					yAngleV = dx * TOUCH_SCALE_FACTOR / timeSpan;
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			// 若在非移动状态且角度速度为0则看选中的是哪幅照片来显示
			if (!isMove && yAngleV == 0) {
				// 折算出触控点在NEAR面上的位置
				float nearX = (x - screenWidth / 2) * ratio / (screenWidth / 2);
				float nearY = (screenHeight / 2 - y) / (screenHeight / 2);

				// 先判断点下去的是左边还是右边
				if (x > screenWidth / 2) {// 右边
					ArrayList<CandidateDis> al = new ArrayList<CandidateDis>();
					for (int i = 0; i < PHOTO_COUNT; i++) {
						// 计算此幅照片的角度
						float tempAngle = (i * PHOTO_ANGLE_SPAN + yAngle) % 360;
						// 若角度在270到360范围内则在右边的前面
						if (tempAngle > 270 && tempAngle < 360) {
							al.add(new CandidateDis(tempAngle - 270, tempAngle,
									i));
						}
					}
					// 根据与270度的夹角排序，夹角小的排在前面
					Collections.sort(al);
					// 遍历候选列表谁在X范围内谁单独显示
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
						// 计算此幅照片的角度
						float tempAngle = (i * PHOTO_ANGLE_SPAN + yAngle) % 360;
						// 若角度在180到270范围内则在左边的前面
						if (tempAngle > 180 && tempAngle < 270) {
							al.add(new CandidateDis(270 - tempAngle, tempAngle,
									i));
						}
					}
					// 根据与270度的夹角排序，夹角小的排在前面
					Collections.sort(al);
					// 遍历候选列表谁在X范围内谁单独显示
					currIndex = -1;
					for (CandidateDis cd : al) {
						if (cd.isInXRange(nearX, nearY)) {
							currIndex = cd.index;
							break;
						}
					}
				}
				// 若有选中照片，则设置进入detail显示状态
				if (currIndex != -1) {
					isCheck = true;
				}
			}
			isMove = false;
			break;
		}
		mPreviousX = x;// 记录触控笔位置
		mPreviousY = y;// 记录触控笔位置
		previousTime = currTime;// 记录此次时间
		return true;
	}

	private class SceneRenderer implements GLSurfaceView.Renderer {
		Board tp = new Board(); // 用于显示照片的纹理矩形

		public void onDrawFrame(GL10 gl) {
			// 清除颜色缓存于深度缓存
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
			// 设置当前矩阵为模式矩阵
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			// 设置当前矩阵为单位矩阵
			gl.glLoadIdentity();

			if (isCheck) {
				// 显示某一幅照片，detail状态
				gl.glPushMatrix();
				gl.glTranslatef(0, 0f, -52f);
				gl.glTranslatef(-Board.length - Board.width * 0.5f, 0, 0);
				tp.drawSelf(gl, textureIds[currIndex]);
				gl.glPopMatrix();
			} else {
				// 显示照片组，可触控旋转选择
				gl.glPushMatrix();
				gl.glTranslatef(0, 0f, CENTER_Z);
				yAngle = yAngle % 360;
				gl.glRotatef(yAngle, 0, 1, 0);
				for (int i = 0; i < textureIds.length; i++) {// 遍历纹理数组，显示每幅照片
					gl.glPushMatrix();
					gl.glRotatef(i * PHOTO_ANGLE_SPAN, 0, 1, 0);
					tp.drawSelf(gl, textureIds[i]);
					gl.glPopMatrix();
				}
				gl.glPopMatrix();
			}
		}

		public void onSurfaceChanged(GL10 gl, int width, int height) {
			// 设置视窗大小及位置
			gl.glViewport(0, 0, width, height);
			// 设置当前矩阵为投影矩阵
			gl.glMatrixMode(GL10.GL_PROJECTION);
			// 设置当前矩阵为单位矩阵
			gl.glLoadIdentity();
			// 调用此方法计算产生透视投影矩阵
			gl.glFrustumf(-ratio, ratio, -1, 1, NEAR, 100);
			// 设置为关闭背面剪裁
			gl.glDisable(GL10.GL_CULL_FACE);
		}

		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			
			gl.glEnable(GL10.GL_TEXTURE_2D);
			// 关闭抗抖动
			gl.glDisable(GL10.GL_DITHER);
			// 设置特定Hint项目的模式，这里为设置为使用快速模式
			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
			// 设置屏幕背景色黑色RGBA
			gl.glClearColor(0, 0, 0, 0);
			// 启用深度测试
			gl.glEnable(GL10.GL_DEPTH_TEST);
			// 加载n幅纹理图
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

	// 初始化纹理
	public int initTexture(GL10 gl, int drawableId)// textureId
	{
		// 生成纹理ID
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
