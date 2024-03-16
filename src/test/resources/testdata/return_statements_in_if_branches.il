// foo(0): true
// foo(1): false
// foo(2): true
routine foo(a: integer) : bool is
    if a = 0 then
        return true
    else
        if a = 1 then
            return false
        else
            return true
        end
    end
end
