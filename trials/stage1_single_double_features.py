from Utils import DataFrameCleaner, writeResults
from sklearn.linear_model import LogisticRegression


def run1():
    """
    The first iteration of our exploration into skip prediction using logistic regression was to compare the accuracy of
    models trained on single and double sets of song features.
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

    ## Single Features

    all_results = {}
    for feature in FEATURES:
        model = LogisticRegression(solver='lbfgs', class_weight='balanced').fit(trainX.loc[:, [feature]], trainY)
        result = model.score(testX.loc[:, [feature]], testY)
        all_results[feature] = result

    # Write all results in sorted descending order based on prediction score
    writeResults('output/stage1_single_features_confusion.txt', all_results)

    # All pairs of features
    all_results = {}

    for i in range(len(FEATURES)):
        for j in range(i+1,len(FEATURES)):
            pair = [FEATURES[i], FEATURES[j]]
            model = LogisticRegression(solver='lbfgs', class_weight='balanced').fit(trainX.loc[:, pair], trainY)
            result = model.score(testX.loc[:, pair], testY)
            all_results[tuple(pair)] = result

    # Write all results in sorted descending order based on prediction score
    writeResults('output/stage1_double_features.txt', all_results)
