package in.arjsna.audiorecorder.playlist;

import android.os.Environment;
import in.arjsna.audiorecorder.db.RecordItemDataSource;
import in.arjsna.audiorecorder.db.RecordingItem;
import in.arjsna.audiorecorder.mvpbase.BasePresenter;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.util.ArrayList;
import javax.inject.Inject;

public class PlayListPresenterImpl<V extends PlayListMVPView> extends BasePresenter<V>
    implements PlayListPresenter<V> {
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

  @Override public void renameFile(RecordingItem recordingItem, int adapterPosition, String value) {
    rename(recordingItem, adapterPosition, value).subscribe(new SingleObserver<Integer>() {
      @Override public void onSubscribe(Disposable d) {

      }

      @Override public void onSuccess(Integer position) {
        getAttachedView().notifyListItemChange(position);
      }

      @Override public void onError(Throwable e) {
        getAttachedView().showError(e.getMessage());
      }
    });
  }

  private Single<Integer> rename(RecordingItem recordingItem, int adapterPosition, String name) {
    return Single.create((SingleOnSubscribe<Integer>) e -> {
      File newFile = new File(
          Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundRecorder/" + name);
      if (newFile.exists() && !newFile.isDirectory()) {
        e.onError(new Exception("File with same name already exists"));
      } else {
        File oldFilePath = new File(recordingItem.getFilePath());
        if (oldFilePath.renameTo(newFile)) {
          recordingItem.setName(name);
          recordingItem.setFilePath(newFile.getPath());
          recordItemDataSource.updateRecordItem(recordingItem);
          e.onSuccess(adapterPosition);
        } else {
          e.onError(new Throwable("Cannot Rename file. Please try again"));
        }
      }
    }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
  }

  @Override public void deleteFile(RecordingItem recordingItem, int position) {
    removeFile(recordingItem, position).subscribe(new SingleObserver<Integer>() {
      @Override public void onSubscribe(Disposable d) {

      }

      @Override public void onSuccess(Integer position) {
        getAttachedView().notifyListItemRemove(position);
      }

      @Override public void onError(Throwable e) {
        getAttachedView().showError(e.getMessage());
      }
    });
  }

  private Single<Integer> removeFile(RecordingItem recordingItem, int position) {
    return Single.create((SingleOnSubscribe<Integer>) e -> {
      File file = new File(recordingItem.getFilePath());
      if (file.delete()) {
        recordItemDataSource.deleteRecordItem(recordingItem);
        e.onSuccess(position);
      } else {
        e.onError(new Exception("File deletion failed"));
      }
    }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
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
