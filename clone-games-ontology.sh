#!/bin/bash

cd ..

export WORKING_DIR=`pwd`
echo "> Working dir: $WORKING_DIR"

ls -la

echo "> Getting data..."
git clone git@github.com:Binarios/MciOntology.git

#echo "> Making data dir"
#sudo mv your-data-repo data