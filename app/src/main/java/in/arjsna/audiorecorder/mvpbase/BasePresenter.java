package in.arjsna.audiorecorder.mvpbase;

import io.reactivex.disposables.CompositeDisposable;

public class BasePresenter<V extends IMVPView> implements IMVPPresenter<V> {
  private V mAttachedView;
  private CompositeDisposable compositeDisposable;

  public BasePresenter(CompositeDisposable compositeDisposable) {
    this.compositeDisposable = compositeDisposable;
  }

  @Override public void onAttach(V view) {
    mAttachedView = view;
  }

  @Override public void onDetach() {
    compositeDisposable.dispose();
    mAttachedView = null;
  }

  public V getAttachedView() {
    return mAttachedView;
  }

  public CompositeDisposable getCompositeDisposable() {
    return compositeDisposable;
  }
}
