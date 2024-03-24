// gcd(1, 1): 1
// gcd(2, 4): 2
// gcd(17, 11): 1
// gcd(120, 200): 40

routine gcd(n1 : integer, n2 : integer) : integer is
    var n_1 is n1
    var n_2 is n2

    while n_1 /= n_2 loop
        if n_1 > n_2 then
            n_1 := n_1 - n_2
        else
            n_2 := n_2 - n_1
        end
    end

    return n_1
end
