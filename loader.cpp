#include <iostream>

extern "C" {
    int debug(int);
}

int main() {
    std::cout << "result is " << debug(10) << std::endl;
}
