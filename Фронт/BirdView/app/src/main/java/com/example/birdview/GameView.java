package com.example.birdview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameView extends View {

    private final Bird mainBird;
    private final List<Bird> enemies = new ArrayList<>();
    private float targetX, targetY;
    private int score = 0;
    private final Paint paint;
    private final Bitmap birdBitmap;
    private long lastEnemySpawnTime = 0;
    private static final long ENEMY_SPAWN_INTERVAL = 2000;

    private final Handler handler = new Handler();
    private final Runnable gameLoopRunnable = new Runnable() {
        @Override
        public void run() {
            update();
            invalidate();
            handler.postDelayed(this, 16);
        }
    };

    public GameView(Context context) {
        this(context, null);
    }

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        birdBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bird);
        mainBird = new Bird(100, 100, birdBitmap, 100, 100);
        targetX = mainBird.x;
        targetY = mainBird.y;

        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(60f);

        handler.post(gameLoopRunnable);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.WHITE);
        mainBird.draw(canvas);

        for (Bird enemy : enemies) {
            enemy.draw(canvas);
        }

        canvas.drawText("Score: " + score, 50F, 100F, paint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        targetX = event.getX() - mainBird.width / 2f;
        targetY = event.getY() - mainBird.height / 2f;
        return true;
    }

    private void update() {
        float dx = targetX - mainBird.x;
        float dy = targetY - mainBird.y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist > 5) {
            mainBird.x += dx / dist * 10;
            mainBird.y += dy / dist * 10;
        }

        mainBird.updateHitbox();

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastEnemySpawnTime >= ENEMY_SPAWN_INTERVAL) {
            float y = (float) (Math.random() * getHeight());
            enemies.add(new Bird(getWidth(), y, birdBitmap, 100, 100));
            lastEnemySpawnTime = currentTime;
        }

        Iterator<Bird> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Bird enemy = iterator.next();
            enemy.x -= 5;
            enemy.updateHitbox();

            if (mainBird.intersects(enemy)) {
                iterator.remove();
                score++;
            }

            if (enemy.x + enemy.width < 0) {
                iterator.remove();
            }
        }
    }

    public void stop() {
        handler.removeCallbacks(gameLoopRunnable);
    }
}
