package notes.models.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
public class PhotoDto extends AttachmentDto {
    private int width;
    private int height;
}
