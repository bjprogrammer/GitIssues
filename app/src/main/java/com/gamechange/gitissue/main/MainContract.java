package com.gamechange.gitissue.main;

import com.gamechange.gitissue.model.IssueResponse;

import java.util.List;

public class MainContract{

    interface MainView {
        void showWait();
        void removeWait();
        void onFailure(String appErrorMessage);
        void onSuccess(List<IssueResponse> response);

    }

    interface MainPresenter{
         void getIssueList(int page);
         void unSubscribe();
    }
}
