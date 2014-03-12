#literal match
# return a vector the same length as hwords, each cell contains a index in rword vector as a match
import string_kernel
import pdb
import numpy as np
def stage1(rwords, hwords):
#    pdb.set_trace()
    hv=np.zeros(len(hwords))-np.ones(len(hwords))#initialie to all -1 vector:no match
    rv=np.zeros(len(rwords))#set to 1 if a word got matched with a word in hypo
    for i in range(len(hwords)):
        for j in range(len(rwords)):
            if rv[j]==0:
                hw=hwords[i].lower()
                rw=rwords[j].lower()
                if hw==rw:
                    hv[i]=j
                    rv[j]=0
    return hv

#looser match: character level match
#only apply to those who are not matched in the previous stage
def stage2(rwords, hwords, prevmatch):
    hv=np.zeros(len(hwords))-np.ones(len(hwords))
#    hv+=prevmatch
    taken=[]
    for ind in prevmatch:
        if ind !=-1:
            taken.append(ind)
    taken=set(taken)
#    pdb.set_trace()
    for i in range(len(hwords)):
        for j in range(len(rwords)):
            if prevmatch[i]==-1 and j not in taken: #both not matched yet
                hw=hwords[i].lower()
                rw=rwords[j].lower()
                if (hw in rw and len(hw)>=4) or (rw in hw and len(rw)>=4) or string_kernel.levenshtein(hw, rw)<3:
                    hv[i]=j
                    taken.add(j)
                    break
    return hv

#looser match: semantic level match
#def stage3(rwords, hwords, prevmatch):
'''    

for l in open("data/en-cs.pairs").readlines():
    ref, hyp=l.rstrip().split(' ||| ')
    match1=stage1(ref.split(), hyp.split())
    match2=stage2(ref.split(),hyp.split(),match1)
    print match1
    print match2

    raw_input()
   ''' 
