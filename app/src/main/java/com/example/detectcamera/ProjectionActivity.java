package com.example.detectcamera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;

public class ProjectionActivity extends Activity {

    private static final int REQUEST_CODE_PROJECTION = 2001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MediaProjectionManager projectionManager =
                (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        if (projectionManager != null) {
            startActivityForResult(
                    projectionManager.createScreenCaptureIntent(),
                    REQUEST_CODE_PROJECTION
            );
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PROJECTION) {
            if (resultCode == RESULT_OK && data != null) {
                Intent serviceIntent = new Intent(this, CameraService.class);
                serviceIntent.setAction("ACTION_START_PROJECTION");
                serviceIntent.putExtra("EXTRA_RESULT_CODE", resultCode);
                serviceIntent.putExtra("EXTRA_DATA", data);
                startService(serviceIntent);
            }
        }
        finish();
    }
}
