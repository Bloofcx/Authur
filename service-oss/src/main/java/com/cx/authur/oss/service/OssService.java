package com.cx.authur.oss.service;

import java.io.InputStream;

/**
 * @author ChenXu
 * @create 2022-02-20-20:57
 */
public interface OssService {
    String upload(String module, InputStream file,String fileName);

    void remove(String url);
}
