public class LinearCongruentGenerator {
    public long currentSeed;

    private long a;
    private long b;
    private long m;


    public LinearCongruentGenerator(long seed, long a, long b, long m) {
        if (nod(b, m) != 1){
            throw new RuntimeException("Nod check failed for b = " + b + " m = " + m);
        }
        this.currentSeed = seed;
        this.a = a;
        this.b = b;
        this.m = m;
    }

    private long nod(long a, long b) {
        while (b != 0) {
            long tmp = a % b;
            a = b;
            b = tmp;
        }
        return a;
    }

    public long next(){
        return currentSeed = (a * currentSeed + b) % m;
    }
}
