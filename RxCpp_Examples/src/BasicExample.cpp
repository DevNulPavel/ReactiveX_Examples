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
    
    // Затем принимаем эти значения
    rxcpp::operators::flat_map([&](int i){
        // Создаем новые данные
        auto body =
        // Создаем поток значений от А до А+10
        rxcpp::sources::from((uint8_t)('A' + i)) |
        // Повторяем от 4х до 18ти раз
        rxcpp::operators::repeat(dist(gen)) |
        // TODO: ???
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
    
    // TODO: ???
    rxcpp::operators::window(17) |
    
    rxcpp::operators::flat_map([](rxcpp::observable<uint8_t> w){
        return w |
        rxcpp::operators::reduce(
               std::vector<uint8_t>(),
               [](std::vector<uint8_t> v, uint8_t b){
                   v.push_back(b);
                   return v;
               }) |
        rxcpp::operators::as_dynamic();
    }) |
    
    rxcpp::operators::tap([](std::vector<uint8_t>& v){
        // print input packet of bytes
        std::copy(v.begin(), v.end(), std::ostream_iterator<long>(std::cout, " "));
        std::cout << std::endl;
    });
    
    //
    // recover lines of text from byte stream
    //
    
    auto removespaces = [](std::string s){
        s.erase(remove_if(s.begin(), s.end(), ::isspace), s.end());
        return s;
    };
    
    // create strings split on \r
    auto strings = bytes |
    rxcpp::operators::concat_map([](std::vector<uint8_t> v){
        std::string s(v.begin(), v.end());
        std::regex delim(R"/(\r)/");
        std::cregex_token_iterator cursor(&s[0], &s[0] + s.size(), delim, {-1, 0});
        std::cregex_token_iterator end;
        std::vector<std::string> splits(cursor, end);
        return rxcpp::sources::iterate(move(splits));
    }) |
    rxcpp::operators::filter([](const std::string& s){
        return !s.empty();
    }) |
    rxcpp::operators::publish() |
    rxcpp::operators::ref_count();
    
    // filter to last string in each line
    auto closes = strings |
    rxcpp::operators::filter(
           [](const std::string& s){
               return s.back() == '\r';
           }) |
    rxcpp::operators::map([](const std::string&){return 0;});
    
    // group strings by line
    auto linewindows = strings |
    window_toggle(closes | rxcpp::operators::start_with(0), [=](int){return closes;});
    
    // reduce the strings for a line into one string
    auto lines = linewindows |
    rxcpp::operators::flat_map([&](rxcpp::observable<std::string> w) {
        return w | rxcpp::operators::start_with<std::string>("") | rxcpp::operators::sum() | rxcpp::operators::map(removespaces);
    });
    
    // print result
    lines |
    rxcpp::operators::subscribe<std::string>(rxcpp::util::println(std::cout));
}
