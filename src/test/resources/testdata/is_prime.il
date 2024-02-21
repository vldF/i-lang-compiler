routine isPrime(n: integer) : integer is
    if n % 2 = 0 then
        return n = 2
    end

    var d : integer is 3
    while d * d <= n and n % d /= 0 loop
        d := d + 2
    end
    return d * d > n
end
