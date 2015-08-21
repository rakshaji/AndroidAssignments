package com.raksha.assignment.assignment4;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

/**
 * Created by Raksha on 3/27/2015.
 */
public class Circle {

    public static int circleCount = 0;

    private float radius;
    private float velocityX;
    private float velocityY;

    private PointF currPoint;
    private Paint circlePaint;

    private static int[] colorOptions = {
            Color.rgb(255, 71, 25),
            Color.rgb(51, 133, 255),
            Color.BLACK,
            Color.rgb(204, 102, 255),
            Color.rgb(163, 255, 117),
            Color.rgb(255, 153, 255),
            Color.rgb(120, 230, 230),
            Color.rgb(230, 230, 120),
            Color.GRAY,
            Color.rgb(184, 0, 92),
            Color.rgb(230, 135, 135),
            Color.rgb(181, 181, 69),
            Color.rgb(53, 204, 255),
            Color.rgb(102, 255, 153),
            Color.rgb(255, 153, 51)
    };

    public Circle(){
        // set different color for each circle
        circlePaint = new Paint();
        circlePaint.setColor(colorOptions[circleCount]);

        // set the default radius
        radius = 70f;
        circleCount += 1;
    }

    public void onDraw(Canvas canvas) {
        if(canvas != null) {
            // draw a circle
            canvas.drawCircle(getCurrPoint().x,
                    getCurrPoint().y,
                    getRadius(),
                    getCirclePaint());
        }
    }

    public PointF getCurrPoint() {
        return currPoint;
    }

    public void setCurrPoint(PointF currPoint) {
        this.currPoint = currPoint;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(float velocityY) {
        this.velocityY = velocityY;
    }

    public float getVelocityX() {
        return velocityX;
    }

    public void setVelocityX(float velocityX) {
        this.velocityX = velocityX;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public Paint getCirclePaint() {
        return circlePaint;
    }

    public void updateCirclePosition(Canvas canvas, int maxWidth, int maxHeight) {
        if(canvas == null)
            return;

        float xPosition = getCurrPoint().x;
        float yPosition = getCurrPoint().y;
        float radius = getRadius();
        float xVelocity = getVelocityX();
        float yVelocity = getVelocityY();

        // use velocity to determine new co-ordinates
        xPosition += xVelocity;
        yPosition += yVelocity;

        // check if circle hits/crosses the screen boundary
        // if yes, reverse the velocity to give imitation of bounce
        if (yPosition - radius < 0 || yPosition + radius > maxHeight) {
            if (yPosition - radius < 0) {
                yPosition = radius;
            } else {
                yPosition = maxHeight - radius;
            }

            yVelocity *= -1;
            setVelocityY(yVelocity);
        }
        if (xPosition - radius < 0 || xPosition + radius > maxWidth) {
            if (xPosition - radius < 0) {
                xPosition = radius;
            } else {
                xPosition = maxWidth - radius;
            }
            xVelocity *= -1;
            setVelocityX(xVelocity);
        }
        getCurrPoint().set(xPosition, yPosition);
    }
}
