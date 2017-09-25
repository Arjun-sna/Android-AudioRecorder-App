package in.arjsna.audiorecorder.libs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import in.arjsna.audiorecorder.R;
import java.util.Timer;
import java.util.TimerTask;

public class FillSeekBar extends FrameLayout {
  private int mProgress;
  private Solid mSolid;

  private final int DEFAULT_FILL_COLOR = Color.WHITE;
  private final int DEFAULT_PROGRESS = 80;
  private int mSolidRight;
  private double mMaxValue = 1.0;

  public FillSeekBar(Context context, AttributeSet attrs) {
    super(context, attrs);
    //load styled attributes.
    final TypedArray attributes = context.getTheme()
        .obtainStyledAttributes(attrs, R.styleable.FillSeekBar, R.attr.fillseekbarViewStyle, 0);
    int mFillColor =
        attributes.getColor(R.styleable.FillSeekBar_fill_color, DEFAULT_FILL_COLOR);
    mProgress = attributes.getInt(R.styleable.FillSeekBar_progress, DEFAULT_PROGRESS);
    attributes.recycle();
    mSolid = new Solid(context, null);
    mSolid.initPaint(mFillColor);
    addView(mSolid);
    setProgress(0);
  }

  public void setMaxVal(double maxVal) {
    this.mMaxValue = maxVal;
    setAutoProgress();
  }

  public void setProgress(int progress) {
    this.mProgress = progress > mMaxValue ? (int) mMaxValue : progress;
    computeProgressRigth();
  }

  @Override public void onWindowFocusChanged(boolean hasWindowFocus) {
    super.onWindowFocusChanged(hasWindowFocus);
    if (hasWindowFocus) {
      computeProgressRigth();
    }
  }

  private void computeProgressRigth() {
    mSolidRight = (int) (getWidth() * (1f - mProgress / mMaxValue));
    Log.i("Stats " , mSolidRight + " " + mProgress);
    ViewGroup.LayoutParams params = mSolid.getLayoutParams();
    if (params != null) {
      ((LayoutParams) params).rightMargin = mSolidRight;
    }
    mSolid.setLayoutParams(params);
  }

  @Override public Parcelable onSaveInstanceState() {
    // Force our ancestor class to save its state
    Parcelable superState = super.onSaveInstanceState();
    SavedState ss = new SavedState(superState);
    ss.progress = mProgress;
    return ss;
  }

  @Override public void onRestoreInstanceState(Parcelable state) {
    SavedState ss = (SavedState) state;
    super.onRestoreInstanceState(ss.getSuperState());
    setProgress(ss.progress);
  }

  private static class SavedState extends BaseSavedState {
    int progress;

    /**
     * Constructor called from {@link android.widget.ProgressBar#onSaveInstanceState()}
     */
    SavedState(Parcelable superState) {
      super(superState);
    }

    /**
     * Constructor called from {@link #CREATOR}
     */
    private SavedState(Parcel in) {
      super(in);
      progress = in.readInt();
    }

    @Override public void writeToParcel(Parcel out, int flags) {
      super.writeToParcel(out, flags);
      out.writeInt(progress);
    }

    public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
      public SavedState createFromParcel(Parcel in) {
        return new SavedState(in);
      }

      public SavedState[] newArray(int size) {
        return new SavedState[size];
      }
    };
  }

  public void setAutoProgress() {
    timer = new Timer();
    timer.scheduleAtFixedRate(timerTask, 0 , 10);
  }
  Handler handler = new Handler(Looper.getMainLooper());
  Timer timer;
  TimerTask timerTask = new TimerTask() {
    @Override public void run() {
      handler.post(new Runnable() {
        @Override public void run() {
          setProgress(mProgress + 10);
        }
      });
    }
  };

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
      progressPaint.setStyle(Paint.Style.FILL);
      progressPaint.setAntiAlias(true);
    }

    @Override protected void onDraw(Canvas canvas) {
      super.onDraw(canvas);
      canvas.drawRect(getLeft(), 0, getRight(), getBottom(), progressPaint);
    }
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    if (timer != null) {
      timer.cancel();
      timer = null;
    }
  }
}
