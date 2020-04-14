'''
April Gustafson and Stephen Dorris
CS 5100
Utils.py
This file has some utility classes/methods for our project.
'''

from math import floor
import pandas as pd
from sklearn.utils import shuffle
from sklearn.linear_model import LogisticRegression
from sklearn.preprocessing import OneHotEncoder, LabelEncoder
import numpy as np

'''
Class DataFrameCleaner
Purpose:

    Modularize dataset cleansing and manipulation done during each of our Logistic Regression trials
    to eliminate Code Reuse. Has ability to output Splitted Training and Testing Subsets.

Does:

    - Loads Data from Spotify DataSets
    - Ability to rename and normalize given features
    - Ability to one-hot-encode non numerical features and makes feature names readable for that.
    - Shuffles, Splits and returns proper training and testing data.
'''


class DataFrameCleaner:

    def __init__(self, songsPath=None, sessionsPath=None, df= None):
        if df is not None:
            self.df = df
        else:
            self.df = self.joinDFs(pd.read_csv(songsPath),
                               pd.read_csv(sessionsPath))


    def copy(self):
        return DataFrameCleaner(None,None,self.df.copy(deep=True))


    def joinDFs(self, songs, sessions):
        '''
         internal method joinDFs
        :does - joins sessions and songs dfs on track id key to create full dataset
              - shuffles df so training and testing have random distribution of songs.
        :param sessions: sessions dataset filepath
        :param songs: songs dataset filepath
        :return : joined df
        '''

        # clean sessions track id key, change y label to 'skipped' for readability
        sessions.rename(columns={'track_id_clean': 'track_id'}, inplace=True)
        sessions['not_skipped'] = sessions['not_skipped'].map(lambda x: not x)
        sessions.rename(columns={'not_skipped': 'skipped'}, inplace=True)

        # join and shuffle
        return shuffle(pd.merge(sessions, songs, on='track_id'))

    def filter(self, featuresWanted):
        '''
        :does - keep only features wanted for analysis based on features wanted list.
        :param featuresWanted: list of feature strings to project.
        :returns: void.
        '''
        # always place skipped at end.
        if 'skipped' in featuresWanted:
            featuresWanted.remove('skipped')

        self.df = self.df.loc[:, featuresWanted + ['skipped']]

    def pctNormalize(self, feature):
        '''
        :does - inplace mapping of non- normalized (skewed) feature to percentile rank of feature
        :param feature: skewed feature to "fix"
        :return: void.
        '''
        self.df[feature] = self.df[feature].rank(pct=True)

    def encode(self, feature):
        '''
        One-Hot-Encode categorical features, append these feature columns to this DFC's dataframe, remove the original
        feature column from this DFC's dataframe
        :param feature: categorical feature to encode
        :return: list of encoded features that replaced feature param.
        '''
        one_hot_features = pd.get_dummies(self.df[feature])
        self.df = pd.concat([self.df, one_hot_features], axis=1)
        return list(one_hot_features.columns)

    def setData(self, pctTrainingSplit=80):
        '''
        :does - sets training data and testing data. Each split has feature vector and label vector
                isolated.
        :param pctTrainingSplit: % of data records to have as training (1-pctTrainingSplit) will be
        testing %.
        :return: dictionary of {'training': tuple(X vector,y vector), 'testing' : tuple(X vector, y vector)}
        '''
        split_training_testing_index = floor(len(self.df.index) * float(pctTrainingSplit / 100))

        training = self.df[:split_training_testing_index]
        cols = list(training.columns)
        cols.remove('skipped')
        trainX = training.loc[:, cols]
        trainy = training['skipped']

        testing = self.df[split_training_testing_index:]
        cols = list(testing.columns)
        cols.remove('skipped')
        testX = testing.loc[:, cols]
        testy = testing['skipped']

        self.trainX = trainX
        self.trainy = trainy

        self.testX = testX
        self.testy = testy



'''
Utility Class to generate list of all subsets for given input list. Used as subroutine
throughout our project as brute force approach.
'''


class Subsets:
    def __init__(self, lst):
        self.lst = lst
        self.all_subsets = []

    def setSubsets(self):
        self.all_subsets = []
        self.subsetsHelper([], 0)

    def subsetsHelper(self, temp, index):
        if index < len(self.lst):
            self.subsetsHelper(temp, index + 1)
            temp = [] + temp
            temp.append(self.lst[index])
            self.subsetsHelper(temp, index + 1)
        else:
            if temp:
                self.all_subsets.append(temp)


def writeResults(fp, resultsDict, sort = True):
    '''
    Writes Results in form of "Feature(s).... Score", most often in  descending sorted order
    :param resultsDict k = feature(s) as tuple, v = score as float
    '''

    res_tuple_list = [[key] + [val] for key, val in resultsDict.items()]

    if sort:
        res_tuple_list.sort(key=lambda lst: lst[len(lst) - 1], reverse=True)

    with open(fp, "w+") as f:
        # if last line, dont add newline char.
        i = 0
        for element in res_tuple_list:
            out = str(element)
            if i != len(res_tuple_list) - 1:
                out += '\n'
            f.write(out)

            i += 1
