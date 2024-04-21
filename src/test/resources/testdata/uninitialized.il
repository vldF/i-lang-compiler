// main(): 0

type TreeNode is record
    var value : real
    var key : integer
    var child : TreeNode
    var children : array [2] TreeNode
end

routine main(): integer is
  var node1: TreeNode

  if (node1.value /= 0.0) then
    return 1
  end

  if (node1.key /= 0) then
    return 1
  end

  if (node1.child /= uninitialized) then
    return 1
  end

  node1.child := node1

  if (node1.child = uninitialized) then
    return 1
  end

//  if (node1.children /= uninitialized) then
//    return 1
//  end

  return 0
end
