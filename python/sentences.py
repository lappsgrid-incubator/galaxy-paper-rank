# Runs all the text files in the input directory through the NLTK
# sentence splitter and writes the files, one sentence per line, to
# the output directory.

import os
import sys
import nltk

def usage():
    print("USAGE: python sentences.py /path/to/input/dir /path/to/output/dir")

def run(indir,outdir):
    for f in os.listdir(indir):
        if f.endswith(".txt"):
            infile = os.path.join(indir, f)
            with open(infile, "r") as txt_file:
                outpath = os.path.join(outdir, f)
                with open(outpath, "w") as out_file:
                    for s in nltk.sent_tokenize(txt_file.read()):
                        out_file.write(' '.join(s.split()))
                        out_file.write('\n')
                print(f"Wrote {outpath}")


if __name__ == "__main__":
    if len(sys.argv) != 3:
        usage()
        sys.exit(1)

    indir = sys.argv[1]
    outdir = sys.argv[2]
    if not os.path.isdir(indir):
        print(f"{indir} is not a directory.")
        usage()
        sys.exit(1)
    if not os.path.isdir(outdir):
        print(f"{outdir} is not a directory")
        usage()
        sys.exit(1)

    run(indir,outdir)

def unused():
    if len(sys.argv) != 2:
        usage()
        sys.exit(1)

    infile = sys.argv[1]
    print("Sentence splitting {infile")
    with open(infile, "r") as in_file:
        count=0
        sents = nltk.sent_tokenize(in_file.read())
        print(f"There are {len(sents)} sentences")
        print(' '.join(sents[0].split()))
