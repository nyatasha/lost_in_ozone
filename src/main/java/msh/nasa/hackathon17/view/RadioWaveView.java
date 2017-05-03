package msh.nasa.hackathon17.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;

import msh.nasa.hackathon17.R;
import msh.nasa.hackathon17.util.ResourcesUtil;

/**
 * Created by mohsen_shahini on 5/3/17.
 */

public class RadioWaveView extends InflateFrameLayout {

    private static final int ANIMATION_CIRCLE_DURATION = 2500;

    private int highRadius;
    private int mediumRadius;
    private int lowRadius;
    private int normalRadius;

    public RadioWaveView(Context context) {
        super(context);
    }

    public RadioWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RadioWaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RadioWaveView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init() {

        highRadius = 75;
        mediumRadius = 150;
        lowRadius = 300;
        normalRadius = 600;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawHighWave(canvas);
    }

    @Override
    protected int getViewLayout() {
        return 0;
    }

    private void drawHighWave(Canvas canvas) {
        Paint coreOrbitPaint = new Paint();
        coreOrbitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        coreOrbitPaint.setColor(ResourcesUtil.getColor(getContext(), R.color.highCircle));
        coreOrbitPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        Point centerPoint = new Point();
        centerPoint.x = 50;
        centerPoint.y = 150;
        canvas.drawCircle(centerPoint.x, centerPoint.y, highRadius, coreOrbitPaint);
    }
}
