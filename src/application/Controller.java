package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

/**
 * Controller class for String Searching algorithm program in JavaFX.
 *
 * @author Sanjeev Viswan
 * @version 1.0
 */
public class Controller {

  @FXML
  private Text resultDisplay;

  @FXML
  private Text fileDisplay;

  @FXML
  private TextArea textInput;

  @FXML
  private TextField patternInput;

  private String sequence;
  private String pattern;

  private static final int BASE = 113; // For Rabin-Karp Hash Calculation


  /**
   * Triggers Naive (brute-force) search.
   *
   * @param e On click of button.
   */
  public void naive(ActionEvent e) {
    sequence = textInput.getText();
    pattern = patternInput.getText();

    if (validateInput(pattern, sequence)) {
      naiveSearch(pattern, sequence);
    }

  }

  /**
   * Triggers Knuth-Morris-Pratt search.
   *
   * @param e On click of button.
   */
  public void kmp(ActionEvent e) {
    sequence = textInput.getText();
    pattern = patternInput.getText();

    if (validateInput(pattern, sequence)) {
      kmpSearch(pattern, sequence);
    }
  }

  /**
   * Triggers Boyer-moore search.
   *
   * @param e On click of button.
   */
  public void boyerMoore(ActionEvent e) {

    sequence = textInput.getText();
    pattern = patternInput.getText();

    if (validateInput(pattern, sequence)) {
      boyerMooreSearch(pattern, sequence);
    }
  }

  /**
   * Triggers Rabin-Karp search.
   *
   * @param e On click of button.
   */
  public void rabinKarp(ActionEvent e) {
    sequence = textInput.getText();
    pattern = patternInput.getText();

    if (validateInput(pattern, sequence)) {
      rabinKarpSearch(pattern, sequence);
    }

  }


  /**
   * Empty/null pattern and sequence checker.
   *
   * @param pattern  Pattern to search for.
   * @param sequence Text to search.
   * @return true if inputs are valid, false otherwise.
   */
  public boolean validateInput(String pattern, String sequence) {
    if (sequence == null || pattern == null || sequence.length() == 0 || pattern.length() == 0) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setHeaderText("Input not valid");
      errorAlert.setContentText("The sequence and pattern fields must be filled.");
      errorAlert.showAndWait();
      return false;
    } else if (pattern.length() > sequence.length()) {
      Alert errorAlert = new Alert(Alert.AlertType.ERROR);
      errorAlert.setHeaderText("Input not valid");
      errorAlert.setContentText("Target pattern cannot be longer than text.");
      errorAlert.showAndWait();
      return false;
    }

    return true;
  }

  /**
   * Naive (brute-force) search algorithm for strings.
   *
   * @param pattern  Pattern to search for.
   * @param sequence Text to search.
   */
  public void naiveSearch(String pattern, String sequence) {
    List<Integer> foundOccurrences = new ArrayList<>();
    int comparisons = 0;
    int matches = 0;
    for (int i = 0; i <= sequence.length() - pattern.length(); i++) {
      for (int j = 0; j < pattern.length(); j++) {
        comparisons++;
        if (pattern.charAt(j) != sequence.charAt(i + j)) {
          break;
        }

        if (j == pattern.length() - 1) {
          foundOccurrences.add(i);
          matches++;
        }
      }
    }
    updateNaiveResults(matches, comparisons, foundOccurrences, pattern, sequence);

  }

  /**
   * File output and GUI display of Naive (brute-force) search results.
   *
   * @param matches     Amount of matches.
   * @param comparisons Amount of comparisons made.
   * @param occurrences List of matching indices.
   * @param pattern     Pattern to search for.
   * @param sequence    Searched text.
   */
  public void updateNaiveResults(int matches, int comparisons, List<Integer> occurrences,
                                 String pattern, String sequence) {
    resultDisplay.setText("Found " + matches + " matches in " + comparisons + " comparisons.");
    if (matches > 0) {
      try {
        File output = new File("matches.txt");
        BufferedWriter bw = new BufferedWriter(
                new FileWriter(output));
        bw.write("Found " + matches + " matches in " + comparisons + " comparisons using "
                + "Naive (brute-force) searching. \n");
        bw.write("Pattern: " + pattern + "\n");
        bw.write("Text: " + sequence + "\n\n");
        for (Integer occurrence : occurrences) {
          bw.write("Match at index " + occurrence + "!\n");
        }
        fileDisplay.setText("Results saved to " + output.getAbsolutePath());
        bw.close();
      } catch (IOException e) {
        e.printStackTrace();
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText("File write error");
        errorAlert.setContentText("An error occurred while writing the output file.");
        errorAlert.showAndWait();
      }
    } else {
      fileDisplay.setText(":(");
    }
  }

  /**
   * Knuth-Morris-Pratt search algorithm for strings, using a Failure Table.
   *
   * @param pattern  Pattern to search for.
   * @param sequence Text to search.
   */
  public void kmpSearch(String pattern, String sequence) {
    List<Integer> foundOccurrences = new ArrayList<>();
    int[] failureTable = buildFailureTable(pattern);
    int j = 0;
    int k = 0;
    int matches = 0;
    int comparisons = 0;

    while (k < sequence.length()) {
      comparisons++;

      if (pattern.charAt(j) == (sequence.charAt(k))) {
        j++;
        k++;
        if (j == pattern.length()) {
          matches++;
          foundOccurrences.add(k - j);
          j = failureTable[j - 1];
        }
      } else if (j == 0) {
        k++;
        if (sequence.length() - k < pattern.length()) {
          updatekmpResults(matches, comparisons, foundOccurrences, pattern, sequence, failureTable);
          return;
        }
      } else {
        j = failureTable[j - 1];
      }
    }
    updatekmpResults(matches, comparisons, foundOccurrences, pattern, sequence, failureTable);
  }

  /**
   * File output and GUI display of Knuth-Morris-Pratt search results.
   *
   * @param matches      Amount of matches.
   * @param comparisons  Amount of comparisons made.
   * @param occurrences  List of matching indices.
   * @param pattern      Pattern to search for.
   * @param sequence     Text being searched.
   * @param failureTable KMP Failure Table.
   */
  public void updatekmpResults(int matches, int comparisons, List<Integer> occurrences,
                               String pattern, String sequence, int[] failureTable) {
    resultDisplay.setText("Found " + matches + " matches in " + comparisons + " comparisons.");
    if (matches > 0) {
      try {
        File output = new File("matches.txt");
        BufferedWriter bw = new BufferedWriter(
                new FileWriter(output));
        bw.write("Found " + matches + " matches in " + comparisons + " comparisons using "
                + "Knuth-Morris-Pratt searching. \n");
        bw.write("Pattern: " + pattern + "\n");
        bw.write("Text: " + sequence + "\n\n");
        bw.write("Knuth-Morris-Pratt Failure Table:\n");
        for (int i = 0; i < pattern.length(); i++) {
          bw.write(pattern.charAt(i) + ": " + failureTable[i] + "\n");
        }
        bw.write("\n");
        for (Integer occurrence : occurrences) {
          bw.write("Match at index " + occurrence + "!\n");
        }
        fileDisplay.setText("Results saved to " + output.getAbsolutePath());
        bw.close();
      } catch (IOException e) {
        e.printStackTrace();
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText("File write error");
        errorAlert.setContentText("An error occurred while writing the output file.");
        errorAlert.showAndWait();
      }
    } else {
      fileDisplay.setText(":(");
    }
  }


  /**
   * Build Failure Table for Knuth-Morris-Pratt pattern searching.
   *
   * @param pattern Pattern to search for.
   * @return Array of KMP Failure Table values for given pattern.
   */
  public static int[] buildFailureTable(String pattern) {
    int[] failureTable = new int[pattern.length()];
    int i = 0;
    int j = 1;
    while (j < pattern.length()) {
      if (pattern.charAt(i) == pattern.charAt(j)) {
        failureTable[j] = i + 1;
        i++;
        j++;
      } else if (i == 0) {
        failureTable[j] = 0;
        j++;
      } else {
        i = failureTable[i - 1];
      }
    }
    return failureTable;
  }

  /**
   * Boyer-Moore search algorithm for strings, using a Last Occurrence Table.
   *
   * @param pattern  Pattern to search for.
   * @param sequence Text to search.
   */
  public void boyerMooreSearch(String pattern, String sequence) {
    List<Integer> foundOccurrences = new ArrayList<>();
    int matches = 0;
    int comparisons = 0;

    HashMap<Character, Integer> last = (HashMap<Character, Integer>) buildLastTable(pattern);

    int i = 0;
    while (i <= sequence.length() - pattern.length()) {
      int j = pattern.length() - 1;
      while (j >= 0 && (sequence.charAt(i + j) == pattern.charAt(j))) {
        comparisons++;
        j--;
      }
      if (j == -1) {
        foundOccurrences.add(i);
        matches++;
        i++;
      } else {
        int shiftValue = last.getOrDefault(sequence.charAt(i + j), -1);
        if (shiftValue < j) {
          i = i + j - shiftValue;
        } else {
          i++;
        }
      }
    }

    updateBoyerResults(matches, comparisons, foundOccurrences, pattern, sequence, last);
  }

  /**
   * File output and GUI display of Boyer-Moore search results.
   *
   * @param matches     Amount of matches.
   * @param comparisons Amount of comparisons made.
   * @param occurrences List of matching indices.
   * @param pattern     Pattern to search for.
   * @param sequence    Text being searched.
   * @param lastTable   Last Occurrence Table.
   */
  public void updateBoyerResults(int matches, int comparisons, List<Integer> occurrences,
                                 String pattern, String sequence,
                                 Map<Character, Integer> lastTable) {
    resultDisplay.setText("Found " + matches + " matches in " + comparisons + " comparisons.");
    if (matches > 0) {
      try {
        File output = new File("matches.txt");
        BufferedWriter bw = new BufferedWriter(
                new FileWriter(output));
        bw.write("Found " + matches + " matches in " + comparisons + " comparisons using "
                + "Boyer-Moore searching. \n");
        bw.write("Pattern: " + pattern + "\n");
        bw.write("Text: " + sequence + "\n\n");
        bw.write("Boyer-Moore Last Occurrence Table:\n");
        Set<Character> keys = lastTable.keySet();
        for (Character key : keys) {
          bw.write(key + ": " + lastTable.get(key) + "\n");
        }
        bw.write("\n");
        for (Integer occurrence : occurrences) {
          bw.write("Match at index " + occurrence + "!\n");
        }
        fileDisplay.setText("Results saved to " + output.getAbsolutePath());
        bw.close();
      } catch (IOException e) {
        e.printStackTrace();
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText("File write error");
        errorAlert.setContentText("An error occurred while writing the output file.");
        errorAlert.showAndWait();
      }
    } else {
      fileDisplay.setText(":(");
    }
  }

  /**
   * Constructs Last Occurrence Table for Boyer-Moore searching.
   *
   * @param pattern Pattern to search for.
   * @return HashMap of Last Occurrence Table.
   */
  public static Map<Character, Integer> buildLastTable(String pattern) {
    HashMap last = new HashMap(pattern.length());
    for (int i = 0; i < pattern.length(); i++) {
      last.put(pattern.charAt(i), i);
    }
    return last;
  }


  /**
   * Rabin-Karp search algorithm for strings, using a rolling hash.
   *
   * @param pattern  Pattern to search for.
   * @param sequence Text to search.
   */
  public void rabinKarpSearch(CharSequence pattern,
                              CharSequence sequence) {

    int patternLength = pattern.length();
    List<Integer> foundOccurrences = new ArrayList<>();


    int patternHash = initialHash(pattern);
    int textHash = initialHash(sequence.subSequence(0, pattern.length()));
    int matches = 0;
    int comparisons = 0;

    // Exception for match at index 0
    if (textHash == patternHash) {
      int textCheckIndex = 0;
      while (textCheckIndex < patternLength) {
        comparisons++;
        if (sequence.charAt(textCheckIndex) == pattern.charAt(textCheckIndex)) {
          textCheckIndex++;
        } else {
          break;
        }
      }
      if (textCheckIndex == patternLength) {
        foundOccurrences.add(0);
        matches++;
      }
    }

    for (int i = 1; i <= sequence.length() - patternLength; i++) {
      textHash = (textHash - (sequence.charAt(i - 1) * (int) Math.pow(BASE, pattern.length() - 1)))
              * BASE + sequence.charAt(i + patternLength - 1);
      if (textHash == patternHash) {
        int textCheckIndex = 0;
        while (textCheckIndex < patternLength) {
          comparisons++;
          if (sequence.charAt(i + textCheckIndex) == pattern.charAt(textCheckIndex)) {
            textCheckIndex++;
          } else {
            break;
          }
        }
        if (textCheckIndex == patternLength) {
          foundOccurrences.add(i);
          matches++;
        }
      }
    }
    updateRabinKarpResults(matches, comparisons, patternHash, foundOccurrences, pattern, sequence);
  }

  /**
   * Computes initial Rabin-Karp hash for given input text.
   *
   * @param text Text to compute initial Rabin-Karp hash for.
   * @return Initial Rabin-Karp hash of input text.
   */
  private int initialHash(CharSequence text) {
    int hash = 0;
    for (int i = 0; i < text.length(); i++) {
      hash += text.charAt(i) * (int) Math.pow(BASE, text.length() - 1 - i);
    }
    return hash;
  }

  /**
   * File output and GUI display of Rabin-Karp search results.
   *
   * @param matches     Amount of matches.
   * @param comparisons Amount of comparisons made.
   * @param patternHash Rabin-Karp calculated hash of pattern.
   * @param occurrences List of matching indices.
   * @param pattern     Pattern to search for.
   * @param sequence    Text being searched.
   */
  public void updateRabinKarpResults(int matches, int comparisons, int patternHash,
                                     List<Integer> occurrences, CharSequence pattern,
                                     CharSequence sequence) {
    resultDisplay.setText("Found " + matches + " matches in " + comparisons + " comparisons.");
    if (matches > 0) {
      try {
        File output = new File("matches.txt");
        BufferedWriter bw = new BufferedWriter(
                new FileWriter(output));
        bw.write("Found " + matches + " matches in " + comparisons + " comparisons using "
                + "Rabin-Karp searching. \n");
        bw.write("Pattern: " + pattern + "\n");
        bw.write("Text: " + sequence + "\n\n");
        bw.write("Rabin-Karp Pattern Hash: " + patternHash + "\n\n");
        for (Integer occurrence : occurrences) {
          bw.write("Match at index " + occurrence + "!\n");
        }
        fileDisplay.setText("Results saved to " + output.getAbsolutePath());
        bw.close();
      } catch (IOException e) {
        e.printStackTrace();
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText("File write error");
        errorAlert.setContentText("An error occurred while writing the output file.");
        errorAlert.showAndWait();
      }
    } else {
      fileDisplay.setText(":(");
    }
  }
}
