package notes.configs;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VkConfig {

    private final int groupId;
    private final String accessToken;

    public VkConfig(
            @Value("${vk.groupId}") int groupId,
            @Value("${vk.accessToken}") String accessToken
    ) {
        this.groupId = groupId;
        this.accessToken = accessToken;
    }

    @Bean
    public VkApiClient getVkApiClient() {
        TransportClient transportClient = HttpTransportClient.getInstance();
        return new VkApiClient(transportClient);
    }

    @Bean
    public GroupActor getGroupActor() {
        return new GroupActor(groupId, accessToken);
    }

}
