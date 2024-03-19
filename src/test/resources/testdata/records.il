// main(0, 0.0, false): 1.0
// main(1, 0.0, false): 4.0
// main(0, 1.0, false): 4.0
// main(0, 0.0, true): 1.0
// main(1, 1.0, true): 7.0

type T2 is record
  var innerIntField : integer
  var innerRealField : real
  var innerBoolField : bool
end

type T1 is record
  var intField : integer
  var realField : real
  var boolField : bool
  var userTypeField : T2
end

routine main(i : integer, r : real, b: bool) : real is
  var t1 : T1;
  t1.intField := i
  t1.realField := r
  t1.boolField := b

  var t2 : T2;
  t2.innerIntField := i * 2
  t2.innerRealField := r * 2
  t2.innerBoolField := b xor true

  t1.userTypeField := t2

  var v1 : real
  var v2 : real

  if (t1.boolField) then
    v1 := 1.0
  end

  if (t2.innerBoolField) then
    v2 := 1.0
  end

  return t1.intField + t1.realField + v1 + v2 + t1.userTypeField.innerIntField + t1.userTypeField.innerRealField
end
