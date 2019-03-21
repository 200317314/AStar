package com.kingdomlands.game.core.entities.util.astar;

import com.badlogic.gdx.math.Vector2;
import com.kingdomlands.game.core.stages.StageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David K on Mar, 2019
 */
public class AStarThread implements Runnable {
    private List<Vector2> path = new ArrayList<>();

    @Override
    public void run() {
        AStar aStar = new AStar(250, 250, 0, 3, 20, 30, StageManager.getAllEntityPositions());
        aStar.process();
        path = aStar.getCalculatedPath();
    }

    public List<Vector2> getPath() {
        return path;
    }
}
