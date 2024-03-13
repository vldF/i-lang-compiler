// debug, 10, 26

routine debug(n: integer): integer is
    var i is 1 + 2 + n

    if (i < 0) then
        if (i < -1) then
            i := 2
        end
    else
        i := i * 2
    end

    return i
end
