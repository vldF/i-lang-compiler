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

routine main() : AdderResult is
    return fullAdder(1, 0, 1)
end