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
        return;
    }

    public Tree<String> getBestParse(List<String> sentence) {
        // TODO: implement this method
        int numWords = sentence.size();
        //Initialize scores array
        ArrayList<ArrayList<Counter<String>>> scores = new ArrayList<ArrayList<Counter<String>>>();
        for(int i = 0; i <= numWords; ++i){
            scores.add(new ArrayList<Counter<String>>());
            for(int j = 0; j <= numWords; ++j){
                scores.get(i).add(new Counter<String>());
            }
        }
        //Initialize backPointer array
        ArrayList<ArrayList<Counter<Pair<Integer,Integer>>>> backPointers = new ArrayList<ArrayList<Counter<Pair<Integer,Integer>>>>();
        for(int i = 0; i <= numWords; ++i){
            backPointers.add(new ArrayList<Counter<Pair<Integer,Integer>>>());
            for(int j = 0; j <= numWords; ++j){
                backPointers.get(i).add(new Counter<Pair<Integer,Integer>>());
            }
        }

        for(int i = 0; i < numWords; ++i){
            String word = sentence.get(i);
            for (String tag: lexicon.getAllTags()){
                if(lexicon.isKnown(word) && lexicon.wordToTagCounters.getCount(word,tag) != 0){
                    double prob = lexicon.scoreTagging(word,tag);
                    scores.get(i).get(i+1).setCount(tag,prob);
                }
            }
            //Expand unary
            boolean added = true;
            while (added){
                added = false;
                Set<String> children = grammar.unaryRulesByChild.keySet();
                //Iterating over B in A->B
                for(String child: children){
                    List<Grammar.UnaryRule> unaryRules = grammar.getUnaryRulesByChild(child);
                    //Iterate over all A
                    for(Grammar.UnaryRule rule: unaryRules){
                        String parent = rule.parent;

                        double scoreChild = scores.get(i).get(i+1).getCount(child);
                        if(scoreChild > 0){
                            double prob = rule.getScore() * scoreChild;
                            double scoreParent = scores.get(i).get(i+1).getCount(parent);
                            if(prob > scoreParent){
                                scores.get(i).get(i+1).setCount(parent,prob);

                                added = true;
                            }
                        }
                    }

                }
            }
        }
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
                    for split = begin+1 to end-1
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
