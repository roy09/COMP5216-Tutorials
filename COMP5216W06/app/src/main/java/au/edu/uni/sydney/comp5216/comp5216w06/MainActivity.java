package au.edu.uni.sydney.comp5216.comp5216w06;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public final String APP_TAG = "MobileComputingTutorial";
    public String photoFileName = "photo.jpg";
    public String videoFileName = "video.mp4";

    // request codes
    private static final int MY_PERMISSIONS_REQUEST_OPEN_CAMERA = 101;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHOTOS= 102;
    private static final int MY_PERMISSIONS_REQUEST_RECORD_VIDEO = 103;
    private static final int MY_PERMISSIONS_REQUEST_READ_VIDEOS= 104;
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO= 105;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public Uri getFileUri(String fileName, int type) {
        String typestr = "/images/";
        if (type == 1) {
            typestr = "/videos/";
        } else if (type != 0){
            typestr = "/audios/";
        }
        // Get safe storage directory for photos
        File mediaStorageDir = new File(
                Environment.getExternalStorageDirectory().getPath(),
                typestr+fileName
        );
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.getParentFile().exists()
                && !mediaStorageDir.getParentFile().mkdirs()) {
            Log.d(APP_TAG, "failed to create directory");
        }
        // Return the file target for the photo based on filename
        Uri photoURI = null;
        if (Build.VERSION.SDK_INT >= 24) {
            photoURI = FileProvider.getUriForFile(
                    this.getApplicationContext(),
                    "au.edu.uni.sydney.comp5216.comp5216w06.fileProvider",
                    mediaStorageDir);
        } else {
            photoURI = Uri.fromFile(mediaStorageDir);
        }
        return photoURI;
    }

    MarshMallowPermission marshMallowPermission = new MarshMallowPermission(this);

    public void onTakePhotoClick(View v) {
        // Check permissions
        if (!marshMallowPermission.checkPermissionForCamera()
                || !marshMallowPermission.checkPermissionForExternalStorage()) {
            marshMallowPermission.requestPermissionForCamera();
        } else {
            // create Intent to take a picture and return control to the calling application
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // set file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(new Date());
            photoFileName = "IMG_" + timeStamp + ".jpg";
            Uri file_uri = getFileUri(photoFileName, 0);
            System.out.println(file_uri);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, file_uri);
            // Start the image capture intent to take photo
            startActivityForResult(takePictureIntent, MY_PERMISSIONS_REQUEST_OPEN_CAMERA);
        }
    }

    public void onLoadPhotoClick(View view) {
        if (!marshMallowPermission.checkPermissionForReadfiles()) {
            marshMallowPermission.requestPermissionForReadfiles();
        } else {
            // Create intent for picking a photo from the gallery
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // Bring up gallery to select a photo
            startActivityForResult(intent, MY_PERMISSIONS_REQUEST_READ_PHOTOS);
        }
    }

    public void onLoadVideoClick(View view) {
        if (!marshMallowPermission.checkPermissionForReadfiles()) {
            marshMallowPermission.requestPermissionForReadfiles();
        } else {
            // Create intent for picking a video from the gallery
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            // Bring up gallery to select a photo
            startActivityForResult(intent, MY_PERMISSIONS_REQUEST_READ_VIDEOS);
        }
    }

    public void onRecordVideoClick(View v) {
        // Check permissions
        if (!marshMallowPermission.checkPermissionForCamera()
                || !marshMallowPermission.checkPermissionForExternalStorage()) {
            marshMallowPermission.requestPermissionForCamera();
        } else {
            // create Intent to take a picture and return control to the calling application
            Intent takePictureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            // set file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(new Date());
            videoFileName = "VIDEO_"+timeStamp+".mp4";
            Uri file_uri = getFileUri(videoFileName,1);
            System.out.println(file_uri);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, file_uri);
            // Start the image capture intent to capture video
            startActivityForResult(takePictureIntent, MY_PERMISSIONS_REQUEST_RECORD_VIDEO);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        final VideoView mVideoView = (VideoView) findViewById(R.id.videoview);
        ImageView ivPreview = (ImageView) findViewById(R.id.photopreview);

        mVideoView.setVisibility(View.GONE);
        ivPreview.setVisibility(View.GONE);

        if (requestCode == MY_PERMISSIONS_REQUEST_OPEN_CAMERA) {
            if (resultCode == RESULT_OK) {
                Uri takenPhotoUri = getFileUri(photoFileName,0);
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(takenPhotoUri
                        .getPath());
                // Load the taken image into a preview
                ivPreview.setImageBitmap(takenImage);
                ivPreview.setVisibility(View.VISIBLE);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHOTOS) {
            if (resultCode == RESULT_OK) {
                Uri photoUri = data.getData();
                // Do something with the photo based on Uri
                Bitmap selectedImage;
                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(
                            this.getContentResolver(), photoUri);
                    // Load the selected image into a preview

                    ivPreview.setImageBitmap(selectedImage);
                    ivPreview.setVisibility(View.VISIBLE);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        } else if (requestCode == MY_PERMISSIONS_REQUEST_READ_VIDEOS) {
            if (resultCode == RESULT_OK) {
                Uri videoUri = data.getData();

                mVideoView.setVisibility(View.VISIBLE);
                mVideoView.setVideoURI(videoUri);
                mVideoView.requestFocus();
                mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    // Close the progress bar and play the video
                    public void onPrepared(MediaPlayer mp) {
                        mVideoView.start();
                    }
                });

            }

        }else if (requestCode == MY_PERMISSIONS_REQUEST_RECORD_VIDEO) {
            if (resultCode == RESULT_OK) {
                Uri takenVideoUri = getFileUri(videoFileName,1);
                mVideoView.setVisibility(View.VISIBLE);
                mVideoView.setVideoURI(takenVideoUri);

                mVideoView.requestFocus();
                mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    // Close the progress bar and play the video
                    public void onPrepared(MediaPlayer mp) {
                        mVideoView.start();
                    }
                });
            }
        }
    }






}
