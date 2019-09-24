package com.project.dajver.rxandroidecample;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import com.project.dajver.rxandroidecample.adapter.ArticlesRecyclerAdapter;
import com.project.dajver.rxandroidecample.api.IRepository;
import com.project.dajver.rxandroidecample.api.RepositoryImpl;
import com.project.dajver.rxandroidecample.api.model.ArticlesModel;



public class MainActivity extends AppCompatActivity {

    CompositeDisposable _disposables = new CompositeDisposable();

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        requestTable();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _disposables.dispose();
    }

    private void requestTable(){
        IRepository repo = new RepositoryImpl();

        // Стартуем запрос статей
        Observable<ArrayList<ArticlesModel>> obs = repo.getArticles("https://habr.com/ru/all/");

        // Выполнять будем на IO потоке
        obs = obs.subscribeOn(Schedulers.io());

        // Коллбеки будем получать в главном потоке
        obs = obs.observeOn(AndroidSchedulers.mainThread());

        // Получение данных в главном потоке
        DisposableObserver<ArrayList<ArticlesModel>> observer = new DisposableObserver<ArrayList<ArticlesModel>>() {
            @Override
            public void onNext(ArrayList<ArticlesModel> articlesModels) {
                ArticlesRecyclerAdapter articlesRecyclerAdapter = new ArticlesRecyclerAdapter(MainActivity.this, articlesModels);
                recyclerView.setAdapter(articlesRecyclerAdapter);

                //ProgressBar bar = findViewById(R.id.progressBar);
                //bar.setVisibility(0);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() { }
        };
        Disposable disp = obs.subscribeWith(observer);

        // Нужно для прекращения работы при уничтожении
        _disposables.add(disp);
    }
}
