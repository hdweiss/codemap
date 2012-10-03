package com.hdweiss.codemap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CodeMapView extends SurfaceView implements
		SurfaceHolder.Callback {

	class CodeMapThread extends Thread {
		private SurfaceHolder mSurfaceHolder;
		private Handler mHandler;
		private Context mContext;
		
		private Bitmap mBackground;

		public CodeMapThread(SurfaceHolder surfaceHolder, Context context,
				Handler handler) {
			mSurfaceHolder = surfaceHolder;
			mHandler = handler;
			mContext = context;
		}

		@Override
		public void run() {
			Canvas c = null;
			try {
				c = mSurfaceHolder.lockCanvas(null);
				synchronized (mSurfaceHolder) {
					doDraw(c);
				}
			} finally {
				if (c != null) {
					mSurfaceHolder.unlockCanvasAndPost(c);
				}
			}
		}
		
		private void doDraw(Canvas canvas) {
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setARGB(255, 0, 255, 0);
			
			canvas.drawBitmap(mBackground, 0, 0, null);
			
			canvas.drawLine(10, 10, 50, 50, paint);
		}
		
        public void setSurfaceSize(int width, int height) {
            // synchronized to make sure these all change atomically
            synchronized (mSurfaceHolder) {
				mBackground = Bitmap.createBitmap(width, height,
						Bitmap.Config.ARGB_8888);
            	
//                mCanvasWidth = width;
//                mCanvasHeight = height;
//
//                // don't forget to resize the background image
//                mBackgroundImage = Bitmap.createScaledBitmap(
//                        mBackgroundImage, width, height, true);
            }
        }
	}

	private CodeMapThread thread;

	public CodeMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // create thread only; it's started in surfaceCreated()
        thread = new CodeMapThread(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {
            }
        });

        setFocusable(true);
	}

	public CodeMapThread getThread() {
		return this.thread;
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
        thread.setSurfaceSize(width, height);
	}

	public void surfaceCreated(SurfaceHolder holder) {
        //thread.setRunning(true);
        thread.start();
    }

	public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        //thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
	}

}
