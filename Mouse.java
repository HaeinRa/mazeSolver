import java.math.BigDecimal; // 부동소수점때문에 정확한 값이 안 나와서 사용
public class Mouse {
    private Point location; // 쥐의 좌표, 쥐의 시야 3x3 이용
    private int energy; // 에너지
    private double mana; // 마나
    private int scanCount; // 스캔 횟수
    Maze map;

    public Mouse(Point location, int energy, Maze map ) {
        this.location = location; // 쥐의 좌표를 계속 갱신해주는 메소드도 필요하지 않나?
        this.energy = energy;
        this.map = map;
        this.scanCount = 0;
        this.mana = 3;
    }

    public void move() { // 쥐 이동 : 이동 거리, 방향, 에너지 등 업데이트
        this.energy -= 1;
        this.mana = mana + 0.1;
    }

    public void changeLocation(Point point){
        this.location = point;
    }


    public void scan(){  // 스캔할 때 : 마나 감소, 스캔 횟수 증가시키기
        if(mana >= 3) { // 이 조건은 scan에서 확인할 거긴 한데, test해보려고 한 거임
            mana = mana - 3;
            scanCount ++;
        }
//        this.mana -= 3;
//        scanCount ++;
    }

    public int getEnergy() { // 쥐의 에너지 반환
        return energy;
    }

    public double getMana() { // 쥐의 마나 반환
        return mana;
    }

    public Point getLocation() { // 쥐의 좌표 반환
        return location;
    }

    public int getScanCount(){ // 스캔 횟수 반환
        return scanCount;
    }






    @Override
    public String toString() {
        return "Location: " + location +
                ", Energy: " + energy +
                ", Mana: " + mana +
                ", Scan Count: " + scanCount;
    }



    public static void main(String[] args) {
//        Point initialLocation = new Point(3, 100); // 초기 위치
//        int initialEnergy = 100; // 초기 에너지
//
//        Mouse mouse = new Mouse(initialLocation, initialEnergy);
//        System.out.println("mouse의 초기 상태 >>> "+mouse +"\n"); // 객체 출력
//
//        mouse.move();
//        System.out.println("mouse가 move한 상태 >>> "+mouse +"\n"); // 이동 후 객체 출력
//
//        // 다른 동작 수행...
//
//        // 확인하고자 하는 다양한 동작 수행 후 객체 출력
//        mouse.scan();
//        System.out.println("mouse가 scan한 상태 >>> "+mouse +"\n");
//
//        mouse.move();
//        System.out.println("mouse의 move한 상태 >>> "+mouse +"\n");
//
//        mouse.move();
//        System.out.println("mouse의 move한 상태 >>> "+mouse +"\n");

    }

}

