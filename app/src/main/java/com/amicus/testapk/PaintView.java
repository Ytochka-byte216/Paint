package com.amicus.testapk;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by sizik on 16.11.2026.
 */
public class PaintView extends View {

    ArrayList<Stroke> strokes = new ArrayList<>();
    Stroke cuttentStroke;
    int currentColor = Color.BLACK;
    float strokeWidth = 10f;

    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        for (Stroke stroke : strokes) {
            canvas.drawPath(stroke.path, stroke.paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                int color = eraserOn ? Color.WHITE : currentColor; //возможно будет ошибка в 56 строке
                float width = eraserOn ? strokeWidth * 2f : strokeWidth; // ластик толще
                cuttentStroke = new Stroke(color, width);
                cuttentStroke.path.moveTo(x,y);
                strokes.add(cuttentStroke);
                removedStrokes.clear();
                return true;

            case MotionEvent.ACTION_MOVE:
                if (cuttentStroke !=null){
                    cuttentStroke.path.lineTo(x,y);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        invalidate();
        return true;
    }

    public void changeColor(Button button){
        Random random = new Random();

        currentColor = Color.rgb(random.nextInt(256),random.nextInt(256),random.nextInt(256));
        button.setBackgroundColor(currentColor);
    }

    public void clearCanvas(){
        strokes.clear();
        invalidate();
    }

    public void saveToGallery(Context context){
        Bitmap bitmap = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME,"drawings_"+System.currentTimeMillis()+".png");
        values.put(MediaStore.Images.Media.MIME_TYPE,"image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES+"/MyDrawings");

        ContentResolver resolver = context.getContentResolver();
        try {
            Uri uri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            Uri imageUrl = resolver.insert(uri,values);

            if(imageUrl!=null){
                try(OutputStream outputStream = resolver.openOutputStream(imageUrl)) {
                    bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
                }
            }
            Toast.makeText(context, "Сохранено в галерее", Toast.LENGTH_SHORT).show();
        }catch (IOException e){
            e.printStackTrace();
            Toast.makeText(context, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
        }

    }

    // ластик
    boolean eraserOn = false;

    // для redo - здесь хранятся отменённые штрихи
    ArrayList<Stroke> removedStrokes = new ArrayList<>();

    // переключение ластика
    public void setEraser(boolean on){
        eraserOn = on;
    }

    // толщина кисти
    public void setStrokeWidth(float width){
        strokeWidth = width;
    }

    // отмена последнего штриха
    public void undo(){
        if(strokes.size() > 0){
            Stroke last = strokes.remove(strokes.size() - 1);
            removedStrokes.add(last);
            invalidate();
        }
    }

    // вернуть отменённый штрих
    public void redo(){
        if(removedStrokes.size() > 0){
            Stroke last = removedStrokes.remove(removedStrokes.size() - 1);
            strokes.add(last);
            invalidate();
        }
    }
}
