package vn.edu.uit.chat_application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.edu.uit.chat_application.entity.BlockRelationship;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BlockRelationshipSentDto {
    private UUID id;
    private UserSentDto blocked;

    public static BlockRelationshipSentDto from(BlockRelationship blockRelationship) {
        return new BlockRelationshipSentDto(blockRelationship.getId(), UserSentDto.from(blockRelationship.getBlocked()));
    }
}
