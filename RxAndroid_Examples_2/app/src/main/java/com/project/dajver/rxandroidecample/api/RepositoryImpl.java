package com.project.dajver.rxandroidecample.api;

import com.project.dajver.rxandroidecample.api.model.ArticlesModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * Created by gleb on 11/14/17.
 */

public class RepositoryImpl implements IRepository {

    @Override
    public Observable<ArrayList<ArticlesModel>> getArticles(String urlLink) {
        Observable<ArrayList<ArticlesModel>> obs = Observable.create(observableEmitter -> {
            ArrayList<ArticlesModel> articlesModels = new ArrayList<>();
            Document doc;
            try {
                // Получаем верстку страницы
                doc = Jsoup.connect(urlLink).get();

                // Получаем элементы
                Elements titleElement = doc.getElementsByClass("post__title_link");
                Elements textElement = doc.getElementsByClass("post__text post__text-html js-mediator-article");

                for(int i = 0; i < titleElement.size(); i++) {
                    // Получаем ссылку на картинку
                    Elements imgsrc = textElement.get(i).select("img");
                    String url = imgsrc.attr("src");

                    // Получаем текст заголовка
                    Elements ahref = titleElement.get(i).select("a");
                    String titleText = ahref.text();

                    // Создаем переменную, которая содержит наши данные
                    ArticlesModel model = new ArticlesModel();
                    model.setName(titleText);
                    model.setImageUrl(url);

                    // Сохраняем в общий массив
                    articlesModels.add(model);
                }

                // Возвращаем элементы
                observableEmitter.onNext(articlesModels);
            } catch (IOException e) {
                observableEmitter.onError(e);
            } finally {
                observableEmitter.onComplete();
            }
        });
        return obs;
    }
}
