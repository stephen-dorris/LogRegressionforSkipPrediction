from Utils import DataFrameCleaner, writeResults
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import confusion_matrix
from sklearn.preprocessing import LabelEncoder


def run6():
    """
    The sixth iteration of our exploration into skip prediction using logistic regression was to investigate the confusion
    matrices for predictions using individual features, individual normalized features, and, finally, with the optimal
    feature set that we have found so far.
    """

    FEATURES = ['duration', 'us_popularity_estimate', 'acousticness', 'danceability', 'energy', 'tempo']
    SONGS = 'tf_mini.csv'
    SESSIONS = 'log_mini.csv'
    ## Clean, Shuffle, Split our Data for regression trials.

    # All stage 6 results come from the same data split due to dfc.copy().

    # Send data to DataFrameCleaner
    dfc = DataFrameCleaner(SONGS, SESSIONS)

    # Copy the shuffled data to use to analyze our best feature set so far
    best = dfc.copy()

    # Filter out the features we want
    dfc.filter(FEATURES)

    # Copy the shuffled data to use to analyze normalized single features
    normalize = dfc.copy()

    # set training and testing split for single features (not normalized)
    dfc.setData()
    # Get train and testing splits
    trainX = dfc.trainX
    trainY = dfc.trainy
    testX = dfc.testX
    testY = dfc.testy

    # Single Features (not normalized)
    all_results = {}
    for feature in FEATURES:
        model = LogisticRegression(solver='lbfgs', class_weight='balanced').fit(trainX.loc[:, [feature]], trainY)
        result = round(model.score(testX.loc[:, [feature]], testY), 2)
        # Use predictions from model to create confusion matrix
        y_pred = model.predict(testX.loc[:, [feature]])
        cm = confusion_matrix(testY, y_pred)
        # Calculate percent accuracy for not skip labels (based on confusion matrix)
        pct_acc_not_skip = round(cm[0][0] / (cm[0][0] + cm[0][1]), 2)
        # Calculate percent accuracy for skip labels (based on confusion matrix)
        pct_acc_skip = round(cm[1][1] / (cm[1][0] + cm[1][1]), 2)
        # Save results
        all_results[feature] = "accuracy: {}, not skip accuracy {}, skip accuracy {}".format(result, pct_acc_not_skip,
                                                                                             pct_acc_skip)
    # Write all results in sorted descending order based on prediction score
    writeResults('output/stage6_single_features_confusion.txt', all_results)


    # Normalize features with skewed distribution
    skewed_features = ['duration', 'us_popularity_estimate', 'tempo']
    for feature in skewed_features:
        normalize.pctNormalize(feature)
    # set training and testing split
    normalize.setData()
    # Get train and testing splits
    trainX = normalize.trainX
    trainY = normalize.trainy
    testX = normalize.testX
    testY = normalize.testy

    all_results = {}
    for feature in FEATURES:
        model = LogisticRegression(solver='lbfgs', class_weight='balanced').fit(trainX.loc[:, [feature]], trainY)
        result = round(model.score(testX.loc[:, [feature]], testY), 2)
        # Use predictions from model to create confusion matrix
        y_pred = model.predict(testX.loc[:, [feature]])
        cm = confusion_matrix(testY, y_pred)
        # Calculate percent accuracy for not skip labels (based on confusion matrix)
        pct_acc_not_skip = round(cm[0][0] / (cm[0][0] + cm[0][1]), 2)
        # Calculate percent accuracy for skip labels (based on confusion matrix)
        pct_acc_skip = round(cm[1][1] / (cm[1][0] + cm[1][1]), 2)
        # Save results
        all_results[feature] = "accuracy: {}, not skip accuracy {}, skip accuracy {}".format(result, pct_acc_not_skip,
                                                                                             pct_acc_skip)

    writeResults('output/stage6_single_features_normalized_confusion.txt', all_results)




    BEST_FEATURES = ['duration', 'us_popularity_estimate', 'acousticness',
                     'energy', 'tempo', 'hist_user_behavior_reason_start',
                     'context_type']

    best.filter(BEST_FEATURES)

    # Encode categorical features
    categorical_features = ['hist_user_behavior_reason_start', 'context_type']
    labeler = LabelEncoder()
    for feature in categorical_features:
        labels = labeler.fit_transform(best.df[feature])
        mappings = {index: label for index, label in enumerate(labeler.classes_)}
        best.df[feature] = labels

    # set training and testing split
    best.setData()
    # Get train and testing splits
    trainX = best.trainX
    trainY = best.trainy
    testX = best.testX
    testY = best.testy

    model = LogisticRegression(solver='lbfgs', class_weight='balanced').fit(trainX.loc[:, BEST_FEATURES], trainY)
    result = round(model.score(testX.loc[:, BEST_FEATURES], testY), 2)
    # Use predictions from model to create confusion matrix
    y_pred = model.predict(testX.loc[:, BEST_FEATURES])
    cm = confusion_matrix(testY, y_pred)
    # Calculate percent accuracy for not skip labels (based on confusion matrix)
    pct_acc_not_skip = round(cm[0][0] / (cm[0][0] + cm[0][1]), 2)
    # Calculate percent accuracy for skip labels (based on confusion matrix)
    pct_acc_skip = round(cm[1][1] / (cm[1][0] + cm[1][1]), 2)
    # Save results
    total_result = "accuracy: {}, not skip accuracy {}, skip accuracy {}".format(result, pct_acc_not_skip, pct_acc_skip)

    with open('output/stage6_best_confusion.txt', 'w') as confusion:
        confusion.write(total_result)

