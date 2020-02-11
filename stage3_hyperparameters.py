from Utils import *
from sklearn.linear_model import LogisticRegression


def run3():

    """
    The third iteration of our exploration into skip prediction using logistic regression was to compare the accuracy of
    models trained with different hyperparameters.
    """

    FEATURES = ['duration', 'us_popularity_estimate', 'acousticness', 'danceability', 'energy', 'tempo']
    SONGS = 'data/tf_mini.csv'
    SESSIONS = 'data/log_mini.csv'

    ## Clean, Shuffle, Split our Data for regression trials.

    # Send data to DataFrameCleaner
    dfc = DataFrameCleaner(SONGS, SESSIONS)
    # Filter out the features we want

    dfc.filter(FEATURES)
    # set training and testing split

    dfc.setData()
    # Get train and testing splits
    trainX = dfc.trainX
    trainY = dfc.trainy
    testX = dfc.testX
    testY = dfc.testy

    subsetter = Subsets(FEATURES)
    subsetter.setSubsets()
    all_subsets = subsetter.all_subsets

    # Train on all subsets and save resulting score
    all_results = {}
    c_values = [.1, .2, .3, .4, .5, .6, .7, .8, .9, 1.0]
    for val in c_values:
        max_score = float("-inf")
        max_feature_set = None
        for feature in all_subsets:
            model = LogisticRegression(C=val, solver='lbfgs', class_weight='balanced').fit(trainX.loc[:, feature], trainY)
            result = model.score(testX.loc[:, feature], testY)
            if result > max_score:
                max_score = result
                max_feature_set = feature
                all_results[val] = (max_feature_set, max_score)


    # Write all results in sorted descending order based on prediction score
    writeResults('output/stage3_hyperparameters.txt', all_results)
