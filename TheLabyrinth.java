import java.util.*;

class Coordinate {
    int x, y;

    Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    Coordinate[] getNeighbours() {
        return new Coordinate[] {
            new Coordinate(x, y - 1), new Coordinate(x + 1, y), 
            new Coordinate(x, y + 1), new Coordinate(x - 1, y)
        };
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Coordinate that = (Coordinate) obj;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}

class Player {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static int height, width;
    private static char[][] map;
    private static Coordinate currentPos;
    private static final String[] DIRECTIONS = {"UP", "RIGHT", "DOWN", "LEFT"};
    private static final int UP = 0, RIGHT = 1, DOWN = 2, LEFT = 3;

    private static char getMapCell(Coordinate coord) {
        return map[coord.y][coord.x];
    }

    private static boolean isInMap(Coordinate coord) {
        return coord.x >= 0 && coord.x < width && coord.y >= 0 && coord.y < height;
    }

    private static boolean shouldAvoid(Coordinate coord, char[] avoid) {
        for (char ch : avoid) {
            if (getMapCell(coord) == ch) return true;
        }
        return false;
    }

    public static void updateMapAndPosition() {
        currentPos.y = SCANNER.nextInt();
        currentPos.x = SCANNER.nextInt();

        for (int y = 0; y < height; y++) {
            String row = SCANNER.next();
            if (currentPos.y - 2 <= y && y <= currentPos.y + 2) {
                for (int x = Math.max(currentPos.x - 2, 0); x < Math.min(currentPos.x + 3, width); x++) {
                    map[y][x] = row.charAt(x);
                }
            }
        }
    }

    public static boolean navigateTo(char target, char[] toAvoid, boolean onlyReveal) {
        int[][] predecessors = new int[height][width];
        for (int[] row : predecessors) Arrays.fill(row, -1);

        Queue<Coordinate> queue = new LinkedList<>();
        queue.add(currentPos);

        while (!queue.isEmpty() && getMapCell(queue.peek()) != target) {
            Coordinate[] neighbours = queue.remove().getNeighbours();
            for (int i = 0; i < neighbours.length; i++) {
                Coordinate neighbour = neighbours[i];
                if (isInMap(neighbour) && !shouldAvoid(neighbour, toAvoid) && predecessors[neighbour.y][neighbour.x] == -1) {
                    predecessors[neighbour.y][neighbour.x] = (i + 2) % 4;
                    queue.add(neighbour);
                }
            }
        }

        if (queue.isEmpty()) return false;

        Coordinate targetCell = queue.remove();
        Stack<Integer> path = new Stack<>();
        for (Coordinate iter = targetCell; !iter.equals(currentPos); iter = iter.getNeighbours()[predecessors[iter.y][iter.x]]) {
            path.push((predecessors[iter.y][iter.x] + 2) % 4);
        }

        while ((onlyReveal && getMapCell(targetCell) == target) || (!onlyReveal && !path.isEmpty())) {
            System.out.println(DIRECTIONS[path.pop()]);
            updateMapAndPosition();
        }
        return true;
    }

    public static void main(String[] args) {
        height = SCANNER.nextInt();
        width = SCANNER.nextInt();
        currentPos = new Coordinate(-1, -1);

        int alarmRounds = SCANNER.nextInt();
        map = new char[height][width];
        for (char[] row : map) Arrays.fill(row, '?');

        updateMapAndPosition();

        char[] avoidWallsAndControlRoom = {'#', 'C'};
        while (navigateTo('?', avoidWallsAndControlRoom, true));

        char[] avoidWalls = {'#', '?'};
        if (!navigateTo('C', avoidWalls, false)) {
            System.out.println("Failed to reach control room");
        } else if (!navigateTo('T', avoidWalls, false)) {
            System.out.println("Failed to return to start");
        }
    }
}
