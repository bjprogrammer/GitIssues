package com.gamechange.gitissue.network;

import android.content.Context;

import com.gamechange.gitissue.model.CommentsResponse;
import com.gamechange.gitissue.model.IssueResponse;
import com.gamechange.gitissue.utils.Constants;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


//Networking using RxJava
public class Service {
    Context context;
    public Service(Context context){
        this.context=context;
    }


    public void getIssueList(final IssueCallback callback, int page){
        NetworkAPI.getClient(context).create(NetworkService.class).getIssueList(Constants.SORT,Constants.DIRECTION, Constants.STATE,page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<List<IssueResponse>>() {
                @Override
                public void onSubscribe(Disposable d) {
                    callback.getDisposable(d);
                }

                @Override
                public void onNext(List<IssueResponse> results) {
                    callback.onSuccess(results);
                }

                @Override
                public void onError(Throwable e) {
                    callback.onError(new NetworkError(e));
                }

                @Override
                public void onComplete() { }
            });
    }

    public void getCommentList(final CommentCallback callback, String id){
        NetworkAPI.getClient(context).create(NetworkService.class).getCommentList(id, Constants.SORT, Constants.DIRECTION)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<List<CommentsResponse>>() {
                @Override
                public void onSubscribe(Disposable d) {
                    callback.getDisposable(d);
                }

                @Override
                public void onNext(List<CommentsResponse> results) {
                    callback.onSuccess(results);
                }

                @Override
                public void onError(Throwable e) {
                    callback.onError(new NetworkError(e));
                }

                @Override
                public void onComplete() { }
            });
    }

    public interface IssueCallback{
        void onSuccess(List<IssueResponse> response);
        void onError(NetworkError networkError);
        void getDisposable(Disposable disposable);
    }

    public interface CommentCallback{
        void onSuccess(List<CommentsResponse> response);
        void onError(NetworkError networkError);
        void getDisposable(Disposable disposable);
    }
}

