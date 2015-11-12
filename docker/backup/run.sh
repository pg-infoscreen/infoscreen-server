docker run --name backup --link mysql:mysql -v /home/pg587_1/docker/backup:/home/pg587_1/docker/backup -v /home/pg587_nb1/docker/play/upload:/home/pg587_nb1/docker/play/upload -d backup:latest
