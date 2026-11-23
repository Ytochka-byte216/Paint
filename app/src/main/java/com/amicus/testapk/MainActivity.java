package com.amicus.testapk;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    PaintView paintView;
    Button save;
    Button clear;
    Button change;

    //ДОБАВЛЕНЫ ФУНКЦИИ: ЛАСТИ, ОТМЕНИТЬ ПОСЛДЕНЕЕ ДЕЙСТВИЕ И ВЕРНУТЬ ОТМЕНЕННОЕ ДЕЙСТВИЕ
    Button eraser;
    Button undo;
    Button redo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        save = findViewById(R.id.btnSave);
        clear = findViewById(R.id.btnClear);
        change = findViewById(R.id.btnColor);
        paintView = findViewById(R.id.paintView);
        eraser = findViewById(R.id.btnEraser);
        undo = findViewById(R.id.btnUndo);
        redo = findViewById(R.id.btnRedo);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.saveToGallery(MainActivity.this);
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.clearCanvas();
            }
        });


        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.changeColor(change);
            }
        });

        eraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // просто переключаем режим
                boolean nowOn = !paintView.eraserOn;
                paintView.setEraser(nowOn);
                eraser.setText(nowOn ? "eraser: on" : "eraser");
            }
        });

        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.undo();
            }
        });

        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.redo();
            }
        });

    }
}