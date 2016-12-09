package com.yalin.freevideo.navigation;

import com.yalin.freevideo.navigation.NavigationModel.NavigationItemEnum;

/**
 * YaLin
 * 2016/12/9.
 */

public class NavigationConfig {
    public final static NavigationItemEnum[] COMMON_ITEMS_AFTER_CUSTOM =
            new NavigationItemEnum[]{NavigationItemEnum.EXPLORE,
                    NavigationItemEnum.SETTINGS, NavigationItemEnum.ABOUT};

    private static NavigationItemEnum[] concatenateItems(NavigationItemEnum[] first,
                                                         NavigationItemEnum[] second) {
        NavigationItemEnum[] items = new NavigationItemEnum[first.length + second.length];
        for (int i = 0; i < first.length; i++) {
            items[i] = first[i];
        }
        for (int i = 0; i < second.length; i++) {
            items[first.length + i] = second[i];
        }
        return items;
    }

    public static NavigationItemEnum[] appendItem(NavigationItemEnum[] first,
                                                  NavigationItemEnum second) {
        return concatenateItems(first, new NavigationItemEnum[]{second});
    }

}
