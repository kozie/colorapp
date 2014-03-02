package nl.inventeers.colorapp;

import java.io.File;
import java.io.IOException;

import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	static final int BROWSE_IMG = 0x1001;
	static final int CAPTURE_IMG = 0x1002;
	
	ImageView img;
	Button browseBtn;
	Button camBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // No title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        
        // Image view
        img = (ImageView) findViewById(R.id.img_view);
        img.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				ImageView img = (ImageView) v;
				
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					int x = (int) event.getX();
					int y = (int) event.getY();
					
					Bitmap bmp = ((BitmapDrawable) img.getDrawable()).getBitmap();
					
					// Calc scaling
					
					
					return false;
					//int pixel = bmp.getPixel(x, y);
					
					//Toast.makeText(getApplicationContext(), "Color " + pixel, Toast.LENGTH_SHORT).show();
				}
				
				return false;
			}
		});
        
        // Browser button
        browseBtn = (Button) findViewById(R.id.browse_btn);
        browseBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, BROWSE_IMG);
			}
		});
        
        // Photo button
        camBtn = (Button) findViewById(R.id.cam_btn);
        camBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(i, CAPTURE_IMG);
			}
		});
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	Bitmap imgBmp;
    	Uri selectedImg = null;
    	
    	if (resultCode == RESULT_OK) {
    		switch (requestCode) {
			case BROWSE_IMG:
				selectedImg = data.getData();
				
				break;
				
			case CAPTURE_IMG:
				/*Bundle extra = data.getExtras();
				imgBmp = (Bitmap) extra.get("data");
				
				img.setAdjustViewBounds(true);
				img.setScaleType(ScaleType.FIT_XY);
				img.setImageBitmap(imgBmp);*/
				
				selectedImg = data.getData();
				
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
				
				Log.i(ACTIVITY_SERVICE, "w:" + newWidth);
				Log.i(ACTIVITY_SERVICE, "h:" + newHeight);
				
				float scaleWidth = ((float) newWidth) / origWidth;
				float scaleHeight = ((float) newHeight) / origHeight;
				
				Matrix matrix = new Matrix();
				matrix.postScale(scaleWidth, scaleHeight);
				matrix.postRotate(rotate);
				
				try {
					Bitmap scaled = Bitmap.createBitmap(imgBmp, 0, 0, origWidth, origHeight, matrix, true);
					img.setAdjustViewBounds(true);
					img.setScaleType(ScaleType.FIT_XY);
					img.setImageBitmap(scaled);
					
					Toast.makeText(getApplicationContext(), "Afbeelding klaar voor gebruik", Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					Log.e(ACTIVITY_SERVICE, e.getMessage());
				}
    		}
    	}
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