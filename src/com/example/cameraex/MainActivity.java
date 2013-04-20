package com.example.cameraex;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.app.Activity;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//フルスクリーンの設定
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(new CameraView(this));
	}

}
