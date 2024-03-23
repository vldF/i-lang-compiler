// main_(1): 5
// main_(2): 10
// main_(3): 15

routine get_array(i : integer) : array[] integer is
  var arr : array[5] integer

  arr[1] := i
  arr[2] := i
  arr[3] := i
  arr[4] := i
  arr[5] := i

  return arr
end

routine main_(i : integer) : integer is
  var arr : array[] integer is get_array(i)

  return arr[1] + arr[2] + arr[3] + arr[4] + arr[5]
end
