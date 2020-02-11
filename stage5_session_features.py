from Utils import *
from sklearn.linear_model import LogisticRegression


def run5():

    """
    The fifth iteration of our exploration into skip prediction using logistic regression was to compare the accuracy of
    models trained with some features from the session log.  This included transforming and encoding categorical attributes.
    """
    FEATURES = ['duration', 'us_popularity_estimate', 'acousticness', 'danceability', 'energy', 'tempo',
                'hist_user_behavior_reason_start', 'context_type']
    SONGS = 'data/tf_mini.csv'
    SESSIONS = 'data/log_mini.csv'

    ## Clean, Shuffle, Split our Data for regression trials.

    # Send data to DataFrameCleaner
    dfc = DataFrameCleaner(SONGS, SESSIONS)

    x = dfc.df

    # Filter out the features we want
    dfc.filter(FEATURES)

    # Normalize skewed features
    skewed_features = ['duration', 'us_popularity_estimate', 'tempo']
    for feature in skewed_features:
        dfc.pctNormalize(feature)

    # Encode categorical features
    categorical_features = ['hist_user_behavior_reason_start', 'context_type']
    expanded_session_features = []
    for feature in categorical_features:
        FEATURES.remove(feature)
        expanded_session_features = expanded_session_features + dfc.encode(feature)

    # set training and testing split
    dfc.setData()
    # Get train and testing splits
    trainX = dfc.trainX
    trainY = dfc.trainy
    testX = dfc.testX
    testY = dfc.testy

    # Train on all subsets of original features + expanded session features and save resulting score
    subset_creator = Subsets(FEATURES)
    subset_creator.setSubsets()
    all_subsets = subset_creator.all_subsets
    for set in all_subsets:
        set += expanded_session_features

    all_results = {}
    for feature in all_subsets:
        model = LogisticRegression(C=0.1, solver='lbfgs', class_weight='balanced').fit(trainX.loc[:, feature], trainY)
        result = model.score(testX.loc[:, feature], testY)
        all_results[tuple(feature)] = result

    # Write all results in sorted descending order based on prediction score
    writeResults('output/stage5_session_features.txt', all_results)
