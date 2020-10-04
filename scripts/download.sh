#!/usr/bin/env bash

echo $1
echo $2

if [[ ! -e $1 ]] ; then
	echo "No CSV file provided"
	exit 1
fi

if [[ ! -d $2 ]] ; then
	echo "No output directory provided"
	exit 1
fi

for url in `cat $1 | cut -f1 -d,` ; do
	wget -P $2 -U mozilla --random-wait $url
done
