// forForwardTest(0): 0
// forForwardTest(1): 5
// forForwardTest(2): 10

// forReverseTest(0): 0
// forReverseTest(1): 5
// forReverseTest(2): 10

routine forForwardTest(i : integer) : integer is
  var arr : array[5] integer
  var res : integer is 0;

  arr[1] := i
  arr[2] := i
  arr[3] := i
  arr[4] := i
  arr[5] := i

  for idx in 1..5 loop
    res := res + arr[idx]
  end

  return res
end

routine forReverseTest(i : integer) : integer is
  var arr : array[5] integer
  var res : integer is 0;

  arr[1] := i
  arr[2] := i
  arr[3] := i
  arr[4] := i
  arr[5] := i

  for idx reversed in 1..5 loop
    res := res + arr[idx]
  end

  return res
end
