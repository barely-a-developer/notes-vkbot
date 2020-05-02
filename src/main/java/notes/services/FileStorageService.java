package notes.services;

import com.google.gson.Gson;
import com.vk.api.sdk.objects.messages.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import notes.clients.FileStorageClient;
import notes.models.dto.StorageFileDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final FileStorageClient fileStorageClient;

    public void deleteForMessage(Message message) {
        // TODO Check DB for attachments for message and delete files + udpate DB or flag for deletion eligibility in DB
        throw new UnsupportedOperationException("DeleteForMessage not implemented");
    }

    public StorageFileDto saveFile(String fileName, byte[] fileBytes) {
        ResponseEntity<String> response = fileStorageClient.saveFile(fileName, fileBytes);
        if (response.getStatusCode().equals(HttpStatus.CREATED)) {
            StorageFileDto storageFileDto = new Gson().fromJson(response.getBody(), StorageFileDto.class);
            log.info("File [" + fileName + "] saved to FileStorage with ID [" + storageFileDto.getStorageId() + "]");
            return storageFileDto;
        } else {
            throw new RuntimeException("Could not save to FileStorage. Response: " + response.getStatusCode() + " " + response.getBody());
        }
    }
}
