package io.reactivex.android.samples;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import javax.net.ssl.HttpsURLConnection;

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

        //onRunSchedulerExampleButtonClicked();
        testOperatorsMethod2();
    }

    @Override protected void onDestroy() {
        super.onDestroy();

        // обрубаем все запущенные задачи
        disposables.clear();
    }

    void onRunSchedulerExampleButtonClicked() {
        // Создаем непосредственно задачу, которая будет выполняться
        ObservableOnSubscribe<String> urlSource = new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                int delay = 10;
                SystemClock.sleep(delay);
                emitter.onNext("http://devnulpavel.ddns.net:8080/");
                SystemClock.sleep(delay);
                emitter.onNext("http://devnulpavel.ddns.net:8080/");
                emitter.onComplete();
            }
        };

        Observable<String> observable = Observable.create(urlSource);
        // По передаваемым URL будем делать новый запрос на сервер
        observable = observable.flatMap(urlString ->{
            // Инициируем запрос на полученный URL, дальше результат как-то будем обрабатывать
            Observable<String> urlReceiver = Observable.fromCallable(()->{
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                try {
                    InputStream inputStream = new BufferedInputStream(conn.getInputStream());

                    Scanner s = new Scanner(inputStream).useDelimiter("<body>");
                    return s.hasNext() ? s.next() : "";
                } finally {
                    conn.disconnect();
                }
            });
            // Запросы будем исполнять на io потоке
            urlReceiver = urlReceiver.subscribeOn(Schedulers.io());
            // Для отладки будем выводить текущий поток
            urlReceiver = urlReceiver.doOnNext(text ->{
                Log.d(TAG, "Text request thread: " + Thread.currentThread().getName());
            });
            return urlReceiver;
        });
        // Фильтруем пустые строки
        observable = observable.filter(text -> !text.isEmpty());
        // Выдавать будем с определенной задержкой результаты
        observable = observable.delay(100, TimeUnit.MILLISECONDS);
        // Запускать будем в IO треде
        observable = observable.subscribeOn(Schedulers.io());
        // Коллбеки будут вызываться уже в главном потоке
        observable = observable.observeOn(AndroidSchedulers.mainThread());
        // Для отладки будем выводить текущий поток
        observable = observable.doOnNext(text ->{
            Log.d(TAG, "Receive thread: " + Thread.currentThread().getName());
        });
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

    void testOperatorsMethod1(){
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

    void testOperatorsMethod2(){

        // Метод subscribeOn, говорит в какой поток наблюдаемый источник будет кидать свои значения
        // в данном случае - значения будут и после приходить том же потоке, так как поток не меняется
        // интересным, является то, что влияние оказывает только первая строчка
        Observable<Integer> obs1 = Observable.just(1, 2, 3, 4, 5, 6);
        obs1 = obs1.subscribeOn(Schedulers.computation());
        obs1 = obs1.subscribeOn(Schedulers.io());
        obs1 = obs1.subscribeOn(AndroidSchedulers.mainThread());
        obs1 = obs1.doOnNext(integer -> Log.d(TAG, "obs1, thread: " + Thread.currentThread().getName()) );

        // Метод observeOn, говорит на каком потоке мы должны ожидать сообщения
        // данный метод можно без проблем вызывать множество раз для переключения потоков
        Observable<Integer> obs2 = Observable.just(1, 2, 3, 4, 5, 6);
        obs2 = obs2.subscribeOn(Schedulers.newThread());
        obs2 = obs2.doOnNext(integer -> Log.d(TAG, "obs2, thread: " + Thread.currentThread().getName()));
        obs2 = obs2.observeOn(Schedulers.io());
        obs2 = obs2.doOnNext(integer -> Log.d(TAG, "obs2, thread: " + Thread.currentThread().getName()) );
        obs2 = obs2.observeOn(Schedulers.single()); // Специальный шедулер, который основывается на единственном потоке для выполнения последовательной работы
        obs2 = obs2.doOnNext(integer -> Log.d(TAG, "obs2, thread: " + Thread.currentThread().getName()) );
        obs2 = obs2.observeOn(AndroidSchedulers.mainThread());
        obs2 = obs2.doOnNext(integer -> Log.d(TAG, "obs2, thread: " + Thread.currentThread().getName()) );

        Observable<Integer> finalObs = obs1.concatWith(obs2);
        Single<Long> completeObs = finalObs.count();
        //Boolean completed = completeObs.blockingGet();
        completeObs = completeObs.observeOn(AndroidSchedulers.mainThread());
        Disposable disp1 = completeObs.subscribe(complete -> Log.d(TAG, "Thread: " + Thread.currentThread().getName() + " -> All work complete") );


        // Возможен вариант, когда мы запускаем задачу, которая выдает в фоне данные, а ждем мы их в текущем вызванном потоке
        // все подобные методы начинаются с blocking*
        Observable<Integer> obs3 = Observable.create((ObservableEmitter<Integer> emitter)->{
            emitter.onNext(1);
            emitter.onNext(2);
            emitter.onNext(3);
            emitter.onComplete();
        });
        obs3 = obs3.subscribeOn(Schedulers.computation()); // Данные будут выдываться в потоке вычисления
        obs3 = obs3.doOnNext(integer -> Log.d(TAG, "obs1, thread: " + Thread.currentThread().getName()) );
        obs3.blockingForEach(val ->{
            Log.d(TAG, "obs3, thread: " + Thread.currentThread().getName() + " received val->" + val);
        });


        // Возможен вариант flatMap, который с помощью первой функции получает дополнительную инфу о нем,
        // затем вторым параметром эту инфу использует в своих целях и выдает совокупный результат
        List<Integer> sourcesList = Arrays.asList(1, 2, 3, 4);
        Observable<List<Integer>> obs4 = Observable.just(sourcesList);
        Observable<Integer> obs5 = obs4.flatMap(list -> Observable.fromIterable(list) );
        obs5 = obs5.flatMap(val -> Observable.just(val+10), (val, newInfo) -> {
            val += 100;
            return val + newInfo;
        });
        obs5.blockingForEach(val ->{
            Log.d(TAG, "obs5, thread: " + Thread.currentThread().getName() + " received val->" + val);
        });

        // Так же мы можем выполнять конкатенацию элементов друг с другом
        Observable<Integer> obs6_1 = Observable.just(1, 2, 3, 4);
        Observable<Integer> obs6_2 = Observable.just(1, 2, 3, 4);
        Observable<Integer> obs7 = obs6_1.zipWith(obs6_2, (val1, val2)-> val1 + val2);
        obs7.blockingForEach(val ->{
            Log.d(TAG, "obs8, thread: " + Thread.currentThread().getName() + " received val->" + val);
        });
    }
}
