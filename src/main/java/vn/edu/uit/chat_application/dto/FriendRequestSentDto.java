package vn.edu.uit.chat_application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.edu.uit.chat_application.entity.FriendRequest;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class FriendRequestSentDto {
    private enum Type {
        FROM, TO
    }

    private UUID id;
    private Type type;
    private UserSentDto user;

    public static FriendRequestSentDto from(FriendRequest friendRequest, Type type) {
        return switch (type) {
            case FROM ->
                    new FriendRequestSentDto(friendRequest.getId(), type, UserSentDto.from(friendRequest.getFrom()));
            case TO -> new FriendRequestSentDto(friendRequest.getId(), type, UserSentDto.from(friendRequest.getTo()));
        };
    }

    public static FriendRequestSentDto fromFrom(FriendRequest friendRequest) {
        return from(friendRequest, Type.FROM);
    }

    public static FriendRequestSentDto fromTo(FriendRequest friendRequest) {
        return from(friendRequest, Type.TO);
    }

}
