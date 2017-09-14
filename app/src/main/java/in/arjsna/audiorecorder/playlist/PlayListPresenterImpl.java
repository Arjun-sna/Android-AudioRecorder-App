package in.arjsna.audiorecorder.playlist;

import in.arjsna.audiorecorder.db.RecordItemDataSource;
import in.arjsna.audiorecorder.db.RecordingItem;
import in.arjsna.audiorecorder.mvpbase.BasePresenter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import javax.inject.Inject;

public class PlayListPresenterImpl<V extends PlayListMVPView> extends BasePresenter<V> implements PlayListPresenter<V> {
  @Inject
  public RecordItemDataSource recordItemDataSource;
  @Inject
  public PlayListPresenterImpl(CompositeDisposable compositeDisposable) {
    super(compositeDisposable);
  }

  @Override public void onViewInitialised() {
    fillAdapter();
    getAttachedView().startWatchingForFileChanges();
  }

  @Override public void onDetach() {
    getAttachedView().stopWatchingForFileChanges();
    super.onDetach();
  }

  private void fillAdapter() {
    getCompositeDisposable().add(recordItemDataSource.getAllRecordings()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe((recordingItems) -> {
          if (recordingItems.size() > 0) {
            getAttachedView().showData((ArrayList<RecordingItem>) recordingItems);
          } else {
            getAttachedView().setRecordingListInVisible();
            getAttachedView().setEmptyLabelVisible();
          }
        }));
  }
}
