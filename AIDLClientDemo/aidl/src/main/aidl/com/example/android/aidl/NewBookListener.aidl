// NewBookListener.aidl
package com.example.android.aidl;

import com.example.android.aidl.Book;

interface NewBookListener {
    void bookArrived(in Book book);
}
