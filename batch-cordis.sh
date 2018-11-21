#!/usr/bin/env bash

if [ $# -eq 0 ]
  then
    echo -e "\nPlease add NextCloud credentials when call: '$0 user:pwd' to run this command!\n"
    exit 1
fi

pwd=$1
directory=Datasets/Research/CORDIS
#./createFolder.sh $directory $pwd
for TOPICS in 70 150
do
    echo "Training for $TOPICS .."
    cp application-template.properties application.properties
    sed -i "s/#topics#/$TOPICS/g" application.properties
    sed -i "s/#size#/-1/g" application.properties
    sed -i "s/#id#/id/g" application.properties
    sed -i "s/#name#/title/g" application.properties
    sed -i "s/#text#/objective/g" application.properties
    sed -i "s/#format#/jsonl_gz/g" application.properties
    sed -i "s/#dockerHub#/false/g" application.properties
    sed -i "s/#file#/\/home\/cbadenes\/corpus\/cordis\/cordis.jsonl.gz/g" application.properties
    sed -i "s/#image#/registry.bdlab.minetur.es\/cordis-model:1.2-$TOPICS/g" application.properties
    sed -i "s/#description#/Topic Model created from Community Research and Development Information Service projects between 1984-2018/g" application.properties
    sed -i "s/#title#/CORDIS Topic Model - Infrastructure as Category/g" application.properties
    ./retrain-export-model.sh
    ./createFolder.sh $directory/$TOPICS-topics $pwd
    ./uploadModel.sh $directory/$TOPICS-topics $pwd
done