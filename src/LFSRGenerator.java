import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.stream.Collectors;

public class LFSRGenerator {
    private LinkedList<Integer> connectionVector;
    public LinkedList<Integer> registerVector;

    private int rounds = 32;

    public String makeIteration(String initConnectionVector) {

        connectionVector = new LinkedList<>(
                Arrays.stream(initConnectionVector.split(""))
                        .map(Integer::parseInt)
                        .toList()
        );

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < rounds; i++) {
            System.out.println(this);
            int currentRegister = registerVector.getLast();
            ListIterator<Integer> connectionIterator = connectionVector.listIterator(connectionVector.size());
            ListIterator<Integer> registerIterator = registerVector.listIterator(registerVector.size() - 1);
            while (connectionIterator.hasPrevious() && registerIterator.hasPrevious()) {
                Integer previousConnection = connectionIterator.previous();
                Integer previousRegister = registerIterator.previous();
                if (previousConnection == 1) {
                    currentRegister ^= previousRegister;
                }
            }
            registerVector.addFirst(currentRegister);
            registerVector.removeLast();
            sb.append(currentRegister);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "LFSRGenerator{" + "connectionVector=" + connectionVector + ", registerVector=" + registerVector + '}';
    }

}
