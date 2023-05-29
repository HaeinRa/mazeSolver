public class Cell {
    private boolean isAvailable; // 지나갈 수 있음
    private boolean isWall; // 벽
    private boolean isExit; // 출구
    private boolean isVisited; // 방문한 곳
    private boolean isBranch; // 분기점인가?

    public enum State {
        AVAILABLE,
        WALL,
        EXIT,
        VISIT,
        NotRecommended,
        BRANCH
    }

    public Cell(int info) {
        this.isAvailable = (info == 0 || info == 2 || info == 3); // 이거 바꿔야 할 듯
        this.isWall = (info == 1);
        this.isExit = (info == 2);
        this.isVisited = (info == 3);
        this.isBranch = false;
    }

    public Cell(Cell cell) {
        this.isAvailable = cell.isAvailable;
        this.isWall = cell.isWall;
        this.isExit = cell.isExit;
        this.isVisited = cell.isVisited;
        this.isBranch = cell.isBranch;
    }

    public static Cell createCopy(Cell cell) {
        return new Cell(cell);
    }

    public boolean isWall() {
        return this.isWall;
    }

    public boolean isExit() {
        return this.isExit;
    }

    public boolean isAvailable() {
        return this.isAvailable;
    }

    public boolean isVisited() {
        return this.isVisited;
    }

    public boolean isBranch(){
        return this.isBranch;
    }


    //방문 가능한데, 이미 방문했을 수도 있는거 아닌가? 상태를 딱 하나만 정하는게 맞나?
    //다시 되돌아갈 때, 방문 했던 곳을 가야하잖아.
    //각 변수들이 있는데 getState가 필요한가?
    public State getState() {
        if(isWall) {
            return State.WALL;
        }
        else if(isExit){
            return State.EXIT;
        }
        else if(isBranch){
            return State.BRANCH;
        }
        else if(isAvailable){
            if(isVisited)
                return State.VISIT;
            else
                return State.AVAILABLE;
        }
        else // 벽도 아니고, 출구도 아니고, 이용 가능한 곳이 아닌.  또는 방문을 했었지만 지금은 이용 가능한 곳이 아닌 추천하지 않는
            return State.NotRecommended;
    }

    // 일관성 있게 vist()를 setState에 합치자.
//  public void visit() {
//    this.isVisited = true;
//  }
    public void setState(State state) {
        switch(state){
            case WALL:
                this.isAvailable = false;
                this.isWall = true;
                this.isExit = false;
                this.isVisited = false;
                break;
            case AVAILABLE:
                this.isAvailable = true;
                this.isWall = false;
                this.isExit = false;
                this.isVisited = false;
                this.isBranch = false;
                break;
            case EXIT:
                this.isAvailable = true;
                this.isWall = false;
                this.isExit = true;
                break;
            case VISIT:
                this.isVisited = true;
                break;
            case NotRecommended:
                if(this.isBranch)
                    break;
                this.isAvailable = false;
                this.isVisited = false;
                this.isWall = false;
                this.isBranch = false;
                this.isExit = false;
                break;
            case BRANCH:
                this.isBranch = true;
                this.isVisited = true;
                break;
            default:
                System.out.println("That state does not exist.");
        }

    }
}
