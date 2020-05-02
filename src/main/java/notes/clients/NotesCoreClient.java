package notes.clients;

import notes.models.dto.NoteDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "NotesCoreClient", url = "${services.notes.core.url}")
public interface NotesCoreClient {

    @RequestMapping(method = RequestMethod.POST, value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    NoteDto postNote(NoteDto noteDto);
}
