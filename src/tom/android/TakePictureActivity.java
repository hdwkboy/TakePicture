package tom.android;

import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TakePictureActivity extends Activity {
	final static int CAMERA_RESULT = 0;

	Uri imageFileUri;

	ImageView returnedImageView;
	Button takePictureButton;
	Button saveDataButton;
	TextView titleTextView;
	TextView descriptionTextView;
	EditText titleEditText;
	EditText descriptionEditText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		returnedImageView = (ImageView) findViewById(R.id.ReturnedImageView);
		takePictureButton = (Button) findViewById(R.id.TakePictureButton);
		saveDataButton = (Button) findViewById(R.id.SaveDataButton);
		titleTextView = (TextView) findViewById(R.id.TitleTextView);
		descriptionTextView = (TextView) findViewById(R.id.DescriptionTextView);
		titleEditText = (EditText) findViewById(R.id.TitleEditText);
		descriptionEditText = (EditText) findViewById(R.id.DescriptionEditText);

		takePictureButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				imageFileUri = getContentResolver().insert(
						Media.EXTERNAL_CONTENT_URI, new ContentValues());
				Intent i = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
						imageFileUri);
				startActivityForResult(i, CAMERA_RESULT);
			}
		});

		saveDataButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ContentValues contentValues = new ContentValues(3);
				contentValues.put(Media.DISPLAY_NAME, titleEditText.getText()
						.toString());
				contentValues.put(Media.DESCRIPTION, descriptionEditText
						.getText().toString());
				getContentResolver().update(imageFileUri, contentValues, null,
						null);
				Toast bread = Toast.makeText(TakePictureActivity.this,
						"Record Updated", Toast.LENGTH_SHORT);
				bread.show();
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		if (resultCode == RESULT_OK) {
			int dw = 200; // Make it at most 200 pixels wide
			int dh = 200; // Make it at most 200 pixels tall

			try {
				BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
				bmpFactoryOptions.inJustDecodeBounds = true;
				Bitmap bmp = BitmapFactory
						.decodeStream(
								getContentResolver().openInputStream(
										imageFileUri), null, bmpFactoryOptions);

				int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight
						/ (float) dh);
				int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth
						/ (float) dw);
				if (heightRatio > 1 && widthRatio > 1) {
					if (heightRatio > widthRatio) {
						bmpFactoryOptions.inSampleSize = heightRatio;
					} else {
						bmpFactoryOptions.inSampleSize = widthRatio;
					}
				}
				bmpFactoryOptions.inJustDecodeBounds = false;
				bmp = BitmapFactory
						.decodeStream(
								getContentResolver().openInputStream(
										imageFileUri), null, bmpFactoryOptions);

				returnedImageView.setImageBitmap(bmp);
			} catch (FileNotFoundException e) {
				Log.v("ERROR", e.toString());
			}
		}
	}
}