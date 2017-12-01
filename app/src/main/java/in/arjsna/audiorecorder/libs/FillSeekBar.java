package in.arjsna.audiorecorder.libs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import in.arjsna.audiorecorder.R;
import in.arjsna.audiorecorder.theme.ThemedActivity;

public class FillSeekBar extends FrameLayout {
  private long mProgress = 0;
  private Solid mSolid;

  private final int DEFAULT_FILL_COLOR = Color.WHITE;
  private double mMaxValue = 1.0;

  public FillSeekBar(Context context, AttributeSet attrs) {
    super(context, attrs);
    //load styled attributes.
    final TypedArray attributes = context.getTheme()
        .obtainStyledAttributes(attrs, R.styleable.FillSeekBar, R.attr.fillseekbarViewStyle, 0);
    int mFillColor = ((ThemedActivity) context).getPrimaryColor();
    mProgress = attributes.getInt(R.styleable.FillSeekBar_progress, 0);
    attributes.recycle();
    mSolid = new Solid(context, null);
    mSolid.initPaint(mFillColor);
    addView(mSolid, 0, LayoutParams.MATCH_PARENT);
  }

  public void setMaxVal(double maxVal) {
    this.mMaxValue = maxVal;
  }

  public void setProgress(long progress) {
    mProgress = progress > mMaxValue ? (long) mMaxValue : progress;
    computeProgressRight();
  }

  private void computeProgressRight() {
    int mSolidRight = (int) (getWidth() * (1f - mProgress / mMaxValue));
    //Log.i("Stats ", mSolidRight + " " + mProgress);
    ViewGroup.LayoutParams params = mSolid.getLayoutParams();
    if (params != null) {
      ((LayoutParams) params).width = getWidth() - mSolidRight;
    }
    mSolid.setLayoutParams(params);
  }

  private static class Solid extends View {

    private Paint progressPaint;

    public Solid(Context context, AttributeSet attrs) {
      this(context, attrs, 0);
    }

    public Solid(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      LinearLayout.LayoutParams params =
          new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
              LinearLayout.LayoutParams.WRAP_CONTENT);
      params.weight = 1;
      setLayoutParams(params);
    }

    public void initPaint(int mFillColor) {
      progressPaint = new Paint();
      progressPaint.setColor(mFillColor);
      progressPaint.setAlpha(125);
      progressPaint.setStyle(Paint.Style.FILL);
      progressPaint.setAntiAlias(true);
    }

    @Override protected void onDraw(Canvas canvas) {
      super.onDraw(canvas);
      //Log.i("Statsinneer ", getRight() + " ");
      canvas.drawRect(getLeft(), 0, getWidth(), getBottom(), progressPaint);
    }
  }
}
