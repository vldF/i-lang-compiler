// whileLoop(0): 0
// whileLoop(1): 5
// whileLoop(2): 10

routine whileLoop(i : integer) : integer is
  var arr : array[5] integer
  var res : integer is 0;

  arr[1] := i
  arr[2] := i
  arr[3] := i
  arr[4] := i
  arr[5] := i

  var iter : integer is 1;

  while iter <= 5 loop
    res := res + arr[iter]
    iter := iter + 1
  end

  return res
end
