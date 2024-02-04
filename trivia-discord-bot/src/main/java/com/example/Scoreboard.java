package com.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Scoreboard {
    private static Map<String, Integer> scores = new HashMap<String, Integer>();
    private static final String FILE_PATH = "scoreboard.txt";

    /**
     * Represents a scoreboard for keeping track of player scores.
     */
    public Scoreboard() {
        loadScoresFromFile(FILE_PATH);
    }

    /**
     * Returns a string representation of all the scores in the scoreboard.
     *
     * @return a string containing all the scores in the format "playerName: score"
     */
    public static String getAllScores() {
        loadScoresFromFile(FILE_PATH);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Increments the score for the specified username.
     * If the username already exists in the scores map, the score is incremented by
     * 1.
     * If the username does not exist, a new entry is created with a score of 1.
     * The updated scores are then saved to a file.
     *
     * @param username the username for which the score should be incremented
     * @return the updated score for the specified username
     */
    public int incrementScore(String username) {
        int currentScore = 0;
        if (scores.containsKey(username)) {
            currentScore = scores.get(username);
            currentScore++;
            scores.put(username, currentScore);
        } else {
            currentScore = 1;
            scores.put(username, currentScore);
        }
        saveScoresToFile(FILE_PATH);
        return currentScore;
    }

    /**
     * Saves the scores to a file.
     * 
     * @param filePath the path of the file to save the scores to
     */
    private void saveScoresToFile(String filePath) {
        try {
            File file = new File(filePath);
            FileWriter writer = new FileWriter(file);
            for (Map.Entry<String, Integer> entry : scores.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads scores from a file and updates the scoreboard.
     *
     * @param filePath the path to the file containing the scores
     */
    private static void loadScoresFromFile(String filePath) {
        try {
            Path path = Path.of(filePath);
            if (Files.exists(path)) {
                Files.lines(path).forEach(line -> {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        String username = parts[0];
                        int score = Integer.parseInt(parts[1]);
                        scores.put(username, score);
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
