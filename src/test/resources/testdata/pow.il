// pow(1.0, 0): 1.0
// pow(1.0, 1): 1.0
// pow(1.0, 10): 1.0
// pow(2.0, 2): 4.0
// pow(10.0, 2): 100.0
// pow(3.14, 2): 9.8596
// pow(1.5, 4): 5.0625
// pow(4.0, -1): 0.25
// pow(4.0, -2): 0.0625
routine pow(a: real, n: integer) : real is
    if a = 0 then
        return 1.0
    else
        if n = 1 then
            return a
        else
            if n < 0 then
                return 1.0 / pow(a, -n)
            end
        end
    end

    return a * pow(a, n - 1)
end
