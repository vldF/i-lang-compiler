routine any(arr : array [] bool) : bool is
    for i in 1..(arr.size) loop
        if arr[i] = true then
            return true
        end
    end

    return false
end

routine all(arr : array [] bool) : bool is
    for i in 1..(arr.size) loop
        if arr[i] = false then
            return false
        end
    end

    return true
end

routine main() : bool is
    var arrayFullOfTrues : array [5] bool
    arr1[1] := true
    arr1[2] := true
    arr1[3] := true
    arr1[4] := true
    arr1[5] := true

    var arrayFullOfFalses : array [5] bool
    arr1[1] := false
    arr1[2] := false
    arr1[3] := false
    arr1[4] := false
    arr1[5] := false

    var arrayWithOneTrue : array [5] bool
    arr1[1] := false
    arr1[2] := false
    arr1[3] := true
    arr1[4] := false
    arr1[5] := false

    var arrayWithOneFalse : array [5] bool
    arr1[1] := true
    arr1[2] := true
    arr1[3] := false
    arr1[4] := true
    arr1[5] := true

    var tmpArray : array[4] bool
    tmpArray[1] := all(arrayFullOfTrues)
    tmpArray[2] := not any(arrayFullOfFalses)
    tmpArray[3] := any(arrayWithOneTrue)
    tmpArray[4] := not all(arrayWithOneFalse)

    return all(tmpArray) // true
end
