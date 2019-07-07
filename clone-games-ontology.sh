#!/bin/bash

export WORKING_DIR=`pwd`
echo "> Working dir: $WORKING_DIR"

echo "> Getting data..."
git clone git@github.com:Binarios/MciOntology.git

#echo "> Making data dir"
#sudo mv your-data-repo data