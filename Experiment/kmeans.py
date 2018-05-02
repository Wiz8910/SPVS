#Experiment where we do Kmeans clustering
#assumes there are csv files in mapping directory listed below
#Output average, best and worse purity,rand index and fscore of clusters for each given csv file
#
import re
from sklearn import metrics
from sklearn.metrics import f1_score

import numpy as np
from numpy import linalg
from spherecluster import SphericalKMeans
import configparser

classMap = dict()
simpleClassMap = dict()

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

def main():
    #files we want to read
    Config = configparser.ConfigParser()
    Config.read("python.ini")
    configSection = ConfigSectionMap(Config,"config")
    maxFreq = configSection["maxfrequency"]
    minFreq = configSection["minfrequency"]
    useLsi = configSection["uselsi"]
    synonomized = configSection["synonomized"]

    dataDir = "mappings\\"
    csvFiles = [
        "TestDoc2Vec.csv",
	    "TestSarGreedyMapping.csv",
        "TestHothoMapping.csv","TestRecuperoMapping.csv", "TestBaseMapping.csv", "TestAllMapping.csv"
    ]
    lsiFiles = [
       "LSI_TestAllMapping.csv", "LSI_TestBaseMapping.csv", 
	   "LSI_TestSarMapping.csv"
    ]
    if(useLsi =='1'):
        csvFiles = lsiFiles+csvFiles
    outputFile = "Min"+str(minFreq)+"Max"+str(maxFreq)+".txt"
    if(synonomized=='1'):
        outputFile = "Synonomized"+outputFile
    #outputFiles = dict()
   # for file in csvFiles:
    #    outputFiles[file] = file.replace(".csv",".txt")
    #how many times to run experiment to average results
    numTries = 15
    #remember my code assumes min clusters> number of classes of dataset
    clusterIncrement = 1
    minClusters = 4
    maxClusters = 8
    #now go through files
    for csvFile in csvFiles:
        #first get data
        data = []
        allLabels = []
        labels = []
        #baseLables=[]
        size = 0
        with open(dataDir+csvFile) as file:
            # first line is terms we don't care about
            file.readline()
            for line in file.readlines():
                lineArgs = line.split(',')
                # last arg is newline, don't know why python grabs that
                lineArgs.pop(-1)
                if(size==0):
                    size = len(lineArgs)-1
                # and get the filename
                # class is second part of directory structure
                # baseDir\\subDir\\fileName
                label = lineArgs.pop(0)
                label = re.split(r'[\\]', label)[1]
                #rest of items are numbers denoting amount of each column labels
                lineArgs = [float(i) for i in lineArgs]
                # convert label to sequential index for kmeans
                if not label in classMap:
                    classMap[label] = len(classMap)
                index = classMap[label]
                labels.append(index)
                '''
                # convert label to sequential index for kmeans
                if not instanceLabels in simpleClassMap:
                    simpleClassMap[instanceLabels] = len(simpleClassMap)
                index = simpleClassMap[instanceLabels]
                # first item is label,file name
                baseLables.append(index)
                # first item is label,file
                # name
                # rest are coordinates
                '''
                data.append(lineArgs)
            #now compute svd for data
            #u, sigma, vt = linalg.svd(data,full_matrices=False)
            #data = np.dot(u, np.dot(np.diag(sigma), vt))
        #now do k means for specified range of cluster values

        allLabels.append(labels)
        count=0
        for labels in allLabels:
            count = count+1
            totalFScore = 0
            totalRand =0
            totalPurity =0
            minFScore = 100
            minRand =100
            minPurity =100
            maxFScore = 0
            maxRand =0
            maxPurity =0
            for k in range(minClusters, maxClusters,clusterIncrement):
                avgFscore = 0
                avgRand = 0
                avgPurity = 0
                for i in range(0, numTries):
                    numClusters = k  # len(classMap)*2
                    #use spherical kmeans
                    clusters = SphericalKMeans(
                               n_clusters=numClusters, max_iter=25)
                    #this was how you use scikit kmeans
                    #clusters = KMeans(
                    #    n_clusters=numClusters, init='k-means++', max_iter=50, precompute_distances=True)
                    #then fit data to clusters
                    predictedCluster = clusters.fit_predict(data, labels)
                    # now have to figure label for each cluster
                    counts = [[0 for i in range(0, numClusters)]
                              for k in range(0, numClusters)]
                    for i in range(0, len(predictedCluster)):
                        counts[predictedCluster[i]][labels[i]] = counts[predictedCluster[i]][labels[i]] + 1
                    # now count precision and recall
                    clusterLabels = []
                    purity = 0
                    for clusterCount in counts:
                        # purity is #predicted label/number of items in cluster
                        # index of max element specifies that clusters count
                        mostCommonIndex = clusterCount.index(max(clusterCount))
                        purity += clusterCount[mostCommonIndex]
                        clusterLabels.append(mostCommonIndex)
                    # now array of predicted label for each cluster based on
                    # predictedCluster of data
                    kmeansPredictions = [clusterLabels[cluster]
                                         for cluster in predictedCluster]
                    avgRand += metrics.adjusted_rand_score(
                        labels, kmeansPredictions)
                    avgFscore += f1_score(labels,
                                          kmeansPredictions, average='macro')
                    # purity is number of max labels for each cluster/num data
                    avgPurity += purity / len(data)
                # get precision and recall
                # first precision is accuarcies with in each cluster
                avgFscore = avgFscore / numTries
                avgRand = avgRand / numTries
                avgPurity = avgPurity / numTries
                minFScore = avgFscore if avgFscore<minFScore else minFScore
                maxFScore = avgFscore if avgFscore>maxFScore else maxFScore
                minRand = avgRand if avgRand<minRand else minRand
                maxRand = avgRand if avgRand>maxRand else maxRand
                minPurity = avgPurity if avgPurity<minPurity else minPurity
                maxPurity = avgPurity if avgPurity>maxPurity else maxPurity
                totalFScore = totalFScore + avgFscore
                totalPurity = totalPurity + avgPurity
                totalRand = totalRand + avgRand
                print(csvFile + " " + str(numClusters) + "," + str(avgFscore) + "," +
                      str(avgRand) + "," + str(avgPurity) + "," + str(size) + "\n")
            totalFScore = totalFScore/(maxClusters-minClusters)
            totalRand = totalRand/(maxClusters-minClusters)
            totalPurity = totalPurity/(maxClusters-minClusters)
            with open(str(count)+outputFile,"a") as f:
                f.write(str(csvFile)+ "\t" + str(totalFScore) + "\t" +
                  str(totalRand) + "\t" + str(totalPurity)+ "\t"+str(size)+ "\t"+
                  str(minFScore)+ "\t" +str(minRand)+ "\t" + str(minPurity)+ "\t"+
                  str(maxFScore) + "\t" + str(maxRand) + "\t" + str(maxPurity)+"\n")



if __name__ == "__main__":
    main()
