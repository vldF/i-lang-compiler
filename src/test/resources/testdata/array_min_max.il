routine min(arr : array[] integer) : integer is
    var current is arr[1]

    for i in 2..(arr.size) loop
        if arr[i] < current then
            current := arr[i]
        end
    end
end

routine max(arr : array[] integer) : integer is
    var current is arr[1]

    for i in 2..(arr.size) loop
        if arr[i] > current then
            current := arr[i]
        end
    end
end

routine main() : integer is
    var arr : array [5] integer
    arr[1] := 1
    arr[2] := 2
    arr[3] := 3
    arr[4] := 4
    arr[5] := 5

    return max(arr) + min(arr)
end
