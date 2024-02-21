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
