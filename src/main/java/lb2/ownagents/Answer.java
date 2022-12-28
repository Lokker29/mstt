package lb2.ownagents;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lb2.environment.wumpusworld.WumpusPercept;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Answer {
    WumpusPercept wumpusPercept;
    int iteration;
}
