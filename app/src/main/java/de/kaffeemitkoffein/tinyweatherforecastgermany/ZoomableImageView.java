/**
 * This file is part of TinyWeatherForecastGermany.
 *
 * Copyright (c) 2020, 2021, 2022, 2023, 2024 Pawel Dube
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.view.ScaleGestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import java.util.ArrayList;

/**
 * ZoomableImageView is a simple class that enhances an ImageView with the capability to zoom in and out.
 *
 * @author Pawel Dube
 *
 * <h3>Requirements and limitations:</h3>
 * <ul>
 * <li>the imageview has to be populated with a bitmap. Other content will not work.</li>
 * <li>WIDTH AND HEIGHT OF THE IMAGEVIEW MUST BE SET TO "MATCH_PARENT". When set to "wrap_content", the view will shrink
 *   when the bitmap inside the view shrinks due to zooming!</li>
 * </ul>
 *
 * <h3>Gestures:</h3>
 * this class catches the usual "zoom in" and "zoom out" gestures:
 * <ul>
 *   <li>zoom in  = move two pointers (e.g. two fingers) away from each other while touching the screen</li>
 *   <li>zoom out = move two pointers (e.g. two fingers) closer to each other while touching the screen</li>
 * </ul>
 *  <p>moving one pointer (e.g. one finger) while touching the screen moves the scaled bitmap within the imageview</p>
 *
 * <h3>Notes:</h3>
 * <ul>
 *   <li> this class always "consumes" the provided motionEvent, because the underlying ScaleGestureDetector does so.
 *   Therefore, there are NEVER any "unused" events after this class has been called. However, this is not a limitation,
 *   see example how to use. </li>
 * </ul>
 *
 * <h3>How to use:</h3>
 * <ol>
 *  <li>create an instance of ZoomableImageView providing Context, the imageview and the bitmap,</li>
 * <li>and optionally override "onGestureFinished" to catch the results once a gesture is done. If you do not need to
 *    further react after a zoom/move gesture, you can skip this.</li>
 * <li>Register an OnTouchListerner and handle over the motionEvent to the ZoomableImageView.</li>
 * </ol>
 * <p>That's all.</p>
 * <br>
 *         mapImageView = (ImageView) findViewById(R.id.map);
 *
 *         zoomableImageView = new ZoomableImageView(getApplicationContext(),mapImageView,BitmapFactory.decodeResource(getResources(),R.drawable.germany_nc,null)){
 *             @Override
 *             public void onGestureFinished(float scaleFactor, float xFocus, float yFocus, float xFocusRelative, float yFocusRelative, RectF currentlyVisibleArea){
 *                 Log.v("ZT","-------------------------------------");
 *                 Log.v("ZT","The scale factor is "+scaleFactor);
 *                 Log.v("ZT","Focus: abs: "+yFocus+"/"+xFocus+"  rel: "+xFocusRelative+"/"+yFocusRelative);
 *                 Log.v("ZT","Visible rectangle: "+Math.round(currentlyVisibleArea.left)+"/"+Math.round(currentlyVisibleArea.top)+" "+Math.round(currentlyVisibleArea.right)+"/"+Math.round(currentlyVisibleArea.bottom));
 *             }
 *         };
 *
 *         mapImageView.setOnTouchListener(new View.OnTouchListener() {
 *             @Override
 *             public boolean onTouch(View view, MotionEvent motionEvent) {
 *                 // here, you can do something with the view and the motionEvent, if necessary.
 *                 // then, handle it over to this class:
 *                 return zoomableImageView.onTouchEvent(motionEvent);
 *                 // zoomableImageView always returns true, meaning that the motionEvent is "consumed".
 *             }
 *         });
 * <p>
 * About the motionEvent:
 * zoomableImageView ALWAYS RETURNS TRUE, because the underlying ScaleGestureDetector always "consumes" all
 * motionEvents. If you need to do other things with the motionEvent, do so in the onTouch callback (see above).
 * </p>
 */

public class ZoomableImageView {

    /**
     * The current zoom:<br>
     * 1 = no zoom,<br>
     * values below 1 mean the bitmap is zoomed in (enlarged)<br>
     * values above 1 mean the bitmap is zoomed out (shrunk).<br>
     * Values above 1 are discouraged and may lead to unexpected behaviour of the imageview/bitmap.<br>
     *
     * <p><b>Notice</b>: this value is only accurate once a scale gesture finished. During a scale gesture, it holds the
     * scale factor from the beginning of the gesture. In other words: it is updated at the end of the gesture,
     * and not while a gesture is being performed.</p>
     *
     * <p>Override onGestureFinished to get the results of a gesture.</p>
     */
    public float scaleFactor;

    /**
     * The current focus (=center) visible. Both variables refer to the x/y coordinates of the underlying bitmap.
     */
    public float xFocus;
    public float yFocus;
    /**
     * Default max. zoom factor, 0.25f = 4x zoom.
     */
    public float minScaleRange=0.25f;
    /**
     * Default min. zoom factor, values above 1 are discouraged.
     */
    public float maxScaleRange=1f;
    /**
     * Width of the span at start of enlarge/shrink gesture.
     */
    private float startSpan;
    /**
     * Values holding the last movement position.
     */
    private float xMoveStart = 0;
    private float yMoveStart = 0;
    /**
     * Holds the borders of the visible area of the bimap. May be null before first gestures.
     */
    public RectF temporaryVisibleArea;

    public float lastPressX;
    public float lastPressY;

    private Bitmap bitmap;
    private ImageView imageView;
    public ZoomGestureListener zoomGestureListener;
    public ScaleGestureDetector scaleGestureDetector;
    private Context context;
    private float imageViewWidth;
    private float imageViewHeight;

    private float stretchFactor = 1;
    private boolean fillViewPort = false;

    private ArrayList<Float> spriteX;
    private ArrayList<Float> spriteY;
    private ArrayList<Bitmap> spriteBitmap;
    private ArrayList<Integer> spriteFixPoint;
    private ArrayList<Float> spriteMaxScaleFactor;

    /**
     * Initializes this class. Values must not be null.
     *
     * @param context the application context
     * @param imageView the imageview
     * @param bitmap_src the bitmap to be used in the imageview
     */


    public ZoomableImageView(Context context,ImageView imageView, final Bitmap bitmap_src){
        this.context = context;
        this.imageView = imageView;
        this.imageViewWidth  = bitmap_src.getWidth();
        this.imageViewHeight = bitmap_src.getHeight();
        this.bitmap = bitmap_src.copy(Bitmap.Config.ARGB_8888,true);
        this.fillViewPort = false;
        initDefaults();
    }

    public ZoomableImageView(Context context, final ImageView imageView, final Bitmap bitmap_src, final boolean fillViewPort){
        this.context = context;
        this.imageView = imageView;
        this.fillViewPort = fillViewPort;
        imageView.post(new Runnable() {
            @Override
            public void run() {
                if (fillViewPort){
                    scaleBitmapToFillViewPort(bitmap_src);
                } else {
                    imageViewWidth = bitmap.getWidth();
                    imageViewHeight = bitmap.getHeight();
                    bitmap = bitmap_src.copy(Bitmap.Config.ARGB_8888,true);
                }
                initDefaults();
                redrawBitmap();
            }
        });
    }

    private void scaleBitmapToFillViewPort(final Bitmap bitmap_src){
        imageViewWidth = imageView.getWidth();
        imageViewHeight = imageView.getHeight();
        float xstretchFactor = imageViewHeight/bitmap_src.getHeight();
        float ystretchFactor = imageViewWidth/bitmap_src.getWidth();
        stretchFactor = Math.max(xstretchFactor,ystretchFactor);
        int targetX = Math.round((stretchFactor)*bitmap_src.getWidth());
        int targetY = Math.round((stretchFactor)*bitmap_src.getHeight());
        bitmap = Bitmap.createScaledBitmap(bitmap_src,targetX,targetY,true);
    }

    /**
     * Inits default values.
     */

    private void initDefaults(){
        xFocus = bitmap.getWidth()/2f;
        yFocus = bitmap.getHeight()/2f;
        scaleFactor = 1.0f;
        zoomGestureListener = new ZoomGestureListener();
        scaleGestureDetector = new ScaleGestureDetector(context,zoomGestureListener);
    }

    /**
     * Optional method to set the min and max scale range.<br> Example:
     * setScaleRange(0.25,1); sets a maximum zoom in of 4x and a maximum zoom out to the original size.
     *
     * @param min the max. zoom, must be lower than max (e.g. 0.25 means a max. zoom of 4x).
     * @param max the min. zoom, typically 1 (original size). Values above 1 are discouraged.
     */

    public void setScaleRange(float min, float max){
        this.minScaleRange = min;
        this.maxScaleRange = max;
    }

    /**
     * Updates the bitmap with a new one. If the old and new bitmap have the same width and height, the
     * current zoom values and the focus are preserved.
     *
     * @param newbitmap the new bitmap, must not be null
     */

    public void updateBitmap(Bitmap newbitmap){
        if ((newbitmap.getHeight()!=bitmap.getHeight()) || (newbitmap.getWidth()!=bitmap.getWidth())){
            initDefaults();
        }
        if (fillViewPort){
            scaleBitmapToFillViewPort(newbitmap);
        } else {
            this.bitmap = newbitmap.copy(Bitmap.Config.ARGB_8888,true);
        }
        redrawBitmap();
    }

    /**
     * Static class that holds values how to fix a sprite to the underlying bitmap.
     *
     * For most use cases, this should be SPRITECENTER. This guarantees that the sprite center floats above the
     * position relative to the x/y coordinates of the underlying bitmap, no matter how the underlying bitmap is zoomed
     * in or out.
     *
     * However, it might be necessary to fix the sprite to one of the corners; in this case, the sprite will be fixed to
     * the underlying pixel at one of the corners. This might be appropriate when you e.g. use an arrow that should
     * keep pointing at something when zooming in and out.
     */

    public static class SPRITEFIXPOINT {
        public static final int TOP_LEFT     = 0;
        public static final int TOP_RIGHT    = 1;
        public static final int BOTTOM_LEFT  = 2;
        public static final int BOTTOM_RIGHT = 3;
        public static final int SPRITECENTER = 4;
    }

    /**
     * Sprite concept: a "sprite" is a small bitmap/icon that is fixed to
     * an absolute position on top of the underlying bitmap. When the underlying bitmap gets zoomed in or out,
     * the sprite does NOT zoom but always keeps the same size. It remains at the same (relative) position fixed
     * to the underlying bitmap. Got it?
     *
     * Adds a sprite.
     *
     * @param bitmap the bitmap holding the sprite. May not be null.
     * @param x x-position (on the underlying bitmap) where to fix the sprite
     * @param y y-position (on the underlying bitmap) where to fix the sprite
     * @param fixpoint how to fix the sprite, see SPRITEFIXPOINT class
     *
     * @return number of the sprite
     */

    public int addSpite(Bitmap bitmap, float x, float y, int fixpoint, Float sf){
        if ((spriteX == null) || (spriteY == null) || (spriteBitmap == null)){
            spriteX = new ArrayList<Float>();
            spriteY = new ArrayList<Float>();
            spriteBitmap = new ArrayList<Bitmap>();
            spriteFixPoint = new ArrayList<Integer>();
            spriteMaxScaleFactor = new ArrayList<Float>();

        }
        spriteX.add(x);
        spriteY.add(y);
        spriteBitmap.add(bitmap);
        spriteFixPoint.add(fixpoint);
        if ((sf==null) || (sf>1)){
            spriteMaxScaleFactor.add(0f);
        } else {
            spriteMaxScaleFactor.add(sf);
        }
        return spriteBitmap.size();
    }

    /**
     * Always called when a gesture (zoom or movement) finished.<br>
     *
     * Override this method to do something once a zoom / movement inside the imageview finished.<br>
     *
     * The focus is the visible center coordinate.<br>
     *
     * @param scaleFactor the current scale factor
     * @param lastPressX the absolute x of last pointer/touch, reference is the whole bitmap
     * @param lastPressY the absolute y of last pointer/touch, reference is the whole bitmap
     * @param xFocus the absolute x focus in pixels, reference is the whole bitmap
     * @param yFocus the absolute y focus in pixels, reference is the whole bitmap
     * @param xFocusRelative the relative x focus (0 {@literal <}= xFocusRelative {@literal <}= 1), reference is the whole bitmap
     * @param yFocusRelative the relative y focus (0 {@literal <}= yFocusRelative {@literal <}= 1), reference is the whole bitmap
     * @param currentlyVisibleArea rectangle holding the coordinates of the visible area in pixels
     */

    public void onGestureFinished(float scaleFactor, float lastPressX, float lastPressY, float xFocus, float yFocus, float xFocusRelative, float yFocusRelative, RectF currentlyVisibleArea){
        // things to do after gesture finished.
    }

    /**
     * Returns the current focus as a relative position, returns a value between 0 (=left border of the bitmap) and
     * 1 (=right border of the bitmap). The position refers to the bitmap.<br>
     *
     * The focus is the visible center coordinate.<br>
     *
     * @return the relative x coordinate of the focus, 0 {@literal <}= x {@literal <}= 1
     */

    public float getRelativeXFocus(){
        return xFocus/bitmap.getWidth();
    }

    /**
     * Returns the current focus as a relative position, returns a value between 0 (=left top of the bitmap) and
     * 1 (=bottom of the bitmap). The position refers to the bitmap.<br>
     *
     * The focus is the visible center coordinate.<br>
     *
     * @return the relative y coordinate of the focus, 0 {@literal <}= y {@literal <}= 1
     */

    public float getRelativeYFocus(){
        return yFocus/bitmap.getHeight();
    }

    /**
     * Returns the absolute focus x coordinate in pixels. The coordinate refers to the bitmap size in pixels.<br>
     *
     * The focus is the visible center coordinate.<br>
     *
     * @return absolute x coordinate
     */

    public float getXFocus(){
        return xFocus;
    }

    /**
     * Returns the absolute focus y coordinate in pixels. The coordinate refers to the bitmap size in pixels.<br>
     *
     * The focus is the visible center coordinate.
     *
     * @return absolute y coordinate
     */

    public float getYFocus(){
        return yFocus;
    }

    /**
     * Use this method to supply all motionEvents from the imageview to this class. This is typically called from
     * an OnTouchListener that is attached to the imageview. See example above.
     *
     * @param motionEvent
     * @return if the motionEvent was used, always returns TRUE.
     */

    public boolean onTouchEvent(MotionEvent motionEvent){
        int widthVisible  = Math.round(imageViewWidth * scaleFactor);
        int heightVisible = Math.round(imageViewHeight * scaleFactor);

        lastPressX = motionEvent.getX()/imageView.getWidth()*widthVisible+(xFocus-widthVisible/2);
        lastPressY = motionEvent.getY()/imageView.getHeight()*heightVisible+(yFocus-heightVisible/2);
        scaleGestureDetector.onTouchEvent(motionEvent);
        if (motionEvent.getPointerCount()==1){
            if (motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                xMoveStart = motionEvent.getX();
                yMoveStart = motionEvent.getY();
            }
            if (motionEvent.getAction()==MotionEvent.ACTION_MOVE){
                moveMap(xMoveStart-motionEvent.getX(),yMoveStart-motionEvent.getY());
                xMoveStart = motionEvent.getX();
                yMoveStart = motionEvent.getY();
            }
        }
        if (motionEvent.getAction() == MotionEvent.ACTION_UP){
            onGestureFinished(scaleFactor,lastPressX,lastPressY,getXFocus(),getYFocus(),getRelativeXFocus(),getRelativeYFocus(),temporaryVisibleArea);
        }
        return true;
    }

    /**
     * Redraws the bitmap inside the imageview.
     *
     * @param scaleFactor the scale factor to use.
     */

    private void redrawBitmap(float scaleFactor){
        int widthVisible  = Math.round(imageViewWidth * scaleFactor);
        int heightVisible = Math.round(imageViewHeight * scaleFactor);

        int left = Math.round((xFocus - widthVisible/2f));
        int top = Math.round((yFocus - heightVisible/2f));
        // fail-safe boundaries
        while (left<0){
            left++;
            xFocus++;
        }
        while (top<0){
            top++;
            yFocus++;
        }
        while (left+widthVisible>bitmap.getWidth()){
            left--;
            xFocus--;
        }
        while (top+heightVisible>bitmap.getHeight()){
            top--;
            yFocus--;
        }
        if (left<0){
            left=0;
        }
        if (top<0){
            top=0;
        }
        temporaryVisibleArea = new RectF(left,top,left+widthVisible,top+heightVisible);
        Bitmap bitmap = Bitmap.createBitmap(this.bitmap,left,top,widthVisible,heightVisible);
        Bitmap newbitmap = Bitmap.createScaledBitmap(bitmap,Math.round(imageViewWidth),Math.round(imageViewHeight),false);
        if ((spriteY != null)  && (spriteX != null) && (spriteBitmap !=null)){
            for (int i=0; i<spriteBitmap.size(); i++){
                // check for maxScaleFactor
                if ((spriteMaxScaleFactor.get(i)==null) || (spriteMaxScaleFactor.get(i)==0) || (spriteMaxScaleFactor.get(i)>=scaleFactor)){
                    if ((spriteX.get(i)>left) && (spriteX.get(i)<left+widthVisible+spriteBitmap.get(i).getWidth()) && (spriteY.get(i)>top-spriteBitmap.get(i).getHeight()) && (spriteY.get(i)<top+heightVisible)){
                        Paint paint = new Paint();
                        paint.setStyle(Paint.Style.FILL_AND_STROKE);
                        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
                        Canvas canvas = new Canvas(newbitmap);
                        if (spriteFixPoint.get(i)==SPRITEFIXPOINT.TOP_LEFT){
                            // this keeps alignment top-left correct
                            canvas.drawBitmap(spriteBitmap.get(i),Math.round((spriteX.get(i)-left)*(imageViewWidth/widthVisible)),Math.round(spriteY.get(i)-top)*(imageViewHeight/heightVisible),paint);
                        }
                        if (spriteFixPoint.get(i)==SPRITEFIXPOINT.BOTTOM_LEFT){
                            // this keeps alignment bottom-left
                            canvas.drawBitmap(spriteBitmap.get(i),Math.round((spriteX.get(i)-left)*(imageViewWidth/widthVisible)),Math.round(spriteY.get(i)-top+spriteBitmap.get(i).getHeight())*(imageViewHeight/heightVisible)-spriteBitmap.get(i).getHeight(),paint);
                        }
                        if (spriteFixPoint.get(i)==SPRITEFIXPOINT.BOTTOM_RIGHT){
                            // this keeps alignment bottom-right
                            canvas.drawBitmap(spriteBitmap.get(i),Math.round((spriteX.get(i)-left+spriteBitmap.get(i).getWidth())*(imageViewWidth/widthVisible))-spriteBitmap.get(i).getWidth(),Math.round(spriteY.get(i)-top+spriteBitmap.get(i).getHeight())*(imageViewHeight/heightVisible)-spriteBitmap.get(i).getHeight(),paint);
                        }
                        if (spriteFixPoint.get(i)==SPRITEFIXPOINT.TOP_RIGHT){
                            // this keeps alignment top-right
                            canvas.drawBitmap(spriteBitmap.get(i),Math.round((spriteX.get(i)-left+spriteBitmap.get(i).getWidth())*(imageViewWidth/widthVisible))-spriteBitmap.get(i).getWidth(),Math.round(spriteY.get(i)-top)*(imageViewHeight/heightVisible),paint);
                        }
                        if (spriteFixPoint.get(i)==SPRITEFIXPOINT.SPRITECENTER){
                            // this keeps alignment sprite-center
                            canvas.drawBitmap(spriteBitmap.get(i),Math.round((spriteX.get(i)-left+spriteBitmap.get(i).getWidth()/2f)*(imageViewWidth/widthVisible))-spriteBitmap.get(i).getWidth()/2f,Math.round(spriteY.get(i)-top+spriteBitmap.get(i).getHeight()/2f)*(imageViewHeight/heightVisible)-spriteBitmap.get(i).getHeight()/2f,paint);
                        }
                    }
                }
            }
        }
        imageView.setImageBitmap(newbitmap);
    }

    /**
     * Redraws the bitmap inside the imageview. Uses the currently set scale factor.
     */

    public void redrawBitmap(){
        redrawBitmap(scaleFactor);
    }

    /**
     * Handles the move of the bitmap with one pointer (e.g. one finger).
     *
     * @param xDelta horizontal movement pixels
     * @param yDelta vertical movement in pixels
     */

    private void moveMap(float xDelta, float yDelta){
        int widthVisible  = Math.round(imageViewWidth * scaleFactor);
        int heightVisible = Math.round(imageViewHeight * scaleFactor);
        xFocus = xFocus + (xDelta/ imageView.getWidth())*widthVisible;
        yFocus = yFocus + (yDelta/ imageView.getHeight())*heightVisible;
        float rightBound = bitmap.getWidth()-widthVisible/2f;
        float leftBound  = widthVisible/2f;
        float topBound = heightVisible/2f;
        float bottomBound = bitmap.getHeight() - heightVisible/2f;

        // correct focus to prevent out of areas in bitmap
        if (xFocus<leftBound){
            xFocus = leftBound;
        }
        if (xFocus>rightBound){
            xFocus = rightBound;
        }
        if (yFocus<topBound){
            yFocus = topBound;
        }
        if (yFocus>bottomBound){
            yFocus = bottomBound;
        }
        redrawBitmap();
    }

    /**
     * The underlying SimpleOnScaleGestureListener.
     */

    private class ZoomGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{

        public ZoomGestureListener() {
            xFocus = bitmap.getWidth()/2f;
            yFocus = bitmap.getHeight()/2f;
        }

        /**
         * Handles the scale gesture & the animation.<br>
         *
         * To achieve a better accuracy, the scale factor is calculated at the end of the
         * gesture.
         *
         * @param scaleGestureDetector
         * @return
         */

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            float scf = startSpan/scaleGestureDetector.getCurrentSpan();
            float absScf = scf * scaleFactor;
            if (absScf<minScaleRange){
                absScf = minScaleRange;
            }
            if (absScf>maxScaleRange){
                absScf = maxScaleRange;
            }
            redrawBitmap(absScf);
            return true;
        }

        /**
         * Handles the start of the scale gesture. The startSpan is preserved for later
         * calculation of the scaleFactor once the gesture finished.
         *
         * @param scaleGestureDetector
         * @return
         */

        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            startSpan = scaleGestureDetector.getCurrentSpan();
            return super.onScaleBegin(scaleGestureDetector);
        }

        /**
         * Handles the end of the scale gesture. The scaleFactor is calculated & updated.
         *
         * @param scaleGestureDetector
         */

        @Override
        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
            float scf = startSpan/scaleGestureDetector.getCurrentSpan();
            scaleFactor = scaleFactor * scf;
            if (scaleFactor<minScaleRange){
                scaleFactor = minScaleRange;
            }
            if (scaleFactor>maxScaleRange){
                scaleFactor = maxScaleRange;
            }
            super.onScaleEnd(scaleGestureDetector);
        }
    }

    public final static String STATE_SCALEFACTOR="STATE_SCALEFACTOR";
    public final static String STATE_XFOCUS="STATE_XFOCUS";
    public final static String STATE_YFOCUS="STATE_YFOCUS";

    public Bundle saveZoomViewState(){
        Bundle bundle = new Bundle();
        bundle.putFloat(STATE_SCALEFACTOR,scaleFactor);
        bundle.putFloat(STATE_XFOCUS,xFocus);
        bundle.putFloat(STATE_YFOCUS,yFocus);
        return bundle;
    }

    public void restoreZoomViewState(Bundle zoomViewState){
        scaleFactor = zoomViewState.getFloat(STATE_SCALEFACTOR,scaleFactor);
        xFocus      = zoomViewState.getFloat(STATE_XFOCUS,xFocus);
        yFocus      = zoomViewState.getFloat(STATE_YFOCUS,yFocus);
        redrawBitmap();
    }
}