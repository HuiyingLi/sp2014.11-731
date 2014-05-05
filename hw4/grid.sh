for f in `ls $1`
do
    echo $1/$f
#    cat tmp3_3/$f |./score-meteor|grep 'Final score:'
    cat $1/$f |./score-bleu
done
