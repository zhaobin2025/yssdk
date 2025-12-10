package com.yasee.yaseejava.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.yasee.yaseejava.R;

import java.util.LinkedList;
import java.util.Queue;

public class LiveECGView extends SurfaceView implements SurfaceHolder.Callback {
    private Paint paintGrid, paintECG;
    private Queue<Float> ecgData = new LinkedList<>(); // FIFO 队列存储 ECG 数据
    private float scaleX = 4, scaleY = 4.5f, baseLine = 30;
    private int maxPoints = 330, baseLineLevel = 0;
    private SurfaceHolder holder;
    private boolean isDrawing = false;

    public LiveECGView(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = getHolder();
        holder.addCallback(this);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LiveECGView);
            scaleY = typedArray.getFloat(R.styleable.LiveECGView_ecgScaleY, 1.0f); // 默认 yScale 为 1.0
            baseLine = typedArray.getFloat(R.styleable.LiveECGView_baseLine, 1.0f); // 默认 yScale 为 1.0
            typedArray.recycle(); // 释放资源
        }
        init();
    }

    private void init() {
        paintGrid = new Paint();
        paintGrid.setColor(Color.LTGRAY);
        paintGrid.setStrokeWidth(1);
        paintGrid.setStyle(Paint.Style.STROKE);

        paintECG = new Paint();
        paintECG.setColor(Color.GREEN);
        paintECG.setStrokeWidth(3);
        paintECG.setStrokeJoin(Paint.Join.BEVEL);
        paintECG.setStyle(Paint.Style.STROKE);
    }

    /**
     * 设置新的点
     * */
    public void addEcgData(float value) {
        if (ecgData.size() >= maxPoints) {
            ecgData.poll(); // 移除最早的数据，实现滚动
        }
        ecgData.offer(value);
        drawECG(); // 立即更新 ECG
    }

    /**
     * 清空
     * */
    public void clean() {
        ecgData.clear();
        drawECG(); // 立即更新 ECG
    }

    /**
     * 设置压力 level; level 越高 代表数值越大;
     * <br/> 如果高度有限 必须重新设置 baseLine 来适应值; 保证 UI 适配度
     * */
    public void setBaselineLevel(int level) {
        if (baseLineLevel == level) return;
        baseLineLevel = level;
        baseLine =  baseLine * (1.f + baseLineLevel / 10.f);
    }


    private void drawECG() {
        if (!isDrawing) return;
        Canvas canvas = holder.lockCanvas();
        if (canvas == null) return;

        int width = getWidth();
        int height = getHeight();
        canvas.drawColor(Color.BLACK); // 清空画布

        // **绘制网格**
        for (int i = 0; i < width; i += 50) {
            canvas.drawLine(i, 0, i, height, paintGrid);
        }
        for (int j = 0; j < height; j += 50) {
            canvas.drawLine(0, j, width, j, paintGrid);
        }

        // **绘制 ECG 曲线**
        if (ecgData.size() > 1) {
            Float[] dataArray = ecgData.toArray(new Float[0]);
            for (int i = 1; i < dataArray.length; i++) {
                float x1 = (i - 1) * scaleX;
                float y1 = height - dataArray[i - 1] * scaleY;
                float x2 = i * scaleX;
                float y2 = height - dataArray[i] * scaleY;
                canvas.drawLine(x1, y1 + baseLine, x2, y2 + baseLine, paintECG);
            }
        }

        holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isDrawing = true;
        drawECG(); // 开始绘制
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isDrawing = false;
    }
}