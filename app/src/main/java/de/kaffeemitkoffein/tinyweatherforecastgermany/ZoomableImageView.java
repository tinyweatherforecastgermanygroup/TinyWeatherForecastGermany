package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.content.Context;
import android.view.ScaleGestureDetector;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * ZoomableImageView is a simple class that enhances an ImageView with the capability to zoom in and out.
 *
 * @author Pawel Dube
 *
 * Requirements and limitations:
 * - the imageview has to be populated with a bitmap. Other content will not work.
 * - WIDTH AND HEIGHT OF THE IMAGEVIEW MUST BE SET TO "MATCH_PARENT". When set to "wrap_content", the view will shrink
 *   when the bitmap inside the view shrinks due to zooming!
 *
 * Gestures:
 * - this class catches the usual "zoom in" and "zoom out" gestures:
 *   zoom in  = move two pointers (e.g. two fingers) away from each other while touching the screen
 *   zoom out = move two pointers (e.g. two fingers) closer to each other while touching the screen
 * - moving one pointer (e.g. one finger) while touching the screen moves the scaled bitmap within the imageview
 *
 * Notes:
 * - this class always "consumes" the provided motionEvent, because the underlying ScaleGestureDetector does so.
 *   Therefore, there are NEVER any "unused" events after this class has been called. However, this is not a limitation,
 *   see example how to use.
 *
 * How to use:
 * 1) create an instance of ZoomableImageView providing Context, the imageview and the bitmap,
 * 2) and optionally override "onGestureFinished" to catch the results once a gesture is done. If you do not need to
 *    further react after a zoom/move gesture, you can skip this.
 * 3) Register an OnTouchListerner and handle over the motionEvent to the zoomableImageView.
 * That's all.
 *
 *        mapImageView = (ImageView) findViewById(R.id.map);
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
 *
 * About the motionEvent:
 * zoomableImageView ALWAYS RETURNS TRUE, because the underlying ScaleGestureDetector always "consumes" all
 * motionEvents. If you need to do other things with the motionEvent, do so in the onTouch callback (see above).
 *
 */

public class ZoomableImageView {

    /**
     * The current zoom:
     * 1 = no zoom,
     * values below 1 mean the bitmap is zoomed in (enlarged)
     * values above 1 mean the bitmap is zoomed out (shrunk).
     * Values above 1 are discouraged and may lead to unexpected behaviour of the imageview/bitmap.
     *
     * Notice: this value is only accurate once a scale gesture finished. During a scale gesture, it holds the
     * scale factor from the beginning of the gesture. In other words: it is updated at the end of the gesture,
     * and not while a gesture is being performed.
     *
     * Override onGestureFinished to get the results of a gesture.
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

    private Bitmap bitmap;
    private ImageView imageView;
    public ZoomGestureListener zoomGestureListener;
    public ScaleGestureDetector scaleGestureDetector;
    private Context context;

    /**
     * Initializes this class. Values must not be null.
     *
     * @param context the application context
     * @param imageView the imageview
     * @param bitmap the bitmap to be used in the imageview
     */

    public ZoomableImageView(Context context,ImageView imageView, Bitmap bitmap){
        this.context = context;
        this.imageView = imageView;
        this.bitmap = bitmap.copy(Bitmap.Config.ARGB_8888,true);
        initDefaults();
    }

    /**
     * Optional method to set the min and max scale range. Example:
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
        this.bitmap = newbitmap.copy(Bitmap.Config.ARGB_8888,true);
        redrawBitmap();
    }

    /**
     * Always called when a gesture (zoom or movement) finished.
     *
     * Override this method to do something once a zoom / movement inside the imageview finished.
     *
     * The focus is the visible center coordinate.
     *
     * @param scaleFactor the current cale factor
     * @param xFocus the absolute x focus in pixels, reference is the whole bitmap
     * @param yFocus the absolute y focus in pixels, reference is the whole bitmap
     * @param xFocusRelative the relative x focus (0 <= xFocusRelative <= 1), reference is the whole bitmap
     * @param yFocusRelative the relative y focus (0 <= yFocusRelative <= 1), reference is the whole bitmap
     * @param currentlyVisibleArea rectangle holding the coordinates of the visible area in pixels
     */

    public void onGestureFinished(float scaleFactor, float xFocus, float yFocus, float xFocusRelative, float yFocusRelative, RectF currentlyVisibleArea){
        // things to do after gesture finished.
    }

    /**
     * Returns the current focus as a relative position, returns a value between 0 (=left border of the bitmap) and
     * 1 (=right border of the bitmap). The position refers to the bitmap.
     *
     * The focus is the visible center coordinate.
     *
     * @return the relative x coordinate of the focus, 0 <= x <= 1
     */

    public float getRelativeXFocus(){
        return xFocus/bitmap.getWidth();
    }

    /**
     * Returns the current focus as a relative position, returns a value between 0 (=left top of the bitmap) and
     * 1 (=bottom of the bitmap). The position refers to the bitmap.
     *
     * The focus is the visible center coordinate.
     *
     * @return the relative y coordinate of the focus, 0 <= y <= 1
     */

    public float getRelativeYFocus(){
        return yFocus/bitmap.getHeight();
    }

    /**
     * Returns the absolute focus x coordinate in pixels. The coordinate refers to the bitmap size in pixels.
     *
     * The focus is the visible center coordinate.
     *
     * * @return absolute x coordinate
     */

    public float getXFocus(){
        return xFocus;
    }

    /**
     * Returns the absolute focus y coordinate in pixels. The coordinate refers to the bitmap size in pixels.
     *
     * The focus is the visible center coordinate.
     *
     * * @return absolute y coordinate
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
            onGestureFinished(scaleFactor,getXFocus(),getYFocus(),getRelativeXFocus(),getRelativeYFocus(),temporaryVisibleArea);
        }

        return true;
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
        redrawBitmap();
    }

    /**
     * Redraws the bitmap inside the imageview.
     *
     * @param scaleFactor the scale factor to use.
     */

    private void redrawBitmap(float scaleFactor){
        int widthVisible  = Math.round(bitmap.getWidth()*scaleFactor);
        int heightVisible = Math.round(bitmap.getHeight()*scaleFactor);
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
        imageView.setImageBitmap(bitmap);
    }

    /**
     * Redraws the bitmap inside the imageview. Uses the currently set scale factor.
     */

    private void redrawBitmap(){
        redrawBitmap(scaleFactor);
    }

    /**
     * Handles the move of the bitmap with one pointer (e.g. one finger).
     *
     * @param xDelta horizontal movement pixels
     * @param yDelta vertical movement in pixels
     */

    private void moveMap(float xDelta, float yDelta){

        int widthVisible  = Math.round(bitmap.getWidth()*scaleFactor);
        int heightVisible = Math.round(bitmap.getHeight()*scaleFactor);

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
         * Handles the scale gesture & the animation.
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

}
