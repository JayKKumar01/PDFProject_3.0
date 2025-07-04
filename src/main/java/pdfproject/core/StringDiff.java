package pdfproject.core;

import pdfproject.Config;
import pdfproject.constants.Operation;
import pdfproject.models.WordInfo;
import pdfproject.utils.FontInfoUtil;
import pdfproject.utils.WordUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for comparing two lists of WordInfo objects and identifying differences.
 */
public class StringDiff {


    /**
     * Gets a custom list containing information about equal, deleted, and added WordInfo objects.
     *
     * @param words1 List of WordInfo objects from the first source.
     * @param words2 List of WordInfo objects from the second source.
     * @return CustomList containing equal, deleted, and added WordInfo objects.
     */
    public static List<WordInfo> compare(List<WordInfo> words1, List<WordInfo> words2) {
        if (words1 == null || words2 == null){
            return null;
        }
        List<WordInfo> result = new ArrayList<>();
        int m = words1.size();
        int n = words2.size();

        int[][] LCSuffix = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (words1.get(m - i).getWord().equals(words2.get(n - j).getWord())) {
                    LCSuffix[i][j] = LCSuffix[i - 1][j - 1] + 1;
                } else {
                    LCSuffix[i][j] = Math.max(LCSuffix[i - 1][j], LCSuffix[i][j - 1]);
                }
            }
        }

        int i = m;
        int j = n;
        while (i > 0 && j > 0) {
            WordInfo wordInfo1 = words1.get(m - i);
            WordInfo wordInfo2 = words2.get(n - j);
            String w1 = wordInfo1.getWord();
            String w2 = wordInfo2.getWord();
            if (w1.equals(w2)) {

                if (WordUtil.isWordInfoSame(wordInfo1, wordInfo2)) {
                    wordInfo1.addOperation(Operation.EQUAL);
                } else {
                    wordInfo2.setOtherTextPositions(wordInfo1.getTextPositions());
                    FontInfoUtil.setFontDiffInfo(wordInfo2);
                    result.add(wordInfo2);
                }
                i--;
                j--;
            } else if (LCSuffix[i - 1][j] > LCSuffix[i][j - 1]) {
                wordInfo1.addOperation(Operation.DELETED);
                FontInfoUtil.setFontInfo(Operation.DELETED, wordInfo1);
                result.add(wordInfo1);
                i--;
            } else {
                wordInfo2.addOperation(Operation.ADDED);
                FontInfoUtil.setFontInfo(Operation.ADDED, wordInfo2);
                result.add(wordInfo2);
                j--;
            }
        }

        while (i > 0) {
            // confirm del here also, use refresh list after detecting issue
            WordInfo wordInfo1 = words1.get(m - i);
            wordInfo1.addOperation(Operation.DELETED);
            FontInfoUtil.setFontInfo(Operation.DELETED, wordInfo1);
            result.add(wordInfo1);
            i--;
        }

        while (j > 0) {
            WordInfo wordInfo2 = words2.get(n - j);
            wordInfo2.addOperation(Operation.ADDED);
            FontInfoUtil.setFontInfo(Operation.ADDED, wordInfo2);
            result.add(wordInfo2);
            j--;
        }
        return result;
    }
}