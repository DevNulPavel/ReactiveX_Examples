import java.lang.Thread;
import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class Main {
    public static void main(String[] args) {
        Flowable.just("Hello world").subscribe(System.out::println);

        Flowable.fromCallable(() -> {
            Thread.sleep(1000); //  imitate expensive computation
            return "Done";
        }).
                subscribeOn(Schedulers.io()).
                observeOn(Schedulers.single()).
                subscribe(System.out::println, Throwable::printStackTrace);

        try {
            Thread.sleep(2000); // <--- wait for the flow to finish
        }catch (Exception e){
        }
    }
}