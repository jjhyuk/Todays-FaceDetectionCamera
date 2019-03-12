package com.example.jang.facedetection;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.jang.facedetection.camera.CameraSourcePreview;
import com.example.jang.facedetection.camera.GraphicOverlay;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FaceFilterActivity extends AppCompatActivity {
    private static final String TAG = "FaceTracker";

    private CameraSource mCameraSource = null;

    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;

    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    Button rotate;
    Button btn1;
    Button btn2;
    Button btn3;
    Button btn4;
    Button btn5;
    int test = R.drawable.orginal;
    Boolean fb = false;

     GraphicOverlay mOverlay;
     FaceGraphic mFaceGraphic;

    Button btn ;
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_face_filter);

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);

        rotate = findViewById(R.id.rotate);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fb == false)
                {
                    fb = true;
                    mCameraSource.stop();
                    mCameraSource.release();
                    onResume();
                }
                else
                {
                    fb = false;
                    mCameraSource.stop();
                    mCameraSource.release();
                    onResume();
                }
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                test= R.drawable.ryan;
                mCameraSource.stop();
                mCameraSource.release();
                onResume();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                test= R.drawable.apeach;
                mCameraSource.stop();
                mCameraSource.release();
                onResume();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraSource.takePicture(shutterCallback,pictureCallback);
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                test= R.drawable.orginal;
                mCameraSource.stop();
                mCameraSource.release();
                onResume();
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                test= R.drawable.neo;
                mCameraSource.stop();
                mCameraSource.release();
                onResume();
            }
        });


        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.

    }


    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_LONG)
                .setAction(R.string.ok, listener)
                .show();
    }


    private void createCameraSource(Boolean fb) {

        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.ACCURATE_MODE)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        if (!detector.isOperational()) {

            Log.w(TAG, "Face detector dependencies are not yet available.");
        }

        if(fb == false) {
            mCameraSource = new CameraSource.Builder(context, detector)
                    .setRequestedPreviewSize(640, 480)
                    .setFacing(CameraSource.CAMERA_FACING_FRONT)
                    .setRequestedFps(30.0f)
                    .build();
        }
        else
        {
            mCameraSource = new CameraSource.Builder(context, detector)
                    .setRequestedPreviewSize(640, 480)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedFps(30.0f)
                    .build();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(fb);
        } else {
            requestCameraPermission();
        }


        startCameraSource();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource(fb);
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Face Tracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }


    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }


    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay,test);
        }
    }


    private class GraphicFaceTracker extends Tracker<Face> {


        GraphicFaceTracker(GraphicOverlay overlay,int test) {
            mOverlay = overlay;

            mFaceGraphic = new FaceGraphic(overlay,test);
        }

        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
        }


        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);
        }


        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }


        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
        }
    }

    CameraSource.ShutterCallback shutterCallback = new CameraSource.ShutterCallback() {
        @Override
        public void onShutter() {
            Toast.makeText(getApplicationContext(),"찰칵!",Toast.LENGTH_SHORT).show();
        }
    };


    CameraSource.PictureCallback pictureCallback = new CameraSource.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes) {
            Bitmap cameraBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Bitmap cameraScaledBitmap = Bitmap.createScaledBitmap(cameraBitmap, cameraBitmap.getWidth(), cameraBitmap.getHeight(), true);
            int hgt = cameraScaledBitmap.getWidth();
            int wid = cameraScaledBitmap.getHeight();

            Bitmap newImage = Bitmap.createBitmap(wid, hgt, Bitmap.Config.ARGB_8888);
            Bitmap overlay = Bitmap.createScaledBitmap(mFaceGraphic.op,hgt,wid,true);
            Canvas canvas = new Canvas(newImage);
            Matrix sideInversion = new Matrix();
            sideInversion.setScale(-1, 1);
            Bitmap sideInversionImg = Bitmap.createBitmap(RotateBitmap(cameraScaledBitmap,270), 0, 0,RotateBitmap(cameraScaledBitmap,270).getWidth(), RotateBitmap(cameraScaledBitmap,270).getHeight(), sideInversion, false);
            canvas.drawBitmap(sideInversionImg , 0, 0, null);
            canvas.drawBitmap(mFaceGraphic.op,mFaceGraphic.left,mFaceGraphic.top,new Paint());

            File storagePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            storagePath.mkdirs();
            String finalName = Long.toString(System.currentTimeMillis());
            File myImage = new File(storagePath, finalName + ".jpg");

            String photoPath = Environment.getExternalStorageDirectory().getAbsolutePath() +"/" + finalName + ".jpg";

            try {
                FileOutputStream fos = new FileOutputStream(myImage);
                newImage.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                fos.close();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Pic not saved", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(getApplicationContext(), "Pic saved in: " + photoPath, Toast.LENGTH_SHORT).show();


            newImage.recycle();
            newImage = null;
            cameraBitmap.recycle();
            cameraBitmap = null;
        }

    };

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}