routine firstIndex(elem : integer, arr : array[] integer) : integer is
    for i in 1..(arr.size) loop
        if arr[i] = elem then
            return i
        end
    end

    return -1
end

routine lastIndex(elem : integer, arr : array[] integer) : integer is
    for i in reverse 1..(arr.size) loop
        if arr[i] = elem then
            return i
        end
    end

    return -1
end

routine main() : integer is
    var arr : array [5] integer
    arr[1] := 1
    arr[2] := 2
    arr[3] := 1
    arr[4] := 3
    arr[5] := 1

    return firstIndex(arr, 2) + lastIndex(arr, 3)
end
