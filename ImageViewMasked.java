package com.cuhksz.learning;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Kinley on 6/21/2017.
 */

public class ImageViewMasked extends AppCompatImageView {

    private Paint maskPaint;
    private Paint imagePaint;
    private int maskDrawableID;
    private int originalDrawableID;
    private Bitmap currentBitmap;

    public ImageViewMasked(Context context) {
        super(context);
        sharedConstructor();
    }

    public ImageViewMasked(Context context, int original, int mask) {
        this(context);
        sharedConstructor();
        maskDrawableID = mask;
        originalDrawableID = original;
    }

    public ImageViewMasked(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        sharedConstructor();
    }

    public void setCurrentBitmap(Bitmap bm) {
        currentBitmap = bm;
        originalDrawableID = -1;
        if (bm == null)
            originalDrawableID = R.drawable.china_flag;
        invalidate();
    }

    public int getOriginalDrawableID() {
        return originalDrawableID;
    }

    public int getMaskDrawableID() {

        return maskDrawableID;
    }

    private void sharedConstructor() {

        currentBitmap = null;

        maskPaint = new Paint();
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        imagePaint = new Paint();
        imagePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));

        maskDrawableID = R.drawable.shape_round;
        originalDrawableID = R.drawable.china_flag;
    }

    public void setMaskDrawableID(int ID) {
        maskDrawableID = ID;
        invalidate();
    }

    public void setOriginalDrawableID(int ID) {
        originalDrawableID = ID;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawImageMask(canvas);

    }

    private void drawImageMask(Canvas canvas) {

//        ImageView imageView = (ImageView) findViewById(getId());

        // Get the masked image and then scale its dimension to that of the layout
        Bitmap maskImage = BitmapFactory.decodeResource(getResources(), maskDrawableID);
        maskImage = Bitmap.createScaledBitmap(maskImage, getWidth(), getHeight(), false);

        Bitmap original;
        if (originalDrawableID == -1)
            original = currentBitmap;
        else
            original = BitmapFactory.decodeResource(getResources(), originalDrawableID);

        // Scale the original image based on masked image (which is scaled to the layout's)
        Bitmap scaledOriginal = Bitmap.createScaledBitmap(original,
                maskImage.getWidth(),maskImage.getHeight(),false);

//        Bitmap original = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

//        canvas.save();
        canvas.drawBitmap(maskImage,0,0,maskPaint);
        canvas.drawBitmap(scaledOriginal,0,0,imagePaint);

        setImageBitmap(maskImage);
        //imageView.setImageBitmap(maskImage);
//        canvas.restore();
    }
}