package madelyntav.c4q.nyc.memeproject;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class EditPhoto extends Activity implements View.OnTouchListener, View.OnDragListener {

    Bitmap b;
    Bitmap bitmap;

    private Button Vanilla;
    private EditText editText, editText2, demoTitle, demoText;


    private ImageView imageView, demoImage;

    Button save;

    private File file;
    private String TAG = "GallerySaving";
    RelativeLayout memeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_photo);

        editText = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/impact.ttf");

        editText.setTypeface(custom_font);
        editText2.setTypeface(custom_font);

        editText.setOnTouchListener(this);
        editText2.setOnTouchListener(this);


        LinearLayout textTop = (LinearLayout) findViewById(R.id.textTop);
        LinearLayout textMid = (LinearLayout) findViewById(R.id.textMid);
        LinearLayout textBot = (LinearLayout) findViewById(R.id.textBottom);

        textBot.setOnDragListener(this);
        textMid.setOnDragListener(this);
        textTop.setOnDragListener(this);

        imageView = (ImageView) findViewById(R.id.mImageView);
        demoImage = (ImageView) findViewById(R.id.demotivationalImage);
        memeLayout = (RelativeLayout) findViewById(R.id.meme);
        imageView = (ImageView) findViewById(R.id.mImageView);
        Vanilla = (Button) findViewById(R.id.vanilla);
        editText = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);
        demoTitle = (EditText) findViewById(R.id.demotivationalTitle);
        demoText = (EditText) findViewById(R.id.demotivationalText);


        //opens pic in this activity
        if (getIntent().hasExtra("byteArray")) {
            b = BitmapFactory.decodeByteArray(getIntent().getByteArrayExtra("byteArray"), 0, getIntent().getByteArrayExtra("byteArray").length);
            imageView.setImageBitmap(b);
            demoImage.setImageBitmap(b);

        } else {

//        } else {

            //retrieve passed uri
            Uri uri = getIntent().getExtras().getParcelable("image");
            //retrieve bitmap uri from intent
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

            } catch (IOException e) {
                e.printStackTrace();
            }
            //create bitmap for use within activity
            try {
                bitmap = Bitmap.createBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri));
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(bitmap);
            demoImage.setImageBitmap(bitmap);

            if (demoImage == null) {
                Log.d("demoImage", "Is null");
            }
        }

//        Button share = (Button) findViewById(R.id.share);
//        share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                b=getBitmapFromView(findViewById(R.id.meme));
//
//                String pathOfBmp = MediaStore.Images.Media.insertImage(getContentResolver(), b,"title", null);
//                Uri bmpUri= Uri.parse(pathOfBmp);
//
//                Intent attachIntent = new Intent(Intent.ACTION_SEND);
//                attachIntent.putExtra(Intent.EXTRA_STREAM,  bmpUri);
//                attachIntent.setType("image/png");
//                startActivity(attachIntent);
//            }
//        });
//=======
//                } catch (IOException e) {
//                   e.printStackTrace();
//                }
//            //create bitmap for use within activity
//            try {
//                bitmap = Bitmap.createBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri));
//                } catch (IOException e) {
//                   e.printStackTrace();
//                }
//            imageView.setImageBitmap(bitmap);
//        }

        Button share = (Button) findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                b = getBitmapFromView(findViewById(R.id.meme));
                b = getBitmapFromView(memeLayout);

                String pathOfBmp = MediaStore.Images.Media.insertImage(getContentResolver(), b, "title", null);
                Uri bmpUri = Uri.parse(pathOfBmp);

                Intent attachIntent = new Intent(Intent.ACTION_SEND);
                attachIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                attachIntent.setType("image/png");
                startActivity(attachIntent);
            }
        });
    }


    public void vanillaM(View v) {
//        memeLayout.setBackgroundColor(Color.WHITE);
//        editText.setBackgroundColor(Color.WHITE);
//        editText2.setBackgroundColor(Color.WHITE);
        editText.setHint("write something here");
        editText2.setHint("and here");
        editText.setVisibility(View.VISIBLE);
        editText2.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);
        demoImage.setVisibility(View.INVISIBLE);
        demoTitle.setVisibility(View.INVISIBLE);
        demoText.setVisibility(View.INVISIBLE);
    }

    public void demotivate(View v) {
        memeLayout.setBackgroundColor(Color.BLACK);
        imageView.setVisibility(View.INVISIBLE);
        editText.setVisibility(View.INVISIBLE);
        editText2.setVisibility(View.INVISIBLE);
        demoImage.setVisibility(View.VISIBLE);
        demoTitle.setVisibility(View.VISIBLE);
        demoText.setVisibility(View.VISIBLE);

    }

    //onClick method for the save button. Calls other methods to create the save image function
    public void storeImage(View v) {
        Bitmap image = getBitmapFromView(memeLayout);
        File pictureFile = createImageFile();
        addImageToFile(image, pictureFile);

    }

    /**
     * Create a File for saving an image or video
     * Handles file name, and where to store it
     */
    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("MMddyyyy_HHmmss").format(new Date());
        String imageName = "MEME_" + timeStamp + ".jpg";

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(path, imageName);
        Log.d("Path: ", path.getPath());
        return imageFile;

    }

    private void addImageToFile(Bitmap image, File file) {
        FileOutputStream fos = null;
        //Tries to add the bitmap image to the file
        try {
            fos = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);

            Toast.makeText(this, "Saved image to camera pictures", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.d(TAG, "trying to compress image did not work" + e.getMessage());
        } finally {
            try {
                if (fos != null) {
                    fos.close(); //Closes the fileoutput stream
                    //Scans the image file that was just created so user can immediately see it in Pictures
                    try {
                        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri uri = Uri.fromFile(file);
                        mediaScanIntent.setData(uri);
                        sendBroadcast(mediaScanIntent);
                    } catch (Exception e) {
                        Log.d("scanIntent", "Failed: ");
                    }
                }
            } catch (IOException e) {
                Log.d(TAG, "fos did not close " + e.getMessage());
            }
        }

    }


    //Takes the current view and creates a bitmap representing that view.
    public Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);

        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }

    // onTouch and onDrag work together to allow for views to be moved around within the layout
    //to children of that layout
    public boolean onTouch(View v, MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDrag(null, shadowBuilder, v, 0);
            v.isInEditMode();
            v.setVisibility(View.INVISIBLE);

            return true;
        } else {
            return false;
        }
    }


    public boolean onDrag(View v, DragEvent e) {
        if (e.getAction() == DragEvent.ACTION_DROP) {
            View view = (View) e.getLocalState();
            ViewGroup from = (ViewGroup) view.getParent();
            from.removeView(view);
            LinearLayout to = (LinearLayout) v;
            to.addView(view);
            view.setVisibility(View.VISIBLE);
            view.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);

        }
        return true;
    }
}





