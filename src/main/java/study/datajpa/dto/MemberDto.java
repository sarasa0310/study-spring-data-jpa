package study.datajpa.dto;

import lombok.Data;
import study.datajpa.entity.Member;

@Data
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
