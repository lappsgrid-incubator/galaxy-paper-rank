import os
import sys
from io import StringIO

from pdfminer.converter import TextConverter
from pdfminer.layout import LAParams
from pdfminer.pdfdocument import PDFDocument
from pdfminer.pdfinterp import PDFResourceManager, PDFPageInterpreter
from pdfminer.pdfpage import PDFPage
from pdfminer.pdfparser import PDFParser

def run(pdfDir, txtDir):
    if not os.path.isdir(pdfDir):
        print(f"{pdfDir} is not a directory.")
        return
    if not os.path.isdir(txtDir):
        print(f"{txtDir} is not a directory")
        return
    process(pdfDir, txtDir)

def process(pdfDir, txtDir):
    for f in os.listdir(pdfDir):
        if os.path.isdir(f):
            newTxtDir = os.path.join(txtDir, f)
            if not os.path.exists(newTxtDir):
                os.makedirs(newTxtDir)
            process(os.path.join(pdfDir,f), newTxtDir)
        elif f.endswith(".pdf"):
            print(f"Converting {f}")
            try:
                txt = read(os.path.join(pdfDir, f))
                outpath = os.path.join(txtDir, f.replace(".pdf", ".txt"))
                with open(outpath, "w") as txt_file:
                    txt_file.write(txt)
                print(f"Wrote {outpath}")
            except Exception as e:
                print(f"Unable to convert {f} : " + str(e))


def read(path):
    output_string = StringIO()
    with open(path, 'rb') as in_file:
        parser = PDFParser(in_file)
        doc = PDFDocument(parser)
        rsrcmgr = PDFResourceManager()
        device = TextConverter(rsrcmgr, output_string, laparams=LAParams())
        interpreter = PDFPageInterpreter(rsrcmgr, device)
        for page in PDFPage.create_pages(doc):
            interpreter.process_page(page)

    return output_string.getvalue()

if __name__ == '__main__':
    if len(sys.argv) != 3:
        print("USAGE: python pdf2text.py /pdf/dir/ /txt/dir/")
        sys.exit(1)

    run(sys.argv[1], sys.argv[2])