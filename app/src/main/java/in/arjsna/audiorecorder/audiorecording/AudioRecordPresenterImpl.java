package in.arjsna.audiorecorder.audiorecording;

import in.arjsna.audiorecorder.mvpbase.BasePresenter;
import io.reactivex.disposables.CompositeDisposable;
import javax.inject.Inject;

public class AudioRecordPresenterImpl<V extends AudioRecordMVPView> extends BasePresenter<V>
    implements AudioRecordPresenter<V> {

  @Inject
  public AudioRecordPresenterImpl(CompositeDisposable compositeDisposable) {
    super(compositeDisposable);
  }
}
