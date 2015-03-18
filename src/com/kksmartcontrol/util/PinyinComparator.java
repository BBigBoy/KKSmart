package com.kksmartcontrol.util;

import java.util.Comparator;

import com.kksmartcontrol.bean.VideoInfoBean;

/**
 * 
 * @author Mr.Z
 */
public class PinyinComparator implements Comparator<VideoInfoBean> {

    public int compare(VideoInfoBean o1, VideoInfoBean o2) {
	if (o1.getSortLetters().equals("@") || o2.getSortLetters().equals("#")) {
	    return -1;
	} else if (o1.getSortLetters().equals("#")
		|| o2.getSortLetters().equals("@")) {
	    return 1;
	} else {
	    return o1.getSortLetters().compareTo(o2.getSortLetters());
	}
    }

}
