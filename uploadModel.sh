./upload.sh docker/corpus/doctopics.csv.gz $1/doctopics.csv.gz $2
./upload.sh docker/model/model-inferencer.bin $1/model-inferencer.bin $2
./upload.sh docker/model/model-parameters.bin $1/model-parameters.bin $2
./upload.sh docker/model/model-settings.bin $1/model-settings.bin $2
./upload.sh docker/model/model-topic-words.csv.gz $1/model-topic-words.csv.gz $2
./upload.sh docker/model/model-topics.csv.gz $1/model-topics.csv.gz $2
./upload.sh docker/model/model-topic-neighbours.csv.gz $1/model-topic-neighbours.csv.gz $2
./upload.sh application.properties $1/application.properties $2