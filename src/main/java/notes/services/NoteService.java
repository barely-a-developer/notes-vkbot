package notes.services;

import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.messages.MessageAttachment;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.photos.PhotoSizes;
import com.vk.api.sdk.objects.photos.PhotoSizesType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import notes.models.dto.NoteDto;
import notes.models.dto.PhotoDto;
import notes.models.dto.StorageFileDto;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoteService {

    private final RestTemplate restTemplate;
    private final FileStorageService fileStorageService;

    public NoteDto translateToNoteDto(Message message) {
        log.info("Start translating VK Message to NoteDto");
        try {
            NoteDto noteDto = createNoteDtoWithoutAttachments(message);
            translateAttachments(message, noteDto);
            log.info("Finished translating VK Message to NoteDto");
            return noteDto;
        } catch (RuntimeException ex) {
            rollbackForMessage(message);
            throw new RuntimeException("Could not translate to Note from Message with id[" + message.getId() + "]", ex);
        }

    }

    private NoteDto createNoteDtoWithoutAttachments(Message message) {
        return NoteDto.builder()
                      .message(message.getText())
                      .build();
    }

    private void translateAttachments(Message message, NoteDto noteDto) {
        for (MessageAttachment messageAttachment : message.getAttachments()) {
            Optional.ofNullable(messageAttachment.getPhoto())
                    .map(this::createPhotoDto)
                    .ifPresent(noteDto::addAttachmentDto);
        }
    }

    private PhotoDto createPhotoDto(Photo photo) {
        log.info("Translating the Photo attachment");
        PhotoSizes photoSizes = findUrlByPhotoSizeType(photo);
        URL url = photoSizes.getUrl();
        StorageFileDto storageFileDto = savePhotoToStorage(url);
        return PhotoDto.builder()
                       .originalUrl(url.toString())
                       .width(photoSizes.getWidth())
                       .height(photoSizes.getHeight())
                       .storageId(storageFileDto.getStorageId())
                       .build();
    }

    private StorageFileDto savePhotoToStorage(URL url) {
        byte[] imageBytes = downloadFile(url);
        String fileName = FilenameUtils.getName(url.getPath());
        return fileStorageService.saveFile(fileName, imageBytes);
    }

    private PhotoSizes findUrlByPhotoSizeType(Photo photo) {
        // TODO Try to find from the largest to the smallest?
        return photo.getSizes().stream()
                    .filter(photoSizes -> photoSizes.getType().equals(PhotoSizesType.X))
                    .findAny()
                    .orElseThrow();
    }

    private byte[] downloadFile(URL url) {
        byte[] bytes = restTemplate.getForObject(url.toString(), byte[].class);
        if (bytes == null) {
            throw new RuntimeException("Could not download the file");
        }
        log.info("Downloaded file from [" + url.toString() + "]");
        return bytes;
    }

    public void rollbackForMessage(Message message) {
//        fileStorageService.deleteForMessage(message);
    }

}