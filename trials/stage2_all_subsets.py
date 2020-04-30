from Utils import *
from sklearn.linear_model import LogisticRegression


def run2():
    """
    The second iteration of our exploration into skip prediction using logistic regression was to compare the accuracy of
    models trained on every subset of chosen song features.
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
    for feature in all_subsets:
        model = LogisticRegression(solver='lbfgs', class_weight='balanced').fit(trainX.loc[:, feature], trainY)
        result = model.score(testX.loc[:, feature], testY)
        all_results[tuple(feature)] = result

    # Write all results in sorted descending order based on prediction score
    writeResults('output/stage2_all_subsets.txt', all_results)
