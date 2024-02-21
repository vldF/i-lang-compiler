routine quickSort(arr : array[] integer, low : integer, high : integer) is
    if (arr.size = 0 or low >= high) then
        return
    end

    var middle : integer is low + (high - low) / 2
    var midElement : integer is arr[middle]
    var temp : integer

    var i : integer is low
    var j : integer is high

    while i <= j loop
        while arr[i] < midElement loop
            i := i + 1
        end

        while arr[i] > midElement loop
            j := j - 1
        end

        if  i <= j then
            temp := arr[i]
            arr[i] := arr[j]
            arr[j] := temp
            i := i + 1
            j := j - 1
        end
    end

    if low < j then
        quickSort(arr, low, j)
    end

    if high > i then
        quickSort(arr, i, high)
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
    quickSort(arr)

    return isSorted(arr)
end
