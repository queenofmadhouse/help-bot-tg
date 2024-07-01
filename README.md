# Help-bot

## Setup
### Basic settings:
~~~
1) POSTGRES_DB_URL: URL for connecting to the database (in the format: jdbc:postgresql://HOST:PORT/DB_NAME)
2) POSTGRES_DB_USERNAME: Username for connecting to the database.
3) POSTGRES_DB_PASSWORD: Password for connecting to the database.
4) BOT_TOKEN: Bot token obtained from @BotFather.
5) URL: URL for telegram webapp (https, for dev it's ok to use ngrok for example)
~~~

### Templates for copying
#### Docker
~~~bash
-e "POSTGRES_DB_URL=<postgres_url>" -e "POSTGRES_DB_USERNAME=<posgres_username>" -e "POSTGRES_DB_PASSWORD=<password>" -e "BOT_TOKEN=<token>" -e "URL=<url>"
~~~
#### IDEA
~~~
POSTGRES_DB_URL=<postgres_url>;POSTGRES_DB_USERNAME=<posgres_username>;POSTGRES_DB_PASSWORD=<password>;BOT_TOKEN=<token>;URL=<url>
~~~
