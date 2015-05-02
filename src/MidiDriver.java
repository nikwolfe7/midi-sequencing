import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

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
    File file = new File("." + sep + MIDI_classical + sep + "Bach" + sep
            + "Bwv0565-Toccata-and-Fugue-In-Dm-A.mid");
    doSequencing(file);
  }

  public static void doSequencing(File file) throws Exception {
    readSequence(getSequence(file));
  }

  public static void readSequence(Sequence seq) {
    int trackNumber = 0;
    for (Track track : seq.getTracks()) {
      trackNumber++;
      System.out.println("Track: " + trackNumber + ": size = " + track.size() + "\n");
      for (int i = 0; i < track.size(); i++) {
        MidiEvent event = track.get(i);
        MidiMessage message = event.getMessage();
        System.out.print("t=" + event.getTick() + ": ");
        if (message instanceof ShortMessage) {
          ShortMessage sm = (ShortMessage) message;
          System.out.print("Channel: " + sm.getChannel() + " ");
          int key = sm.getData1();
          int octave = (key / 12) - 1;
          int note = key % 12;
          String noteName = NOTE_NAMES[note];
          int velocity = sm.getData2();
          String noteStatus = "";
          if (sm.getCommand() == NOTE_OFF) {
            noteStatus = "NOTE_OFF";
          } else if (sm.getCommand() == NOTE_ON) {
            noteStatus = "NOTE_ON";
          } else {
            System.out.println("Command: " + sm.getCommand());
          }
          if (noteStatus != "") {
            System.out.println(noteStatus + " " + noteName + octave + " key = " + key
                    + " veloctiy: " + velocity);
          }
        }
      }
      System.out.println();
    }
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
  public static Sequence getSequence(File file) throws Exception {
    return MidiSystem.getSequence(file);
  }

}
