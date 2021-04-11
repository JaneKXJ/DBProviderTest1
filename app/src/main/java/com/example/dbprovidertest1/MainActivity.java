package com.example.dbprovidertest1;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.dbprovidertest1.Utils.LogUtil;
import com.example.dbprovidertest1.adapter.InfoAdapter;
import com.example.dbprovidertest1.data.DBDataManager;
import com.example.dbprovidertest1.data.JobInfo;
import com.example.dbprovidertest1.data.JobInfoTable;
import com.example.dbprovidertest1.data.UserInfo;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private List<UserInfo> mUserInfos = new ArrayList<>();
    private List<JobInfo> mJobInfos = new ArrayList<>();
    private List<String> mInfos = new ArrayList<>();
    private RecyclerView mDataDisplay;
    private Button mAddBtn, mDeleteBtn, mDeleteAllBtn, mSelectOneBtn, mSelectAllBtn, mUpdateBtn, mSelectUserBtn;
    private EditText mUserNameEt, mAgeEt, mJobEt, mJobName, mJobDes;
    private JobInfoObserver mJobInfoObserver;
    private InfoAdapter mInfoAdapter;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mJobInfoObserver = new JobInfoObserver(new Handler());
        this.getContentResolver().registerContentObserver(JobInfoTable.getContentUri(), true, mJobInfoObserver);
        initView();
    }

    public class JobInfoObserver extends ContentObserver {
        public JobInfoObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            LogUtil.i(TAG, "onChange");
            super.onChange(selfChange);
            refreshJobDataDisplay();
        }
    }

    private void initView() {
        mAddBtn = findViewById(R.id.add_data_btn);
        mDeleteBtn = findViewById(R.id.delete_data_btn);
        mDeleteAllBtn = findViewById(R.id.delete_all_data_btn);
        mUpdateBtn = findViewById(R.id.update_data_btn);
        mSelectOneBtn = findViewById(R.id.search_data_btn);
        mSelectAllBtn = findViewById(R.id.search_all_btn);
        mSelectUserBtn = findViewById(R.id.search_user_btn);

        mUserNameEt = findViewById(R.id.name_et);
        mAgeEt = findViewById(R.id.age_et);
        mJobEt = findViewById(R.id.job_et);
        mJobName = findViewById(R.id.job_name_et);
        mJobDes = findViewById(R.id.job_des_et);

        mDataDisplay = findViewById(R.id.data_display_rv);
        mInfoAdapter = new InfoAdapter(mInfos);
        mDataDisplay.setAdapter(mInfoAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDataDisplay.setLayoutManager(layoutManager);

        mAddBtn.setOnClickListener(this);
        mDeleteBtn.setOnClickListener(this);
        mDeleteAllBtn.setOnClickListener(this);
        mUpdateBtn.setOnClickListener(this);
        mSelectOneBtn.setOnClickListener(this);
        mSelectAllBtn.setOnClickListener(this);
        mSelectUserBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.add_data_btn:
                insertJobData();
                //insertMultiJobData();
                break;
            case R.id.update_data_btn:
                updateJobData();
                break;
            case R.id.delete_data_btn:
                //deleteOneJobData();
                deleteMultiJobData();
                break;
            case R.id.delete_all_data_btn:
                deleteAllJobData();
                break;
            case R.id.search_data_btn:
                getOneJobData();
                break;
            case R.id.search_all_btn:
                getAllJobData();
                break;
            case R.id.search_user_btn:
                getAllUserData();
                break;
        }
    }

    /**
     * 刷新界面数据
     */
    private void refreshJobDataDisplay() {
        LogUtil.i(TAG, "refreshJobDataDisplay()");
        getAllJobData();
        mInfoAdapter.notifyDataSetChanged();
    }

    /**
     * 获取界面输入信息
     *
     * @return
     */
    public UserInfo getUserInfo() {
        String name = mUserNameEt.getText().toString();
        if (TextUtils.isEmpty(name)) {
            name = "default";
        }
        String age = mAgeEt.getText().toString();
        if (TextUtils.isEmpty(age)) {
            age = "0";
        }
        String job = mJobEt.getText().toString();
        if (TextUtils.isEmpty(job)) {
            job = "Java";
        }
        UserInfo userInfo = new UserInfo(name, Integer.parseInt(age), job);
        LogUtil.i(TAG,"getUserInfo userInfo: " + userInfo);
        return userInfo;
    }

    /**
     * 获取界面输入信息
     *
     * @return
     */
    public JobInfo getJobInfo() {
        String job_name = mJobName.getText().toString();
        if (TextUtils.isEmpty(job_name)) {
            job_name = "default";
        }
        String job_des = mJobDes.getText().toString();
        if (TextUtils.isEmpty(job_des)) {
            job_des = "default";
        }
        JobInfo jobInfo = new JobInfo(job_name,job_des);
        LogUtil.i(TAG,"getJobInfo jobInfo: " + jobInfo);
        return jobInfo;
    }

    /**
     * 查询所有用户数据
     */
    /**
     * 查询所有数据
     */
    public void getAllUserData() {
        LogUtil.i(TAG, "getAllUserData");
        DBDataManager.getInstance(mContext).getAllUserInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<UserInfo>>() {
                    @Override
                    public void accept(List<UserInfo> userInfos) throws Exception {
                        if (userInfos != null ) {
                            LogUtil.i(TAG, "userInfos.size(): " + userInfos.size());
                      /*      mUserInfos.clear();
                            mUserInfos.addAll(userInfos);*/
                            mInfos.clear();
                            for (UserInfo userInfo : userInfos){
                                mInfos.add(userInfo.toString());
                            }
                            mInfoAdapter.notifyDataSetChanged();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtil.i(TAG, throwable.getMessage());
                    }
                });

    }



    /**
     * 添加job数据信息
     */
    public void insertJobData() {
        LogUtil.i(TAG, "insertJobData");
        DBDataManager.getInstance(mContext).addJob(getJobInfo())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean result) throws Exception {
                        if (result) {
                            Toast.makeText(mContext, "插入成功", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mContext, "插入失败", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtil.i(TAG, throwable.getMessage());
                    }
                });
    }

    /**
     * 添加job数据信息
     */
    public void insertMultiJobData() {
        LogUtil.i(TAG, "insertMultiJobData");
        List<JobInfo> jobInfos = new ArrayList<>();
        jobInfos.add(getJobInfo());
        jobInfos.add(getJobInfo());
        jobInfos.add(getJobInfo());
        DBDataManager.getInstance(mContext).addMultiJob(jobInfos)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean result) throws Exception {
                        if (result) {
                            Toast.makeText(mContext, "插入成功", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mContext, "插入失败", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtil.i(TAG, throwable.getMessage());
                    }
                });
    }

    /**
     * 删除某个job信息
     */
    public void deleteOneJobData() {
        String name = mJobName.getText().toString();
        LogUtil.i(TAG, "deleteOneJobData + name: " + name);
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(mContext, "名称不能是空", Toast.LENGTH_SHORT).show();
            return;
        }
        DBDataManager.getInstance(mContext).deleteJob(name)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean result) throws Exception {
                        if (result) {
                            Toast.makeText(mContext, "删除成功", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mContext, "删除失败", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtil.i(TAG, throwable.getMessage());
                    }
                });
    }

    /**
     * 删除某个job信息
     */
    public void deleteMultiJobData() {
        getAllJobData();
        List<String> names = new ArrayList<>();
        if (mJobInfos.size() < 2){
            return;
        }
        for (int i = 0; i < 2; i ++){
            names.add(mJobInfos.get(i).getName());
        }
        DBDataManager.getInstance(mContext).deleteMultiJob(names)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean result) throws Exception {
                        if (result) {
                            Toast.makeText(mContext, "删除成功", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mContext, "删除失败", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtil.i(TAG, throwable.getMessage());
                    }
                });
    }

    /**
     * 删除所有job数据
     */
    public void deleteAllJobData() {
        LogUtil.i(TAG, "deleteAllJobData: ");
        DBDataManager.getInstance(mContext).deleteAllJob()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean result) throws Exception {
                        if (result) {
                            Toast.makeText(mContext, "删除成功", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mContext, "删除失败", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtil.i(TAG, throwable.getMessage());
                    }
                });
    }

    /**
     * 根据名字更新某个job的信息
     */
    public void updateJobData() {
        String name = mJobName.getText().toString();
        LogUtil.i(TAG, "updateJobData + name: " + name);
        if (TextUtils.isEmpty(name)) {
            return;
        }
        Observable<Boolean> isUserExit = DBDataManager.getInstance(mContext).isJobExitInDb(name);
        Observable<Boolean> update = DBDataManager.getInstance(mContext).updateJob(name,getJobInfo());

        Observable.concat(isUserExit,update)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean result) throws Exception {
                        if (result) {
                            Toast.makeText(mContext, "更新成功", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mContext, "更新失败", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtil.i(TAG, throwable.getMessage());
                    }
                });
    }

    /**
     * 查询所有数据
     */
    public void getAllJobData() {
        LogUtil.i(TAG, "getAllJobData");
        DBDataManager.getInstance(mContext).getAllJobs()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<JobInfo>>() {
                    @Override
                    public void accept(List<JobInfo> jobInfos) throws Exception {
                        if (jobInfos != null ) {
                            LogUtil.i(TAG, "jobInfos.size(): " + jobInfos.size());
                            /*mJobInfos.clear();
                            mJobInfos.addAll(jobInfos);
                            LogUtil.i(TAG, "mJobInfos.size(): " + mJobInfos.size());*/
                            mInfos.clear();
                            for (JobInfo jobInfo : jobInfos){
                                mInfos.add(jobInfo.toString());
                            }
                            mInfoAdapter.notifyDataSetChanged();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtil.i(TAG, throwable.getMessage());
                    }
                });

    }

    /**
     * 查询单个数据
     */
    public void getOneJobData() {
        String name = mJobName.getText().toString();
        LogUtil.i(TAG, "getOneJobData + name: " + name);
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(mContext, "名称不能是空", Toast.LENGTH_SHORT).show();
            return;
        }
        DBDataManager.getInstance(mContext).getOneJob(name)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JobInfo>() {
                    @Override
                    public void accept(JobInfo jobInfo) throws Exception {
                        LogUtil.i(TAG, "jobInfo :" + jobInfo);
                        if (jobInfo.getName() != null) {
                            /*mJobInfos.clear();
                            mJobInfos.add(jobInfo);*/
                            mInfos.clear();
                            mInfos.add(jobInfo.toString());
                            mInfoAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getApplicationContext(), "没有您要查找的信息", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtil.i(TAG, throwable.getMessage());
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.getContentResolver().unregisterContentObserver(mJobInfoObserver);
    }
}
