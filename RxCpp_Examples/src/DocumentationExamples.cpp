#include "DocumentationExamples.h"
#include <rxcpp/rx.hpp>


void documentationExamplesTest() {
    /*{
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
        
        // Принимаем три значения из потока интов, и преобразуем с помощью функции map в новый тип данных
        auto s1 = values.take(3).map([](int prime) {
            return std::make_tuple("1:", prime);
        });
        
        // Принимаем три значения из потока интов, и преобразуем с помощью функции map в новый тип данных
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
        
        // Принимаем значения из потока интов, и преобразуем с помощью функции map в новый тип данных
        auto s1 = values.map([](int prime) {
            return std::make_tuple("1:", prime);
        });
        
        // Принимаем значения из потока интов, и преобразуем с помощью функции map в новый тип данных
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
        
        // Принимаем значения из потока интов, и преобразуем с помощью функции map в новый тип данных
        // Все это происходит в пуле потоков
        auto s1 = values.subscribe_on(threads).map([](int prime) {
            std::this_thread::yield();
            return std::make_tuple("1:", prime);
        });
        
        // Принимаем значения из потока интов, и преобразуем с помощью функции map в новый тип данных
        // Все это происходит в пуле потоков
        auto s2 = values.subscribe_on(threads).map([](int prime) {
            std::this_thread::yield();
            return std::make_tuple("2:", prime);
        });
        
        // Будем сливать вывод приложения, принимать будем только 6 значений, получать будем тоже в потоке в блокирующем последовательном режиме
        auto outFunc = [](const char* s, int p) {
            printf("%s %d\n", s, p);
        };
        s1.merge(s2).take(6).observe_on(threads).as_blocking().subscribe(rxcpp::util::apply_to(outFunc));
        
        printf("Subscribe exit\n");
    }
    
    {
        printf("\n");
        
        // Возвращает список url'ов, основываясь на поиске по содержимому веб-страницы
        auto queryFunc = [](){
            auto urls = rxcpp::observable<>::from<std::string>("url1", "url2", "url3", "url4", "url5");
            return urls;
        };
        
        // Создаем новый генератор (observable) из имеющихся данных
        queryFunc().flat_map([](const std::string& url){
            return rxcpp::observable<>::from(url, "suburl");
        }).
        // Фильтруем только конкретные значения
        filter([](const std::string& url){
            if (url == "suburl") {
                return false;
            }
            return true;
        }).
        // Принимать будем только 4 значения
        take(4).
        // Подписываемся на вывод
        subscribe([](const std::string& url){
            printf("Url: %s\n", url.c_str());
        });
        
        printf("Subscribe exit\n");
    }*/
    
    {
        printf("\n");
        std::cout << "Thread " << std::this_thread::get_id() << ": this is main thread" << std::endl;
        
        // Создаем новый генератор (observable) из имеющихся данных
        rxcpp::observable<>::from<int32_t>(1, 2, 2, 6, 3, 4, 4, 5).
        // Конвертируем данные в тип string
        map([](int32_t number){
            std::string numberStr = std::string("Number: ") + std::to_string(number);
            return numberStr;
        }).
        // Убирает дублирующиеся последовательные значения
        distinct().
        // Весь код ниже будет выполняться на новом потоке
        // TODO: ??? Убирает дублирующиеся последовательные значения
        distinct_until_changed().
        // Специальный экшен, который вызывается на окончание передачи из цепочки выше, некий барьер выполнения?
        // TODO: ???
        finally([](){
            //std::cout << "Thread " << std::this_thread::get_id() << ": " << "The final action for numbers" << std::endl;
        }).
        // Выдавать будем накопленные за заданное время или количество значения
        //buffer_with_time_or_count(std::chrono::milliseconds(10), 2, rxcpp::observe_on_event_loop()). // TODO: Не работает вывод значений ниже
        // Выдавать будем накопленные за заданное время значения
        // buffer_with_time(std::chrono::milliseconds(10), rxcpp::identity_current_thread()). // TODO: Не работает вывод значений ниже
        // Накапливаем значения по 2 штуки, передавать будем вектор из 2х элементов
        buffer(2).
        // Из вектора с 2мя значениями создаем новый источник данных, который разворачивает значения снова в последовательные
        flat_map([](const std::vector<std::string>& bufferedItems){
            std::cout << "Thread " << std::this_thread::get_id() << ": " << "Total buffered items count for 10 mSec is " << static_cast<int64_t>(bufferedItems.size()) << std::endl;
            
            // Итерируемся по элементам коллекции
            return rxcpp::observable<>::iterate(bufferedItems);
            
            // Создаем 2 новых потока из значений и объединяем их
            /*auto iterate1 = rxcpp::observable<>::iterate(bufferedItems);
            auto iterate2 = rxcpp::observable<>::iterate(bufferedItems);
            return iterate1.concat(iterate2);*/
            
            // Скукоживаем по 2 значения, следующий будет принимать std::tuple<std::string, std::string> v
            /*auto iterate1 = rxcpp::observable<>::iterate(bufferedItems);
            auto iterate2 = rxcpp::observable<>::iterate(bufferedItems);
            return iterate1.combine_latest(iterate2);*/
        }).
        // Специальный экшен, который вызывается на окончание передачи из цепочки выше, некий барьер выполнения?
        // TODO: ???
        finally([](){
            //std::cout << "Thread " << std::this_thread::get_id() << ": " << "The final action for flat_map handler" << std::endl;
        }).
        // Выдает элементы с определенной периодичностью
        // TODO: ???
        //delay(std::chrono::milliseconds(1000), rxcpp::observe_on_new_thread()).
        // Выдает новый элемент, с определенной периодичностью, отбрасывая слишком частые
        // TODO: ???
        //debounce(std::chrono::milliseconds(1)).
        tap([](const std::string& text){
            std::cout << "Thread " << std::this_thread::get_id() << ": " << text << std::endl;
        }).
        // Проверяем, что все принятые строки не являются пустыми, возвращает true / false
        all([](const std::string& str){
            if (str.empty() == false) {
                return true;
            }
            return false;
        }).
        // Конвертируем в строку
        map([](bool allIsNotEmpty){
            if (allIsNotEmpty) {
                return "All is not empty";
            }
            return "Someone is empty";
        }).
        tap([](const std::string& text){
            std::cout << "Thread " << std::this_thread::get_id() << ": " << text << std::endl;
        }).
        // Возвращает true если исходный источник данных пустой
        is_empty().
        // Конвертируем в строку
        map([](bool allIsEmpty){
            if (allIsEmpty) {
                return "All is empty";
            }
            return "Someone is not empty";
        }).
        // 4 раза повторяет вывод из предыдущего источника (Исходного источника?)
        //repeat(4).
        // Весь код выше будет выполняться на новом потоке для исходного источника
        subscribe_on(rxcpp::synchronize_new_thread()).
        // Весь код ниже будет выполняться на новом потоке
        observe_on(rxcpp::synchronize_new_thread()).
        // Возвращает новый источник, который содержит блокирующие методы для этого источника
        as_blocking().
        // Подписываемся на вывод
        subscribe([](const std::string& text){
            std::cout << "Thread " << std::this_thread::get_id() << ": " << text << std::endl;
        });
        
        printf("Subscribe exit\n");
    }
}
