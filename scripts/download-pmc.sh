#!/usr/bin/env bash

# A Bash script to download and unpack all articles from PubMed Central.
# Depending on the speed of your network connection this may take some time
# as there are ~48GB of tarballs to download.  Unpacked all the files take
# 231GB

# Where to save the files
DEST=/data/corpora/pmc
FILE_DIR=$DEST/tarballs
DATA_DIR=$DEST/xml

function get() {
    if [[ ! -e $FILE_DIR/$1 ]] ; then
		wget -P $FILE_DIR https://ftp.ncbi.nlm.nih.gov/pub/pmc/oa_bulk/$1
    fi
    echo "Untarring $1"
    tar xzf $FILE_DIR/$1 -C $DATA_DIR
}

start_time=$(date +%s)
for part in A-B C-H I-N O-Z; do
    get comm_use.$part.xml.tar.gz
    get non_comm_use.$part.xml.tar.gz
done
end_time=$(date +%s)
echo "Downloads completed in $[$end_time - $start_time] seconds"