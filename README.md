# Trivia Game Discord Bot

This is a Discord bot that hosts trivia games. It fetches questions from an API, decodes them from Base64, and presents them to users in a Discord server.

[Add to Your Server](https://discord.com/api/oauth2/authorize?client_id=1202920520505495562&permissions=2048&scope=bot)

## Features

- Fetches trivia questions from the [Open Trivia DB](https://opentdb.com)
- Hosts trivia games in a Discord server
- Keeps score of user's who answered the questions correctly


## Development

### Prerequisites

- VSCode
- Docker
- Devcontainers extension

### Getting Started

- `cp example.env .env`
- Obtain a token from [Discord Developer Apps](https://discord.com/developers/applications)
- Place the token in .env
- Reopen the project in devcontainer

## Deployment

- Run from the docker-compose file
