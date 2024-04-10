type TreeNode is record
    var value : real
    var key : integer
    var children : array [2] TreeNode
end

type MayBeNode is record
    var hasValue : bool
    var node : TreeNode
end

routine findNodeByKey(root : TreeNode, key : integer) : MayBeNode is
    if root.key = key then
        var res : MayBeNode
        res.hasValue := true
        res.node := root

        return res
    end

    if root.children[1] /= uninitialized then
        var leftResult is findNodeByKey(root.children[1], key)

        if leftResult.hasValue then
            return leftResult
        end
    end

    if root.children[2] /= uninitialized then
        return findNodeByKey(root.children[2], key)
    end

    var nothingNode : MayBeNode
    nothingNode.hasValue := false

    return nothingNode
end

routine main() : real is
    var root : TreeNode
    root.key := 0
    root.value := 0.0

    var node1 : TreeNode
    node1.key := 1
    node1.value := 1.1

    var node2 : TreeNode
    node2.key := 2
    node2.value := 2.2

    var node3 : TreeNode
    node3.key := 3
    node3.value := 3.3

    var node4 : TreeNode
    node4.key := 4
    node4.value := 4.4

    var node5 : TreeNode
    node5.key := 5
    node5.value := 5.5

    root.children[1] := node1
    root.children[2] := node2

    node1.children[1] := node3
    node1.children[2] := node4

    node3.children[1] := node5

    var result1 is findNodeByKey(root, 5) // node5
    var result2 is findNodeByKey(root, 4) // node4
    var result3 is findNodeByKey(root, 3) // node3
    var result4 is findNodeByKey(root, 2) // node2
    var result5 is findNodeByKey(root, 1) // node1
    var result6 is findNodeByKey(root, 0) // root

    return result1.node.value + result2.node.value + result3.node.value + result4.node.value + result5.node.value + result6.node.value
end
