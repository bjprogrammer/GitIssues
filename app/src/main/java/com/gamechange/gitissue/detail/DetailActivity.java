package com.gamechange.gitissue.detail;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gamechange.gitissue.R;
import com.gamechange.gitissue.databinding.ActivityDetailBinding;

import com.gamechange.gitissue.model.CommentsResponse;
import com.gamechange.gitissue.utils.ConnectivityReceiver;
import com.gamechange.gitissue.utils.Constants;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class DetailActivity extends AppCompatActivity implements DetailContract.DetailView, ConnectivityReceiver.ConnectivityReceiverListener{
    ActivityDetailBinding binding;

    private DetailPresenter presenter;
    private boolean flag = true;
    final static String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private IntentFilter intentFilter;
    private ConnectivityReceiver receiver;

    DetailAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    RecyclerView rv;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        presenter = new DetailPresenter(getApplicationContext(),this);

        setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        adapter = new DetailAdapter();

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(linearLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(adapter);


        presenter.getCommentList(getIntent().getStringExtra(Constants.ISSUE_ID));

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
        flag = (isConnected);
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
    public void onFailure(String appErrorMessage) {
        progressBar.setVisibility(View.GONE);
        Toasty.error(getApplicationContext(), appErrorMessage, Toast.LENGTH_LONG, true).show();

    }

    @Override
    public void onSuccess(List<CommentsResponse> response) {
        progressBar.setVisibility(View.GONE);

        if(response!=null){
            if(!response.isEmpty()){
                adapter.addAll(response);
            }
            else {
                showEmptyDialog();
            }
        }
        else {
            showEmptyDialog();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void showEmptyDialog(){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Nothing to Show")
                .setMessage("No comments available for this issue")
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .show();
    }
}
