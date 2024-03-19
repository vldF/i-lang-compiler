// testRecord(0): 0
// testRecord(1): 2
// testRecord(3): 6
// testRecord(-1): -2

// testArray(1): 5
// testArray(2): 10
// testArray(-1): -5
// testArray(-10): -50

type T is record
  var field : integer
end

routine setField(t : T) is
  t.field := t.field * 2
  return
end

routine testRecord(i : integer) : integer is
  var t : T
  t.field := i

  setField(t)

  return t.field
end


routine fill5(arr : array [] integer, n : integer) is
  arr[1] := n
  arr[2] := n
  arr[3] := n
  arr[4] := n
  arr[5] := n

  var tmp : array[5] integer
  arr := tmp
end

routine testArray(i : integer) : integer is
  var arr : array [5] integer

  fill5(arr, i)

  return arr[1] + arr[2] + arr[3] + arr[4] + arr[5]
end
