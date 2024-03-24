// fib(1): 1
// fib(2): 1
// fib(3): 2
// fib(4): 3
// fib(5): 5
// fib(6): 8
// fib(7): 13

routine fib(n: integer) : integer is
    if n = 1 then
        return 1
    end

    var num1 : integer
    num1 := 0

    var num2 is 1
    var nextNum : integer is num1

    for i in 1..(n - 1) loop
        nextNum := num1 + num2
        num1 := num2; num2 := nextNum
    end

    return nextNum
end
