// fact(1): 1
// fact(2): 2
// fact(3): 6
// fact(4): 24

routine fact(n: integer) : integer is
    if n >= 1 then
        return n * fact(n - 1)
    else
        return 1
    end
end
