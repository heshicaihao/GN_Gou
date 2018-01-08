
package com.gionee.client.business.dbutils;

import android.net.Uri;

public interface DBOperation<T> {

    public Uri insert(T t);

    public int delete(T t);

    public int update(T t);

}
