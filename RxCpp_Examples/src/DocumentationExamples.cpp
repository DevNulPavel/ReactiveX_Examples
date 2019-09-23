#include "DocumentationExamples.h"
#include <rxcpp/rx.hpp>


void documentationExamplesTest() {
    {
        printf("\n");
        
        // Функция, которая будет выдывать данные в поток
        auto intFlow = [](rxcpp::subscriber<int> s){
            // Выдаем значения
            s.on_next(1);
            s.on_next(2);
            s.on_next(3);
            s.on_next(4);
            // Завершаем передачу в поток, блокировка на subscribe завершается
            s.on_completed();
        };
        // Непосредственно поток данных
        auto ints = rxcpp::observable<>::create<int>(intFlow);
        
        // Коллбеки, которые будут вызываться на вызовы в данных потока выше
        auto onNext = [](int v){
            printf("OnNext received: %d\n", v);
        };
        auto onCompleted = [](){
            printf("OnCompleted called\n");
        };
        ints.subscribe(onNext, onCompleted);
        
        printf("Subscribe exit\n");
    }
    
    {
        printf("\n");
        
        auto values1 = rxcpp::observable<>::range(1, 5);
        
        // Коллбеки, которые будут вызываться на вызовы в данных потока выше
        auto onNext = [](int v){
            printf("OnNext received: %d\n", v);
        };
        auto onCompleted = [](){
            printf("OnCompleted called\n");
        };
        values1.subscribe(onNext, onCompleted);
        
        printf("Subscribe exit\n");
    }
    
    {
        printf("\n");
        
        std::array<int, 3 > a = {{1, 2, 3}};
        auto values1 = rxcpp::observable<>::iterate(a);
        
        // Коллбеки, которые будут вызываться на вызовы в данных потока выше
        auto onNext = [](int v){
            printf("OnNext received: %d\n", v);
        };
        auto onCompleted = [](){
            printf("OnCompleted called\n");
        };
        values1.subscribe(onNext, onCompleted);
        
        printf("Subscribe exit\n");
    }
    
    {
        printf("\n");
        
        // Создаем бесконечный поток int переменных до переполнения
        auto values = rxcpp::observable<>::range(1);
        
        // Принимаем три значения из потока интов
        auto s1 = values.take(3).map([](int prime) {
            return std::make_tuple("1:", prime);
        });
        
        // Принимаем три значения из потока интов
        auto s2 = values.take(3).map([](int prime) {
            return std::make_tuple("2:", prime);
        });
        
        // Сначала обрабатываем поток из s1, потом поток из s2, при этом выводим значения
        auto outputFunc = [](const char* s, int p) {
            printf("%s %d\n", s, p);
        };
        s1.concat(s2).subscribe(rxcpp::util::apply_to(outputFunc));
        
        printf("Subscribe exit\n");
    }
    
    {
        printf("\n");
        
        // Создаем бесконечный поток int переменных до переполнения
        auto values = rxcpp::observable<>::range(1);
        
        // Принимаем значения из потока интов
        auto s1 = values.map([](int prime) {
            return std::make_tuple("1:", prime);
        });
        
        // Принимаем значения из потока интов
        auto s2 = values.map([](int prime) {
            return std::make_tuple("2:", prime);
        });
        
        // Обрабатываем одновременно поток из s1 и из s2, обрабатываем 6 значений входящих, при этом выводим значения
        auto outFunc = [](const char* s, int p) {
            printf("%s %d\n", s, p);
        };
        s1.merge(s2).take(6).as_blocking().subscribe(rxcpp::util::apply_to(outFunc));
        
        printf("Subscribe exit\n");
    }
    
    {
        printf("\n");
        
        // Шедулер с пулом потоков
        auto threads = rxcpp::observe_on_event_loop();
        
        // Создаем бесконечный поток int переменных до переполнения
        auto values = rxcpp::observable<>::range(1);
        
        auto s1 = values.subscribe_on(threads).map([](int prime) {
            std::this_thread::yield();
            return std::make_tuple("1:", prime);
        });
        
        auto s2 = values.subscribe_on(threads).map([](int prime) {
            std::this_thread::yield();
            return std::make_tuple("2:", prime);
        });
        
        auto outFunc = [](const char* s, int p) {
            printf("%s %d\n", s, p);
        };
        s1.merge(s2).take(6).observe_on(threads).as_blocking().subscribe(rxcpp::util::apply_to(outFunc));
        
        printf("Subscribe exit\n");
    }
}