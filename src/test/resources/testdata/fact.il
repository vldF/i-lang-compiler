routine fact(n: integer) : integer is
    if n >= 1 then
        return n * fact(n - 1)
    else
        return 1
    end
end