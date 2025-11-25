//this code was not written by me!! I followed and edited https://stackoverflow.com/a/2433454.
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import javax.sound.sampled.*;

public class bgm {

    private static final int BUFFER_SIZE = 4096;

    private AudioInputStream[] streams;
    private SourceDataLine line;
    private boolean[] activeTrack;
    private boolean running = true;
    private int numTracks;
    private ArrayList<String> Slist = new ArrayList<>();

    private int volume = 5;

    public bgm() {
        Slist.add("sounds/bgm0.wav");
        Slist.add("sounds/bgm1.wav");
        Slist.add("sounds/bgm5.wav");
        Slist.add("sounds/bgm10.wav");
        Slist.add("sounds/bgm20.wav");
        Slist.add("sounds/bgm50.wav");
        numTracks = Slist.size();
        streams = new AudioInputStream[numTracks];
        activeTrack = new boolean[numTracks];

        try {
            for (int i = 0; i < numTracks; i++) {
                URL url = getClass().getClassLoader().getResource(Slist.get(i));
                if (url == null) throw new RuntimeException("Cannot find resource: " + Slist.get(i));
                AudioInputStream original = AudioSystem.getAudioInputStream(url);
                AudioFormat base = original.getFormat();
                AudioFormat target = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        base.getSampleRate(),
                        16,
                        base.getChannels(),
                        base.getChannels() * 2,
                        base.getSampleRate(),
                        false
                );
                streams[i] = AudioSystem.getAudioInputStream(target, original);
            }

            AudioFormat format = streams[0].getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Thread t = new Thread(this::streamLoop);
        t.setDaemon(true);
        t.start();

        for (int i = 0; i < numTracks; i++) setActiveTrack(i);
        setActiveTrack(0);
    }

    private void streamLoop() {
        byte[] buffer = new byte[BUFFER_SIZE];
        while (running) {
            for (int i = 0; i < numTracks; i++) {
                if (!activeTrack[i]) continue;
                try {
                    int bytesRead = streams[i].read(buffer, 0, BUFFER_SIZE);
                    if (bytesRead == -1) {
                        streams[i].close();
                        URL url = getClass().getClassLoader().getResource(Slist.get(i));
                        AudioInputStream original = AudioSystem.getAudioInputStream(url);
                        AudioFormat base = original.getFormat();
                        AudioFormat target = new AudioFormat(
                                AudioFormat.Encoding.PCM_SIGNED,
                                base.getSampleRate(),
                                16,
                                base.getChannels(),
                                base.getChannels() * 2,
                                base.getSampleRate(),
                                false
                        );
                        streams[i] = AudioSystem.getAudioInputStream(target, original);
                        bytesRead = streams[i].read(buffer, 0, BUFFER_SIZE);
                    }
                    if (bytesRead > 0) {
                        applyVolume(buffer, bytesRead);
                        line.write(buffer, 0, bytesRead);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void applyVolume(byte[] buf, int len) {
        float mul = volume / 10.0f;
        for (int i = 0; i < len - 1; i += 2) {
            int sample = (buf[i + 1] << 8) | (buf[i] & 0xff);
            sample = (int)(sample * mul);
            buf[i]     = (byte)(sample & 0xff);
            buf[i + 1] = (byte)((sample >> 8) & 0xff);
        }
    }

    public void setVolume(int v) {
        volume = v;
    }

    public void setActiveTrack(int index) {
        for (int i = 0; i < activeTrack.length; i++) {
            activeTrack[i] = (i == index);
        }
    }

    public void stopAll() {
        running = false;
        if (line != null) {
            line.stop();
            line.close();
        }
        for (AudioInputStream stream : streams) {
            try { if (stream != null) stream.close(); } catch (IOException ignored) {}
        }
    }
}
