routine avg(arr : array[] integer) : real is
    var sum : integer is 0

    for i in 1..(arr.size) loop
        sum := sum + arr[i]
    end

    return sum * 1.0 / arr.size
end

routine main() : real is
    var arr : array [5] integer
    arr[1] := 1
    arr[2] := 2
    arr[3] := 3
    arr[4] := 4
    arr[5] := 5

    return avg(arr)
end
