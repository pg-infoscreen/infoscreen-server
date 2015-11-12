#!/bin/bash
docker run --name play --link mysql:mysql -v /home/pg587_nb1/docker/play/upload:/var/play/infoscreen/scheduler/play/upload -v /etc/localtime:/etc/localtime:ro -p 80:9000 -d play:latest
