//go:build !solution

package treeiter

type MyNode[T any] interface {
	Left() *T
	Right() *T
}

func DoInOrder[T MyNode[T]](tree *T, f func(*T)) {
	if tree == nil {
		return
	}
	tp := *tree
	left := tp.Left()
	if left != nil {
		DoInOrder(left, f)
	}

	f(tree)
	right := tp.Right()
	if right != nil {
		DoInOrder(right, f)
	}
}
