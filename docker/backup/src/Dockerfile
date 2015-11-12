# CHOSING DEBIAN
FROM debian:latest

MAINTAINER Tim Tegeler <tim.tegeler@tu-dortmund.de>

# INSTALLING SOFTWARE
RUN apt-get update
RUN apt-get install -y mysql-client-5.5
RUN apt-get install -y gzip

# ADDING USER AND GROUP
RUN groupadd pg587 -g 8018
RUN useradd -g pg587 -u 8044 pg587

# MAKING DIRECTORIES
RUN mkdir -p /home/pg587_1/docker/backup
RUN mkdir -p /home/pg587_nb1/docker/play/upload
RUN chown -R pg587:pg587 /home/pg587_1
RUN chown -R pg587:pg587 /home/pg587_nb1

# USING TEGELER AS USER
USER pg587

# ADDING FILES
ADD ./mysqlbackup.sh /home/pg587/mysqlbackup.sh

#STARTSCRIPT AUSFÜHREN
CMD ["bash", "/home/pg587/mysqlbackup.sh"]
