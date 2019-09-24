#include "BasicExample.h"
#include <regex>
#include <random>
#include <rxcpp/rx.hpp>

/*using namespace std;
using namespace std::chrono;
 
// Помещаем все нужное из rxcpp в пространство имен Rx
namespace Rx {
    using namespace rxcpp;
    using namespace rxcpp::sources;
    using namespace rxcpp::operators;
    using namespace rxcpp::util;
}
using namespace Rx;*/


void basicExampleTest() {
    // Создаем рандомный генератор
    std::random_device rd;
    std::mt19937 gen(rd());
    std::uniform_int_distribution<> dist(4, 18);
    
    // Для тестовых целей будем создавать поток байтов
    auto bytes =
    
    // Начинаем поток данных с диапазона значений от 0 до 10
    rxcpp::sources::range(0, 10) |
    
    // Затем принимаем эти значения и создаем новый subscriber из значений
    rxcpp::operators::flat_map([&](int i){
        // Создаем новые данные
        auto body =
        // Создаем поток значений от А до А+10
        rxcpp::sources::from((uint8_t)('A' + i)) |
        // Повторяем от 4х до 18ти раз
        rxcpp::operators::repeat(dist(gen)) |
        // Возвращаем новый observable который выполняет "забывание типа" данного observable
        rxcpp::operators::as_dynamic();
        
        // Создаем новый поток-разделитель
        auto delim = rxcpp::sources::from((uint8_t)'\r');
        
        // Создаем поток данных с результатом
        auto resultStream =
        // Объединяем поток символов и поток разделителей
        rxcpp::sources::from(body, delim) |
        // Соединяем эти значения
        rxcpp::operators::concat();
        
        return resultStream;
    }) |
    
    // Return an observable that emits connected, non-overlapping windows, each containing at most count items from the source observable. If the skip parameter is set, return an observable that emits windows every skip items containing at most count items from the source observable.
    // Возвращает observable который выбрасывает соединенные, не перекрывающиеся окна. Каждый содержащий наибольшее количество итемов из исходного observable.
    // Если параметр skip установлен, возвращается observable, который выбрасывает окна, каждый раз пропуская итемы, содержащие наиболее количество итемов из исходного observable
    rxcpp::operators::window(17) |
    
    // Создаем новый observable на основании входных данных
    rxcpp::operators::flat_map([](rxcpp::observable<uint8_t> w){
        // Скукоживаем входящий поток байтов в вектор с этими байтами
        std::vector<uint8_t> resultVector;
        return w |
        rxcpp::operators::reduce(resultVector, [](std::vector<uint8_t> v, uint8_t b){
           v.push_back(b);
           return v;
        }) |
        rxcpp::operators::as_dynamic();
    }) |
    
    // Промежуточный обработчик значений с методами onNext, onCompleted
    rxcpp::operators::tap([](const std::vector<uint8_t>& v){
        // Для отладки выводим значения
        std::copy(v.begin(), v.end(), std::ostream_iterator<long>(std::cout, " "));
        std::cout << std::endl;
    });
    
    // Функция удаления пробелов из строки и возвращения новой
    auto removespaces = [](std::string s){
        s.erase(remove_if(s.begin(), s.end(), ::isspace), s.end());
        return s;
    };
    
    // Новый поток создания строк из байтов
    auto strings = bytes |
    
    // For each item from this observable use the CollectionSelector to produce an observable and subscribe to that observable. For each item from all of the produced observables use the ResultSelector to produce a value to emit from the new observable that is returned.
    // Для каждого итема из этого observable используя CollectionSelector, чтобы создавать новый observable и подписаться на этот observable.
    // Для каждого итема из
    // TODO: ???
    rxcpp::operators::concat_map([](std::vector<uint8_t> v){
        // Создаем строку из набора байт
        std::string s(v.begin(), v.end());
        // Создаем регулярку для разделения
        std::regex delim(R"/(\r)/");
        // Выполняем разделение строки для получения вектора строк
        std::cregex_token_iterator cursor(&s[0], &s[0] + s.size(), delim, {-1, 0});
        std::cregex_token_iterator end;
        std::vector<std::string> splits(cursor, end);
        // Создаем новый Observable для итерирования
        return rxcpp::sources::iterate(move(splits));
    }) |
    // Фильтруем пустые строки
    rxcpp::operators::filter([](const std::string& s){
        return !s.empty();
    }) |
    // TODO: ???
    rxcpp::operators::publish() |
    // TODO: ???
    rxcpp::operators::ref_count();
    
    // Фильтруем последнее слово каждой линии
    auto closes = strings |
    // Обрабатывать будем только строки которые являются '\r' (концом строки)
    rxcpp::operators::filter([](const std::string& s){
       return s.back() == '\r';
    }) |
    // Заменять будем такие строки на 0
    rxcpp::operators::map([](const std::string&){
        return 0;
    });
    
    // Группируем строки по линиям
    auto linewindows = strings |
    // TODO: ???
    rxcpp::operators::window_toggle(closes | rxcpp::operators::start_with(0), [=](int){
        return closes;
    });
    
    // Скукоживаем отдельные строки в одну строку
    auto lines = linewindows |
    rxcpp::operators::flat_map([&](rxcpp::observable<std::string> w) {
        return w |
        rxcpp::operators::start_with<std::string>("") |
        rxcpp::operators::sum() |
        rxcpp::operators::map(removespaces);
    });
    
    // print result
    lines |
    rxcpp::operators::subscribe<std::string>(rxcpp::util::println(std::cout));
}
