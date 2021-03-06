import os
import sys
from bs4 import BeautifulSoup

failed = 0
passed = 0
skipped = list()
def extract(xml_path, outdir):
    global failed, passed
    try:
        with open(xml_path, "r") as xml_file:
            doc = BeautifulSoup(xml_file, 'xml')
    except:
        print(sys.exc_info()[0])
        failed += 1
        return False

    body = doc.find_all("body")
    if body is None or len(body) == 0:
        print(f"{xml_path}: No body element found.")
        failed += 1
        return False
    fname = os.path.basename(xml_path).replace(".xml", ".txt")
    txt_path = os.path.join(outdir, fname)
    with open(txt_path, "w") as txt_file:
        txt_file.write(body[0].text)

    passed += 1
    return True

def process(indir, outdir):
    global skipped
    for f in os.listdir(indir):
        full_path = os.path.join(indir, f)
        if os.path.isfile(full_path) and full_path.endswith(".xml"):
            extract(full_path, outdir)
        elif os.path.isdir(full_path):
            child = os.path.join(outdir, os.path.basename(f))
            if not os.path.exists(child):
                os.makedirs(child, 0o774)
            process(full_path, child)
        else:
            skipped.append(full_path)

def ok(directory, type):
    if not os.path.exists(directory):
        print(f"The {type} directory {indir} does not exist")
        return False
    if not os.path.isdir(directory):
        print(f"{directory} is not a directory")
        return False
    return True

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("USAGE: python xml2text.py <INDIR> <OUTDIR>")
        print("\nXML files will be read from <INDIR> and the extracted text written to <OUTDIR>")
        print("Both directories must exist and the directory structure in <INDIR> will")
        print("be created inside <OUTDIR>.")
        sys.exit(1)

    indir = sys.argv[1]
    if not ok(indir, "input"):
        sys.exit(1)
    outdir = sys.argv[2]
    if not ok(outdir, "output"):
        sys.exit(1)

    process(indir, outdir)
    print(f"Wrote {passed} files.")
    print(f"Encountered {failed} problems.")
    print(f"Skipped {len(skipped)} files.")
    for f in skipped:
        print(f)
