public class Mouse {
    private Point location; // 쥐의 좌표
    private int energy; // 에너지
    private int mana; // 마나
    private int scanCount; // 스캔 횟수
    private Point sight; // 쥐의 시야 3x3

    public Mouse(Point location, int energy ) {
        this.location = location;
        this.energy = energy;
        mana = 3;
    }

    public void move() { // 쥐 이동 : 이동 거리, 방향, 에너지 등 업데이트
        this.energy -= 1;
        this.mana +=0.1;
    }

    public void decreaseMana(){ // 마나 감소 메소드 -> item 메소드 내에서 사용
        this.mana -= 3;
    }

    public int getEnergy() { // 쥐의 에너지 반환
        return energy;
    }

    public int getMana() { // 쥐의 마나 반환
        return mana;
    }

    public Point getLocation() { // 쥐의 좌표 반환
        return location;
    }

    public int getScanCount(){ // 스캔 횟수 반환
        return scanCount;
    }

    public Point getSight(){ // 쥐의 시야 반환
        return sight;
    }

    @Override
    public String toString() {
        return "Location: " + location +
                ", Energy: " + energy +
                ", Mana: " + mana +
                ", Scan Count: " + scanCount;
    }

}
