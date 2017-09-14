package in.arjsna.audiorecorder.di.components;

import dagger.Component;
import in.arjsna.audiorecorder.di.PerService;
import in.arjsna.audiorecorder.di.modules.ServiceModule;
import in.arjsna.audiorecorder.recordingservice.AudioRecordService;

@PerService
@Component(dependencies = ApplicationComponent.class, modules = ServiceModule.class)
public interface ServiceComponent {
  void inject(AudioRecordService audioRecordService);
}
