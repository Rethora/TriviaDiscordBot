package com.example;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import discord4j.core.object.entity.channel.MessageChannel;
import com.fasterxml.jackson.databind.JsonNode;

public class Game {
    private Question question;
    private final MessageChannel channel;
    private HashMap<String, String> args;
    private Scoreboard scoreboard;

    /**
     * Represents a game of trivia.
     * 
     * @param channel the Discord message channel where the game is played
     * @param args    the command line arguments passed to the game
     */
    public Game(MessageChannel channel, String[] args) {
        this.channel = channel;
        this.scoreboard = new Scoreboard();
        this.args = parseArgs(args);
    }

    public void start() {
        getNewQuestion();
    }

    /**
     * Parses the command line arguments and stores them in a map.
     *
     * @param args the command line arguments to parse
     */
    private HashMap<String, String> parseArgs(String[] args) {
        HashMap<String, String> argsMap = new HashMap<String, String>();
        for (String arg : args) {
            if (!arg.contains("="))
                continue;

            String[] parts = arg.split("=");
            argsMap.put(parts[0], parts[1]);
        }

        return argsMap;
    }

    /**
     * Retrieves a new question from the API and sends it to the channel.
     * If an error occurs during the retrieval process, an error message is sent to
     * the channel.
     */
    public void getNewQuestion() {
        try {
            JsonNode response = API.fetchWithRefresh(this.args);
            JsonNode result = response.get("results").get(0);
            this.question = new Question(result);
            channel.createMessage(this.question.askQuestion()).block();
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            channel.createMessage("Problem occurred getting a new question").block();
        }
    }

    /**
     * Attempts to answer the trivia question with the given arguments and username.
     * 
     * @param args     The arguments containing the player's guess.
     * @param username The username of the player.
     */
    public void attempt(String[] args, String username) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            stringBuilder.append(args[i]);
            if (i < args.length - 1) {
                stringBuilder.append(" ");
            }
        }
        String playerGuess = stringBuilder.toString();
        boolean isCorrect = this.question.getCorrectAnswer().equalsIgnoreCase(playerGuess)
                || this.question.getCorrectAnswerNum() == Integer.parseInt(playerGuess);
        if (isCorrect) {
            channel.createMessage("Correct!").block();
            // TODO:
            // Add score tracking
            int newScore = scoreboard.incrementScore(username);
            String pointText = newScore == 1 ? "point" : "points";
            channel.createMessage(
                    playerGuess + " is correct! " + username + " now has " + newScore + " " + pointText + ".")
                    .block();
            getNewQuestion();
        } else {
            channel.createMessage(playerGuess + " is incorrect!").block();
        }
    }
}
