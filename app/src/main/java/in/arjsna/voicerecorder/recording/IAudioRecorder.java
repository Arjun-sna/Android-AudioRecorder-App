package in.arjsna.voicerecorder.recording;

/**
 * Interface for audio recorder
 */
interface IAudioRecorder {
    void startRecord();
    void finishRecord();
    boolean isRecording();
}
