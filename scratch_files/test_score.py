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
logs.rename(columns={'track_id_clean': 'track_id'}, inplace=True)

# change not_skipped column to skipped (and flip boolean values)
logs['not_skipped'] = logs['not_skipped'].map(lambda x: not x)
logs.rename(columns={'not_skipped': 'skipped'}, inplace=True)

# join all song features with corresponding skip label
our_data = shuffle(pd.merge(logs, full_features, on='track_id'))
# "Normalize" columns that contain values that are not between 0 and 1
our_data['rank_duration'] = our_data['duration'].rank(pct=True)
our_data['rank_release_year'] = our_data['release_year'].rank(pct=True)
our_data['rank_us_popularity_estimate'] = our_data['us_popularity_estimate'].rank(pct=True)
our_data['rank_dyn_range_mean'] = our_data['dyn_range_mean'].rank(pct=True)
our_data['rank_instrumentalness'] = our_data['instrumentalness'].rank(pct=True)
our_data['rank_loudness'] = our_data['loudness'].rank(pct=True)
our_data['rank_tempo'] = our_data['tempo'].rank(pct=True)

split_training_testing_index = floor(len(our_data.index) * 0.80)

training = our_data[:split_training_testing_index]
trainy = training['skipped']

testing = our_data[split_training_testing_index:]
testy = testing['skipped']

features_list = ['duration', 'release_year', 'us_popularity_estimate', 'dyn_range_mean', 'loudness',
                 'tempo']
rank_features_list = ['rank_duration', 'rank_release_year', 'rank_us_popularity_estimate', 'rank_dyn_range_mean',
                      'rank_loudness', 'rank_tempo']
comparison = {}
for (feature, rank_feature) in zip(features_list, rank_features_list):
    comparison[feature] = []
    trainX = training.loc[:, [feature]]
    testX = testing.loc[:, [feature]]
    model = LogisticRegression(C=0.1, solver='lbfgs', class_weight='balanced').fit(trainX, trainy)
    comparison[feature].append(model.score(testX, testy))

    train_rank_X = training.loc[:, [rank_feature]]
    test_rank_X = testing.loc[:, [rank_feature]]
    rank_model = LogisticRegression(C=0.1, solver='lbfgs', class_weight='balanced').fit(train_rank_X, trainy)
    comparison[feature].append(rank_model.score(test_rank_X, testy))

for feature in comparison:
    print(feature, "\t", comparison[feature])
    print(round(comparison[feature][1] - comparison[feature][0], 3))






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