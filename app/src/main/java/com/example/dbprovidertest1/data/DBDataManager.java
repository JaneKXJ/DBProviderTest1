package com.example.dbprovidertest1.data;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.example.dbprovidertest1.Utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

public class DBDataManager {
    public static final String TAG = DBDataManager.class.getSimpleName();
    public static DBDataManager instance;
    private Context mContext;

    public DBDataManager(Context context) {
        this.mContext = context;
    }

    public static DBDataManager getInstance(Context context) {
        if (null == instance) {
            synchronized (DBHelper.class) {
                if (null == instance) {
                    instance = new DBDataManager(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    /**
     * 获取全部user
     * @return
     */
    public Observable<List<UserInfo>> getAllUserInfo() {
        return Observable.create(new ObservableOnSubscribe<List<UserInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<List<UserInfo>> e) throws Exception {
                LogUtil.i(TAG," subscribe --- getAllJobs --- thread: " + Thread.currentThread().getName());
                List<UserInfo> list = new ArrayList<>();
                Uri uri = UserInfoTable.getContentUri();
                Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        UserInfo info = UserInfoTable.getValues(cursor);
                        list.add(info);
                    }
                    cursor.close();
                }
                e.onNext(list);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 添加job信息
     * @param info
     */
    public Observable<Boolean> addJob(final JobInfo info) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                LogUtil.i(TAG," subscribe --- addJob --- thread: " + Thread.currentThread().getName());
                ContentResolver contentResolver = mContext.getContentResolver();
                Uri uri = JobInfoTable.getContentUri();
                ContentValues values = JobInfoTable.putValues(info);
                Uri res = contentResolver.insert(uri, values);
                LogUtil.i(TAG," subscribe --- addJob --- res " + res);
                if (res == null){
                    e.onNext(false);
                } else {
                    e.onNext(true);
                    mContext.getContentResolver().notifyChange(uri, null);
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 添加多个job信息
     * @param infos
     */
    public Observable<Boolean> addMultiJob(final List<JobInfo> infos) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                LogUtil.i(TAG," subscribe --- addMultiJob --- thread: " + Thread.currentThread().getName());
                ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<>();
                ContentResolver contentResolver = mContext.getContentResolver();
                Uri uri = JobInfoTable.getContentUri();
                for(JobInfo info : infos){
                    ContentValues values = JobInfoTable.putValues(info);
                    contentProviderOperations.add(ContentProviderOperation.newInsert(uri).withValues(values).build());
                }

                ContentProviderResult[] res = contentResolver.applyBatch(DBInfoProvider.AUTHORITY,contentProviderOperations);
                LogUtil.i(TAG," subscribe --- addMultiJob --- res " + res);
                if (res.length >= 1){
                    e.onNext(true);
                    mContext.getContentResolver().notifyChange(uri, null);
                } else {
                    e.onNext(false);
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 根据名字更新job信息
     * @param name
     * @param info
     */
    public Observable<Boolean> updateJob(final String name, final JobInfo info) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                LogUtil.i(TAG," subscribe --- updateUser --- thread: " + Thread.currentThread().getName());
                ContentResolver contentResolver = mContext.getContentResolver();
                Uri uri = JobInfoTable.getContentUri();
                ContentValues values = new ContentValues();
                values.put(JobInfoTable.DES, info.getDes());
                values.put(JobInfoTable.NAME, info.getName());
                int res = contentResolver.update(uri, values, JobInfoTable.NAME + "=?", new String[]{name});
                LogUtil.i(TAG," subscribe --- updateUser --- res " + res);
                if (res > 0){
                    e.onNext(true);
                    mContext.getContentResolver().notifyChange(uri, null);
                } else {
                    e.onNext(false);
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }


    /**
     * 根据名称删除某个job信息
     *
     * @param name
     */
    public Observable<Boolean> deleteJob(final String name) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                LogUtil.i(TAG," subscribe --- deleteUser --- thread: " + Thread.currentThread().getName());
                ContentResolver contentResolver = mContext.getContentResolver();
                Uri uri = JobInfoTable.getContentUri();
                int res = contentResolver.delete(uri, JobInfoTable.NAME + "=?", new String[]{name});
                LogUtil.i(TAG," subscribe --- deleteUser --- res " + res);
                if (res > 0){
                    e.onNext(true);
                    mContext.getContentResolver().notifyChange(uri, null);
                } else {
                    e.onNext(false);
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 根据名称删除多个job信息
     *
     * @param names
     */
    public Observable<Boolean> deleteMultiJob(final List<String> names) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                LogUtil.i(TAG," subscribe --- deleteUser --- thread: " + Thread.currentThread().getName());
                ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<>();
                ContentResolver contentResolver = mContext.getContentResolver();
                Uri uri = JobInfoTable.getContentUri();
                for (String name : names){
                    contentProviderOperations.add(ContentProviderOperation.newDelete(uri)
                            .withSelection(JobInfoTable.NAME + "=?", new String[]{name}).build());
                }
                ContentProviderResult[] res = contentResolver.applyBatch(DBInfoProvider.AUTHORITY,contentProviderOperations);

                LogUtil.i(TAG," subscribe --- deleteUser --- res " + res);
                if (res.length >= 1){
                    e.onNext(true);
                    mContext.getContentResolver().notifyChange(uri, null);
                } else {
                    e.onNext(false);
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }


    /**
     * 根据名称删除某个job信息
     */
    public Observable<Boolean> deleteAllJob() {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                LogUtil.i(TAG," subscribe --- deleteAllUser --- thread: " + Thread.currentThread().getName());
                ContentResolver contentResolver = mContext.getContentResolver();
                Uri uri = JobInfoTable.getContentUri();
                int res = contentResolver.delete(uri, null, null);
                LogUtil.i(TAG," subscribe --- deleteAllUser --- res " + res);
                if (res > 0){
                    e.onNext(true);
                    mContext.getContentResolver().notifyChange(uri, null);
                } else {
                    e.onNext(false);
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 根据名称获取job信息
     *
     * @param jobName
     * @return
     */
    public Observable<JobInfo> getOneJob(final String jobName) {
        return Observable.create(new ObservableOnSubscribe<JobInfo>() {
            @Override
            public void subscribe(ObservableEmitter<JobInfo> emitter) throws Exception {
                LogUtil.i(TAG," subscribe --- getOneUser --- thread: " + Thread.currentThread().getName());
                JobInfo jobInfo = new JobInfo(null,null);
                Uri uri = JobInfoTable.getContentUri();
                Cursor cursor = mContext.getContentResolver().query(uri, null, JobInfoTable.NAME + "=?", new String[]{jobName}, null);

                if (cursor != null) {
                    if (cursor.moveToNext()) {
                        jobInfo = JobInfoTable.getValues(cursor);
                    }
                    cursor.close();
                }
                emitter.onNext(jobInfo);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 某个job是否存在
     *
     * @param jobName
     * @return
     */
    public Observable<Boolean> isJobExitInDb(final String jobName) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                LogUtil.i(TAG," subscribe --- isUserExitInDb --- thread: " + Thread.currentThread().getName());
                Uri uri = JobInfoTable.getContentUri();
                Cursor cursor = mContext.getContentResolver().query(uri, null, JobInfoTable.NAME + "=?", new String[]{jobName}, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.close();
                    emitter.onNext(true);
                    emitter.onComplete();
                } else {
                    cursor.close();
                    emitter.onNext(false);
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 获取全部job
     * @return
     */
    public Observable<List<JobInfo>> getAllJobs() {
        return Observable.create(new ObservableOnSubscribe<List<JobInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<List<JobInfo>> e) throws Exception {
                LogUtil.i(TAG," subscribe --- getAllJobs --- thread: " + Thread.currentThread().getName());
                List<JobInfo> list = new ArrayList<>();
                Uri uri = JobInfoTable.getContentUri();
                Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        JobInfo info = JobInfoTable.getValues(cursor);
                        list.add(info);
                    }
                    cursor.close();
                }
                e.onNext(list);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }


}
