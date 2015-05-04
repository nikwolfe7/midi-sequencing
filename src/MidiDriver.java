import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import org.apache.commons.lang3.StringUtils;

public class MidiDriver {

  private static String MIDI_classical = "MIDI_classical";

  private static String MIDI_hip_hop_rap = "MIDI_hip_hop_rap";

  private static String MIDI_rock_metal_country = "MIDI_rock_metal_country";

  private static String MIDICheck = "MIDICheck";

  private static String sep = System.getProperty("file.separator");

  public static final int NOTE_ON = 0x90;

  public static final int NOTE_OFF = 0x80;

  public static final String[] NOTE_NAMES = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A",
      "A#", "B" };

  public static void main(String[] args) throws Exception {
    processFolder("test");
    processFolder(MIDI_classical);
    processFolder(MIDI_hip_hop_rap);
    processFolder(MIDI_rock_metal_country);
    processFolder(MIDICheck);
  }

  private static void processFolder(String folder) throws Exception {
    List<String> files = getFilesInDir(folder);
    for (String filename : files) {
      File file = new File(filename);
      Map<Long, ArrayList<Integer>> timesteps = doSequencing(file);
      printToFile(timesteps, filename);
    }
  }

  private static List<String> getFilesInDir(String directory) throws IOException {
    List<String> files = new LinkedList<String>();
    Files.walk(Paths.get(directory)).forEach(filePath -> {
      if (Files.isRegularFile(filePath)) {
        files.add(filePath.toString());
      }
    });
    ListIterator<String> iter = files.listIterator();
    while (iter.hasNext()) {
      String file = iter.next();
      if (!file.toLowerCase().endsWith(".mid")) {
        // System.out.println(file);
        iter.remove();
      }
    }
    return files;
  }

  private static void printToFile(Map<Long, ArrayList<Integer>> tracks, String filename)
          throws IOException {
    filename = filename.replace(".mid", ".txt");
    File file = new File(filename);
    FileWriter writer = new FileWriter(file);
    LinkedList<Entry<Long, ArrayList<Integer>>> results = new LinkedList<Map.Entry<Long, ArrayList<Integer>>>(
            tracks.entrySet());
    Collections.sort(results, new Comparator<Map.Entry<Long, ArrayList<Integer>>>() {
      public int compare(Entry<Long, ArrayList<Integer>> o1, Entry<Long, ArrayList<Integer>> o2) {
        return o1.getKey().compareTo(o2.getKey());
      }
    });
    for (Entry<Long, ArrayList<Integer>> elem : results) {
      ArrayList<Integer> intList = elem.getValue();
      Collections.sort(intList, new Comparator<Integer>() {
        public int compare(Integer o1, Integer o2) {
          return o1.compareTo(o2);
        }
      });
      String joinedStr = StringUtils.join(intList, "_");
      // System.out.println(joinedStr);
      writer.write(joinedStr + "\n");
    }
    writer.close();
  }

  public static Map<Long, ArrayList<Integer>> doSequencing(File file) throws Exception {
    return readSequence(getSequence(file));
  }

  public static Map<Long, ArrayList<Integer>> readSequence(Sequence seq) {
    int trackNumber = 0;
    Map<Long, ArrayList<Integer>> map = new HashMap<Long, ArrayList<Integer>>();
    if (seq != null) {
      for (Track track : seq.getTracks()) {
        trackNumber++;
        System.out.println("Track: " + trackNumber + ": size = " + track.size() + "\n");
        for (int i = 0; i < track.size(); i++) {
          MidiEvent event = track.get(i);
          MidiMessage message = event.getMessage();
          if (message instanceof ShortMessage) {
            ShortMessage sm = (ShortMessage) message;
            int key = sm.getData1();
            int octave = (key / 12) - 1;
            int note = key % 12;
            String noteName = NOTE_NAMES[note];
            int velocity = sm.getData2();
            String noteStatus = "";
            if (sm.getCommand() == NOTE_OFF) {
              noteStatus = "note_off";
            } else if (sm.getCommand() == NOTE_ON) {
              noteStatus = "note_on";
            } else {
              // System.out.println("Command: " + sm.getCommand());
            }
            if (noteStatus != "" && velocity > 0) {
              String noteWord = "ch" + sm.getChannel() + "_" + noteStatus + "_" + noteName + octave
                      + "_key" + key;// + "_vel" + velocity;
              if (map.containsKey(event.getTick())) {
                map.get(event.getTick()).add(key);
              } else {
                map.put(event.getTick(), new ArrayList<Integer>());
                map.get(event.getTick()).add(key);
              }
              // System.out.print("\nt=" + event.getTick() + ": " + printable);
              // System.out.print("\nt=" + event.getTick() + ": " + noteWord);
            }
          }
        }
        // System.out.println();
      }
    }
    return map;
  }

  /**
   * Read a Sequence object from the given file.
   * 
   * @param file
   *          the file from which to read the Sequence
   * @return the Sequence object
   * @throws MidiUnavailableException
   * @throws InvalidMidiDataException
   *           if we were unable to read the Sequence
   * @throws IOException
   *           if an I/O error happened while reading
   */
  public static Sequence getSequence(File file) {
    try {
      return MidiSystem.getSequence(file);
    } catch (InvalidMidiDataException e) {
      System.out.println("File: " + file.toString());
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("File: " + file.toString());
      e.printStackTrace();
    }
    return null;
  }

}
