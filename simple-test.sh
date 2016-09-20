#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n'

NAME=$1
shift

printf "\n - $NAME (args=$*)\n"
./cleanup.sh

docker run -e "UNIFORM_OUTPUT=true" rapidoid/rapidoid:$TAG "$@" > output/$NAME.txt 2>&1

./cleanup.sh
