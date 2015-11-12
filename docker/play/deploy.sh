#!/bin/bash
#PlayImage umbenennen
docker tag play:latest play:old

#unnötiges play:latest entfernen
bash removeImage.sh

#neues play:latest erstellen
bash build.sh

#alten PlayContainer stoppen
bash stop.sh

#alten PlayContainer entfernen
bash remove.sh

#neuen PlayContainer erstellen und starten
bash run.sh

#play:old entfernen
docker rmi play:old
