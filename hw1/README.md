The project impementation includes the following modules:

 - IBM model1
 - IBM model2
 - Diagonal tension on alignment
 - Combination of forward and backward alignment
 - Text normalization

The main parameters for tunning:

 - Which model to use
 - With or without NULL
 - Tension factor
 - Text normalization factor (normalization methods, max word length, etc.)

The best result is reached at the following parameter settings:
 - IBM model1
 - With diagonal tension
 - Without NULL
 - Combine forward backward, with intersection and alignments where neither e or f are not covered in the intersection
 - With lower case normalization
 - With maximum word length=5 normalization
 - Without number normalization

