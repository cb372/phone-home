#!/bin/sh
echo "Copying $(dirname $0)/../client/*.js to $(dirname $0)/src/main/webapp/js"
cp $(dirname $0)/../client/*.js $(dirname $0)/src/main/webapp/js
