package vn.edu.uit.chat_application.dto.received;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementReceivedDto {
    private List<String> tos;
    private String content;
}
