package notes.models.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
public class NoteDto {
    private Long id;
    private String message;
    private Date createdDate;

    @Builder.Default
    private List<AttachmentDto> attachments = new ArrayList<>();

    public boolean addAttachmentDto(AttachmentDto attachmentDto) {
        return this.attachments.add(attachmentDto);
    }
}

