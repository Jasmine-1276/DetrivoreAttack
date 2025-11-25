//this code was not written by me!! I followed https://www.baeldung.com/java-play-sound 
// as well as some others for additional functionality.

import java.io.*;
import java.net.URL;
import javax.sound.sampled.*;

public class sfx {
    private byte[] data;
    private AudioFormat format;
    private boolean fp = true;

    public sfx(String path) {
        try {
            URL url = getClass().getClassLoader().getResource(path);
            if (url == null) {
                throw new FileNotFoundException("Sound not found: " + path);
            }

            AudioInputStream ais = AudioSystem.getAudioInputStream(url);

            format = ais.getFormat();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[4096];
            int read;
            while ((read = ais.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }

            data = baos.toByteArray();
            ais.close();

            play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(format, data, 0, data.length);
            if (fp) {
            	fp = false;
            } else {
            clip.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
