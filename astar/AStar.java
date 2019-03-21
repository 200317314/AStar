package com.kingdomlands.game.core.entities.util.astar;

import com.badlogic.gdx.math.Vector2;
import com.kingdomlands.game.core.entities.util.astar.Cell;

import java.util.*;

/**
 * Created by David K on Mar, 2019
 */
public class AStar {
    //costs for diagonal and vertical/horizontal moves
    public static final int DIAGONAL_COST = 14, V_H_COST = 10;

    //cells of our grid
    private Cell[][] grid;

    //the set of nodes to be evaluated, lowest cost cells in first
    private PriorityQueue<Cell> openCells;

    //the set of nodes already evaluated
    private boolean[][] closedCells;

    //start cell
    private int startX, startY;

    //end cell
    private int endX, endY;

    public AStar(int width, int height, int startX, int startY, int endX, int endY, List<Vector2> entities) {
        grid = new Cell[width][height];
        closedCells = new boolean[width][height];
        openCells = new PriorityQueue<>(Comparator.comparingInt(Cell::getFinalCost));

        startCell(startY, startX);
        endCell(endY, endX);

        //init heuristic
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j] = new Cell(i, j);
                grid[i][j].setHeuristicCost(Math.abs(i - endY) + Math.abs(j - endX));
                grid[i][j].setSolution(false);
            }
        }

        grid[startY][startX].setFinalCost(0);

        //add blocks to grid ie entities
        /*for (int i = 0; i < blocks.length; i++) {
            addBlockOnCell(blocks[i][0], blocks[i][1]);
        }*/

        for (Vector2 e : entities) {
            addBlockOnCell((int)e.x, (int)e.y);
        }
    }

    public void addBlockOnCell(int x, int y) {
        grid[y][x] = null;
    }

    public void startCell(int x, int y) {
        startX = x;
        startY = y;
    }

    public void endCell(int x, int y) {
        endX =x;
        endY = y;
    }

    public void updateCostIfNeeded(Cell current, Cell t, int cost) {
        if (Objects.isNull(t) || closedCells[t.getX()][t.getY()]) {
            return;
        }

        int tFinalCost = t.getHeuristicCost() + cost;
        boolean isOpen = openCells.contains(t);

        if (!isOpen || tFinalCost < t.getFinalCost()) {
            t.setFinalCost(tFinalCost);
            t.setParent(current);

            if (!isOpen)
                openCells.add(t);
        }
    }

    public void process() {
        openCells.add(grid[startX][startY]);
        Cell current;

        while (true) {
            current = openCells.poll();

            if (Objects.isNull(current)) {
                break;
            }

            closedCells[current.getX()][current.getY()] = true;

            if (current.equals(grid[endX][endY])) {
                return;
            }

            Cell t;

            if (current.getX() - 1 >= 0) {
                t = grid[current.getX() - 1][current.getY()];
                updateCostIfNeeded(current, t, current.getFinalCost() + V_H_COST);

                if (current.getY() - 1 >= 0) {
                    t = grid[current.getX() - 1][current.getY() - 1];
                    updateCostIfNeeded(current, t, current.getFinalCost() + DIAGONAL_COST);
                }

                if (current.getY() + 1 < grid[0].length) {
                    t = grid[current.getX() - 1][current.getY() + 1];
                    updateCostIfNeeded(current, t, current.getFinalCost() + DIAGONAL_COST);
                }
            }

            if (current.getY() - 1 >= 0) {
                t = grid[current.getX()][current.getY() - 1];
                updateCostIfNeeded(current, t, current.getFinalCost() + V_H_COST);
            }

            if (current.getY() + 1 < grid[0].length) {
                t = grid[current.getX()][current.getY() + 1];
                updateCostIfNeeded(current, t, current.getFinalCost() + V_H_COST);
            }

            if (current.getX() + 1 < grid.length) {
                t = grid[current.getX() + 1][current.getY()];
                updateCostIfNeeded(current, t, current.getFinalCost() + V_H_COST);

                if (current.getY() - 1 >= 0) {
                    t = grid[current.getX() + 1][current.getY() - 1];
                    updateCostIfNeeded(current, t, current.getFinalCost() + DIAGONAL_COST);
                }

                if (current.getY() + 1 > grid[0].length) {
                    t = grid[current.getX() + 1][current.getY() + 1];
                    updateCostIfNeeded(current, t, current.getFinalCost() + DIAGONAL_COST);
                }
            }
        }
    }

    public void display() {
        System.out.println("Grid: ");

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (i == startX && j == startY) {
                    System.out.print("SC ");
                } else if (i == endX && j == endY) {
                    System.out.print("DC ");
                } else if (grid[i][j] != null) {
                    System.out.printf("%-3d", 0);
                } else {
                    System.out.print("BL ");
                }
            }

            System.out.println();
        }

        System.out.println();
    }

    public void displayScores() {
        System.out.println("\nScores for cells: ");

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] != null) {
                    System.out.printf("%-3d ", grid[i][j].getFinalCost());
                } else {
                    System.out.print("BL ");
                }
            }

            System.out.println();
        }

        System.out.println();
    }

    public void displaySolution() {
        if (closedCells[endX][endY]) {
            //We track back the path
            System.out.println("Path: ");
            Cell current = grid[endX][endY];
            System.out.println(current);
            grid[current.getX()][current.getY()].setSolution(true);

            while (current.getParent() != null) {
                System.out.println(" -> " + current.getParent());
                grid[current.getParent().getX()][current.getParent().getY()].setSolution(true);
                current = current.getParent();
            }

            System.out.println("\n");

            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[i].length; j++) {
                    if (i == startX && j == startY) {
                        System.out.print("SC ");
                    } else if (i == endX && j == endY) {
                        System.out.print("DC ");
                    } else if (grid[i][j] != null) {
                        System.out.printf("%-3s", grid[i][j].isSolution() ? "X" : "0");
                    } else {
                        System.out.print("BL ");
                    }
                }

                System.out.println();
            }

            System.out.println();
        } else {
            System.out.println("No possible path");
        }
    }

    public List<Vector2> getCalculatedPath() {
        List<Vector2> path = new ArrayList<>();

        if (closedCells[endX][endY]) {
            //We track back the path
            Cell current = grid[endX][endY];
            grid[current.getX()][current.getY()].setSolution(true);

            while (current.getParent() != null) {
                path.add(new Vector2(current.getParent().getX(), current.getParent().getY()));
                grid[current.getParent().getX()][current.getParent().getY()].setSolution(true);
                current = current.getParent();
            }
        } else {
            System.out.println("No possible path");
        }

        return path;
    }
}
