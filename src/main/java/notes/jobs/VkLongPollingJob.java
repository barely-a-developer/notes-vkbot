package notes.jobs;

import com.google.gson.JsonObject;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.exceptions.LongPollServerKeyExpiredException;
import com.vk.api.sdk.objects.callback.longpoll.responses.GetLongPollEventsResponse;
import com.vk.api.sdk.objects.groups.LongPollServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import notes.services.VkService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
@RequiredArgsConstructor
public class VkLongPollingJob {
    private final VkApiClient vkApiClient;
    private final GroupActor groupActor;
    private final VkService vkService;

    private static final int LONG_POLL_WAIT_TIME = 25;

    private String ts;
    private String server;
    private String key;

    @PostConstruct
    private void startNewSession() {
        LongPollServer longPollServer = getLongPollServer();
        ts = longPollServer.getTs();
        key = longPollServer.getKey();
        server = longPollServer.getServer();
        log.debug("Session properties were set");
    }

    @Scheduled(fixedDelay = 1000L)
    private void pollForNewMessagesFromTheBot() {
        log.debug("Long Poll Request Job Started");
        GetLongPollEventsResponse eventsResponse = getLongPollEventsResponse();
        for (JsonObject jsonObject : eventsResponse.getUpdates()) {
            vkService.processMessage(jsonObject);
        }
        ts = eventsResponse.getTs().toString();
        log.debug("Long Poll Request Job Finished");
    }

    private LongPollServer getLongPollServer() {
        try {
            return vkApiClient.groups().getLongPollServer(groupActor, groupActor.getGroupId()).execute();
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not get LongPollServer");
        }
    }

    private GetLongPollEventsResponse getLongPollEventsResponse() {
        try {
            return vkApiClient
                    .longPoll()
                    .getEvents(server, key, Integer.valueOf(ts))
                    .waitTime(LONG_POLL_WAIT_TIME)
                    .execute();
        } catch (LongPollServerKeyExpiredException ex) {
            log.debug("Session expired");
            startNewSession();
            return getLongPollEventsResponse();
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not get LongPollEventsResponse");
        }
    }

}
