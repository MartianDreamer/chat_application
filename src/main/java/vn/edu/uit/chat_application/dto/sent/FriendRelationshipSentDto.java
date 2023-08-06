package vn.edu.uit.chat_application.dto.sent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.edu.uit.chat_application.entity.FriendRelationship;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FriendRelationshipSentDto {
    private UUID id;
    private UserSentDto friend;
    private LocalDateTime since;

    public static FriendRelationshipSentDto from(FriendRelationship friendRelationship, UUID selfId) {
        if (selfId.equals(friendRelationship.getFirst().getId())) {
            return new FriendRelationshipSentDto(
                    friendRelationship.getId(),
                    UserSentDto.from(friendRelationship.getSecond()),
                    friendRelationship.getSince()
            );
        } else if(selfId.equals(friendRelationship.getSecond().getId())) {
            return new FriendRelationshipSentDto(
                    friendRelationship.getId(),
                    UserSentDto.from(friendRelationship.getFirst()),
                    friendRelationship.getSince()
            );
        }
        throw new IllegalArgumentException("not a self id");
    }
}
