// logic(false, false, false): false
// logic(false, false, true): true
// logic(false, true, false): false
// logic(false, true, true): true
// logic(true, false, false): true
// logic(true, false, true): false
// logic(true, true, false): false
// logic(true, true, true): false

routine logic(a : boolean, b : boolean, c : boolean) : boolean is
  var temp : boolean is a and b;
  return (temp or c) xor a;
end
