package com.gamechange.gitissue.detail;

import android.content.Context;
import android.os.Handler;

import com.gamechange.gitissue.model.CommentsResponse;
import com.gamechange.gitissue.network.NetworkError;
import com.gamechange.gitissue.network.Service;

import java.util.List;

import io.reactivex.disposables.Disposable;

public class DetailPresenter implements DetailContract.DetailPresenter{

    private DetailContract.DetailView view;
    private Disposable disposable;

    private Context context;

    public DetailPresenter(Context context,DetailContract.DetailView view) {
        this.view=view;
        this.context=context;
    }

    public void getCommentList(String commentId) {
        view.showWait();

        new Service(context).getCommentList(new Service.CommentCallback() {
            @Override
            public void onSuccess(List<CommentsResponse> response)  {

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
                            view.onFailure(networkError.getAppErrorMessage());
                    }
                }, 300);
            }

            @Override
            public void getDisposable(Disposable d) {
                disposable=d;
            }
        }, commentId);
    }

    public  void unSubscribe(){
        view=null;
        if(disposable!=null){
            disposable.dispose();
        }
    }
}
