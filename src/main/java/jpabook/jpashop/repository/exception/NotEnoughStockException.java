package jpabook.jpashop.repository.exception;

public class NotEnoughStockException extends RuntimeException{

    
    //'내장 클래스 RuntimeException의 아래 내장 메소드들'을 '오버라이딩'해주는 것
    //왜냐하면, 아래 형식에 맞춰서 '예외 발생시키는 로직 작성하는 곳'들에서 그 형식대로 작성해줘야 하기 때문!
    public NotEnoughStockException() {
        super();
    }

    public NotEnoughStockException(String message) {
        super(message);
    }

    public NotEnoughStockException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEnoughStockException(Throwable cause) {
        super(cause);
    }

    protected NotEnoughStockException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
