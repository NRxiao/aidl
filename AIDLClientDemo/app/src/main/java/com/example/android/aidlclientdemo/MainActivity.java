package com.example.android.aidlclientdemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.android.aidl.Book;
import com.example.android.aidl.IBookManager;
import com.example.android.aidl.NewBookListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private IBookManager mIBookManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("com.example.android.aidlservicedemo",
                "com.example.android.aidlservicedemo.AIDLService");
        intent.setComponent(componentName);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1){
                Book book = (Book) msg.obj;
                Log.i(TAG, "handleMessage: " + book.getCode());
            }
            super.handleMessage(msg);
        }
    };

    private NewBookListener mNewBookListener = new NewBookListener.Stub() {
        @Override
        public void bookArrived(Book book) throws RemoteException {
            mHandler.obtainMessage(1, book).sendToTarget();
        }
    };

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIBookManager = IBookManager.Stub.asInterface(service);
            try {
                mIBookManager.registerListener(mNewBookListener);
                Log.i(TAG, "onServiceConnected: 绑定成功");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIBookManager = null;
            Log.i(TAG, "onServiceDisconnected: 解绑");
        }
    };

    @Override
    protected void onDestroy() {
        if (mIBookManager != null && mIBookManager.asBinder().isBinderAlive()){
            try {
                mIBookManager.unregisterListener(mNewBookListener);
                Log.i(TAG, "onDestroy: 解绑成功");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mServiceConnection);
        super.onDestroy();
    }
}
