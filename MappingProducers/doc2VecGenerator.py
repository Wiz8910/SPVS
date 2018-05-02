#use genism to produce doc2vec file mappings
import gensim
from gensim import models
import os
import string

from nltk.corpus import stopwords as sw
from nltk.corpus import wordnet as wn
from nltk import wordpunct_tokenize
from nltk import WordNetLemmatizer
from nltk import sent_tokenize
from nltk import pos_tag

import configparser
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

def main():

    #files we want to read
    Config = configparser.ConfigParser()
    Config.read("python.ini")
    configSection = ConfigSectionMap(Config,"config")
    synonomized = int(configSection['synonomized'])==1
    maxFreq = int(configSection['maxfrequency'])
    minFreq = int(configSection['minfrequency'])
    docVectors = "docVectors.txt"
    fileNames = "fileNames.txt"
    if(synonomized):
        docVectors = "Synonomized"+docVectors
        fileNames = "Synonomized"+fileNames
    with open(docVectors,"r") as taggedLineDocument,open(fileNames,"r") as Names:
        labels=[]
        start = time.time()

        for line in Names.readlines():
            labels.append(line.strip("\n"))
        documents = models.doc2vec.TaggedLineDocument(taggedLineDocument)
        model = models.doc2vec.Doc2Vec(documents=documents,window=3,min_count=minFreq,workers=4,iter=300)
        model.train(documents,total_examples=model.corpus_count,epochs=model.iter)
        #now build the csv file from the model

        with open("mappings\\\\TestDoc2Vec.csv","w") as csvOut:
            for i in range(0,len(labels)):
                csvOut.write(labels[i]+","+",".join([str(num) for num in model.docvecs[i]])+"\n")#.doctag_syn0[i]])+"\n")
        end = time.time()
        total = end-start
        with open('Min'+str(minFreq)+'Max'+str(maxFreq)+'times.txt','a') as f:
            f.write("TestDoc2Vec.csv\t"+str(total)+"\n")
if __name__ == "__main__":
    main()
