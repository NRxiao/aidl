package com.example.android.aidlservicedemo;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.example.android.aidl.Book;
import com.example.android.aidl.IBookManager;
import com.example.android.aidl.NewBookListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class AIDLService extends Service {

    /**
     * RemoteCallbackList 系统提供用来删除跨进程listener的接口
     */

    private static final String TAG = "AIDLService";

    private CopyOnWriteArrayList<Book> mCopyOnWriteArrayList = new CopyOnWriteArrayList<>();

    private AtomicBoolean mAtomicBoolean = new AtomicBoolean(false);

    private CopyOnWriteArrayList<NewBookListener> mNewBookListeners = new CopyOnWriteArrayList<>();

    private RemoteCallbackList<NewBookListener> mNewBookListenerRemoteCallbackList = new RemoteCallbackList<>();

    private IBookManager mIBookManager = new IBookManager.Stub() {
        @Override
        public void addBook(Book book) throws RemoteException {
            mCopyOnWriteArrayList.add(book);
        }

        @Override
        public List<Book> getBooks() throws RemoteException {
            return mCopyOnWriteArrayList;
        }

        @Override
        public void registerListener(NewBookListener listener) throws RemoteException {
//            if (!mNewBookListeners.contains(listener)){
//                mNewBookListeners.add(listener);
//                Log.i(TAG, "registerListener: 注册成功");
//            }else {
//                Log.i(TAG, "registerListener: 用户已存在");
//            }
            mNewBookListenerRemoteCallbackList.register(listener);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Log.i(TAG, "registerListener: " + mNewBookListenerRemoteCallbackList.getRegisteredCallbackCount());
            }
        }

        @Override
        public void unregisterListener(NewBookListener listener) throws RemoteException {
//            if (mNewBookListeners.contains(listener)){
//                mNewBookListeners.remove(listener);
//                Log.i(TAG, "unregisterListener: 取消成功");
//            }else {
//                Log.i(TAG, "unregisterListener: 还未注册");
//            }
//            Log.i(TAG, "unregisterListener: " + mNewBookListeners.size());
            mNewBookListenerRemoteCallbackList.unregister(listener);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Log.i(TAG, "registerListener: " + mNewBookListenerRemoteCallbackList.getRegisteredCallbackCount());
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mIBookManager.asBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!mAtomicBoolean.get()){
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Book book = new Book("new Book", mCopyOnWriteArrayList.size());
                    mCopyOnWriteArrayList.add(book);
//                    for (int i = 0; i < mNewBookListeners.size(); i++){
//                        NewBookListener listener = mNewBookListeners.get(i);
//                        try {
//                            listener.bookArrived(book);
//                        } catch (RemoteException e) {
//                            e.printStackTrace();
//                        }
//                    }
                    int n = mNewBookListenerRemoteCallbackList.beginBroadcast();
                    for (int i = 0; i < n; i++){
                        NewBookListener listener = mNewBookListenerRemoteCallbackList.getBroadcastItem(i);
                        if (listener != null){
                            try {
                                listener.bookArrived(book);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    mNewBookListenerRemoteCallbackList.finishBroadcast();
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        mAtomicBoolean.set(true);
        super.onDestroy();
    }

    /**
     * 遍历RemoteCallbackList的方法
     * beginBroadcast()，finishBroadcast()必须配套使用
     * @param list
     */
    private void traverse(RemoteCallbackList<NewBookListener> list){
        final int N = list.beginBroadcast();
        for (int i = 0; i < N; i++){
            NewBookListener listener = list.getBroadcastItem(i);
        }
        list.finishBroadcast();
    }
}
