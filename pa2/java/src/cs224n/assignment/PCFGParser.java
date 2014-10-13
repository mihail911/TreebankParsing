package cs224n.assignment;

import cs224n.ling.Tree;
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
        return null;
    }
}
