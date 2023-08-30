package study.datajpa.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import study.datajpa.entity.Member;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberDto {

    private final Long memberId;
    private final String username;
    private final String teamName;

    public static MemberDto toDto(Member member) {
        return new MemberDto(
                member.getId(),
                member.getUsername(),
                member.getTeam().getName()
        );
    }

}
