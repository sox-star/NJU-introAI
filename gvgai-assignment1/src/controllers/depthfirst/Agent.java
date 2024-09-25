package controllers.depthfirst;

import java.util.Stack;
import java.util.ArrayList;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

/**
 * Created with IntelliJ IDEA.
 * @author younghzhe
 */
public class Agent extends AbstractPlayer {

    private int currentStep = 0;
    private ArrayList<Types.ACTIONS> path = new ArrayList<>();

    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer)
    {
        StateObservation stCopy = so.copy();
        DepthFirstAgent(stCopy);
    }

    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        currentStep++;
        return path.get(currentStep - 1);
    }

    private ArrayList<Types.ACTIONS> DepthFirstAgent(StateObservation so) {
        Stack<StateObservation> states = new Stack<>();
        Stack<ArrayList<Types.ACTIONS>> paths = new Stack<>();
        ArrayList<StateObservation>  estates = new ArrayList<>();
        states.push(so);
        paths.push(new ArrayList<>());
        while (!states.isEmpty() && states.peek().getGameWinner() != Types.WINNER.PLAYER_WINS) {
            StateObservation state = states.pop();
            ArrayList<Types.ACTIONS> currentPath = paths.pop();
            estates.add(state);
            ArrayList<Types.ACTIONS>  actions = state.getAvailableActions();
            for (Types.ACTIONS action:actions) {
                StateObservation stCopy = state.copy();
                stCopy.advance(action);
                boolean isVisited = false;
                for (StateObservation o : estates) {
                    if (stCopy.equalPosition(o)) {
                        isVisited = true;
                        break;
                    }
                }
                if (!isVisited) {
                    states.push(stCopy);
                    ArrayList<Types.ACTIONS> newPath = new ArrayList<>(currentPath);
                    newPath.add(action);
                    paths.push(newPath);
                }
            }
        }
        path = paths.pop();
        return path;
    }
}