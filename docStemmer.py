import gensim
from gensim import models
import os
import string
import re
from nltk.corpus import stopwords as sw
from nltk.corpus import wordnet as wn
from nltk import wordpunct_tokenize
from nltk import WordNetLemmatizer
from nltk import sent_tokenize
from nltk import pos_tag

import configparser
import xml.etree.ElementTree as XML
from sklearn.base import BaseEstimator, TransformerMixin
import time


def ConfigSectionMap(Config,section):
    dict1 = {}
    options = Config.options(section)
    for option in options:
        try:
            dict1[option] = Config.get(section, option)
        except:
            print("exception on %s!" % option)
            dict1[option] = None
    return dict1

class NLTKPreprocessor(BaseEstimator, TransformerMixin):

    def __init__(self, stopwords=None, punct=None,
                 lower=True, strip=True):
        self.lower      = lower
        self.strip      = strip
        self.stopwords  = stopwords or set(sw.words('english'))
        self.punct      = punct or set(string.punctuation)
        self.lemmatizer = WordNetLemmatizer()

    def fit(self, X, y=None):
        return self

    def inverse_transform(self, X):
        return [" ".join(doc) for doc in X]

    def transform(self, X):
        return [
            list(self.tokenize(doc)) for doc in X
        ]

    def tokenize(self, document):
        # Break the document into sentences
        for sent in sent_tokenize(document):
            # Break the sentence into part of speech tagged tokens
            for token, tag in pos_tag(wordpunct_tokenize(sent)):
                # Apply preprocessing to the token
                token = token.lower() if self.lower else token
                token = token.strip() if self.strip else token
                token = token.strip('_') if self.strip else token
                token = token.strip('*') if self.strip else token

                # If stopword, ignore token and continue
                if token in self.stopwords:
                    continue

                # If punctuation, ignore token and continue
                if all(char in self.punct for char in token):
                    continue

                # Lemmatize the token and yield
                lemma = self.lemmatize(token, tag)
                yield lemma

    def lemmatize(self, token, tag):
        tag = {
            'N': wn.NOUN,
            'V': wn.VERB,
            'R': wn.ADV,
            'J': wn.ADJ
        }.get(tag[0], wn.NOUN)

        return self.lemmatizer.lemmatize(token, tag)

def generateStemmedDocs(synonomized,reuters):
    # first need to produce txt file from all documents,
    # have to seperate words by whitespace
    outRoot = 'PreprocessedtestDataSet'
    docVectors = "docVectors.txt"
    fileNames = "fileNames.txt"
    preprocessor = NLTKPreprocessor()
    #newsgroup has each file individually
    if(not reuters):
        rootdir = 'testDataSet'
        with open(fileNames, 'w') as namesOut, open(docVectors, 'w') as contentsOut:
            for subdir, dirs, files in os.walk(rootdir):
                for file in files:
                    with open(os.path.join(subdir, file), "r") as f:
                        content = f.read()  # [x.strip() for x in f.readlines()]
                        content = list(preprocessor.tokenize(content))
                    namesOut.write(os.path.join(subdir, file) + "\n")
                    line = " ".join(content)
                    contentsOut.write(line + "\n")
                    # also write to sanitized document collection
                    if not os.path.exists(subdir.replace(rootdir, outRoot)):
                        os.makedirs(subdir.replace(rootdir, outRoot))
                    with open(os.path.join(subdir, file).replace(rootdir, outRoot), 'w') as synOut:
                        synOut.write(line)
    #reuters just has a xml document for each category
    else:
        rootdir = 'reuters'
        with open(fileNames, 'w') as namesOut, open(docVectors, 'w') as contentsOut:
            for subdir, dirs, files in os.walk(rootdir):
                for file in files:
                    with open(os.path.join(subdir, file),"r") as f:
                        print(file)
                        xml = f.read()
                        tree = XML.fromstring("<?xml version=\"1.0\"?>\n<root>\n"+ xml + "\n</root>")
                        for doc in tree.findall('REUTERS'):

                            topics = doc.find('TOPICS').findall('D')
                            #if no topics omit from our dataset
                            if(len(topics)>0):
                                topicString = ""
                                for topic in topics:
                                    topicString += topic.text+"."
                                fullPath = topicString+doc.attrib['NEWID']
                                textObj = doc.find('TEXT')
                                if textObj is None:
                                    print(topicString+" no text")
                                else:
                                    title = textObj.find('TITLE')
                                    content = ""
                                    if not title is None:
                                        content += title.text
                                    body = textObj.find('BODY')
                                    if not body is None:
                                        content += "\n" +body.text
                                    if not content=="":
                                        content = list(preprocessor.tokenize(content))
                                        line = " ".join(content)
                                        namesOut.write(fullPath + "\n")
                                        contentsOut.write(line + "\n")
                                        #only going to group by first topic
                                        if not os.path.exists(os.path.join(outRoot,topics[0].text)):
                                            os.makedirs(os.path.join(outRoot,topics[0].text))
                                        # also write to sanitized document collection
                                        with open(os.path.join(outRoot,topics[0].text,doc.attrib['NEWID']), 'w') as synOut:
                                            synOut.write(line)

#class LabeledLineSentence(object):
    #def __iter__(self):
        #with open("docVectors.txt", encoding='utf-8') as fin:
            #for item_no, sentence in enumerate(fin):
                #yield models.Word2Vec..LabeledSentence(words=sentence.split(),labels=[item_no])

                    #def __iter__(self):
    #    for line in open("docVectors.txt"):
    #        yield dictionary.doc2bow(line.lower().split())


def main():
    Config = configparser.ConfigParser()
    Config.read("python.ini")
    configSection = ConfigSectionMap(Config, "config")
    synonomized = int(configSection['synonomized']) == 1
    reuters = int(configSection['reuters']) == 1
    generateStemmedDocs(synonomized,reuters)

if __name__ == "__main__":
    main()
