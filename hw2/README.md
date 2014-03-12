Implemented Algorithms:
 - Sentence level BLEU (up to trigram, though only unigram and bigram are used)
 - Metoer style alignment/map
Implemented string kenels:
 - Levenshtein distance. Comments: very slow
 - Longest common sequence. Comments: faster than edit distance 
 - String prefix
Other features:
 - Language Model feature, Czech language model built by mitLM toolkit with Katz backoff (up to trigram, not used since it lower results)
 - Semantic similarity through vectors built on Czech corpus by Word2Vec tool (not used since it lower results)
