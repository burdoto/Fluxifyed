package flustix.fluxifyed.database.api.v1.components;

import net.dv8tion.jda.api.entities.Member;

public class APIMember extends APIUser {
    public final String nickname;
    public final String onlineStatus;
    public final APIColor color;
    public final boolean owner;

    public APIMember(Member member) {
        super(member.getUser());

        nickname = member.getNickname();
        onlineStatus = member.getOnlineStatus().getKey();
        color = new APIColor(member.getColor());
        owner = member.isOwner();
    }
}
