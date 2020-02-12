import pandas as pd
from sklearn.utils import shuffle

# import features and log datasets to join for training and testing.

full_features = pd.read_csv("tf_mini.csv")
full_logs = pd.read_csv("log_mini.csv")

# take only these features from log track_id_clean , not_skipped

logs = full_logs.loc[:, ['track_id_clean', 'not_skipped', 'hist_user_behavior_reason_start', 'context_type',
                         'long_pause_before_play', 'hist_user_behavior_is_shuffle', 'premium']]
logs.rename(columns={'track_id_clean': 'track_id'}, inplace=True)

# change not_skipped column to skipped (and flip boolean values)
logs['not_skipped'] = logs['not_skipped'].map(lambda x: not x)
logs.rename(columns={'not_skipped': 'skipped'}, inplace=True)

# join all song features with corresponding skip label
our_data = shuffle(pd.merge(logs, full_features, on='track_id'))

# "Normalize" skewed distributions
our_data['duration'] = our_data['duration'].rank(pct=True)
our_data['us_popularity_estimate'] = our_data['us_popularity_estimate'].rank(pct=True)
our_data['tempo'] = our_data['tempo'].rank(pct=True)

# Encode numeric value for non-numeric features
# non_numeric_features = ['hist_user_behavior_reason_start', 'context_type']
# labeler = LabelEncoder()
# for item in non_numeric_features:
#     labels = labeler.fit_transform(our_data[item])
#     mappings = {index: label for index, label in enumerate(labeler.classes_)}
#     our_data[item] = labels

# Dummy code the non-numerical features
features_list = ['duration', 'us_popularity_estimate', 'acousticness', 'danceability', 'energy',
                 'tempo', 'long_pause_before_play', 'hist_user_behavior_is_shuffle']
expanded_session_features = []
session_features = ['hist_user_behavior_reason_start', 'context_type']
for item in session_features:
    one_hot_features = pd.get_dummies(our_data[item])
    expanded_session_features = expanded_session_features + list(one_hot_features.columns)
    our_data = pd.concat([our_data, one_hot_features], axis=1)

print(list(one_hot_features.columns))
# Split testing and training data
'''
split_training_testing_index = floor(len(our_data.index) * 0.80)
training = our_data[:split_training_testing_index]
trainy = training['skipped']
testing = our_data[split_training_testing_index:]
testy = testing['skipped']

# Make a loop and perform regression once per feature in full features to explore quality of model.
subset_creator = Subsets(features_list)
subset_creator.setSubsets()
all_subsets = subset_creator.all_subsets
for set in all_subsets:
    set += expanded_session_features
results = {}

for feature_set in all_subsets:
    trainX = training.loc[:, feature_set]
    model = LogisticRegression(C=0.1, solver='lbfgs', class_weight='balanced').fit(trainX, trainy)
    result = model.score(testing.loc[:, feature_set], testy)
    results[tuple(feature_set)] = result

res_tuple_list = [list(key)+ [val] for key,val in results.items()]
res_tuple_list.sort(key=lambda tup:tup[len(tup)-1], reverse=True)
with open("new_all_subsets_session_features.txt","w+") as f:
    for element in res_tuple_list:
        f.write(str(element) + '\n')

'''
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
