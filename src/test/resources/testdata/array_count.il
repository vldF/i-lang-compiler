// main(): 3

routine count(elem : integer, arr : array[] integer) : integer is
    var res is 0

    for i in 1..(arr.size) loop
        if arr[i] = elem then
            res := res + 1
        end
    end

    return res
end

routine main() : integer is
    var arr : array [5] integer
    arr[1] := 1
    arr[2] := 2
    arr[3] := 1
    arr[4] := 3
    arr[5] := 1

    return count(1, arr) // 3
end
