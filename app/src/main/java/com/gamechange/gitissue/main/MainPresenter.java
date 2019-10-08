package com.gamechange.gitissue.main;

import android.content.Context;
import android.os.Handler;

import com.gamechange.gitissue.model.IssueResponse;
import com.gamechange.gitissue.network.NetworkError;
import com.gamechange.gitissue.network.Service;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MainPresenter implements MainContract.MainPresenter{

    private MainContract.MainView view;
    private Disposable disposable;

    private Context context;

    public MainPresenter(Context context,MainContract.MainView view) {
        this.view=view;
        this.context=context;
    }

    public void getIssueList(int page) {
        if(page==1) {
            view.showWait();
        }

        new Service(context).getIssueList(new Service.IssueCallback() {
            @Override
            public void onSuccess(List<IssueResponse> response)  {

                if(page==1) {
                    view.removeWait();
                }

                if(response!=null) {
                    view.onSuccess(response);
                }

            }

            @Override
            public void onError(final NetworkError networkError) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(page==1) {
                            view.removeWait();
                        }
                        view.onFailure(networkError.getAppErrorMessage());
                    }
                }, 300);
            }

            @Override
            public void getDisposable(Disposable d) {
                disposable=d;
            }
        }, page);
    }

    public void unSubscribe(){
        view=null;

        if(disposable!=null){
            disposable.dispose();
        }
    }
}
