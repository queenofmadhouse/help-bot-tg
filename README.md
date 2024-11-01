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

## API

### Endpoints
#### Get all urgent request
1) URL: /api/request/urgent
2) Method: GET
3) Parameters: -
4) Response example:
~~~
[
    {
        "id": 2,
        "tgChatId": 225773842,
        "userName": "meow",
        "userPronouns": "meow",
        "requestDate": "2024-10-28 22:59",
        "requestText": "meow",
        "relatedAdminId": null,
        "inWork": false,
        "inTheArchive": false,
        "urgent": true
    },
    {
        "id": 1,
        "tgChatId": 225773842,
        "userName": "meow",
        "userPronouns": "123",
        "requestDate": "2024-10-28 22:57",
        "requestText": "123231___",
        "relatedAdminId": null,
        "inWork": false,
        "inTheArchive": false,
        "urgent": true
    }
]
~~~
#### Get all regular request
1) URL: /api/request/regular
2) Method: GET
3) Parameters: -
4) Response example:
~~~
[
    {
        "id": 2,
        "tgChatId": 225773842,
        "userName": "meow",
        "userPronouns": "meow",
        "requestDate": "2024-10-28 22:59",
        "requestText": "meow",
        "relatedAdminId": null,
        "inWork": false,
        "inTheArchive": false,
        "urgent": false
    },
    {
        "id": 1,
        "tgChatId": 225773842,
        "userName": "meow",
        "userPronouns": "123",
        "requestDate": "2024-10-28 22:57",
        "requestText": "123231___",
        "relatedAdminId": null,
        "inWork": false,
        "inTheArchive": false,
        "urgent": false
    }
]
~~~
#### Get request related to user
1) URL: /api/request/my
2) Method: GET
3) Parameters: -
4) Response example
~~~
[
    {
        "id": 2,
        "tgChatId": 225773842,
        "userName": "meow",
        "userPronouns": "meow",
        "requestDate": "2024-10-28 22:59",
        "requestText": "meow",
        "relatedAdminId": 225773842,
        "inWork": false,
        "inTheArchive": false,
        "urgent": true
    },
    {
        "id": 1,
        "tgChatId": 225773842,
        "userName": "meow",
        "userPronouns": "123",
        "requestDate": "2024-10-28 22:57",
        "requestText": "123231___",
        "relatedAdminId": 225773842,
        "inWork": false,
        "inTheArchive": false,
        "urgent": false
    }
]
~~~
#### Get all messages related to request
1) URL: /api/message/get/{id}
2) Method: GET
3) Parameters: {id} = request id 
4) Response example:
~~~
[
    {
        "id": 1,
        "request": {
            "id": 1,
            "tgChatId": 225773842,
            "userName": "meow",
            "userPronouns": "123",
            "requestDate": "2024-10-28 22:57",
            "requestText": "123231___",
            "relatedAdminId": 225773842,
            "inWork": false,
            "inTheArchive": false,
            "urgent": false
        },
        "userChatId": 225773842,
        "adminChatId": 225773842,
        "fromAdmin": true,
        "messageText": "Здравствуйте! Спасибо, что написали нам ❤️\n\nВы можете обратиться в бесплатную дружественную юр. службу \"Первой линии\", их бот в ТГ @FirstLineHelpBot \n\nТакже, вы можете обратиться и к другим юристам с нашего сайта: https://translyaciya.com/lawyers\n\n\nПишите, если будут еще вопросы!\nберегите себя ♡\nС любовью, Мурзик\n______________________________________________\n\nУ нас есть чаты по городам и интересам для трансгендерных, небинарных, интерсекс-персон и персон в гендерном поиске.\nВы можете вступить в чат, или стать основатель_ницей нового чата, если такого еще нет.\nгорода: https://translyaciya.com/cities\nинтересы: https://translyaciya.com/interest",
        "timestamp": null
    },
    {
        "id": 10,
        "request": {
            "id": 1,
            "tgChatId": 225773842,
            "userName": "meow",
            "userPronouns": "123",
            "requestDate": "2024-10-28 22:57",
            "requestText": "123231___",
            "relatedAdminId": 225773842,
            "inWork": false,
            "inTheArchive": false,
            "urgent": false
        },
        "userChatId": 225773842,
        "adminChatId": 225773842,
        "fromAdmin": true,
        "messageText": "Здравствуйте! Спасибо, что написали нам ❤️\n\nВы можете обратиться в бесплатную дружественную юр. службу \"Первой линии\", их бот в ТГ @FirstLineHelpBot \n\nТакже, вы можете обратиться и к другим юристам с нашего сайта: https://translyaciya.com/lawyers\n\n\nПишите, если будут еще вопросы!\nберегите себя ♡\nС любовью, Мурзик\n______________________________________________\n\nУ нас есть чаты по городам и интересам для трансгендерных, небинарных, интерсекс-персон и персон в гендерном поиске.\nВы можете вступить в чат, или стать основатель_ницей нового чата, если такого еще нет.\nгорода: https://translyaciya.com/cities\nинтересы: https://translyaciya.com/interest",
        "timestamp": null
    },
    {
        "id": 11,
        "request": {
            "id": 1,
            "tgChatId": 225773842,
            "userName": "meow",
            "userPronouns": "123",
            "requestDate": "2024-10-28 22:57",
            "requestText": "123231___",
            "relatedAdminId": 225773842,
            "inWork": false,
            "inTheArchive": false,
            "urgent": false
        },
        "userChatId": 225773842,
        "adminChatId": 225773842,
        "fromAdmin": true,
        "messageText": "Здравствуйте! Спасибо, что написали нам ❤️\n\nВы можете обратиться в бесплатную дружественную юр. службу \"Первой линии\", их бот в ТГ @FirstLineHelpBot \n\nТакже, вы можете обратиться и к другим юристам с нашего сайта: https://translyaciya.com/lawyers\n\n\nПишите, если будут еще вопросы!\nберегите себя ♡\nС любовью, Мурзик\n______________________________________________\n\nУ нас есть чаты по городам и интересам для трансгендерных, небинарных, интерсекс-персон и персон в гендерном поиске.\nВы можете вступить в чат, или стать основатель_ницей нового чата, если такого еще нет.\nгорода: https://translyaciya.com/cities\nинтересы: https://translyaciya.com/interest",
        "timestamp": null
    }
]
~~~
#### Send message
1) URL: /api/message/send
2) Method: POST
3) Parameters: -
4) JSON example:
~~~
{
        "id": null,
        "request": {
            "id": 1,
            "tgChatId": 225773842,
            "userName": "meow",
            "userPronouns": "123",
            "requestDate": "2024-10-28 22:57",
            "requestText": "123231___",
            "relatedAdminId": 225773842,
            "inWork": true,
            "inTheArchive": false,
            "urgent": true
        },
        "userChatId": 225773842,
        "adminChatId": 225773842,
        "fromAdmin": true,
        "messageText": "Здравствуйте! Спасибо, что написали нам ❤️\n\nВы можете обратиться в бесплатную дружественную юр. службу \"Первой линии\", их бот в ТГ @FirstLineHelpBot \n\nТакже, вы можете обратиться и к другим юристам с нашего сайта: https://translyaciya.com/lawyers\n\n\nПишите, если будут еще вопросы!\nберегите себя ♡\nС любовью, Мурзик\n______________________________________________\n\nУ нас есть чаты по городам и интересам для трансгендерных, небинарных, интерсекс-персон и персон в гендерном поиске.\nВы можете вступить в чат, или стать основатель_ницей нового чата, если такого еще нет.\nгорода: https://translyaciya.com/cities\nинтересы: https://translyaciya.com/interest",
        "timestamp": null
}
~~~
#### Accept request (start work with it)
1) URL: /api/request/accept/{id}
2) Method: POST
3) Parameters: id = request id
#### Deny request
1) URL: /api/request/deny/{id}
2) Method: POST
3) Parameters: id = request id