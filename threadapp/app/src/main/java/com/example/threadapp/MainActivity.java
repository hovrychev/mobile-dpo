package com.example.threadapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.textView);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Определяем объект Runnable
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        // получаем текущее время
                        Calendar c = Calendar.getInstance();
                        int hours = c.get(Calendar.HOUR_OF_DAY);
                        int minutes = c.get(Calendar.MINUTE);
                        int seconds = c.get(Calendar.SECOND);
                        String time = hours + ":" + minutes + ":" + seconds;
                        // отображаем в текстовом поле
                        textView.post(new Runnable() {
                            public void run() {
                                textView.setText(time);
                            }
                        });
                    }
                };
                // Определяем объект Thread - новый поток
                Thread thread = new Thread(runnable);
                // Запускаем поток
                thread.start();
            }
        });
    }
}