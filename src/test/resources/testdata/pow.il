// pow(1.0, 0.0): 1.0
// pow(1.0, 1.0): 1.0
// pow(1.0, 10.0): 1.0
// pow(2.0, 2.0): 4.0
// pow(10.0, 2.0): 100.0
// pow(3.14, 2.0): 9.8596
// pow(1.5, 4.0): 5.0625
routine pow(a: real, n: real) : real is
    if a = 0 then
        return 1.0
    else
        if n = 1 then
            return a
        else
            if n < 0 then
                return 1 / pow(a, -n)
            end
        end
    end

    return a * pow(a, n - 1)
end
