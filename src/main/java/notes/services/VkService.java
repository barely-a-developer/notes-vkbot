package notes.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.callback.messages.CallbackMessage;
import com.vk.api.sdk.objects.messages.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import notes.models.NoteDto;
import notes.translators.NoteDtoTranslator;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class VkService {

    private final NotesCoreService notesCoreService;
    private final VkApiClient vkApiClient;
    private final GroupActor groupActor;

    private static final String CALLBACK_EVENT_MESSAGE_NEW = "message_new";
    private static final String CONFIRMATION_MESSAGE_TEXT = "Message saved";

    public CallbackMessage<Message> parseVkCallbackMessage(JsonObject jsonObject) {
        log.debug(jsonObject.toString());
        String type = jsonObject.get("type").getAsString();
        if (CALLBACK_EVENT_MESSAGE_NEW.equalsIgnoreCase(type)) {
            return new Gson().fromJson(jsonObject, new TypeToken<CallbackMessage<Message>>() {
            }.getType());
        } else {
            throw new RuntimeException("Not a message type event");
        }
    }

    public void processMessage(JsonObject jsonObject) {
        Message message = parseVkCallbackMessage(jsonObject).getObject();
        NoteDto noteDto = NoteDtoTranslator.newNoteDtoFromMessage(message);
        notesCoreService.addNote(noteDto);
        sendConfirmationMessage(message);
    }

    private void sendConfirmationMessage(Message message) {
        sendReplyToMessage(message, CONFIRMATION_MESSAGE_TEXT);
    }

    private void sendReplyToMessage(Message message, String text) {
        try {
            vkApiClient.messages().send(groupActor)
                    .randomId(new Random().nextInt())
                    .peerId(message.getPeerId())
                    .replyTo(message.getId())
                    .message(text)
                    .execute();
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not send ConfirmationMessage");
        }
    }
}
