package vn.edu.uit.chat_application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SendingMessageDto {
    private String content;
    private List<AttachmentDto> attachments;
}
