package notes.translators;

import com.vk.api.sdk.objects.messages.Message;
import lombok.experimental.UtilityClass;
import notes.models.NoteDto;

@UtilityClass
public class NoteDtoTranslator {

    public NoteDto newNoteDtoFromMessage (Message message) {
        return NoteDto.builder()
                .message(message.getText())
                .build();
    }

}