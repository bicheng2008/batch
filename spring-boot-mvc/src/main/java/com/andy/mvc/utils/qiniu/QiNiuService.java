package com.andy.mvc.utils.qiniu;

import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * <p>
 *
 * @author Leone
 **/
@Service
public class QiNiuService {

    private final Logger logger = LoggerFactory.getLogger(QiNiuService.class);

    private Configuration configuration;

    private UploadManager uploadManager;

    private Auth auth;

    @Resource
    private QiNiuProperties properties;

    @PostConstruct
    public void init() {
        configuration = new Configuration(Zone.zone0());
        uploadManager = new UploadManager(configuration);
        auth = Auth.create(properties.getAccessKey(), properties.getSecretKey());
    }

    /**
     * 获取token
     *
     * @return
     */
    public String getToken() {
        return auth.uploadToken(properties.getBucket());
    }

    /**
     * 获取token
     *
     * @param bucket
     * @return
     */
    public QiNiuToken getToken(String bucket) {
        String token = auth.uploadToken(properties.getBucket(), bucket);
        return new QiNiuToken(token);
    }

    /**
     * 上传单个文件
     *
     * @param file
     * @return
     */
    public FileVO upload(MultipartFile file) {
        try {
            byte[] fileBytes = file.getBytes();
            String token = getToken();
            Response res = uploadManager.put(fileBytes, null, token);
            QiNiu qiniu = res.jsonToObject(QiNiu.class);
            return new FileVO(Collections.singletonList(properties.getLinkAddress() + qiniu.getKey()));
        } catch (IOException e) {
            logger.error("message:{}", e.getMessage());
            return null;
        }
    }

    /**
     * 批量上传文件
     *
     * @param files
     * @return
     */
    public FileVO uploadBatch(MultipartFile[] files) {
        if (Objects.isNull(files)) {
            throw new RuntimeException("file array is empty");
        }
        List<String> list = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            try {
                byte[] fileBytes = files[i].getBytes();
                Response res = uploadManager.put(fileBytes, null, getToken());
                QiNiu qiniu = res.jsonToObject(QiNiu.class);
                list.add(properties.getLinkAddress() + qiniu.getKey());
            } catch (IOException e) {
                logger.error("message:{}", e.getMessage());
            }
        }
        return new FileVO(list);
    }

    /**
     * 上传流文件
     *
     * @param inputStream
     * @return
     */
    public FileVO uploadStream(InputStream inputStream) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            inputStream.close();
            byte[] byteData = outputStream.toByteArray();
            Response res = uploadManager.put(byteData, null, getToken());
            QiNiu qiniu = res.jsonToObject(QiNiu.class);
            return new FileVO(Collections.singletonList(properties.getLinkAddress() + qiniu.getKey()));
        } catch (IOException e) {
            logger.error("message:{}", e.getMessage());
            return null;
        }
    }

}
