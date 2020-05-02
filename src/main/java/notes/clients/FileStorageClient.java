package notes.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class FileStorageClient {

    private final String fileStorageUrl;
    private final RestTemplate restTemplate;

    public FileStorageClient(
            @Value("${services.notes.filestorage.url}") String fileStorageUrl,
            RestTemplate restTemplate
    ) {
        this.fileStorageUrl = fileStorageUrl;
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> saveFile(String fileName, byte[] fileBytes) {
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        ByteArrayResource contentsAsResource = new ByteArrayResource(fileBytes) {
            @Override
            public String getFilename() {
                return fileName;
            }
        };
        multiValueMap.add("file", contentsAsResource);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(multiValueMap, headers);
        return restTemplate.postForEntity(fileStorageUrl, requestEntity, String.class);
    }
}
