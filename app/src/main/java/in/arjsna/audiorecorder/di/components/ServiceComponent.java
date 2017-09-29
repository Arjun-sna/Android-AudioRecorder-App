package in.arjsna.audiorecorder.di.components;

import dagger.Component;
import in.arjsna.audiorecorder.di.modules.ServiceModule;
import in.arjsna.audiorecorder.di.scopes.ServiceScope;
import in.arjsna.audiorecorder.recordingservice.AudioRecordService;

@ServiceScope
@Component(dependencies = ApplicationComponent.class, modules = ServiceModule.class)
public interface ServiceComponent {
  void inject(AudioRecordService audioRecordService);
}
