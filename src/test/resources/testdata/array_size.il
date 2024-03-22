//array_size_test() : 113

routine array_size_test() : integer is
  var arr1 : array[1] integer
  var arr2 : array[2] integer
  var arr10 : array[10] integer
  var arr100 : array[100] integer

  return arr1.size + arr2.size + arr10.size + arr100.size
end
