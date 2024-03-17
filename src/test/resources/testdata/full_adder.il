// main(false, false, false): 00
// main(false, false, true): 01
// main(false, true, false): 01
// main(false, true, true): 10
// main(true, false, false): 01
// main(true, false, true): 10
// main(true, true, false): 10
// main(true, true, true): 11

type AdderResult is record
    var sum : boolean
    var c_out : boolean
end

routine fullAdder(a : boolean, b : boolean, c_in: boolean) : AdderResult is
    var sum is c_in xor a xor b
    var c_out is (a and b) or (b and c_in) or (a and c_in)

    var result: AdderResult
    result.sum := sum
    result.c_out := c_out

    return result
end

routine main(a : boolean, b : boolean, c_in: boolean) : integer is
    var result : AdderResult is fullAdder(a, b, c_in)
    var resultAsInt : integer;

    if (result.sum) then
      resultAsInt := resultAsInt + 1
    end

    if (result.c_out) then
      resultAsInt := resultAsInt + 10
    end

    return resultAsInt;
end
