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
import notes.models.dto.NoteDto;
import org.springframework.stereotype.Service;

import java.util.Random;

import static notes.util.Constants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class VkService {

    private final CoreService coreService;
    private final NoteService noteService;
    private final VkApiClient vkApiClient;
    private final GroupActor groupActor;

    public void handleVkEvent(JsonObject jsonObject) {
        log.debug("--- Started handling new VK event ---");
        Message message = null;
        try {
            // TODO Save the raw JSON with private data to DB, not logs
            log.debug(jsonObject.toString());
            message = getMessage(jsonObject);
            NoteDto noteDto = noteService.translateToNoteDto(message);
            coreService.addNote(noteDto);
            sendConfirmationMessage(message);
        } catch (RuntimeException ex) {
            if (message != null) {
                noteService.rollbackForMessage(message);
                sendReplyToMessage(message, GENERAL_ERROR_MESSAGE_TEXT);
            }
            throw ex;
        } finally {
            log.debug("--- Finished handling new VK event ---");
        }
    }

    private Message getMessage(JsonObject jsonObject) {
        return getCallbackMessage(jsonObject).getObject();
    }

    private CallbackMessage<Message> getCallbackMessage(JsonObject jsonObject) {
        String type = jsonObject.get("type").getAsString();
        if (CALLBACK_EVENT_TYPE_MESSAGE_NEW.equalsIgnoreCase(type)) {
            return new Gson().fromJson(jsonObject, new TypeToken<CallbackMessage<Message>>() {}.getType());
        } else {
            throw new RuntimeException("Received object is not a new VK message");
        }
    }

    private void sendConfirmationMessage(Message message) {
        sendReplyToMessage(message, CONFIRMATION_MESSAGE_TEXT);
    }

    private void sendReplyToMessage(Message message, String text) {
        try {
            vkApiClient.messages().send(groupActor)
                    // TODO Check VK docs if this random generation is OK
                    .randomId(new Random().nextInt())
                    .peerId(message.getPeerId())
                    .replyTo(message.getId())
                    .message(text)
                    .execute();
        } catch (ApiException | ClientException e) {
            throw new RuntimeException("Could not send reply for message id[" + message.getId() + "]", e);
        }
    }
}
