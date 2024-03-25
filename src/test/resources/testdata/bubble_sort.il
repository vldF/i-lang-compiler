// main(): true

routine bubbleSort(arr : array[] integer) is
    var current is arr[1]
    var temp : integer
    var i : integer is 1

    while i < (arr.size + 1) loop
        var j is 2
        while j <= (arr.size - i + 1) loop
            if arr[j-1] > arr[j] then
                temp := arr[j-1]
                arr[j-1] := arr[j]
                arr[j] := temp
            end
            j := j + 1
        end
        i := i + 1
    end
end

routine isSorted(arr : array[] integer) : bool is
    if arr.size < 2 then
        return true
    end

    for i in 2..(arr.size) loop
        if arr[i - 1] > arr[i] then
            return false
        end
    end

    return true
end

routine main() : bool is
    var arr : array [5] integer
    arr[1] := 2
    arr[2] := 5
    arr[3] := 3
    arr[4] := 0
    arr[5] := 1
    bubbleSort(arr)

    return isSorted(arr)
end
