import sys
from urllib.request import urlopen

import argparse
import json
import csv

class SolrService:
    '''
    A simple class to manage all the bits need to send a query to the Solr
    server and parse the response.
    '''
    def __init__(self, host='143.229.7.99', port='8993', core='pmc', rows=10000, format='json'):
        self.host = host
        self.port = port
        self.core = core
        self.rows = rows
        self.format = format

    def query(self, q, fl=None):
        if fl is None:
            url = f"http://{self.host}:{self.port}/solr/{self.core}/select?q={q}&rows={self.rows}&wt={self.format}"
        else:
            if isinstance(fl, list):
                fl = ",".join(fl)
            url = f"http://{self.host}:{self.port}/solr/{self.core}/select?q={q}&fl={fl}&rows={self.rows}&wt={self.format}"

        connection = urlopen(url)
        return json.loads(connection.read())


def galaxy_index(solr, path):
    keys = ['id', 'pmid', "pmc", "doi", "year", "title"]
    response = solr.query('galaxy', keys)
    print("Writing CSV file.")
    with open(path, 'w', newline='') as csv_file:
        writer = csv.writer(csv_file)
        writer.writerow(keys)
        for doc in response['response']['docs']:
            row = [doc[id] if id in doc else '' for id in keys]
            writer.writerow(row)
    print("Done")

def write_text_files(solr, dir):
    keys = ['id', 'pmid', "pmc", "doi", "body"]
    response = solr.query('galaxy', keys)
    for doc in response['response']['docs']:
        id = doc['pmc'] if 'pmc' in doc else doc['pmid'] if 'pmid' in doc else None
        if id is None:
            if 'doi' in doc:
                print("Unable to save f{doc['doi']}")
            else:
                print(f"Unable to process {doc['id']}")
        else:
            print(f"Writing {id}")
            with open(f"{dir}/PMC{id}.txt", "w") as f:
                f.write(doc['body'])
    print("Done")


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="Simple PMC Solr queries for Galaxy")
    parser.add_argument('-server', required=False, default='143.229.7.99', help='address of the Solr host [143.229.7.99]')
    parser.add_argument('-port', required=False, default='8983', help='Solr port [8983]')
    parser.add_argument('-core', required=False, default='pmc', help='name of the collection to query [pmc]')
    parser.add_argument('-rows', required=False, default=10000, help='number of rows to return')
    fmt = parser.add_mutually_exclusive_group(required=True)
    fmt.add_argument('-t', '--text', metavar="DIR", help='save article bodies to plain text files')
    fmt.add_argument('-c', '--csv', metavar="FILE", help="generate CSV file with ID values")
    args = parser.parse_args(sys.argv[1:])

    solr = SolrService(host=args.server, port=args.port, core=args.core, rows=args.rows)
    if args.text is not None:
        write_text_files(solr, args.text)
    else:
        galaxy_index(solr, args.csv)




