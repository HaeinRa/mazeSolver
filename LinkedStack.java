
class Point {
    int x, y;
    int fScore;
    int gScore; // 지금까지 소모한 비용을 계산해야하므로 가지고 있어야 함
    Point prevPoint; // 이전 노드
    Point(){
        this.x = -99;
        this.y = -99;
    }
    Point(int x, int y){
        this.x = x;
        this.y = y;
    }
    Point(int x, int y, int gScore, int fScore, Point prevPoint) {
        this.x = x;
        this.y = y;
        this.gScore = gScore;
        this.fScore = fScore;
        this.prevPoint = prevPoint;
    }
    void printPoint(){
        System.out.printf("(%d, %d) ", this.x, this.y);
    }

    public Point add() {
        return add(0, 0);
    }

    public Point add(int x) {
        return add(x, 0);
    }

    public Point add(int x, int y) {
        return new Point(this.x + x, this.y + y);
    }


    @Override
    public String toString() {
        return String.format("(%d, %d) ", this.x, this.y);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Point otherPoint = (Point) obj;
        return this.x == otherPoint.x && this.y == otherPoint.y;
    }

}

// 연결리스트를 위한 노드
class Node<T>{
    Node link;
    T data;

    Node(){
        this.link = null;
        this.data = null;
    }
    Node(T data){
        this.link = null;
        this.data = data;
    }
    Node(Node link, T data){
        this.link = link;
        this.data = data;
    }
}
public class LinkedStack<T> {
    Node<T> top;
    int size; // 현재 스택의 크기 카운트

    public LinkedStack() {
        this.top = null;
        this.size = 0;
    }

    public boolean isEmpty() {
        return top == null;
    }
    //isFull 없음

    void clear() {
        this.top = null;
    }

    void push(T data) {
        Node<T> n = new Node<T>(this.top, data);
        this.top = n;
        this.size += 1;
    }

    T pop() {
        if (!this.isEmpty()) {
            Node<T> n = this.top;
            this.top = n.link;
            this.size -= 1;
            return n.data;
        } else { // 비어있을 때 pop 시도
            System.out.println("Empty Stack: Can't do pop function");
            return null;
        }
    }

    // 가장 위의 데이터 값만 가져오는 연산
    T peek() {
        if (!this.isEmpty()) return this.top.data;
        else {
            System.out.println("Empty Stack: Can't do peek function");
            return null;
        }
    }

    // class print 가능

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        Node n = this.top;
        while(n != null) {
            result.append(n.data);
            n = n.link;
        }

        return "top -> "+result.toString()+" ]";
    }

}
