package com.raksha.assignment.assignment4;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.VelocityTracker;
import android.view.View;

import java.util.ArrayList;

public class BouncingCircleSurface extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {

    private int surfaceWidth;
    private int surfaceHeight;

    private Circle selectedCircle;
    private ArrayList<Circle> circleArrayList = new ArrayList<>();

    private VelocityTracker velocity;
    private MoveCircleThread moveCircleThread;

    private android.os.Handler handler = new android.os.Handler();
    private final Runnable increaseSizeOnLongPressRunnable = new Runnable(){
        public void run(){
            try {
                Circle circle = circleArrayList.get(Circle.circleCount - 1);
                float increaseByValue = 3;
                float newRadius = (float) (circle.getRadius() + increaseByValue);
                circle.setRadius(newRadius);
                handler.postDelayed(this, 100);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public BouncingCircleSurface(Context context) {
        super(context);
        getHolder().addCallback(this);
        setOnTouchListener(this);
    }
    public BouncingCircleSurface(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        getHolder().addCallback(this);
        setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(canvas != null) {
            // paint the canvas white
            canvas.drawColor(Color.WHITE);

            // calculate the new position and draw all the circles
            if (circleArrayList != null && circleArrayList.size() > 0) {
                for (int i = 0; i < circleArrayList.size(); i++) {
                    Circle circle = circleArrayList.get(i);
                    circle.updateCirclePosition(canvas, surfaceWidth, surfaceHeight);
                    circle.onDraw(canvas);
                }
            }
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Rect surfaceFrame = holder.getSurfaceFrame();
        surfaceWidth = surfaceFrame.width();
        surfaceHeight = surfaceFrame.height();

        moveCircleThread = new MoveCircleThread(this);
        moveCircleThread.setRunning(true);
        moveCircleThread.start();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;

        moveCircleThread.setRunning(false);
        while (retry) {
            try {
                moveCircleThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        selectedCircle = checkIfAnyExistingBallMoved(x, y);

        // Do not allow more than 15 balls to be created
        if(selectedCircle == null) {
            if(Circle.circleCount >= 15){
                return false;
            }
        }

        handleTouchEvent(event);

        return true;
    }

    private void handleTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handleDownAction(event);
                break;
            case MotionEvent.ACTION_MOVE:
                handleMoveAction(event);
                break;
            case MotionEvent.ACTION_UP:
                handleUpAction(event);
                break;
            default:
                break;
        }
    }

    private void handleUpAction(MotionEvent event) {
        // calculate velocity
        velocity.computeCurrentVelocity(1000/ MoveCircleThread.updateFrequency);

        Circle circle;
        if(selectedCircle != null){
            circle = selectedCircle;
        } else {
            // set the new co-ordinates for circle on movement
            circle = circleArrayList.get(Circle.circleCount - 1);
        }

        circle.setVelocityX(velocity.getXVelocity());
        circle.setVelocityY(velocity.getYVelocity());
        velocity.recycle();
        velocity = null;

        // stop increasing size as soon as finger is removed
        handler.removeCallbacks(increaseSizeOnLongPressRunnable);
    }

    private void handleMoveAction(MotionEvent event) {
        velocity.addMovement(event);

        float x = event.getX();
        float y = event.getY();

        Circle circle;
        if(selectedCircle != null){
            circle = selectedCircle;
        } else {
            // set the new co-ordinates for circle on movement
            circle = circleArrayList.get(Circle.circleCount - 1);
        }

        circle.getCurrPoint().set(x, y);
    }

    private void handleDownAction(MotionEvent event) {
        velocity = VelocityTracker.obtain();
        velocity.addMovement(event);

        float x = event.getX();
        float y = event.getY();

        Circle circle;
        if(selectedCircle != null){
            return;
        } else {
            circle = new Circle();
        }

        // set the new co-ordinates for circle on down
        circle.setCurrPoint(new PointF(x, y));
        circleArrayList.add(circle);

        // start increasing size of circle in 500ms
        handler.postDelayed(increaseSizeOnLongPressRunnable, 1000);
    }

    private Circle checkIfAnyExistingBallMoved(float x, float y) {
        Circle selectedCircle = null;
        int i = 0;
        for(Circle circle : circleArrayList) {
            float radius = circle.getRadius();
            float xPosition = circle.getCurrPoint().x;
            float yPosition = circle.getCurrPoint().y;

            // check if touch co-ordinates falls into a circle co-ordinate range
            if(x >= xPosition - radius && x <= xPosition + radius
                    && y >= yPosition - radius && y <= yPosition + radius) {
                selectedCircle = circle;
            }
            i++;
        }

        return selectedCircle;
    }

    public void clearView(){
        // reset the circle count and the circle list
        Circle.circleCount = 0;
        circleArrayList.clear();
    }
}