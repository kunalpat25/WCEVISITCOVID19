package com.wce.wcevisitcovid19.utils;

import com.wce.wcevisitcovid19.models.Visitor;

import java.util.ArrayList;
import java.util.List;

public class SearchUtils {

    public static boolean isPresent(String filterString, CharSequence constraint)
    {
        filterString = filterString.toLowerCase();
        String process = constraint + "@" + filterString;
        int[] current = zFunction(process);
        boolean isPresent = false;
        int targetSize = constraint.length();
        for (int j = 0; j < filterString.length(); j++) {
            if (current[j + targetSize + 1] == targetSize) {
                isPresent = true;
                break;
            }
        }
        return isPresent;
    }

    public static int[] zFunction(String str) {
        str = str.toLowerCase();
        int[] zArray = new int[str.length()+1];
        int n = str.length();

        // [L,R] make a window which matches with
        // prefix of s
        int L = 0, R = 0;

        for (int i = 1; i < n; ++i) {

            // if i>R nothing matches so we will calculate.
            // Z[i] using naive way.
            if (i > R) {

                L = R = i;

                // R-L = 0 in starting, so it will start
                // checking from 0'th index. For example,
                // for "ababab" and i = 1, the value of R
                // remains 0 and Z[i] becomes 0. For string
                // "aaaaaa" and i = 1, Z[i] and R become 5

                while (R < n && str.charAt(R - L) == str.charAt(R))
                    R++;

                zArray[i] = R - L;
                R--;

            } else {

                // k = i-L so k corresponds to number which
                // matches in [L,R] interval.
                int k = i - L;

                // if Z[k] is less than remaining interval
                // then Z[i] will be equal to Z[k].
                // For example, str = "ababab", i = 3, R = 5
                // and L = 2
                if (zArray[k] < R - i + 1)
                    zArray[i] = zArray[k];

                    // For example str = "aaaaaa" and i = 2, R is 5,
                    // L is 0
                else {


                    // else start from R and check manually
                    L = i;
                    while (R < n && str.charAt(R - L) == str.charAt(R))
                        R++;

                    zArray[i] = R - L;
                    R--;
                }
            }
        }
        return zArray;
    }
}
