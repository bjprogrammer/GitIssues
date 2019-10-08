package com.gamechange.gitissue.detail;

import com.gamechange.gitissue.model.CommentsResponse;

import java.util.List;

public class DetailContract {

    interface DetailView {
        void showWait();
        void onFailure(String appErrorMessage);
        void onSuccess(List<CommentsResponse> response);

    }

    interface DetailPresenter{
         void getCommentList(String commentId);
         void unSubscribe();
    }
}
