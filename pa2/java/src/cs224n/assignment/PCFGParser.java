package cs224n.assignment;

import cs224n.ling.Tree;
import cs224n.util.*;

import java.util.*;

/**
 * The CKY PCFG Parser you will implement.
 */
public class PCFGParser implements Parser {
    private Grammar grammar;
    private Lexicon lexicon;


    public void train(List<Tree<String>> trainTrees) {
        // TODO: before you generate your grammar, the training trees
        // need to be binarized so that rules are at most binary

        for (int i = 0; i < trainTrees.size(); i++) {
            trainTrees.set(i, TreeAnnotations.annotateTree(trainTrees.get(i)));
        }

        lexicon = new Lexicon(trainTrees);
        grammar = new Grammar(trainTrees);
    }

    public Tree<String> getBestParse(List<String> sentence) {
        // TODO: implement this method
        /*
            CKY Algorithm (words, grammar):
            score = new double [#(words)+1][#(words-1)][#nonterms]
            back = new Pair[#(words)+1][#(words)+1][#nonterms]
            for i=0; i<(words); i++
                for A in nonterms:
                    if A -> words[i] in grammar
                        score[i][i+1][A]=P(A -> words[i])
                //handle unaries
                boolean added = true
                while added
                    added = false
                    for A, B in nonterms
                        if score[i][i+1][B] > 0 && A->B in grammar
                            prob = p(A->B)*score[i][i+1][B]
                            if prob > score[i][i+1][A]
                                score[i][i+1][A] = prob
                                back[i][i+1][A] = B
                                added = true
            for span = 2 to #(words)
                for begin = 0 to #(words - span
                    end = begin + span
                    for split = being+1 to end-1
                        for A,B,C in nonterms
                            prob = score[begin][split][B]*score[split[end][C] * P(A->BC)
                            if prob > score[begin][end][A]
                                score[begin][end][A] = prob
                                back[begin][end][A] = new Triple (split,B,C)
                    //handle unaries
                    boolean added = true
                    while added
                        added = false
                        for A, B in nonterms
                            prob = P(A->B)*score[begin][end][B]
                            if prob > score[begin][end][A]
                                score[begin][end][A] = prob
                                back[begin][end][A] = B
                                added = true
            return buildTree (score, back) //return most probable parse


         */
        return null;
    }
}
