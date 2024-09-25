package controllers.Astar;

import java.util.PriorityQueue;
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

    private static class StateWithCost implements Comparable<StateWithCost> {
        StateObservation state;
        ArrayList<Types.ACTIONS> paths;
        double cost;

        public StateWithCost(StateObservation state, ArrayList<Types.ACTIONS> paths, double cost) {
            this.state = state;
            this.paths = paths;
            this.cost = cost;
        }

        @Override
        public int compareTo(StateWithCost other) {
            return Double.compare(this.cost, other.cost);
        }
    }

    private ArrayList<Observation>[] fixedPositions;
    private ArrayList<Observation>[] movingPositions;
    private Vector2d goalpos;
    private Vector2d keypos;
    private int currentStep = 0;
    private ArrayList<Types.ACTIONS> path = new ArrayList<>();

    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer)
    {
        fixedPositions = so.getImmovablePositions();
        movingPositions = so.getMovablePositions();
        goalpos = fixedPositions[1].get(0).position;
        keypos = movingPositions[0].get(0).position;
        StateObservation stCopy = so.copy();
        Astar(stCopy);
    }

    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        currentStep++;
        return path.get(currentStep - 1);
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
            score += distanceToGoal;
        } else {
            score += distanceToKey;
        }
        return score;
    }

    private ArrayList<Types.ACTIONS> Astar(StateObservation so) {
        PriorityQueue<StateWithCost> states = new PriorityQueue<>();
        ArrayList<StateWithCost> estates = new ArrayList<>();
        double nCost = 0;
        states.add(new StateWithCost(so, new ArrayList<>(), 0));
        while (!states.isEmpty() && states.peek().state.getGameWinner() != Types.WINNER.PLAYER_WINS) {
            StateWithCost state = states.poll();
            ArrayList<Types.ACTIONS> currentPath = state.paths;
            boolean Visited = false;
            for (StateWithCost o : estates) {
                if (state.state.equalPosition(o.state)) {
                    Visited = true;
                    break;
                }
            }
            if (!Visited) {
                estates.add(state);
                ArrayList<Types.ACTIONS>  actions = state.state.getAvailableActions();
                for (Types.ACTIONS action:actions) {
                    StateObservation stCopy = state.state.copy();
                    stCopy.advance(action);
                    boolean isVisited = false;
                    for (StateWithCost o : estates) {
                        if (stCopy.equalPosition(o.state)) {
                            isVisited = true;
                            break;
                        }
                    }
                    if (!isVisited && stCopy.getGameWinner() != Types.WINNER.PLAYER_LOSES) {
                        nCost = currentPath.size() - stCopy.getGameScore() + evaluate(stCopy);
                        ArrayList<Types.ACTIONS> newPath = new ArrayList<>(currentPath);
                        newPath.add(action);
                        states.add(new StateWithCost(stCopy, newPath, nCost));
                    }
                }
            }
            }
        path = states.poll().paths;
        return path;
    }
}