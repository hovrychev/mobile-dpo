package com.example.birdview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Bird {
    float x, y;
    Bitmap bitmap;
    int width, height;
    Rect hitbox;

    public Bird(float x, float y, Bitmap bitmap, int width, int height) {
        this.x = x;
        this.y = y;
        this.bitmap = bitmap;
        this.width = width;
        this.height = height;
        this.hitbox = new Rect((int) x, (int) y, (int) (x + width), (int) (y + height));
    }

    public void draw(Canvas canvas) {
        if (bitmap != null) {
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
            canvas.drawBitmap(scaledBitmap, x, y, null);
        }
    }

    public boolean intersects(Bird other) {
        return Rect.intersects(this.hitbox, other.hitbox);
    }

    public void updateHitbox() {
        hitbox.set((int) x, (int) y, (int) (x + width), (int) (y + height));
    }
}
