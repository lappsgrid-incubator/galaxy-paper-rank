# Galaxy Paper Rank
Project home for the paper identification project for Galaxy.

[TOC]

## Requirements

What *exactly* is expected?  A command line program? A web service?  A bash script that can run as a `cron` job?  What is the target OS? Are there any preferences for implementation language(s) or frameworks?

What is the desired output?  Is an email with a list of links to articles sufficient?  Should articles be downloaded for human consumption?



## Document Retreival

Identifying and finding documents is a non-trivial task made difficult because there is no single source for documents. During this stage we should not worry too much about false positives as recall is more important than precision; missing an article that used Galaxy is worse than including an article that doesn't.

#### Google Scholar

Google Scholar is notoriously difficult to scrape.. You know when [Nature publishes papers](https://www.nature.com/articles/d41586-018-04190-5) on how difficult it is that the prospects do not look good. A short term solution would be a simple script/program to scrape URLs and DOIs from the HTML email alerts from Google.  At the least a [bot should be developed](https://repl.it/talk/learn/How-to-Make-a-Python-Email-Bot/8194) to automatically fetch and parse the Google email alerts and either forward links to a retreival system or generate a CSV spreadsheet for human consumption.

| Pros                        | Cons                                       |
| --------------------------- | ------------------------------------------ |
| Likely the most up to date. | Difficult to scrape data.                  |
| Indexes everything          | No API or automation                       |
|                             | Does not provide direct access to articles |



### Crossref

[Crossref](https://www.nature.com/articles/d41586-018-04190-5) only contains metadata about papers and not the papers themselves. It does seem to possess a powerful query interface and an API which make automated queries possible, but a download strategy would need to be devised for each publisher (download URL, credentials if needed, document format etc.)

| Pros                         | Cons                                       |
| ---------------------------  | ------------------------------------------ |
| Indexes (almost?) everything | No direct access to articles               |
| Public API with queries      | Not sure how frequently the index is updated. |


### PubMed Central

PubMed Central only contains a sub-set of the papers from PubMed, but it also has a query API and full texts of the articles are availble.  
| Pros                        | Cons                                       |
| --------------------------- | ------------------------------------------ |
| Public API with queries     | Does not index all areas of research.      |
| FTP access with bulk downloads | |
| Updated frequently, but the *new* articles may be old. | |
| Articles available as XML or plain text. | |


### Publishers

The least cost effective solution would be to devise individual systems for each publisher we can access.
| Pros                        | Cons                                       |
| --------------------------- | ------------------------------------------ |
| The publishers should have everything. | Likely requires a customized solution for every publisher.|

## Document Cleaning
This may or may not be a separate phase or peice of software, but at some point the documents need to be converted into a common fomat for ingestion by the ranking algorithms.  Given the nature of the task simple plain text files may suffice. 



## Paper Ranking
### Low Hanging Fruit
1. Papers that cite one of the main Galaxy papers
2. Papers that contain specific key terms or phrases
3. Other obvious indicators

This could be something as simple as:

```bash
cat paper.txt | grep -e usegalaxy -e galaxyproject -e "The Galaxy platform for accessible, reproducible and collaborative biomedical analyses"
```

### Word Embeddings

*"You shall know a word by the company it keeps."*<br/>                                                   - John Rupert Firth

[Word embeddings](https://medium.com/analytics-vidhya/maths-behind-word2vec-explained-38d74f32726b) are a relatively recent technique to represent words as numerical vectors.  Essentially words are "embedded" in a vector space so that we can do linear algebra with them to reason about the meaning of a word (amongst other things).  For example, with a system trained on a sufficiently large corpus the expression "King" - "Man" + "Woman" will yield a result that is closest to the vector for 'Queen'.

The great simplifying assumption we can make is we are only interested in the context of the word *Galaxy*. So the first proposed solution is to generate word embeddings from a corpus of articles of true positives (articles that used Galaxy) to be used to predict the context of the word Galaxy in candidate articles.



### Edge Cases
1. Papers about systems that are also named Galaxy.  I have found an IT system for managing patient records named Galaxy as well as another gene sequencing tool with Galaxy in the name, although it was not clear if the tool was just a front end to a Galaxy workflow.



# Implementation Notes

CSV files are fine for a "database" for now.  This makes the databases easy to load into spreadsheets for manipulation.



# Evaluation

We will need a hold out set for our final evaluation.  The hold out set can be selected from existing articles, but the IDs should be known so that the hold out documents are not inadvertently scraped from PMC.

An evaluation framework should be set up as early as possible.

1. Divide training data into 10 random partitions.
2. Use nine of the partitions for training and test with the remaining partition.
3. Repeat 10 times using each partition as the evaluation set.
4. Collate results.

The evaluation can be automated (Travis or GitHub actions) to be performed whenever code is pushed to a specific branch.

# Deliverables

See Requirements