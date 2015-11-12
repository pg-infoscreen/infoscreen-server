# CHOSING UBUNTU
FROM ubuntu:14.04


# APT-GET
RUN apt-get update
RUN apt-get install -y software-properties-common
RUN add-apt-repository ppa:webupd8team/java
RUN apt-get update
RUN echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
RUN apt-get install -y oracle-java8-installer
RUN apt-get install -y git
RUN apt-get install -y unzip
RUN apt-get install -y wget
RUN apt-get install -y python
RUN apt-get install -y python-dev
RUN apt-get install -y python-pip
RUN apt-get install -y libxml2-dev
RUN apt-get install -y libxslt-dev
RUN apt-get install -y lib32z1-dev

RUN wget https://github.com/miso-belica/sumy/archive/master.zip && unzip master.zip && cd sumy-master/ && python setup.py install && cd ..
RUN pip install numpy

# MAKING DIRECTORIES
RUN mkdir -p /var/activator

# CHANGING DIRECTORY
WORKDIR /var/activator

# DOWLOADING, UNZIPING AND START ACTIVATOR
RUN wget http://downloads.typesafe.com/typesafe-activator/1.2.10/typesafe-activator-1.2.10-minimal.zip
RUN unzip typesafe-activator-1.2.10-minimal.zip
RUN cp activator-1.2.10-minimal/* /usr/bin/
