// breakForTest(0): 0
// breakForTest(1): 2
// breakForTest(3): 6

// breakWhileTest(0): 0
// breakWhileTest(1): 2
// breakWhileTest(3): 6

routine breakForTest(i : integer) : integer is
  var arr : array[5] integer
  var res : integer is 0;

  arr[1] := i
  arr[2] := i
  arr[3] := i
  arr[4] := i
  arr[5] := i

  for idx in 1..5 loop
    if idx = 3 then
        break
    end

    res := res + arr[idx]
  end

  return res
end

routine breakWhileTest(i : integer) : integer is
  var arr : array[5] integer
  var res : integer is 0;

  arr[1] := i
  arr[2] := i
  arr[3] := i
  arr[4] := i
  arr[5] := i

  var iter : integer is 1;

  while iter <= 5 loop
    if iter = 3 then
      break
    end

    res := res + arr[iter]
    iter := iter + 1
  end

  return res
end
