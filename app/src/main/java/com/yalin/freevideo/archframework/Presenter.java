package com.yalin.freevideo.archframework;

/**
 * YaLin
 * 2016/12/9.
 */

public interface Presenter<Q extends QueryEnum, UA extends UserActionEnum> {
    void loadInitialQueries();
}
