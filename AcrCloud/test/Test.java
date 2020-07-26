import java.io.*;
import java.util.*;

import com.acrcloud.utils.ACRCloudRecognizer;
import recorder.JavaSoundRecorder;
import javaMail.JavaMailUtil;
import java.time.LocalTime;

import org.json.*;




public class Test {

    public static void main(String[] args) {
      int count = 0;
      String lastSong = "";
      boolean sendGuard = false;
      
      String log = "";
      LocalTime startTime = LocalTime.of(9, 00);
      LocalTime endTime = LocalTime.of(17, 00);
      LocalTime currentTime = java.time.LocalTime.now();
      final JavaSoundRecorder recorder = new JavaSoundRecorder();
      
      //Setup the song api
      
      Map<String, Object> config = new HashMap<String, Object>();
      Set<String> songs = new HashSet<String>();
      
      String destinationAddress = "ENTER YOUR DESTINATION EMAIL ADDRESS HERE";

      // replace "XXXXXXXX" with your project's host, access_key and access_secret
      config.put("host", "XXXXXXXX");
      config.put("access_key", "XXXXXXXX");
      config.put("access_secret", "XXXXXXXX");
      
      config.put("debug", false);
      config.put("timeout", 10); // seconds

      ACRCloudRecognizer re = new ACRCloudRecognizer(config);
      
      while (currentTime.isBefore(startTime))
      {
      currentTime = java.time.LocalTime.now();
      try {
        System.out.print("Waiting to start");
        Thread.sleep(25000);
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }
      }
      while (currentTime.isBefore(endTime)) {//The time is between 9-5
        if (count > 2000)
        {
          JavaMailUtil.sendMail(destinationAddress, "API OVERLOAD!!", currentTime.toString(), log);
          break;
        }
        try {
          Thread.sleep(25000);
        } catch (InterruptedException ex) {
          ex.printStackTrace();
        }
          // creates a new thread that waits for a specified
          // of time before stopping
          Thread stopper = new Thread(new Runnable() {
              public void run() {
                  try {
                      Thread.sleep(recorder.RECORD_TIME);
                  } catch (InterruptedException ex) {
                      ex.printStackTrace();
                  }
                  recorder.finish();
              }
          });

          stopper.start();

          // start recording
          recorder.start();
          
          String result = re.recognizeByFile("test/Recording.m4a", 0);
          count ++;
          JSONObject obj = new JSONObject(result);
          JSONObject status = obj.getJSONObject("status");
          int code = status.getInt("code");
          currentTime = java.time.LocalTime.now();
          if (code == 0) // if it's a song.
          {
            JSONObject meta = obj.getJSONObject("metadata");
            JSONArray arr = meta.getJSONArray("music");
            String title = arr.getJSONObject(0).getString("title");
            JSONArray artists = arr.getJSONObject(0).getJSONArray("artists");
            String artist = artists.getJSONObject(0).getString("name");
            String currentSong = title + ", " + artist;
            if (songs.contains(currentSong) == false)
            {
              if (Objects.equals(currentSong, lastSong) == true) // if this is the first time hearing the song
              {
                songs.add(currentSong);
              }
              lastSong = currentSong;
              sendGuard = false;
            }
            else // if the song is in the list
            {
              if (Objects.equals(currentSong, lastSong) == false)
              {
                lastSong = currentSong;
                sendGuard = true;
              }
              else if (sendGuard == true && Objects.equals(currentSong, lastSong) == true)
              {
                JavaMailUtil.sendMail(destinationAddress, currentSong, currentTime.toString(), log);
                lastSong = currentSong;
                sendGuard = false;
              }
              else if (sendGuard == true)
              {
                sendGuard = false;
              }
            }
            System.out.println(currentSong);
            log += currentSong + "@" + currentTime.toString() + ", ";
          }
          
         
      }
      
      }
}