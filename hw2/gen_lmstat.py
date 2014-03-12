import pdb
import pickle
import io
lm=pickle.load(open("lm.pickle","rb"))
def build_lm():
    useful={}
    for l in open("hyp.ngram").readlines():
        useful[l.strip()]=1

    lc=0
    v={}
    try:
        for l in open("cssem.lm").readlines():
            lc+=1
            if lc%10000==0:
                print "processed", lc
#            l=l.encode('utf-8')
            l=l.strip()
            if not l.startswith("\\") and len(l)>0:
                spl=l.strip().split("\t")
                if len(spl)==3:
                    f,gram,b=l.split("\t")
                    if gram in useful:
                        v[gram]=tuple([float(f),float(b)])
    except UnicodeDecodeError:
            print l
            print lc
#        pdb.set_trace()
    pickle.dump(v,open("lm.pickle","wb"))

##takes in unigram, bigram and trigram, as a string
def calc_ngramlm(ngram):
#    pdb.set_trace()
    words=ngram.split()
    n=len(words)
    if ngram in lm:
        return lm[ngram][0]
    else:
        if n==1:
            return -6.0
        elif n==2:
            if words[0] in lm:
                alpha=lm[words[0]][1]
            else:
                alpha=-3
            return calc_ngramlm(ngram.split()[1])+alpha
        elif n==3:
            xy=words[0]+" "+words[1]
            if xy in lm:
                alpha=lm[xy][1]
                return alpha+calc_ngramlm(words[1]+" "+words[2])
            else:
                return calc_ngramlm(words[2])
                
##takes in a list of ngrams
def calc_sentlm(words, n):
    sum=0
    if len(words)>0:
        if n==1 or len(words)==1:
            for w in words:
                sum+=calc_ngramlm(w)
            return sum*1.0/len(words)
        elif n==2 or len(words)>=2:
            for i in range(len(words)-1):
                bi=words[i]+" "+words[i+1]
                sum+=calc_ngramlm(bi)
            return sum*1.0/(len(words)-1)
        elif n==3 and len(words)>2:
            for i in range(len(words)-2):
                tri=words[i]+" "+words[i+1]+" "+words[i+2]
                sum+=calc_ngramlm(tri)
            return sum*1.0/(len(words)-2)
#    return 0

#print calc_sentlm("na jsou to na a", 2)
