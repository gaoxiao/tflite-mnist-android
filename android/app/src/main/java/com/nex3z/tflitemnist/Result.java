package com.nex3z.tflitemnist;

import java.util.HashMap;
import java.util.Map;

public class Result {

    private final long mTimeCost;
    private String result;

    public Result(int[] idx, long timeCost) {
        mTimeCost = timeCost;
        result = Vocab.getResult(idx);
    }


    public String getResult() {
        return result;
    }

    public long getTimeCost() {
        return mTimeCost;
    }
}

class Vocab {
    private static Map<Integer, String> idToToken = new HashMap<Integer, String>() {{
        put(0, ".");
        put(1, "/");
        put(2, "0");
        put(3, "1");
        put(4, "2");
        put(5, "3");
        put(6, "4");
        put(7, "5");
        put(8, "6");
        put(9, "7");
        put(10, "8");
        put(11, "9");
        put(12, "_UNK");
        put(13, "_PAD");
        put(14, "_END");
    }};

    static String getResult(int[] idx) {
        StringBuilder sb = new StringBuilder();
        for (int id : idx) {
            if (id == 14) {
                break;
            }
            sb.append(idToToken.get(id));
        }
        return sb.toString();
    }
}

//0 .
//1 /
//2 0
//3 1
//4 2
//5 3
//6 4
//7 5
//8 6
//9 7
//10 8
//11 9
//12 _UNK
//13 _PAD
//14 _END
