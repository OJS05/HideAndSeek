package pw.biome.hideandseek.objects;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class HSTeam {

    @Getter
    private final String name;

    @Getter
    private final TeamType teamType;

    @Getter
    private final List<HSPlayer> members;

    public HSTeam(String name, TeamType teamType) {
        this.name = name;
        this.members = new ArrayList<>();
        this.teamType = teamType;
    }

    public void addPlayer(HSPlayer hsPlayer) {
        members.add(hsPlayer);
    }

    public void removePlayer(HSPlayer hsPlayer) {
        members.remove(hsPlayer);
    }
}
