package notes.models.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.*;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PhotoDto.class, name = "photo")
})
@JsonTypeName("attachment")
public class AttachmentDto {
    private String originalUrl;
    private String storageId;
}
