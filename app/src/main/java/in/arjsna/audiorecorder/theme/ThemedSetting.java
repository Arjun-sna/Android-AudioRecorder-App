package in.arjsna.audiorecorder.theme;

class ThemedSetting {

  private ThemedActivity activity;

  ThemedSetting(ThemedActivity activity) {
    this.activity = activity;
  }

  public ThemedSetting() {
  }

  public ThemedActivity getActivity() {
    return activity;
  }
}
