// isPrime(2): true
// isPrime(3): true
// isPrime(4): false
// isPrime(5): true
// isPrime(6): false
// isPrime(7): true
// isPrime(8): false
// isPrime(9): false
// isPrime(10): false
// isPrime(11): true

routine isPrime(n: integer) : bool is
    if n = 1 then
        return false
    end
    if n % 2 = 0 then
        return n = 2
    end

    var d : integer is 3
    while d * d <= n and n % d /= 0 loop
        d := d + 2
    end
    return d * d > n
end
