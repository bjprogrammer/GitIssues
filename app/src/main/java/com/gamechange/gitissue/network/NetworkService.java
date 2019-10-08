package com.gamechange.gitissue.network;

import com.gamechange.gitissue.model.CommentsResponse;
import com.gamechange.gitissue.model.IssueResponse;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

//ALL API calls endpoints
public interface NetworkService {

    @GET("repos/firebase/firebase-ios-sdk/issues")
    Observable<List<IssueResponse>> getIssueList(@Query("sort") String sort, @Query("direction") String direction, @Query("state") String state, @Query("page") int page) ;

    @GET("repos/firebase/firebase-ios-sdk/issues/{id}/comments")
    Observable<List<CommentsResponse>> getCommentList(@Path("id") String id, @Query("sort") String sort, @Query("direction") String direction);
}

