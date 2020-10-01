# Gold Standard Publishers

4398 DOI metadata files have a link to download the article for Text Data Mining (TDM) purposes. Of the small sample I checked all TDM files are Open Access and released under a CC, or similar, license.  However, that does not mean the articles are *freely available*: articles from Springer are behind a pay-wall and WIley requires and API key to download articles.

## Notes

The the **Type** column contains the mime type for the download link defined in the DOI metadata.

- ***text/html*** likely indicates the link leads to a HTML page intended for human consumption. We may be able to scrape a download link from such pages but they tend to use features (i.e. cookies, access tokens) that make automated scraping difficult.  In the case of **nature.com** a PDF file can be downloaded by appending the string *.pdf* to the end of the URL.  Similar tactics may be used for other publishers as well.

- ***application/pdf*** I did not try downloading any PDF files.

- ***SAVED*** the mime type was *application/xml* or *text/xml* and we were able to download an XML file.

- ***BLOCKED*** the mime type was *application/xml* or *text/xml* but something prevented the file from being downloaded.

  .

### Publisher Information Sorted by Number of Articles

| Publisher                           | Size | Type            |
|-------------------------------------|------|-----------------|
| api.elsevier.com                    | 1553 | text/xml        |
| link.springer.com                   | 1110 | text/html       |
| www.nature.com                      | 748  | text/html       |
| onlinelibrary.wiley.com             | 308  | application/xml |
| api.wiley.com                       | 236  | application/pdf |
| peerj.com                           | 70   | SAVED           |
| f1000research.com                   | 55   | SAVED           |
| cdn.elifesciences.org               | 53   | SAVED           |
| journals.sagepub.com                | 45   | application/pdf |
| downloads.hindawi.com               | 38   | SAVED           |
| www.researchsquare.com              | 29   | text/html       |
| www.liebertpub.com                  | 24   | BLOCKED         |
| royalsocietypublishing.org          | 21   | BLOCKED         |
| www.karger.com                      | 11   | application/pdf |
| www.degruyter.com                   | 8    | text/html       |
| tandfonline.com                     | 8    | application/pdf |
| www.tandfonline.com                 | 8    | application/pdf |
| www.nrcresearchpress.com            | 7    | BLOCKED         |
| stacks.iop.org                      | 5    | text/html       |
| content.sciendo.com                 | 4    | text/html       |
| www.dovepress.com                   | 4    | application/pdf |
| rep.bioscientifica.com              | 3    | text/html       |
| iopscience.iop.org                  | 3    | text/html       |
| www.bioone.org                      | 3    | application/xml |
| www.theoj.org                       | 3    | application/pdf |
| file.scirp.org                      | 3    | SAVED           |
| jme.bioscientifica.com              | 2    | text/html       |
| brill.com                           | 2    | text/html       |
| d-nb.info                           | 2    | application/pdf |
| www.jgld.ro                         | 2    | application/pdf |
| www.ijdc.net                        | 2    | application/pdf |
| genominfo.org                       | 2    | application/pdf |
| www.scirp.org                       | 2    | application/pdf |
| gatesopenresearch.org               | 2    | SAVED           |
| wellcomeopenresearch.org            | 2    | SAVED           |
| openmicrobiologyjournal.com         | 2    | SAVED           |
| openjournals.wu-wien.ac.at          | 1    | text/plain      |
| joe.bioscientifica.com              | 1    | text/html       |
| journals.ashs.org                   | 1    | text/html       |
| protocolexchange.researchsquare.com | 1    | text/html       |
| online.liebertpub.com               | 1    | BLOCKED         |
| iassistquarterly.com                | 1    | application/pdf |
| ieeexplore.ieee.org                 | 1    | application/pdf |
| revistas.unal.edu.co                | 1    | application/pdf |
| www.pfmjournal.org                  | 1    | application/pdf |
| rupress.org                         | 1    | application/pdf |
| ijdc.net                            | 1    | application/pdf |
| open.lnu.se                         | 1    | application/pdf |
| www.iimmun.ru                       | 1    | application/pdf |
| ajas.info                           | 1    | application/pdf |
| www.pagepress.org                   | 1    | SAVED           |
| mycokeys.pensoft.net                | 1    | SAVED           |
| compcytogen.pensoft.net             | 1    | SAVED           |
| bdj.pensoft.net                     | 1    | SAVED           |



### Publisher Information Sorted by Mime Type

| Publisher                           | Size | Type            |
| ----------------------------------- | ---- | --------------- |
| www.liebertpub.com                  | 24   | BLOCKED         |
| royalsocietypublishing.org          | 21   | BLOCKED         |
| www.nrcresearchpress.com            | 7    | BLOCKED         |
| online.liebertpub.com               | 1    | BLOCKED         |
| peerj.com                           | 70   | SAVED           |
| f1000research.com                   | 55   | SAVED           |
| cdn.elifesciences.org               | 53   | SAVED           |
| downloads.hindawi.com               | 38   | SAVED           |
| file.scirp.org                      | 3    | SAVED           |
| gatesopenresearch.org               | 2    | SAVED           |
| wellcomeopenresearch.org            | 2    | SAVED           |
| openmicrobiologyjournal.com         | 2    | SAVED           |
| www.pagepress.org                   | 1    | SAVED           |
| mycokeys.pensoft.net                | 1    | SAVED           |
| compcytogen.pensoft.net             | 1    | SAVED           |
| bdj.pensoft.net                     | 1    | SAVED           |
| api.wiley.com                       | 236  | application/pdf |
| journals.sagepub.com                | 45   | application/pdf |
| www.karger.com                      | 11   | application/pdf |
| tandfonline.com                     | 8    | application/pdf |
| www.tandfonline.com                 | 8    | application/pdf |
| www.dovepress.com                   | 4    | application/pdf |
| www.theoj.org                       | 3    | application/pdf |
| d-nb.info                           | 2    | application/pdf |
| www.jgld.ro                         | 2    | application/pdf |
| www.ijdc.net                        | 2    | application/pdf |
| genominfo.org                       | 2    | application/pdf |
| www.scirp.org                       | 2    | application/pdf |
| iassistquarterly.com                | 1    | application/pdf |
| ieeexplore.ieee.org                 | 1    | application/pdf |
| revistas.unal.edu.co                | 1    | application/pdf |
| www.pfmjournal.org                  | 1    | application/pdf |
| rupress.org                         | 1    | application/pdf |
| ijdc.net                            | 1    | application/pdf |
| open.lnu.se                         | 1    | application/pdf |
| www.iimmun.ru                       | 1    | application/pdf |
| ajas.info                           | 1    | application/pdf |
| onlinelibrary.wiley.com             | 308  | application/xml |
| www.bioone.org                      | 3    | application/xml |
| link.springer.com                   | 1110 | text/html       |
| www.nature.com                      | 748  | text/html       |
| www.researchsquare.com              | 29   | text/html       |
| www.degruyter.com                   | 8    | text/html       |
| stacks.iop.org                      | 5    | text/html       |
| content.sciendo.com                 | 4    | text/html       |
| rep.bioscientifica.com              | 3    | text/html       |
| iopscience.iop.org                  | 3    | text/html       |
| jme.bioscientifica.com              | 2    | text/html       |
| brill.com                           | 2    | text/html       |
| joe.bioscientifica.com              | 1    | text/html       |
| journals.ashs.org                   | 1    | text/html       |
| protocolexchange.researchsquare.com | 1    | text/html       |
| openjournals.wu-wien.ac.at          | 1    | text/plain      |
| api.elsevier.com                    | 1553 | text/xml        |