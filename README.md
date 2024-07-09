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
-e "POSTGRES_DB_URL=<postgres_url>" -e "POSTGRES_DB_USERNAME=<posgres_username>" -e "POSTGRES_DB_PASSWORD=<password>" -e "BOT_TOKEN=<token>" -e "URL=<url>" -e "REDIS_HOST=<redis_url>" -e "REDIS_PORT=<redis_port>"
~~~
#### IDEA
~~~
POSTGRES_DB_URL=<postgres_url>;POSTGRES_DB_USERNAME=<posgres_username>;POSTGRES_DB_PASSWORD=<password>;BOT_TOKEN=<token>;URL=<url>;REDIS_HOST=<redis_url>;REDIS_PORT=<redis_port>
~~~
#### Docker run
~~~bash
docker run -d --name bot-app --network bot-network -e "POSTGRES_DB_URL=" -e "POSTGRES_DB_USERNAME=" -e "POSTGRES_DB_PASSWORD=" -e "BOT_TOKEN=" -e "REDIS_HOST=" -e "REDIS_PORT="
~~~
