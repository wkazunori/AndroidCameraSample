package com.example.cameraex;

import java.io.FileOutputStream;
import java.io.IOException;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Toast;

public class CameraView extends SurfaceView {

	private SurfaceHolder holder ;//ホルダー
	private Camera camera ;//カメラ
	
	private Context context;
	
	@SuppressWarnings("deprecation")
	public CameraView(Context context) {
		
		super(context);
		
		this.context = context;
		
		holder = getHolder();
		holder.addCallback(new Callback(){

			@Override
			public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
					int arg3) {
				
				//カメラプレビューの開始
				camera.setDisplayOrientation(90);
				camera.startPreview();
				
			}

			@Override
			public void surfaceCreated(SurfaceHolder arg0) {
				
				//カメラの初期化
//				camera = Camera.open();
//				try {
//					camera.setPreviewDisplay(holder);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
				
				int cameraNum = Camera.getNumberOfCameras();
				
				CameraInfo cameraInfo = new CameraInfo();
				
				for(int i=0; i< cameraNum; i++){
					Camera.getCameraInfo(i, cameraInfo);
					if(cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT){
						camera = Camera.open(i);
						try {
							camera.setPreviewDisplay(holder);
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;
					}
				}
				
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder arg0) {
				
				//カメラのプレビュー停止
				camera.setPreviewCallback(null);
				camera.stopPreview();
				camera.release();
				camera = null;
				
			}
			
		});
		
		//プッシュバッファの設定
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			//カメラのスクリーンショット取得
			camera.takePicture(null, null, new PictureCallback(){

				@Override
				public void onPictureTaken(byte[] arg0, Camera arg1) {
					
					String path = Environment.getExternalStorageDirectory() + "/test.jpg";
					
					try {
						data2file(arg0, path);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					//データベースへの保存（ギャラリーで見るため）
					ContentValues values = new ContentValues();
					ContentResolver contentResolver = context.getContentResolver();
					values.put(Images.Media.MIME_TYPE, "image/jpeg");
					values.put("_data", path);
					contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
					
					//カメラアクティビティの終了処理
					Toast.makeText(context, "ここにアクティビティを終了させる処理を書く", Toast.LENGTH_SHORT)
					.show();// TODO
					
					camera.startPreview();
				}
				
				//バイトデータ→ファイル
				private void data2file(byte[] w, String fileName) throws Exception{
					
					FileOutputStream out = null;
					
					Bitmap tmp_bitmap = BitmapFactory.decodeByteArray (w, 0, w.length);
			        int width = tmp_bitmap.getWidth ();
			        int height = tmp_bitmap.getHeight ();
	
			        Matrix matrix = new Matrix ();
			        matrix.postRotate (-90);
	
			        Bitmap bitmap = Bitmap.createBitmap (tmp_bitmap, 0, 0, width, height, matrix, true);
			        out = new FileOutputStream (fileName);
			        bitmap.compress (CompressFormat.JPEG, 100, out);
			        out.close ();
					
				}
			});
		}
		return true;
	}
}
