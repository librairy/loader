pwd=user:pwd
directory=Datasets/Research/OpenResearchCorpus
./createFolder.sh $directory $pwd
for TOPICS in 10 20 30 40 50 60 70 80 90 100 200 300 400 500 600 700 800 900 1000 1100 1200 1300 1400 1500 1600 1700 1800 1900 2000
do
    echo "Training for $TOPICS .."
    cp corpora/openresearch/application-template.properties application.properties
    sed -i "s/#topics#/$TOPICS/g" application.properties
    ./retrain-export-model.sh
    ./createFolder.sh $directory/$TOPICS-topics $pwd
    ./uploadModel.sh $directory/$TOPICS-topics $pwd
done