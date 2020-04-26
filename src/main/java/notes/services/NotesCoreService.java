package notes.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import notes.clients.NotesCoreClient;
import notes.models.NoteDto;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotesCoreService {

    private final NotesCoreClient notesCoreClient;

    public void addNote(NoteDto noteDto) {
        log.info("Sending the note to the Core Service");
        NoteDto newNoteDto = notesCoreClient.postNote(noteDto);
        log.info("Note saved in Core Service with ID {}", newNoteDto.getId());
        log.debug(newNoteDto.toString());
    }

}
