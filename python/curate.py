# Curates a single article, or directory of .txt files and prints 'yes' if the
# article is about Galaxy the Python workflow platform, or 'no' if the article
# is about some other Galaxy, e.g. the phone, astronomy etc.
#
# This skeleton is intended for example purposes only.

import os
import sys

# TODO Actual articul curation
def curate(article):
    with open(article, "r") as file:
        text = file.read().lower()
        if 'samsung' in text:
            return 'no'
        if 'milky way' in text or 'andromeda' in text:
            return 'no'
        if 'galaxy' in text:
            return 'yes'
        return 'no'

def curate_directory(dir):
    for file in os.listdir(dir):
        if (file.endswith(".txt")):
            answer = curate(os.path.join(dir, file))
            print(f"{file}\t{answer}")

def usage():
    print(f"USAGE: python {sys.argv[0]} /input/dir/or/file")

if __name__ == "__main__":
    if len(sys.argv) != 2:
        usage()
        sys.exit(1)

    if os.path.isdir(sys.argv[1]):
        curate_directory(sys.argv[1])
    else:
        answer = curate(sys.argv[1])
        print(f"{sys.argv[1]} {answer}")
