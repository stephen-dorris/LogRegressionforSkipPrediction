

# SkipPrediction
AI project to explore the basics of logistic regression inspired by 
[spotify challenge] (https://www.crowdai.org/challenges/spotify-sequential-skip-prediction-challenge)


We used the conda distribution 4.7.12 :
  -  sklearn library for regression models  
  - pandas  library for database transformation  

# Folders within AIProject/ 

## data
   - tf_mini.csv which has all song features
   - log_mini.csv has sequential listening  sessions
   - both can be joined together on track_id for combined analysis. 

## stage_scripts 
   -  has all of our scripts detailing our progress and learning experience by using logistic regression. Each __stage_x.py__  corresponds to  training of models with different features or hyper parameters, 
   with one of more output files written. 
   -  __Utils.py__ provides a helper function for writing sorted output to the file , producing all subset of given input feature set or testing, and a DataFrameCleaner 
   class for better modularization working with our dataset. 
   
   - __main.py__ runs all stage scripts (with output being written) to output folder.  
## output 
   - for each stage there is one ore more ouput files with  a general schema of "feature,(s), accuracy" where accuracy 
   is  a float describing the  % of test data where the model trained on a given feature(s) accurately predicts 
   the "skip" label. We transformed the "not_skip" label from log_mini.csv to the boolean negation of that label for easier reading: (skip= true means the user skipped the song)
   
   
   We hope there  is enough documentation in each file for easy reading. 
   
##  Authors :  [Stephen Dorris](https//github.com/stephen-dorris) and [April Gustafson] (https://github.com/ajgustafson)   
