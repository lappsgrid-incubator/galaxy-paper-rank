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
|-------------------------------------|------|-----------------|
| api.elsevier.com                    | 1549 | text/xml        |
| link.springer.com                   | 1206 | application/pdf |
| www.nature.com                      | 748  | application/pdf |
| onlinelibrary.wiley.com             | 307  | application/pdf |
| api.wiley.com                       | 240  | application/pdf |
| academic.oup.com                    | 164  | application/pdf |
| peerj.com                           | 69   | application/pdf |
| f1000research.com                   | 55   | application/pdf |
| cdn.elifesciences.org               | 53   | application/pdf |
| journals.sagepub.com                | 45   | application/pdf |
| downloads.hindawi.com               | 39   | application/pdf |
| www.researchsquare.com              | 29   | text/html       |
| www.liebertpub.com                  | 24   | application/xml |
| royalsocietypublishing.org          | 21   | application/pdf |
| pubs.acs.org                        | 19   | application/pdf |
| rupress.org                         | 16   | application/pdf |
| dl.acm.org                          | 12   | application/pdf |
| www.karger.com                      | 11   | application/pdf |
| tandfonline.com                     | 8    | application/pdf |
| www.degruyter.com                   | 8    | text/html       |
| www.tandfonline.com                 | 8    | application/pdf |
| www.nrcresearchpress.com            | 7    | application/xml |
| ieeexplore.ieee.org                 | 6    | application/pdf |
| stacks.iop.org                      | 5    | application/pdf |
| www.scirp.org                       | 5    | application/pdf |
| content.sciendo.com                 | 4    | text/html       |
| www.dovepress.com                   | 4    | application/pdf |
| ashpublications.org                 | 3    | application/pdf |
| iopscience.iop.org                  | 3    | application/pdf |
| rep.bioscientifica.com              | 3    | text/html       |
| www.bioone.org                      | 3    | application/xml |
| www.theoj.org                       | 3    | application/pdf |
| brill.com                           | 2    | text/html       |
| d-nb.info                           | 2    | application/pdf |
| gatesopenresearch.org               | 2    | application/pdf |
| genominfo.org                       | 2    | application/pdf |
| iwaponline.com                      | 2    | application/pdf |
| jme.bioscientifica.com              | 2    | text/html       |
| meridian.allenpress.com             | 2    | application/pdf |
| portlandpress.com                   | 2    | application/pdf |
| pubs.rsc.org                        | 2    | application/pdf |
| wellcomeopenresearch.org            | 2    | application/pdf |
| www.ijdc.net                        | 2    | application/pdf |
| www.jgld.ro                         | 2    | application/pdf |
| avs.scitation.org                   | 1    | application/pdf |
| bdj.pensoft.net                     | 1    | application/pdf |
| compcytogen.pensoft.net             | 1    | application/pdf |
| iassistquarterly.com                | 1    | application/pdf |
| ijdc.net                            | 1    | application/pdf |
| joe.bioscientifica.com              | 1    | text/html       |
| journals.ashs.org                   | 1    | text/html       |
| mycokeys.pensoft.net                | 1    | application/pdf |
| online.liebertpub.com               | 1    | application/xml |
| open.lnu.se                         | 1    | application/pdf |
| openjournals.wu-wien.ac.at          | 1    | application/pdf |
| openmicrobiologyjournal.com         | 1    | application/pdf |
| protocolexchange.researchsquare.com | 1    | text/html       |
| revistas.unal.edu.co                | 1    | application/pdf |
| www.iimmun.ru                       | 1    | application/pdf |
| www.pagepress.org                   | 1    | application/pdf |
| www.pfmjournal.org                  | 1    | application/pdf |