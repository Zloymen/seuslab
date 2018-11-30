Задание в приглашении.

Реализация.

Сервис 1 - Использован верблюд, который тянет исходные файлы с папки data/inbox. Затем тянет их по шелковому пути(преобразовывает их согласно ТЗ). И выкладывает результат в data/outbox.

Запуск.

Вариант 1. 

на локальной машине должен быть установлен Postgres и Redis.

Создаем database и пользователя.

    create user seuslab with createdb login;
    create database seuslab;
    alter database seuslab owner to seuslab;
    alter user seuslab with password 'seuslab';
    
собираем jar файлы с помощью maven

    mvn install
    
запускаем сервис 2

    java -jar service2/target/service2-1.0-SNAPSHOT.jar

запускаем сервис 1 

    java -jar service1/target/service1-1.0-SNAPSHOT.jar    
    
Вариант 2. 

на локальной машине должен быть установлен Postgres и Redis, Docker.

Создаем database и пользователя.

    create user seuslab with createdb login;
    create database seuslab;
    alter database seuslab owner to seuslab;
    alter user seuslab with password 'seuslab';
    
собираем jar файлы с помощью maven

    mvn install
    
создаем docker образ

    cd service2
    mvn com.spotify:dockerfile-maven-plugin:1.3.6:build
    
запускаем сервис 2

    docker run -d 
        --name test_seuslab 
        -p 8686:8787 
        -e SERVICE_PORT=8787 
        -e DATABASE_HOST=<ваш ip> 
        -e DATABASE_PORT=5432 
        -e DATABASE_NAME=seuslab 
        -e DATABASE_USER=seuslab 
        -e DATABASE_PASSWORD=seuslab 
        -e REDIS_HOST=<ваш ip> 
        -e REDIS_PORT=6379 
        seuslab/service2

запускаем сервис 1 

    копируем папку data в service1/target
    java -jar service1/target/service1-1.0-SNAPSHOT.jar
    
    
Вариант 3. 

на локальной машине должен быть установлен Docker, Docker-compose.

собираем jar файлы с помощью maven

    mvn install
    
создаем docker образ
    
    cd service2
    mvn com.spotify:dockerfile-maven-plugin:1.3.6:build
    
запускаем сервис 2

    cd docker
    docker-compose -f app.yml up

запускаем сервис 1 

    копируем папку data в service1/target
    java -jar service1/target/service1-1.0-SNAPSHOT.jar   
    
Вариант 4. 

на локальной машине должен быть установлен Docker, Docker-compose.

собираем jar файлы с помощью maven

    mvn install
    
создаем docker образ
    
    cd service2
    mvn com.spotify:dockerfile-maven-plugin:1.3.6:build

редактируем путь к папке с исходними данными в compose файл app.yml

    volumes:
                - '{путь к локальной папке с исходними данными}:/data'
    
запускаем yml файл 

    cd docker
    docker-compose -f app.yml up

