package com.raksha.assignment.assignment4;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class MoveCircleThread extends Thread {

    private long time;
    public static final int updateFrequency = 50; // 50 times per second
    private boolean isRunning = false;
    private BouncingCircleSurface bouncingCircleSurface;
    private SurfaceHolder surfaceHolder;

    public MoveCircleThread(BouncingCircleSurface bouncingCircleSurface) {
        this.bouncingCircleSurface = bouncingCircleSurface;
        surfaceHolder = this.bouncingCircleSurface.getHolder();
    }

    @Override
    public void run() {
        updateCircles();
    }

    private void updateCircles() {
        Canvas canvas;
        while (isRunning) {
            long currentTimeMillis = System.currentTimeMillis();
            long timeDifference = currentTimeMillis - time;
            if (timeDifference <= (1000 / updateFrequency)) {
                canvas = null;
                try {
                    canvas = surfaceHolder.lockCanvas(null);
                    bouncingCircleSurface.onDraw(canvas);
                } finally {
                    if(canvas != null){
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
            time = currentTimeMillis;
        }
    }

    public void setRunning(boolean run) {
        isRunning = run;
    }
}