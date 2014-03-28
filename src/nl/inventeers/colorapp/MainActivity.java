package nl.inventeers.colorapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

@SuppressLint("SdCardPath")
public class MainActivity extends Activity {
	
	static final int BROWSE_IMG = 0x1001;
	static final int CAPTURE_IMG = 0x1002;
	
	boolean usedOnce = false;
	
	ImageView img;
	ImageButton browseBtn;
	ImageButton camBtn;
	View colorBlock;
	View colorBlockBorder;
	View colorBlockBg;
	ImageView indicator;
	
	Runnable delayedHide;
	Handler handler = new Handler();
	
	int version = android.os.Build.VERSION.SDK_INT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("version", "Version: "+version);
        // No title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        
        // Color block
        colorBlock = findViewById(R.id.colorBlock);
        colorBlockBorder = findViewById(R.id.colorBlockBorder);
        colorBlockBg = findViewById(R.id.colorBlockBg);
        
        // Set delayed hide function
        delayedHide = new Runnable() {
			@Override
			public void run() {
				colorBlock.setVisibility(View.INVISIBLE);
				colorBlockBorder.setVisibility(View.INVISIBLE);
				colorBlockBg.setVisibility(View.INVISIBLE);
			}
		};
		
		// Indicator
		indicator = (ImageView) findViewById(R.id.indicator);
        
        // Image view
        img = (ImageView) findViewById(R.id.img_view);
        img.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if (!usedOnce) return false;
				
				ImageView img = (ImageView) v;
				
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					int x = (int) event.getX();
					int y = (int) event.getY();
					
					LayoutParams params = new LayoutParams(
					        LayoutParams.WRAP_CONTENT,
					        LayoutParams.WRAP_CONTENT
					);
					
					// Dirty hack :'(
					if (version < 11) {
						params.setMargins(x, y, 0, 0);
					} else {
						params.setMargins(x - 12, y - 12, 0, 0);
					}
					
					indicator.setVisibility(View.VISIBLE);
					indicator.setLayoutParams(params);
					
					Bitmap bmp = ((BitmapDrawable) img.getDrawable()).getBitmap();
					
					int pixel = bmp.getPixel(x, y);
					colorBlock.setVisibility(View.VISIBLE);
					colorBlock.setBackgroundColor(pixel);
					
					colorBlockBorder.setVisibility(View.VISIBLE);
					colorBlockBg.setVisibility(View.VISIBLE);
					
					handler.removeCallbacks(delayedHide);
					handler.postDelayed(delayedHide, 4 * 1000);
					
					String colorName = ColorExt.getColorName(pixel);
					if (colorName != null) {
						Toast.makeText(getApplicationContext(), colorName, Toast.LENGTH_LONG).show();
					}
				}
				
				return false;
			}
		});
        
        // Browse button
        browseBtn = (ImageButton) findViewById(R.id.browse_btn);
        browseBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, BROWSE_IMG);
			}
		});
        
        // Photo button
        camBtn = (ImageButton) findViewById(R.id.cam_btn);
        camBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				
				if (_hasImageCaptureBug()) {
				    File tmpFile = new File("/sdcard/tmp");
				    i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(tmpFile));
				    
				    // Another dirty hack :'(
				    tmpFile.mkdirs();
					
					//File file = new File(Environment.getExternalStorageDirectory(), "colorapp.jpg");
					//Uri outputFileUri = Uri.fromFile(file);
					//Uri outputFileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 
				            //new ContentValues());
					//i.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
				}/* else {
				    i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				}*/
				
				startActivityForResult(i, CAPTURE_IMG);
			}
		});
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
    	Bitmap imgBmp;
    	Bitmap scaled;
    	Uri selectedImg = null;
    	
    	if (resultCode == RESULT_OK) {
    		switch (requestCode) {
			case BROWSE_IMG:
			case CAPTURE_IMG:
				if (_hasImageCaptureBug()) {
					//File fi = new File(Environment.getExternalStorageDirectory(), "colorapp.jpg");
					File fi = new File("/sdcard/tmp");
					try {
						//selectedImg = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(getContentResolver(), fi.getAbsolutePath(), null, null));
						selectedImg = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(getContentResolver(), fi.getAbsolutePath(), null, null));
						if (!fi.delete()) {
							Log.i("logMarker", "Failed to delete " + fi);
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				} else {
					selectedImg = data.getData();
				}
				
				break;
    		}
    		
    		if (selectedImg != null) {
	    		String[] filePathCol = { MediaStore.Images.Media.DATA };
				
				Cursor cursor = getContentResolver().query(selectedImg, filePathCol, null, null, null);
				cursor.moveToFirst();
				
				int columnIdx = cursor.getColumnIndex(filePathCol[0]);
				String imgPath = cursor.getString(columnIdx);
				cursor.close();
				
				int rotate = 0;
				File imgFile = new File(imgPath);
				try {
					ExifInterface exif = new ExifInterface(imgFile.getAbsolutePath());
					int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
					
					switch (orientation) {
					case ExifInterface.ORIENTATION_ROTATE_270:
						rotate = 270;
						break;
						
					case ExifInterface.ORIENTATION_ROTATE_180:
						rotate = 180;
						break;
						
					case ExifInterface.ORIENTATION_ROTATE_90:
						rotate = 90;
						break;
					}
				} catch (IOException e) {}
				
				imgBmp = BitmapFactory.decodeFile(imgPath);
				//Bitmap scaled = Bitmap.createScaledBitmap(imgBmp, img.getWidth(), img.getHeight(), true);
				
				int origWidth = imgBmp.getWidth();
				int origHeight = imgBmp.getHeight();
				
				int newWidth = img.getMeasuredWidth();
				int newHeight = img.getMeasuredHeight();
				
				float scaleWidth = ((float) newWidth) / origWidth;
				float scaleHeight = ((float) newHeight) / origHeight;
				
				Matrix matrix = new Matrix();
				matrix.postScale(scaleWidth, scaleHeight);
				matrix.postRotate(rotate);
				
				try {
					imgBmp = Bitmap.createBitmap(imgBmp, 0, 0, origWidth, origHeight, matrix, true);
					scaled = Bitmap.createScaledBitmap(imgBmp, newWidth, newHeight, true);
					
					//img.setAdjustViewBounds(true);
					img.setScaleType(ScaleType.FIT_XY);
					img.setImageBitmap(scaled);
					
					usedOnce = true;
					
					Toast.makeText(getApplicationContext(), "Afbeelding klaar voor gebruik", Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					Log.e(ACTIVITY_SERVICE, e.getMessage());
				}
    		}
    	}
    }
    
    protected boolean _hasImageCaptureBug() {
    	if (version < 11) return true;
    	
    	return false;
        // list of known devices that have the bug
        /*ArrayList<String> devices = new ArrayList<String>();
        devices.add("android-devphone1/dream_devphone/dream");
        devices.add("generic/sdk/generic");
        devices.add("vodafone/vfpioneer/sapphire");
        devices.add("tmobile/kila/dream");
        devices.add("verizon/voles/sholes");
        devices.add("google_ion/google_ion/sapphire");
        
        return devices.contains(android.os.Build.BRAND + "/" + android.os.Build.PRODUCT + "/"
               + android.os.Build.DEVICE);*/
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_about:
			new AlertDialog.Builder(this)
				.setTitle(R.string.dialog_title_about)
				.setMessage(R.string.dialog_content_about)
				.setCancelable(true)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				})
				.show();
			break;
		}
		
		return true;
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        
        return true;
    }   
}