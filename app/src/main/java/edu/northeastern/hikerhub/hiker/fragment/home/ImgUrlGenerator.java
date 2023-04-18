package edu.northeastern.hikerhub.hiker.fragment.home;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ImgUrlGenerator {
    private static ImgUrlGenerator instance;
    private static List<String> imgUrlList = new ArrayList<>();
    private ImgUrlGenerator() {
        imgUrlList.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_9Ku1dlzfD_PY_qMDrPkAbiDjxO7zmvXDkQ&usqp=CAU");
        imgUrlList.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTI0nP9Ya2iOgJSTxojgvH8T2WnTZ5FtzPq6A&usqp=CAU");
        imgUrlList.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRPmMkqJtg4hB_ICqzN-4cIoKEJdOoJNRJ40Q&usqp=CAU");
        imgUrlList.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSYc5fXgn6OJhirIJpsTQzMlytry_xyKPmB9g&usqp=CAU");
        imgUrlList.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTeXKZuWBWd-8YV97SCCXhDtyu-llzPmV_YsA&usqp=CAU");
        imgUrlList.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTmuNX4md1Y47V_DT44c2bdOLWrqLNjyH_ICA&usqp=CAU");
        imgUrlList.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQBu5y39JbzZ00wmAnyj7BHDYwnRJfbOOJUsQ&usqp=CAU");
        imgUrlList.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSexYNheUAm88qnSc8xt6WaLfbuIxjAoPhlgg&usqp=CAU");
        imgUrlList.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS5b7qc_DNKKtzcK4aa9-ho8YsCdHyetg1FMw&usqp=CAU");
        imgUrlList.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS0uKRg0JM_HtJc2AGiBMlmbJw8Yfc5Tpk45w&usqp=CAU");
        imgUrlList.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRr1AxBO2kdlH35XA7WNr7N68pCf46eqbI3Pw&usqp=CAU");
        imgUrlList.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTyvXQKqIRmh4nAMcUSNJurFu3RFMB2Rt5fFEH5owxFUTO3rQV3DzFIVIaIEFWugFTXAjI&usqp=CAU");
        imgUrlList.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTWec1hDenh7fZMSYAKjaGf4d_ugiNCarQwsA&usqp=CAU");
        imgUrlList.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ7cxiYfPiS19PqFfr-jCuHKabhq7YsRUYIBA&usqp=CAU");
        imgUrlList.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSYr2qdhynHqfsOLZBjwyN3c0MlxRaNm4ebig&usqp=CAU");
        imgUrlList.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTwEVWKEyA5lMwJOXJzPOboKmejfwc2HtDDcw&usqp=CAU");
    }
    public static String getImgUrl() {
        int idx = new Random().nextInt(15);
        return imgUrlList.get(idx);
    }

    public static ImgUrlGenerator getInstance() {
        if (instance == null) {
            return new ImgUrlGenerator();
        }
        return instance;
    }
}
