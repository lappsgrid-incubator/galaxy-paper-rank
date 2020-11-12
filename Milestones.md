# Milestones

##  [#1](milestone/1) Data Collection

### Positive Examples

Use Dave’s database to identify, retrieve, and clean text of articles that are POSITIVE examples of a use of Galaxy, save on server

See:
- [#1 Test Data Collection](issues/1)
- [#10 Training Data](issues/1)


### Negative Examples

Use Dave’s database to identify, retrieve, and clean text  of articles that are NEGATIVE examples of a use of Galaxy, save on server
- if not enough negatives, use crossref.org to extract any text with string Galaxy, delete any already identified in Dave’s dataset
- May still contain positives Dave has not seen--alternatives to extract true NEGATIVES from this set:
   - Try to do it automatically [use embeddings information to query? determine by journal type?] 
   - Have Dave (or maybe a student?) curate the articles: create a simple interface where he sees the sentence (or a couple of sentences) containing the word Galaxy and can click “yes” or “no” (preferred method)

See:
- [#6 Fetche articles from PMC](issues/6)
- [#10 Training Data](issues/10)

##  [#2](milestone/6) Machine Learning

Set up and apply machine learning (Nadim should be primarily responsible for this).  Issues should be created for these tasks as needed.

- Download and install BERT on Jetstream (see [#13](issues/13))

- Prepare our data for input (see [#14](issues/14))

  - we need binary classification so this will look like the YELP sentiment data, where we have, say, 1 for YES and 2 for NO 
  - programs to create the proper data sets in the right format are here: https://medium.com/swlh/a-simple-guide-on-using-bert-for-text-classification-bbf041ac8d04 

- Use the pre-trained model (basic, cased) as a language model [if we have adequate resources we could try the large model]

- Test the model on our test data

- Follow https://medium.com/swlh/a-simple-guide-on-using-bert-for-text-classification-bbf041ac8d04 to create a new layer to the model. This will likely take some time (like, days) so it is imperative to use Jetstream here 

- Test the model on our test data

- If appropriate we can play with parameters, but this is time-consuming since each run requires days to complete!

  

##  [#3](milestone/3) Final Deliverable

The final deliverable, for Nadim at least, should be a system that given an input document (or directory of documents) predicts if the document's authors used Galaxy.

**Expected Inputs**

A single text file or directory of files. 

Is extracting text from PDF/XML also part of the this milestone?

**Expected Outputs**

A *yes/no* or probability (0..1) that the authors used Galaxy.



## #4 Ultimate Deliverable

TBD and beyond the scope of this project, but likley a system that sends email to a mailing list with a links to papers and the probability they used Galaxy.  

Periodically (hourly, daily, weekly?) pull email alerts (Google Scholar etc) and look for articles.

A means to change system configuration
- threshold for papers to include in the "Probably Uses Galaxy" list.
- run frequency
- email configuration for mailing list
- any settings for document harvesting
  - publisher server URLs and API keys
  - email alert settings







End of Document



