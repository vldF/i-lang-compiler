// arrays(123, 1): 123
// arrays(234, 2): 234
// arrays(3, 3): 3
// arrays(4, 4): 4
// arrays(5, 5): 5
// arrays(6, 6): 6
// arrays(7, 7): 7
// arrays(8, 8): 8
// arrays(9, 9): 9
// arrays(10, 10): 10

routine arrays(i: integer, n: integer): integer is
    var arr : array [10] integer
    arr[n] := i
    return arr[n]
end
