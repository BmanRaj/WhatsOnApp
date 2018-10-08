package xyz.whatson.android.services;

import java.util.List;

public interface AppCallback<T> {
    public void call(T t);
    public void call();
}
