import os
import csv
import sys
import requests
from bs4 import BeautifulSoup

test_url = "https://cdn.elifesciences.org/articles/25157/elife-25157-v2.xml"

def parse_zotero_csv(path):
    dxdoi = "http://dx.doi.org"
    urls_only = list()
    doi_available = list()
    pubmed = list()
    xml_url = list()
    pdf_url = list()
    no_id = 0
    with open(path, "r") as csv_file:
        reader = csv.reader(csv_file)
        keys = next(reader)
        for row in reader:
            id = row[0]
            doi = row[8]
            url = row[9]
            if doi is not None and len(doi) > 0:
                doi_available.append(f"{dxdoi}/{doi}")
                # if url is None or len(url) == 0:
                #     doi_available.append(f"{dxdoi}/{doi}")
                # else:
                #     doi_available.append(url)
            elif url is not None and len(url) > 0:
                if "doi.org" in url:
                    print(f"Found a DOI in url {url}")
                    doi_available.append(url)
                elif "pubmed" in url or "pmc" in url or "PMC" in url:
                    pubmed.append(url)
                elif url.endswith(".pdf"):
                    pdf_url.append(url)
                elif url.endswith(".xml"):
                    xml_url.append(url)
                else:
                    urls_only.append(url)
            else:
                # print(f"Unable to identify {id}")
                no_id += 1

    print(f"There are {len(doi_available)} articles with a DOI")
    print(f"There are {len(pubmed)} articles from pubmed")
    print(f"There are {no_id} files with no ID or URL")
    print(f"There are {len(pdf_url)} PDF files via URL")
    print(f"There are {len(xml_url)} XML files via URL")
    print(f"There are {len(urls_only)} files with just a URL")

    with open("doi-file-list.txt", "w") as outfile:
        for url in doi_available:
            outfile.write(url)
            outfile.write("\n")
    with open("pdf-file-list.txt", "w") as outfile:
        for url in pdf_url:
            outfile.write(url)
            outfile.write("\n")

    # sources = set()
    # for url in urls_only:
    #     parts = url.split("/")
    #     sources.add(parts[2])
    # print(f"From {len(sources)} different organizations.")
    # for url in sources:
    #     print(url)

def download_xml(url):
    print(f"Downloading article from {url}")
    filename = url.split("/")[-1]
    path = f"xml/{filename}"
    response = requests.get(url)
    if response.status_code == 200:
        with open(path, "w") as xml_file:
            xml_file.write(response.text)
        print(f"Wrote {path}")
    else:
        print(f"Unable to download article {response.status_code} : {response.reason}")

def download_pdf(url):
    filename = url.split("/")[-1]
    path = f"pdf/{filename}"
    with open(path, "w") as xml_file:
        response = requests.get(url)
        if response.status_code == 200:
            xml_file.write(response.content)
            print(f"Wrote {path}")
        else:
            print(f"Unable to download {url}")

def get_doi(doi):
    headers = {"Accept":'application/vnd.crossref.unixsd+xml'}
    print(f"Fetching metadata for {doi}")
    response = requests.get(f"https://dx.doi.org/{doi}", headers=headers)
    if response.status_code != 200:
        print(f"Unable to fetch metadata {response.status_code} : {response.reason}")
    filename = f"doi/{doi}.xml"
    dirname = os.path.dirname(filename)
    if not os.path.exists(dirname):
        os.makedirs(dirname)

    xml = response.text
    with open(filename, "w") as xml_file:
        xml_file.write(xml)

    print(f"Wrote {filename}")
    doc = BeautifulSoup(xml, 'xml')
    resources = doc.find_all('resource', mime_type="application/xml")
    if resources is not None and len(resources) > 0:
        download_xml(resources[0].string)
        return

    resources = doc.find_all('resource', mime_type="application/pdf")
    if resources is not None and len(resources) > 0:
        download_pdf(resources[0].string)
        return

    resources = doc.find_all("resource")
    if resources is not None and len(resources) > 0:
        for resource in resources:
            print(f"Checking {resource.string}")
            if resource.string.endswith(".pdf"):
                download_pdf(resource.string)
                return
            elif resource.string.endswith(".xml"):
                download_xml(resource.string)
                return

    print("No version available for download")


def test_gix064():
    with open("doi/10.1093/gigascience/gix064.xml", "r") as xml_file:
        doc = BeautifulSoup(xml_file, 'xml')
    resources = doc.find_all('resource', mime_type="application/xml")
    if resources is not None and len(resources) > 0:
        print(f"Found {resources[0].string}")
        return

    resources = doc.find_all('resource', mime_type="application/pdf")
    if resources is not None and len(resources) > 0:
        print(f"Found {resources[0].string}")
        return

    resources = doc.find_all("resource")
    if resources is not None and len(resources) > 0:
        print("Found a resource")
        for resource in resources:
            print(f"Checking {resource.string}")
            if resource.string.endswith(".pdf"):
                print(f"Found {resource.string}")
                return
            elif resource.string.endswith(".xml"):
                print(f"Found {resource.string}")
                return

    print("No version available for download")

def parse_doi_xml():
    with open("doi.xml", "r") as xml_file:
        doc = BeautifulSoup(xml_file, 'xml')
    resource = doc.find_all('resource', mime_type="application/xml")
    print(resource[0].string)

if __name__ == "__main__":
    # if len(sys.argv) == 1:
    #     print("USAGE: python doi.py <DOI>")
    #     sys.exit(1)
    #
    # get_doi(sys.argv[1])
    # test_gix064()
    # parse_doi_xml()
    parse_zotero_csv("/Users/suderman/Downloads/Zotero_lib_20200921.csv")