package com.gamechange.gitissue.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gamechange.gitissue.R;
import com.gamechange.gitissue.databinding.ActivityMainBinding;
import com.gamechange.gitissue.detail.DetailActivity;
import com.gamechange.gitissue.model.IssueResponse;
import com.gamechange.gitissue.utils.ConnectivityReceiver;
import com.gamechange.gitissue.utils.Constants;
import com.gamechange.gitissue.utils.PaginationScrollListener;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements MainContract.MainView, ConnectivityReceiver.ConnectivityReceiverListener{
    ActivityMainBinding binding;

    private MainPresenter presenter;
    private boolean flag = true;
    final static String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private IntentFilter intentFilter;
    private ConnectivityReceiver receiver;

    MainAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    RecyclerView rv;
    ProgressBar progressBar;

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = PAGE_START;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        presenter = new MainPresenter(getApplicationContext(),this);

        rv = binding.recyclerView;
        progressBar = binding.progressBar;


        intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
        receiver = new ConnectivityReceiver();

        //Configuring customized Toast messages
        Toasty.Config.getInstance()
                .setErrorColor( getResources().getColor(R.color.colorPrimaryDark) )
                .setSuccessColor(getResources().getColor(R.color.colorPrimaryDark) )
                .setTextColor(Color.WHITE)
                .tintIcon(true)
                .setTextSize(18)
                .apply();

        adapter = new MainAdapter(new MainAdapter.onPressListener() {
            @Override
            public void onClick(String title, String comment) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra(Constants.ISSUE_ID,comment.substring(comment.indexOf("issues/")+ "issues/".length(),comment.indexOf("/comments")));
                intent.putExtra(Constants.TITLE,title);
                startActivity(intent);
            }
        });

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(linearLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(adapter);

        rv.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        presenter.getIssueList(currentPage);
                    }
                }, 1000);
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });


        // mocking network delay for API call
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                presenter.getIssueList(currentPage);

            }
        }, 1000);

    }


    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(receiver, intentFilter);
        ConnectivityReceiver.connectivityReceiverListener = this;
    }

    //Checking internet flag using broadcast receiver
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(flag!=isConnected)
        {
            if(isConnected){
                Toasty.success(this, "Connected to internet", Toast.LENGTH_SHORT, true).show();
            }
            else
            {
                Toasty.error(getApplicationContext(), "Not connected to internet", Toast.LENGTH_LONG, true).show();
            }
        }
        flag= (isConnected);
    }

    @Override
    protected void onDestroy() {
        presenter.unSubscribe();
        presenter = null;
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (receiver != null) {
            try {
                unregisterReceiver(receiver);
            } catch (Exception e) { }
        }
    }

    @Override
    public void showWait() {
        progressBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void removeWait() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onFailure(String appErrorMessage) {
        progressBar.setVisibility(View.GONE);
        Toasty.error(getApplicationContext(), appErrorMessage, Toast.LENGTH_LONG, true).show();

    }

    @Override
    public void onSuccess(List<IssueResponse> response) {
        adapter.removeLoadingFooter();
        isLoading = false;

        if(response!=null){
            if(!response.isEmpty()){
                adapter.addAll(response);
                adapter.addLoadingFooter();
            }
            else
            {
                isLastPage = true;
            }
        }
        else
        {
            isLastPage = true;
        }

    }
}
