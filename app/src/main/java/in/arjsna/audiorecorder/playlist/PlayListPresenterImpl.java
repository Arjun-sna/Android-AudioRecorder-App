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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class PlayListPresenterImpl<V extends PlayListMVPView> extends BasePresenter<V>
    implements PlayListPresenter<V> {
  @Inject
  public RecordItemDataSource recordItemDataSource;

  private int currentPlayingItem;
  private boolean isAudioPlaying = false;
  private boolean isAudioPaused = false;
  private List<RecordingItem> recordingItems = new ArrayList<>();

  @Inject
  public PlayListPresenterImpl(CompositeDisposable compositeDisposable) {
    super(compositeDisposable);
  }

  @Override public void onViewInitialised() {
    fillAdapter();
    getAttachedView().startWatchingForFileChanges();
  }

  @Override public void renameFile(int adapterPosition, String value) {
    rename(recordingItems.get(adapterPosition), adapterPosition, value).subscribe(
        new SingleObserver<Integer>() {
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

  @Override public void deleteFile(int position) {
    removeFile(recordingItems.get(position), position).subscribe(new SingleObserver<Integer>() {
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

  @Override public void onListItemClicked(int position, RecordingItem recordingItem) {
    try {
      if (isAudioPlaying) {
        if (currentPlayingItem == position) {
          if (isAudioPaused) {
            isAudioPaused = false;
            getAttachedView().resumeMediaPlayer(position);
          } else {
            isAudioPaused = true;
            getAttachedView().pauseMediaPlayer(position);
          }
        } else {
          getAttachedView().stopMediaPlayer(currentPlayingItem);
          getAttachedView().startMediaPlayer(position, recordingItem);
          currentPlayingItem = position;
        }
      } else {
        isAudioPlaying = true;
        getAttachedView().startMediaPlayer(position, recordingItem);
        currentPlayingItem = position;
      }
    } catch (IOException e) {
      getAttachedView().showError("Failed to start media Player");
    }
  }

  @Override public RecordingItem getListItemAt(int position) {
    return recordingItems.get(position);
  }

  @Override public void onListItemClick(int position) {

  }

  @Override public void onListItemLongClick(int position) {
    getAttachedView().showFileOptionDialog(position, recordingItems.get(position));
  }

  @Override public int getListItemCount() {
    return recordingItems.size();
  }

  @Override public void shareFileClicked(int position) {
    getAttachedView().shareFileDialog(recordingItems.get(position).getFilePath());
  }

  @Override public void renameFileClicked(int position) {
    getAttachedView().showRenameFileDialog(position);
  }

  @Override public void deleteFileClicked(int position) {
    getAttachedView().showDeleteFileDialog(position);
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
    getAttachedView().stopMediaPlayer(currentPlayingItem);
    super.onDetach();
  }

  private void fillAdapter() {
    getCompositeDisposable().add(recordItemDataSource.getAllRecordings()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe((recordingItems) -> {
          if (recordingItems.size() > 0) {
            this.recordingItems.addAll(recordingItems);
            getAttachedView().notifyListAdapter();
          } else {
            getAttachedView().setRecordingListInVisible();
            getAttachedView().setEmptyLabelVisible();
          }
        }));
  }
}
