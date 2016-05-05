package com.seu.smarthome.ui.base;

import android.os.Bundle;

/**
 * Created by Liujilong on 16/1/20.
 * liujilong.me@gmail.com
 */
public abstract class MVPActivity<View, Presenter extends BasePresenter<View>>  extends BaseActivity{
    protected Presenter mPresenter;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
        mPresenter.attachView((View)this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    protected abstract Presenter createPresenter();
}
