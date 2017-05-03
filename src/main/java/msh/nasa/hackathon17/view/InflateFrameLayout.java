package msh.nasa.hackathon17.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.ButterKnife;

/**
 * @author mohsen_shahini on 5/3/17.
 */

public abstract class InflateFrameLayout extends FrameLayout {

    public InflateFrameLayout(final Context context) {
        super(context);
        init(context, null);
    }

    public InflateFrameLayout(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public InflateFrameLayout(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public InflateFrameLayout(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    protected void init(final Context context, final AttributeSet attrs) {
        final View view = View.inflate(context, getViewLayout(), this);

        ButterKnife.bind(this, view);
        applyAttributes(context, attrs);

        onViewCreated(context);
    }

    protected void onViewCreated(final Context context) {}
    protected void applyAttributes(final Context context, final AttributeSet attrs) {}

    @LayoutRes
    protected abstract int getViewLayout();
}