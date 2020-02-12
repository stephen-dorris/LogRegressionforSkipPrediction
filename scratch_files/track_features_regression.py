import sklearn as sk
from math import floor
import pandas as pd
from sklearn.utils import shuffle
from sklearn.linear_model import LogisticRegression
import numpy as np


# import features and log datasets to join for training and testing.

full_features = pd.read_csv("tf_mini.csv")
full_logs = pd.read_csv("log_mini.csv")


# take only these features from log track_id_clean , not_skipped

logs = full_logs.loc[:, ['track_id_clean', 'not_skipped']]
logs.rename(columns={'track_id_clean':'track_id'}, inplace=True)

# change not_skipped column to skipped (and flip boolean values)
logs['not_skipped'] = logs['not_skipped'].map(lambda x: not x)
logs.rename(columns={'not_skipped':'skipped'}, inplace=True)

# join all song features with corresponding skip label
our_data = shuffle(pd.merge(logs, full_features, on='track_id'))
# "Normalize" pop estimate by subtracting the minimum value from each data point

our_data['us_popularity_estimate'] = our_data['us_popularity_estimate'].map(lambda x: x - 90.0188995011263)

split_training_testing_index = floor(len(our_data.index) * 0.80)

training = our_data[:split_training_testing_index]
trainy = training['skipped']
testing = our_data[split_training_testing_index:]
testy = testing['skipped']

# Make a loop and perform regresssion once per feature in full features to explore quality of model.
features_list = ['duration', 'us_popularity_estimate', 'acousticness', 'danceability', 'energy', 'tempo']

subset_dict = {'a':0,'b':1,'c':2,'d':3,'e':4,'f':5}

all_subsets = []
for a in range(2):
    for b in range(2):
        for c in range(2):
            for d in range(2):
                for e in range(2):
                    for f in range(2):
                        add = []
                        if a == 1:
                            add.append(features_list[subset_dict['a']])
                        if b == 1:
                            add.append(features_list[subset_dict['b']])
                        if c == 1:
                            add.append(features_list[subset_dict['c']])
                        if d == 1:
                            add.append(features_list[subset_dict['d']])
                        if e == 1:
                            add.append(features_list[subset_dict['e']])
                        if f == 1:
                            add.append(features_list[subset_dict['f']])
                        if add:
                            all_subsets.append(add)

results = {}
c_results = []
c_values = [.1, .2, .3, .4, .5, .6, .7, .8, .9, 1.0]
for val in c_values:
    max_score = float("-inf")
    max_feature_set = None
    for feature_set in all_subsets:
        trainX = training.loc[:,feature_set]
        model = LogisticRegression(C=val, solver='lbfgs', class_weight='balanced').fit(trainX, trainy)
        result = model.score(testing.loc[:,feature_set], testy)
        if result > max_score:
            max_score = result
            max_feature_set = feature_set
    c_results.append((val, max_feature_set, max_score))

print(c_results)

# res_tuple_list = [list(key)+ [val] for key,val in results.items()]
# res_tuple_list.sort(key=lambda tup:tup[len(tup)-1], reverse=True)
# with open("all_subsets_features.txt","w+") as f:
#     for element in res_tuple_list:
#         f.write(str(element) + '\n')

'''
for feature1 in features_list:
    trainX = training.loc[:, [feature1]]
    model = LogisticRegression(C=.5, solver='lbfgs', class_weight='balanced').fit(trainX, trainy)
    results[feature1] = model.score(testing.loc[:, [feature1]], testy)
    single_feature_score = results[feature1]
    max_difference_amt = float("-inf")
    max_difference_feature = None
    for feature2 in [x for x in features_list if x!=feature1]:
        trainX = training.loc[:,[feature1,feature2]]
        model = LogisticRegression(C=.5,solver='lbfgs',class_weight='balanced').fit(trainX, trainy)
        results[feature1+" and "+feature2] = model.score(testing.loc[:, [feature1,feature2]], testy)
        single_feature_score - results[feature1+" and "+feature2] > max_difference_amt:





with open("single_and_double_features.txt","w+") as f:
    for x,y in results.items():
        f.write(str((x,y))+'\n')

    #trainX = pd.DataFrame(current_feature_data[feature])
    
    
    
    
    results[tuple(feature_set)]
'''









# find popularity feature for each song in logs, randomly shuffle for randomization an split into
# disjoint training and testing datasets.



'''

trainX = pd.DataFrame(training['us_popularity_estimate'])
trainy = training['skipped']

testX = pd.DataFrame(testing['us_popularity_estimate'])
testy = testing['skipped']


# Parameters we are setting for the log reg:  verbosity = 3
# 1) fit on Xtrain and Ytrain
model = LogisticRegression(C=.2, solver='liblinear', max_iter=100).fit(trainX, trainy)
# 2) score on Xtest and Ytest
results = model.score(testX, testy)
print(results)

'''

