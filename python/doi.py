import os
import csv
import sys
import requests
from bs4 import BeautifulSoup

test_url = "https://cdn.elifesciences.org/articles/25157/elife-25157-v2.xml"

BASE_DIRECTORY = '/data/corpora/curation'
NEGATIVES = f"{BASE_DIRECTORY}/negative"

errors = list()
downloads = list()

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

def download_xml(url, doi, publisher, token):
    print(f"Downloading article from {url}")
    headers = {
        'User-Agent': 'LappsgridDownloader/1.0 (http://www.lappsgrid.org mailto:suderman@cs.vassar.edu)',
        'CR-Clickthrough-Client-Token': token
    }

    filename = url.split("?")[0].split("/")[-1]
    if not filename.endswith(".xml"):
        filename = f"{filename}.xml"

    path = f"{NEGATIVES}/xml/{publisher}/{filename}"
    dirname = os.path.dirname(path)
    if not os.path.exists(dirname):
        os.makedirs(dirname)

    try:
        response = requests.get(url, headers=headers)
    except:
        print(f"Unable to download {url} : {sys.exc_info()[0]}")
        errors.append(f"{doi},{url},500,{sys.exc_info()[0]}")
        return

    if response.status_code == 200:
        with open(path, "w") as xml_file:
            xml_file.write(response.text)
        print(f"Wrote {path}")
        downloads.append(f"{doi},{url},{publisher}")
    else:
        print(f"Unable to download article {response.status_code} : {response.reason}")
        errors.append(f"{doi},{url},{response.status_code},{response.reason}")

def download_pdf(url, doi, publisher, token):
    filename = url.split("/")[-1]
    path = f"{NEGATIVES}/pdf/{publisher}/{filename}"
    dirname = os.path.dirname(path)
    if not os.path.exists(dirname):
        os.makedirs(dirname)

    headers = {
        'User-Agent': 'LappsgridDownloader/1.0 (http://www.lappsgrid.org mailto:suderman@cs.vassar.edu)',
        'CR-Clickthrough-Client-Token': token
    }

    try:
        response = requests.get(url, headers=headers)
    except:
        print(f"Unable to download {url} : {sys.exc_info()[0]}")
        errors.append(f"{doi},{url},500,{sys.exc_info()[0]}")
        return

    if response.status_code == 200:
        print(f"Downloaded {url}")
        with open(path, "wb") as pdf_file:
            pdf_file.write(response.content)
        print(f"Wrote {path}")
        downloads.append(f"{doi},{url},{publisher}")
    else:
        print(f"Unable to download {url}")
        print(f"Status: {response.status_code}")
        print(f"Reason: {response.reason}")
        print(response.headers)
        errors.append(f"{doi},{url},{response.status_code},{response.reason}")


def get_doi(doi, token):
    headers = {
        "Accept":'application/vnd.crossref.unixsd+xml',
        'User-Agent': 'LappsgridDownloader/1.0 (http://www.lappsgrid.org mailto:suderman@cs.vassar.edu)',
        'CR-Clickthrough-Client-Token': token
    }
    print(f"Fetching metadata for {doi}")
    response = requests.get(f"https://dx.doi.org/{doi}", headers=headers)
    if response.status_code != 200:
        print(f"Unable to fetch metadata {response.status_code} : {response.reason}")
    filename = f"{NEGATIVES}/doi/{doi}.xml"
    dirname = os.path.dirname(filename)
    if not os.path.exists(dirname):
        os.makedirs(dirname)

    xml = response.text
    with open(filename, "w") as xml_file:
        xml_file.write(xml)

    print(f"Wrote {filename}")
    doc = BeautifulSoup(xml, 'xml')
    publisher = 'unknown'
    nodes = doc.find_all(name='crm-item', attrs={'name':'publisher-name'})
    if nodes is not None and len(nodes) > 0:
        publisher = nodes[0].string.lower().replace(' ', '_')

    collection = doc.find_all('collection', property='text-mining')
    if collection is None or len(collection) == 0:
        print("No link for TDM found.")
        return
    collection = collection[0]
    resources = collection.find_all('resource', mime_type="application/pdf")
    if resources is not None and len(resources) > 0:
        download_pdf(resources[0].string, doi, publisher, token)
        return

    resources = collection.find_all('resource', mime_type="application/xml")
    if resources is not None and len(resources) > 0:
        download_xml(resources[0].string, doi, publisher, token)
        return

    resources = collection.find_all('resource', mime_type="text/xml")
    if resources is not None and len(resources) > 0:
        download_xml(resources[0].string, doi, publisher, token)
        return

    resources = collection.find_all("resource")
    if resources is not None and len(resources) > 0:
        for resource in resources:
            print(f"Checking {resource.string}")
            if resource.string.endswith(".pdf"):
                download_pdf(resource.string, doi, publisher, token)
                return
            elif resource.string.endswith(".xml"):
                download_xml(resource.string, doi, publisher, token)
                return

    errors.append(f"{doi},unknown,unknown,No file available for download.")
    print("No version available for download")


def get_all_doi(csv_path, token):
    with open(csv_path, "r") as csv_file:
        reader = csv.reader(csv_file)
        keys = next(reader)
        line = next(reader)
        while line[2] != "10.1002/9781119200055.ch9":
            line = next(reader)
            
        for row in reader:
            doi = row[2]
            get_doi(doi, token)
            # print(f"DOI {doi}")

    print(f"Downloaded {len(downloads)} files.")
    print(f"Encountered {len(errors)} errors.")
    with open(f"{BASE_DIRECTORY}/negative-downloaded.csv", "w") as csv_file:
        csv_file.write("doi,url,publisher\n")
        csv_file.writelines(downloads)
    with open(f"{BASE_DIRECTORY}/negative-errors.csv", "w") as csv_file:
        csv_file.write("doi,url,status,reason\n")
        csv_file.writelines(errors)
    print("Done")

def parse_doi_xml(filename):
    with open(filename, "r") as xml_file:
        doc = BeautifulSoup(xml_file, 'xml')


    resource = doc.find_all('collection', property='text-mining')

    resource = resource[0].find_all('resource', mime_type='application/pdf')
    print(len(resource))
    print(resource[0].string)

if __name__ == "__main__":
    if len(sys.argv) == 1:
        print("USAGE: python doi.py [DOI|CSV]")
        sys.exit(1)
    #
    # get_doi(sys.argv[1])
    # test_gix064()
    # parse_doi_xml('/Users/suderman/Projects/identify-galaxy/doi/10.1002/rse2.54.xml')
    token = os.environ.get('CROSSREF_API_TOKEN')
    if token is None:
        print("The CROSSREF_API_TOKEN environment variable has not been set")
    elif sys.argv[1].endswith(".csv"):
        print("Found csv file")
        get_all_doi(sys.argv[1], token)
    else:
        print("Found single DOI")
        get_doi(sys.argv[1], token)
    # parse_zotero_csv("/Users/suderman/Downloads/Zotero_lib_20200921.csv")