package com.portal.portal_cursos.service.implementation;

import com.portal.portal_cursos.service.IAlmacenamientoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.Optional;

@Service
@ConditionalOnProperty(name = "storage.provider", havingValue = "s3", matchIfMissing = true)
public class S3AlmacenamientoServiceImpl implements IAlmacenamientoService {

    private final String bucket;
    private final S3Client s3;
    private final S3Presigner presigner;
    private final String publicBaseUrl;

    public S3AlmacenamientoServiceImpl(
            @Value("${aws.s3.bucket}") String bucket,
            @Value("${aws.region}") String region,
            @Value("${aws.s3.public-base-url}") String publicBaseUrl) {

        this.bucket = bucket;
        this.publicBaseUrl = publicBaseUrl;


        var regionObj = Region.of(region);
        this.s3 = S3Client.builder()
                .region(regionObj)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        this.presigner = S3Presigner.builder()
                .region(regionObj)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Override
    public String subir(String key, InputStream contenido, long contentLength, String contentType) {
        var putReq = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();
        s3.putObject(putReq, RequestBody.fromInputStream(contenido, contentLength));
        return key;
    }


    @Override
    public Optional<String> urlTemporal(String key, int minutos) {
        var presign = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(minutos))
                .getObjectRequest(GetObjectRequest.builder().bucket(bucket).key(key).build())
                .build();
        URL url = presigner.presignGetObject(presign).url();
        return Optional.ofNullable(url).map(URL::toString);
    }

    @Override
    public boolean existe(String key) {
        try {
            s3.headObject(HeadObjectRequest.builder().bucket(bucket).key(key).build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String urlPublica(String key) {
        return publicBaseUrl.endsWith("/")
                ? publicBaseUrl + key
                : publicBaseUrl + "/" + key;
    }


}
