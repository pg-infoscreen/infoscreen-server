# CHOSING DEBIAN
FROM base:latest

ENV LANG C.UTF-8


# ADDING USER AND GROUP
RUN groupadd pg587 -g 8018
RUN useradd -g pg587 -u 8044 pg587

# MAKING DIRECTORIES
RUN mkdir -p /var/play
RUN mkdir -p /home/pg587
RUN chown -R pg587:pg587 /home/pg587

# CHANGING WORKINGDIRECTORY
WORKDIR /var/play

# CLONING SCHEDULER
RUN chmod 600 /root/.ssh/id_rsa
RUN git clone ssh://git@github.com:pg-infoscreen/infoscreen-server.git --branch master --single-branch
WORKDIR infoscreen-server/

# COMPILING SCHEDULER
RUN activator clean stage 

# GIVE USER TEGELER RIGHTS FOR PLAY
RUN chown -R pg587:pg587 /var/play/infoscreen-server

# ADDING STARTSCRIPT
ADD start.sh /home/pg587/start.sh

# EXPOSE PORT FOR PLAY
EXPOSE 9000

# USE TEGELER AS USER
USER pg587

# IMPORT NATURAL LANGUAGE TOKENIZER
RUN python -c 'import nltk; nltk.download("all");'

#STARTSCRIPT AUSFÜHREN
CMD ["bash","/home/pg587/start.sh"]
