#include <iostream>
using namespace std;
int division(int a, int b, int i);
int main(){
    int a, b, i = 0;
    cin >> a >> b;
    cout << division(a, b, i);
    return 0;
}

int division(int a, int b, int i){
    if(a < b){
        return i;
    }else{
        return division(a - b, b, i+=1);
    }
}