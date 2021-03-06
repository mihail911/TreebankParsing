package cs224n.assignment;

import cs224n.ling.Tree;
import cs224n.util.*;

import java.lang.reflect.Array;
import java.util.*;

/**
 * The CKY PCFG Parser you will implement.
 */
public class PCFGParser implements Parser {
    private Grammar grammar;
    private Lexicon lexicon;


    public void train(List<Tree<String>> trainTrees) {
        // Ensure tree is binarized, so rules are at most binary
        for (int i = 0; i < trainTrees.size(); i++) {
            trainTrees.set(i, TreeAnnotations.annotateTree(trainTrees.get(i)));
        }

        lexicon = new Lexicon(trainTrees);
        grammar = new Grammar(trainTrees);
        return;
    }

    public Tree<String> getBestParse(List<String> sentence) {
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
        ArrayList<ArrayList<HashMap<String, Triplet<Integer,String,String>>>> backPointers = new ArrayList<ArrayList<HashMap<String, Triplet<Integer,String,String>>>>();
        for(int i = 0; i <= numWords; ++i){
            backPointers.add(new ArrayList<HashMap<String, Triplet<Integer,String,String>>>());
            for(int j = 0; j <= numWords; ++j){
                backPointers.get(i).add(new HashMap<String, Triplet<Integer,String,String>>());
            }
        }

        // for i=0; i<(words); i++
        for(int i = 0; i < numWords; ++i){

            // for A in preterminals:
            String word = sentence.get(i);
            for (String tag: lexicon.getAllTags()) {
                // if A -> words[i] in grammar
                scores.get(i).get(i+1).setCount(tag, lexicon.scoreTagging(word, tag));
            }
            // expand unary
            boolean added = true;
            while (added) {
                added = false;
                // for A->B in non-terminals
                Set<String> Bs = grammar.unaryRulesByChild.keySet();
                for(String B: Bs){
                    List<Grammar.UnaryRule> unaryRules = grammar.getUnaryRulesByChild(B);
                    for(Grammar.UnaryRule rule: unaryRules){
                        String A = rule.getParent();
                        if(scores.get(i).get(i+1).getCount(B) > 0){
                            double prob = rule.getScore() * scores.get(i).get(i+1).getCount(B);
                            if(prob > scores.get(i).get(i+1).getCount(A)){
                                scores.get(i).get(i+1).setCount(A, prob);
                                backPointers.get(i).get(i+1).put(A, new Triplet<Integer, String, String>(-1, B, null));

                                added = true;
                            }
                        }
                    }
                }
            }
        }
        for (int span=2; span<numWords+1; span++) {

            // for begin = 0 to #(words) - span
            for (int begin=0; begin<(numWords-span+1); begin++) {
                int end = begin + span;
                // for split = begin+1 to end-1
                for (int split=begin+1; split<end; split++) {

                    // for A->B,C in nonterms
                    Set<String> Bs = grammar.binaryRulesByLeftChild.keySet();
                    for (String B: Bs) {
                        List<Grammar.BinaryRule> binaryRules = grammar.getBinaryRulesByLeftChild(B);
                        for (Grammar.BinaryRule rule : binaryRules) {
                            String A = rule.getParent();
                            String C = rule.getRightChild();
                            double prob = scores.get(begin).get(split).getCount(B) * scores.get(split).get(end).getCount(C) * rule.getScore();
                            if (prob > scores.get(begin).get(end).getCount(A)) {
                                scores.get(begin).get(end).setCount(A, prob);
                                backPointers.get(begin).get(end).put(A, new Triplet<Integer, String, String>(split, B, C));
                            }
                        }
                    }
                }

                // handle unaries
                boolean added = true;
                while (added) {
                    added = false;
                    // for A->B in non-terminals
                    Set<String> Bs = grammar.unaryRulesByChild.keySet();
                    for(String B: Bs){
                        List<Grammar.UnaryRule> unaryRules = grammar.getUnaryRulesByChild(B);
                        for(Grammar.UnaryRule rule: unaryRules){
                            String A = rule.getParent();
                            double prob = rule.getScore() * scores.get(begin).get(end).getCount(B);
                            if (prob > scores.get(begin).get(end).getCount(A)) {
                                scores.get(begin).get(end).setCount(A, prob);
                                backPointers.get(begin).get(end).put(A, new Triplet<Integer, String, String>(-1, B, null));
                                added = true;
                            }
                        }
                    }
                }
            }
        }
        return buildTree(sentence, backPointers);
    }

    public Tree<String> buildTree(List<String> sentence, ArrayList<ArrayList<HashMap<String, Triplet<Integer,String,String>>>> backPointers) {
        Tree<String> tree = buildTreeRecur(sentence, 0, backPointers.size()-1, "ROOT", backPointers);
        return TreeAnnotations.unAnnotateTree(tree);
    }

    private Tree<String> buildTreeRecur(List<String> sentence, int begin, int end, String symbol, ArrayList<ArrayList<HashMap<String, Triplet<Integer,String,String>>>> backPointers) {
        // create a tree with the symbol node as the root
        Tree<String> tree = new Tree<String>(symbol);

        // base case: root is preterminal A->B
        if (lexicon.getAllTags().contains(symbol)) {
            // attach the word at this position to
            Tree<String> terminal = new Tree<String>(sentence.get(begin));
            List<Tree<String>> children = Collections.singletonList(terminal);
            tree.setChildren(children);
            return tree;
        }

        // general case: A->B,C
        Triplet<Integer, String, String> backPointer = backPointers.get(begin).get(end).get(symbol);
        int split = backPointer.getFirst();
        String B = backPointer.getSecond();
        String C = backPointer.getThird();

        if (split == -1) {
            // unary rule: look up again
            Tree<String> nonterminal = buildTreeRecur(sentence, begin, end, B, backPointers);
            List<Tree<String>> children = Collections.singletonList(nonterminal);
            tree.setChildren(children);
            return tree;
        } else {
            // binary rule: do left and do right
            Tree<String> BTree = buildTreeRecur(sentence, begin, split, B, backPointers);
            Tree<String> CTree = buildTreeRecur(sentence, split, end, C, backPointers);
            List<Tree<String>> children = new ArrayList<Tree<String>>();
            children.add(BTree);
            children.add(CTree);
            tree.setChildren(children);
            return tree;
        }
    }
}
