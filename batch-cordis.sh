#!/usr/bin/env bash

if [ $# -ne 2 ]
  then
    echo -e "\nPlease add NextCloud and DockerHub credentials when call: '$0 nextcloud_user:nextcloud_pwd dockerhub_user:dockerhub_pwd' to run this command!\n"
    exit 1
fi


############################################################################################################
# Activate this flag to download the original corpus and process the text before generating the model.
# Otherwise, the previously generated BoW will be used.
############################################################################################################
NLP=false
############################################################################################################
# Corpus path
############################################################################################################
CORPUS_NAME=cordis
CORPUS_FILE_URL=https://delicias.dia.fi.upm.es/nextcloud/index.php/s/woBzdYWfJtJ6sfY/download
BOW_FILE_URL=https://delicias.dia.fi.upm.es/nextcloud/index.php/s/XmxEt2SXd44fqAB/download
############################################################################################################


corpus_file=corpora/$CORPUS_NAME/docs.jsonl.gz
bow_file=docker/data/corpus/bows.csv.gz
if [ "$NLP" = true ] ; then
   if [ -f "$corpus_file" ] ; then
        echo "using an existing corpus-file at $corpus_file"
   else
        mkdir -p corpora/$CORPUS_NAME
        curl -o corpora/$CORPUS_NAME/docs.jsonl.gz "$CORPUS_FILE_URL"
   fi
else
   if [ ! -f "$bow_file" ] ; then
        mkdir -p docker/corpus
   fi
   curl -o docker/corpus/bows.csv.gz "$BOW_FILE_URL"
fi


NEXTCLOUD_CREDENTIALS=$1
DOCKERHUB_CREDENTIALS=$2
DOCKER_USER=${DOCKERHUB_CREDENTIALS%%:*}
DOCKER_PWD=${DOCKERHUB_CREDENTIALS##*:}
directory=Datasets/Sesiad/$CORPUS_NAME
./createFolder.sh $directory $NEXTCLOUD_CREDENTIALS


echo "Ready to create topic models from a BoW corpus .."
for TOPICS in 70 150
do
    echo "Setting parameters for a Topic Model with $TOPICS topics.."
    cp application-template.properties application.properties
    sed -i "s/#topics#/$TOPICS/g" application.properties
    sed -i "s/#size#/-1/g" application.properties
    sed -i "s/#id#/id/g" application.properties
    sed -i "s/#name#/title/g" application.properties
    sed -i "s/#text#/objective/g" application.properties
    sed -i "s/#format#/jsonl_gz/g" application.properties
    sed -i "s/#dockerHub#/true/g" application.properties
    sed -i "s/#dockerUser#/$DOCKER_USER/g" application.properties
    sed -i "s/#dockerPassword#/$DOCKER_PWD/g" application.properties
    sed -i "s/#file#/$corpus_file/g" application.properties
    sed -i "s/#image#/sesiad\/$CORPUS_NAME-model:$TOPICS-latest/g" application.properties
    sed -i "s/#description#/Topic Model created from Community Research and Development Information Service projects between 1984-2018/g" application.properties
    sed -i "s/#title#/CORDIS Topic Model - Infrastructure as Category/g" application.properties
    if [ "$NLP" = true ] ; then
        ./train-export-model.sh
        ./upload.sh docker/data/corpus/bows.csv.gz $directory/bows.csv.gz $NEXTCLOUD_CREDENTIALS
    else
        ./retrain-export-model.sh
    fi
    echo "uploading data model to Nextcloud repository"
    ./createFolder.sh $directory/$TOPICS-topics $NEXTCLOUD_CREDENTIALS
    ./uploadModel.sh $directory/$TOPICS-topics $NEXTCLOUD_CREDENTIALS
    echo "data uploaded successfully"

    echo "uploading model as docker image to private repository"
    docker tag sesiad/$CORPUS_NAME-model:$TOPICS-latest registry.bdlab.minetur.es:$CORPUS_NAME-model:$TOPICS-latest
    docker push registry.bdlab.minetur.es/$CORPUS_NAME-model:$TOPICS-latest
    echo "image uploaded succesfully"
done