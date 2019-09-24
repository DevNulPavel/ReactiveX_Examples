package io.reactivex.android.samples;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


interface User {
}

interface UserManager {
    Observable<User> getUser();
    Completable setName(String name);
    Completable setAge(int age);
}

public class MainActivity extends Activity {
    private static final String TAG = "RxAndroidSamples";

    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        View button = findViewById(R.id.button_run_scheduler);
        // По нажатию на кнопку будем стартовать нашу долгую процедуру
        button.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onRunSchedulerExampleButtonClicked();
            }
        });
    }

    @Override protected void onDestroy() {
        super.onDestroy();

        // обрубаем все запущенные задачи
        disposables.clear();
    }

    void onRunSchedulerExampleButtonClicked() {
        Observable<String> observable = sampleObservable();
        // Запускать будем в IO треде
        observable = observable.subscribeOn(Schedulers.io());
        // Коллбеки будут вызываться уже в главном потоке
        observable = observable.observeOn(AndroidSchedulers.mainThread());
        // Назначаем обработчик
        DisposableObserver<String> observer = new DisposableObserver<String>() {
            // Вызывается при завершении задачи
            @Override public void onComplete() {
                Log.d(TAG, "onComplete()");
            }
            // Вызывается при возникновении ошибки
            @Override public void onError(Throwable e) {
                Log.e(TAG, "onError()", e);
            }
            // Вызывается при получении новых значений
            @Override public void onNext(String string) {
                Log.d(TAG, "onNext(" + string + ")");
                TextView textView = findViewById(R.id.textView1);

                CharSequence chars = textView.getText();
                String text = chars.toString();
                text = text.concat(string + " ");

                textView.setText(text);
            }
        };
        Disposable disp = observable.subscribeWith(observer);
        // Сохраняем нашу задачу в рабочий список
        disposables.add(disp);
    }

    static Observable<String> sampleObservable() {
        // Создаем непосредственно задачу, которая будет выполняться
        ObservableOnSubscribe<String> source = new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                int delay = 1000;
                SystemClock.sleep(delay);
                emitter.onNext("One");
                SystemClock.sleep(delay);
                emitter.onNext("Two");
                SystemClock.sleep(delay);
                emitter.onNext("Three");
                SystemClock.sleep(delay);
                emitter.onNext("Four");
                SystemClock.sleep(delay);
                emitter.onNext("Five");
                SystemClock.sleep(delay);
                emitter.onComplete();
            }
        };
        return Observable.create(source);

        /*Callable<ObservableSource<? extends String>> source = new Callable<ObservableSource<? extends String>>() {
            @Override public ObservableSource<? extends String> call() throws Exception {
                // Выполняем какую-то долгую операцию
                SystemClock.sleep(5000);
                // Выдаем новый Observable с нужными нам значениями
                Observable obs = Observable.just("one", "two", "three", "four", "five");
                return obs;
            }
        };
        return Observable.defer(source);*/
    }

    void testObservableCreateMethod(){
        // Способы создания источников
        /*Flowable.just("Hello");
        Flowable.just("Hello", "World");

        Observable.just("Hello");
        Observable.just("Hello", "World");

        Maybe.just("Hello");
        Single.just("Hello");

        String[] array = { "Hello", "World" };
        List<String> list = Arrays.asList(array);

        Flowable.fromArray(array);
        Flowable.fromIterable(list);

        Observable.fromArray(array);
        Observable.fromIterable(list);

        // Так можно создавать источник данных из какой-то там функции
        Observable<String> obs1 = Observable.fromCallable(new Callable<String>() {
            @Override public String call() {
                return "";
            }
        });
        // Аналогичный код с помощью лямбды
        Observable<String> obs2 = Observable.fromCallable(()->{
           return "test";
        });

        Maybe<String> obs3 = Maybe.fromAction(()-> {
            Log.d(TAG, "Hello");
        });
        Maybe<String> obs4 = Maybe.fromRunnable(()-> {
            Log.d(TAG, "Hello");
        });

        Completable obs5 = Completable.fromAction(()-> {
            Log.d(TAG, "Hello");
        });
        Completable obs6 = Completable.fromRunnable(()-> {
            Log.d(TAG, "Hello");
        });

        Observable<String> obs7 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("Hello");
                e.onComplete();
            }
        });
        Observable<String> obs8 = Observable.create((ObservableEmitter<String> e)->{
            e.onNext("test");
            e.onNext("test");
            e.onNext("test");
            e.onComplete();
        });

        View button = findViewById(R.id.button_run_scheduler);
        Observable<String> obs9 = Observable.create((ObservableEmitter<String> e) -> {
            e.setCancellable(() -> {
                button.setOnClickListener(null);
            });
            button.setOnClickListener(v -> {
                e.onNext("test");
            });
        });*/
    }

    void testObserversMethod(){
        Observable<String> o = Observable.just("Hello");

        // Подписывание на события
        o.subscribe(new Observer<String>() {
            @Override public void onNext(String s) {
            }
            @Override public void onComplete() {
            }
            @Override public void onError(Throwable t) {
            }
            @Override public void onSubscribe(Disposable d) {
            }
        });

        o.subscribe(new DisposableObserver<String>() {
            @Override public void onNext(String s) {
            }
            @Override public void onComplete() {
            }
            @Override public void onError(Throwable t) {
            }
        });

        Disposable d = o.subscribeWith(new DisposableObserver<String>() {
            @Override public void onNext(String s) {
            }
            @Override public void onComplete() {
            }
            @Override public void onError(Throwable t) {
            }
        });
        d.dispose();

        CompositeDisposable disposables = new CompositeDisposable();
        disposables.add(o.subscribeWith(new DisposableObserver<String>() {
            @Override public void onNext(String s) {
            }
            @Override public void onComplete() {
            }
            @Override public void onError(Throwable t) {
            }
        }));

        disposables.dispose();
    }

    void testOperatorsMethod(){
        Observable<String> greeting = Observable.just("Hello");
        Observable<String> yelling = greeting.map((String s) -> {
            return s.toUpperCase();
        });

        UserManager um = null;
        Observable<User> user = um.getUser();
        // Получать на конкретном потоке
        Observable<User> mainThreadUser = user.observeOn(AndroidSchedulers.mainThread());

        Observable<String> response = Observable.fromCallable(() -> {
            return "Test";
        });
        // Выполнять на конкретном потоке
        Observable<String> backgroundResponse = response.subscribeOn(Schedulers.io());

        Observable<String> response1 = Observable.fromCallable(() -> {
            return "";
        });
        // Выполнять на конкретном потоке
        response1 = response1.subscribeOn(Schedulers.io());
        // Конвертим значения
        response1 = response1.map(res -> "Result: " + response); // NetworkOnMainThread!
        // Получать на конкретном потоке
        response1 = response1.observeOn(AndroidSchedulers.mainThread());
        Disposable disp = response1.subscribe();
    }
}