type LinkedList is record
    var value : real
    var next : LinkedList
end

routine hasElement(list : LinkedList, value : real) : bool is
    if list.value = value then
        return true
    end

    if list.next /= uninitialized then
        return hasElement(list.next, value)
    end

    return false
end

routine main() : bool is
    var list : LinkedList
    list.value := 1.3

    var list1 : LinkedList
    node1.value := 2.5

    var list2 : LinkedList
        node1.value := 5.0

    var result : bool is false

    if hasElement(list, 5.0) and hasElement(list, 2.5) and hasElement(list, 1.3) then
        result := true
    end

    if hasElement(list, 53.0) then
        result := false
    end

    return result
end
