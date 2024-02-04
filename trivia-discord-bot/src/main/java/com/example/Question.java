package com.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collections;

public class Question {
    private String category;
    private String type;
    private String difficulty;
    private String question;
    private String correctAnswer;
    private int correctAnswerNum;
    private String[] incorrectAnswers;

    /**
     * Represents a question in the trivia game.
     *
     * @param questionData The JSON data containing the question information.
     */
    public Question(JsonNode questionData) {
        parseQuestionData(questionData);
    }

    /**
     * Parses the question data from a JSON node and sets the corresponding fields
     * in the Question object.
     * 
     * @param questionData The JSON node containing the question data.
     */
    public void parseQuestionData(JsonNode questionData) {
        this.category = new String(Base64.getDecoder().decode(questionData.get("category").asText()));
        this.type = new String(Base64.getDecoder().decode(questionData.get("type").asText()));
        this.difficulty = new String(Base64.getDecoder().decode(questionData.get("difficulty").asText()));
        this.question = new String(Base64.getDecoder().decode(questionData.get("question").asText()));
        this.correctAnswer = new String(Base64.getDecoder().decode(questionData.get("correct_answer").asText()));
        this.incorrectAnswers = new String[questionData.get("incorrect_answers").size()];
        for (int i = 0; i < questionData.get("incorrect_answers").size(); i++) {
            this.incorrectAnswers[i] = new String(
                    Base64.getDecoder().decode(questionData.get("incorrect_answers").get(i).asText()));
        }
    }

    /**
     * Returns a formatted string representation of the question, including
     * category, type, difficulty, question, and answers.
     *
     * @return the formatted question string
     */
    public String askQuestion() {
        StringBuilder questionString = new StringBuilder();
        questionString.append("Category: ").append(this.category).append("\n");
        questionString.append("Type: ").append(this.type).append("\n");
        questionString.append("Difficulty: ").append(this.difficulty).append("\n\n");
        questionString.append("Question: ").append(this.question).append("\n\n");
        questionString.append("Answers: ").append("\n");

        // Create a list of all answers
        List<String> allAnswers = new ArrayList<>();
        allAnswers.add(this.correctAnswer);
        allAnswers.addAll(Arrays.asList(this.incorrectAnswers));

        // Shuffle the answers
        Collections.shuffle(allAnswers);

        correctAnswerNum = allAnswers.indexOf(this.correctAnswer) + 1;

        // Append the shuffled answers to the question string
        for (int i = 0; i < allAnswers.size(); i++) {
            questionString.append(i + 1).append(". ").append(allAnswers.get(i)).append("\n");
        }

        return questionString.toString();
    }

    /**
     * Returns the correct answer for the question.
     *
     * @return the correct answer as a String
     */
    public String getCorrectAnswer() {
        return this.correctAnswer;
    }

    /**
     * Returns the number of the correct answer for this question.
     *
     * @return the number of the correct answer
     */
    public int getCorrectAnswerNum() {
        return this.correctAnswerNum;
    }
}
