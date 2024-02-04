package com.example;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;

public class App {
    private static boolean gameRunning = false;
    private static Game game;
    private static String sessionToken;

    public static void main(String[] args) {
        String token = System.getenv("DISCORD_TOKEN");
        DiscordClient client = DiscordClient.create(token);
        GatewayDiscordClient gateway = client.login().block();

        gateway.on(MessageCreateEvent.class).subscribe(event -> {
            Message message = event.getMessage();
            String content = message.getContent();

            if (content.startsWith("!")) {
                String[] commandArgs = content.substring(1).split(" ");
                String command = commandArgs[0];
                MessageChannel channel = message.getChannel().block();

                // Handle different commands based on the command string
                switch (command) {
                    case "tstart":
                        // Handle !startgame command
                        if (!gameRunning) {
                            game = new Game(channel, commandArgs);
                            game.start();
                            gameRunning = true;
                        } else {
                            channel.createMessage("Game already running!").block();
                        }
                        break;
                    case "answer":
                    case "a":
                        // Handle !answer command
                        if (gameRunning) {
                            game.attempt(commandArgs, message.getAuthor().get().getUsername());
                        } else {
                            channel.createMessage("No game running!").block();
                        }
                        break;
                    case "tnext":
                        // Handle !nextquestion command
                        if (gameRunning) {
                            game.getNewQuestion();
                        } else {
                            channel.createMessage("No game running!").block();
                        }
                        break;
                    case "tend":
                        // Handle !endgame command
                        if (gameRunning) {
                            channel.createMessage("Game ended!").block();
                            game = null;
                            gameRunning = false;
                        } else {
                            channel.createMessage("No game running!").block();
                        }
                        break;
                    case "tscoreboard":
                        // Handle !scoreboard command
                        channel.createMessage(Scoreboard.getAllScores()).block();
                        break;
                    case "tcategories":
                        // Handle !categories command
                        channel.createMessage(API.getCategories()).block();
                        break;
                    case "thelp":
                        // Handle !triviahelp command
                        channel.createMessage("Available commands:\n"
                                + "!tstart [difficulty=<easy|medium|hard>] [category=<category_number>] - Start a new game with an optional difficulty and category\n"
                                + "!answer | !a [answer] - Attempt to answer the current question\n"
                                + "!tnext - Get the next question\n"
                                + "!tend - End the current game\n"
                                + "!tcategories - Show the available categories\n"
                                + "!tscoreboard - Show the current scoreboard\n"
                                + "!thelp - Show this help message").block();
                        break;
                    default:
                        // Handle unknown command
                        channel.createMessage("Unknown command!").block();
                        break;
                }
            }
        });

        gateway.onDisconnect().block();
    }

    public static void setSessionToken(String token) {
        sessionToken = token;
    }

    public static String getSessionToken() {
        return sessionToken;
    }
}
