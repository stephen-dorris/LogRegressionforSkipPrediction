from Utils import *
from sklearn.linear_model import LogisticRegression


def run4():

    """
    The fourth iteration of our exploration into skip prediction using logistic regression was to compare the accuracy of
    models trained with some features from the session log.  This included transforming categorical attributes (but not
    encoding them).
    """

    FEATURES = ['duration', 'us_popularity_estimate', 'acousticness', 'danceability', 'energy', 'tempo']
    SONGS = 'data/tf_mini.csv'
    SESSIONS = 'data/log_mini.csv'

    ## Clean, Shuffle, Split our Data for regression trials.

    # Send data to DataFrameCleaner
    dfc = DataFrameCleaner(SONGS, SESSIONS)

    # Filter out the features we want
    dfc.filter(FEATURES)

    ## Normalized Data
    normalize = dfc.copy()

    # Normalize features with skewed distribution
    skewed_features = ['duration', 'us_popularity_estimate', 'tempo']
    for feature in skewed_features:
        normalize.pctNormalize(feature)
    # set training and testing split
    normalize.setData()
    # Get train and testing splits
    normalized_trainX = normalize.trainX
    normalized_trainY = normalize.trainy
    normalized_testX = normalize.testX
    normalized_testY = normalize.testy

    ## Non-Normalized Data

    dfc.setData()
    trainX = dfc.trainX
    trainY = dfc.trainy
    testX = dfc.testX
    testY = dfc.testy

    ## Test Difference in Single Features between Normalization and Non-Normalization.

    all_results = {}
    for feature in skewed_features:
        model = LogisticRegression(solver='lbfgs', class_weight='balanced').fit(trainX.loc[:, [feature]], trainY)
        result = model.score(testX.loc[:, [feature]], testY)
        normalized_model = LogisticRegression(solver='lbfgs', class_weight='balanced').fit(
            normalized_trainX.loc[:, [feature]], normalized_trainY)
        normalized_result = normalized_model.score(normalized_testX.loc[:, [feature]], normalized_testY)

        all_results[feature] = (result, normalized_result)

    # Write all results in sorted descending order based on prediction score
    # writeResults('output/stage4_normalize_single.txt', all_results)


    # Test whether adding normalized features changes best feature set (score) .
    ## All subsets of features with normalized distributions
    subsetter = Subsets(FEATURES)
    subsetter.setSubsets()
    all_subsets = subsetter.all_subsets
    # Train on all subsets and save resulting score
    all_results = {}

    normalized_subsets = []

    skewed_features = ['duration', 'us_popularity_estimate', 'tempo']

    for set in all_subsets:
        if 'duration' in set:

            normalized_subsets.append(set)
        elif 'us_popularity_estimate' in set:
            normalized_subsets.append(set)

        elif 'tempo' in set:
            normalized_subsets.append(set)


    for feature in normalized_subsets:
        model = LogisticRegression(solver='lbfgs', class_weight='balanced').fit(trainX.loc[:, feature], trainY)
        result = model.score(testX.loc[:, feature], testY)

        normalized_model = LogisticRegression(solver='lbfgs', class_weight='balanced').fit(normalized_trainX.loc[:, feature], normalized_trainY)
        normalized_result = normalized_model.score(normalized_testX.loc[:, feature], normalized_testY)

        all_results[tuple(feature)] = normalized_result - result

    # Write all results in sorted descending order based on prediction score
    writeResults('output/stage4_normalized_difference.txt', all_results)

