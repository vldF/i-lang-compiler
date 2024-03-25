// main(): true

routine not(b : bool) : bool is
    if b then return false else return true end
end

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
    arrayFullOfTrues[1] := true
    arrayFullOfTrues[2] := true
    arrayFullOfTrues[3] := true
    arrayFullOfTrues[4] := true
    arrayFullOfTrues[5] := true

    var arrayFullOfFalses : array [5] bool
    arrayFullOfFalses[1] := false
    arrayFullOfFalses[2] := false
    arrayFullOfFalses[3] := false
    arrayFullOfFalses[4] := false
    arrayFullOfFalses[5] := false

    var arrayWithOneTrue : array [5] bool
    arrayWithOneTrue[1] := false
    arrayWithOneTrue[2] := false
    arrayWithOneTrue[3] := true
    arrayWithOneTrue[4] := false
    arrayWithOneTrue[5] := false

    var arrayWithOneFalse : array [5] bool
    arrayWithOneFalse[1] := true
    arrayWithOneFalse[2] := true
    arrayWithOneFalse[3] := false
    arrayWithOneFalse[4] := true
    arrayWithOneFalse[5] := true

    var tmpArray : array[4] bool
    tmpArray[1] := all(arrayFullOfTrues)
    tmpArray[2] := not(any(arrayFullOfFalses))
    tmpArray[3] := any(arrayWithOneTrue)
    tmpArray[4] := not(all(arrayWithOneFalse))

    return all(tmpArray) // true
end
