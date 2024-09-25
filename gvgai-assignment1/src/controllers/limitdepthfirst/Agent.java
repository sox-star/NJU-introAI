package controllers.limitdepthfirst;

import java.util.ArrayList;
import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

/**
 * Created with IntelliJ IDEA.
 * @author younghzhe
*/
public class Agent extends AbstractPlayer {

    private ArrayList<Observation>[] fixedPositions;
    private ArrayList<Observation>[] movingPositions;
    private Vector2d goalpos;
    private Vector2d keypos;
    final private int depth = 3;
    private ArrayList<StateObservation>  paths = new ArrayList<>();

    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer)
    {
        fixedPositions = so.getImmovablePositions();
        movingPositions = so.getMovablePositions();
        goalpos = fixedPositions[1].get(0).position;
        keypos = movingPositions[0].get(0).position;
    }

    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        StateObservation path = stateObs.copy();
        boolean isVisited = false;
        for (StateObservation o : paths) {
            if (stateObs.equalPosition(o)) {
                isVisited = true;
                break;
            }
        }
        if (!isVisited) {
            paths.add(path);
        }
        Types.ACTIONS bestAction = null;
        double bestValue = Double.NEGATIVE_INFINITY;
        ArrayList<Types.ACTIONS>  actions = stateObs.getAvailableActions();
        for (Types.ACTIONS action:actions) {
            StateObservation stCopy = stateObs.copy();
            stCopy.advance(action);
            isVisited = false;
            for (StateObservation o : paths) {
                if (stCopy.equalPosition(o)) {
                    isVisited = true;
                    break;
                }
            }
            if (!isVisited) {
                ArrayList<StateObservation> estates = new ArrayList<>();
                double value = limitDepthFirstAgent(stCopy, depth, estates);
                if (value > bestValue) {
                    bestValue = value;
                    bestAction = action;
                }
            }
        }
        return bestAction;
    }

    private static double manhattanDistance(Vector2d p1, Vector2d p2) {
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    }

    private double evaluate(StateObservation so) {
        double score = 0;
        Vector2d avatarpos = so.getAvatarPosition();
        double distanceToGoal = manhattanDistance(avatarpos, goalpos);
        double distanceToKey = manhattanDistance(avatarpos, keypos);
        if (so.getAvatarType() == 4) {
            score -= distanceToGoal;
        } else {
          score -= distanceToKey;
        }
        return score;
    }

    private double limitDepthFirstAgent(StateObservation so, int depth, ArrayList<StateObservation>  estates) {
        if (depth == 0 || so.isGameOver()) {
            return evaluate(so);
        }
        double bestValue = Double.NEGATIVE_INFINITY;
        estates.add(so);
        for (Types.ACTIONS action:so.getAvailableActions()) {
            StateObservation stCopy = so.copy();
            stCopy.advance(action);
            boolean isVisited = false;
            for (StateObservation o : estates) {
                if (stCopy.equalPosition(o)) {
                    isVisited = true;
                    break;
                }
            }
            if (!isVisited) {
                double value = limitDepthFirstAgent(stCopy, depth - 1, estates);
                bestValue = Math.max(bestValue, value);
            }
        }
        return bestValue;
    }
}