package com.android.core.slidepanel;

/**
 * Created by tuannx on 12/1/2014.
 */
public interface ISlide {
    public void onStartSlide();
    public void onSlide(int offset, int maxDistance);
    public void onSlideFinish();
}
