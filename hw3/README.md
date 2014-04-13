The java decoder implements stack decoders with various pruning schemes.

 - With recombination, i.e. each lm_state is a key in the hashtable (dict in python) with a single hypothesis as value
 - With recombination, each lm_state is a key in the hashtable with a list of hypothesis stored in a priority queue
 - No recombination, all hypothesis are stored in a priority queue regardless of their lm_state.
 - Various beam size are experimented with the above stack data structure with the following prunings:
    - Only search the orderings k words before/after the stack index (#words being translated)
    - Set threshold on TM table and LM table (does not work well)
    - Distance penalty on the difference between word index and stack index. (No penalty always leads to better result, though)

