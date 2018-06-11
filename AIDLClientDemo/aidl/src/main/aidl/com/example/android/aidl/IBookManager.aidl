// IBookManager.aidl
package com.example.android.aidl;

import com.example.android.aidl.Book;
import com.example.android.aidl.NewBookListener;

interface IBookManager {
    void addBook(in Book book);

    List<Book> getBooks();

    void registerListener(NewBookListener listener);

    void unregisterListener(NewBookListener listener);
}
