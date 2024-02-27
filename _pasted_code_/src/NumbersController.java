public class NumbersController {
    public void add(Numbers numbers) {
        double result = numbers.getFirst() + numbers.getSecond() + numbers.getThird();
        numbers.setResult(result);
    }

    public void multiply(Numbers numbers) {
        double result = numbers.getFirst() * numbers.getSecond();
        numbers.setResult(result);
    }
}