routine fib(n: integer) : integer is
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