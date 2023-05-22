
class Point {
    int x, y;
    Point(int x, int y){
        this.x = x;
        this.y = y;
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
        Point tmp = new Point(0,0);
        tmp.x += this.x;
        tmp.y += this.y;
        return tmp;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d) ", this.x, this.y);
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
        for(int i=0; i<this.size; i++) {
            result.append(n.data);
            n = n.link;
        }
        return "top -> "+result.toString()+" ]";
    }
}