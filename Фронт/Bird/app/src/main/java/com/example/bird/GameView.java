package com.example.bird;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private final GameThread thread;
    private final Bird mainBird;
    private final List<Bird> enemies = new ArrayList<>();
    private float targetX, targetY;
    private int score = 0;
    private final Paint paint;
    private final Bitmap birdBitmap;
    private long lastEnemySpawnTime = 0;
    private static final long ENEMY_SPAWN_INTERVAL = 2000;

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        thread = new GameThread(getHolder(), this);
        setFocusable(true);

        birdBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bird);

        mainBird = new Bird(100, 100, birdBitmap, 100, 100);
        targetX = mainBird.x;
        targetY = mainBird.y;

        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(60f);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        targetX = event.getX() - mainBird.width / 2f;
        targetY = event.getY() - mainBird.height / 2f;
        return true;
    }

    public void update() {
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

    public void drawCanvas(Canvas canvas) {
        if (canvas == null) return;

        super.draw(canvas);
        canvas.drawColor(Color.WHITE);

        mainBird.draw(canvas);

        for (Bird enemy : enemies) {
            if (enemy.x >= 0 && enemy.x <= getWidth() && enemy.y >= 0 && enemy.y <= getHeight()) {
                enemy.draw(canvas);
            }
        }

        canvas.drawText("Score: " + score, 50F, 100F, paint);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) { }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                Log.e("TAG", "An error occurred: " + e.getMessage(), e);
            }
        }
    }

    static class GameThread extends Thread {
        private final SurfaceHolder surfaceHolder;
        private final GameView gameView;
        private boolean running = false;

        public GameThread(SurfaceHolder surfaceHolder, GameView gameView) {
            this.surfaceHolder = surfaceHolder;
            this.gameView = gameView;
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        @Override
        public void run() {
            while (running) {
                Canvas canvas = null;
                try {
                    canvas = surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {
                        gameView.update();
                        gameView.drawCanvas(canvas);
                    }
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }
}
