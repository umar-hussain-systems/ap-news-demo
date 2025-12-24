package com.systems.demo.apnewsdemo.datastructures.nodebased;

import lombok.Getter;

@Getter
public class Queue<T> {

    private QueueNode<T> rootNode = null;

    private QueueNode<T> tailNode = null;

    private int size;

    public void insertLast(T t) {
        if (rootNode == null) {
            QueueNode<T> node = new QueueNode<>(new QueueNode.Data<>(t), null, null);
            rootNode = node;
            tailNode = node;
        } else {
            QueueNode<T> node = new QueueNode<>(new QueueNode.Data<>(t), tailNode, null);
            tailNode.next = node;
            tailNode = node;
        }
        size++;
    }

    public void insertFirst(T t) {
        if (rootNode == null) {
            QueueNode<T> node = new QueueNode<>(new QueueNode.Data<>(t), null, null);
            rootNode = node;
            tailNode = node;
        } else {
            QueueNode<T> node = new QueueNode<>(new QueueNode.Data<>(t), null, rootNode);
            rootNode.previous = node;
            rootNode = node;
        }
        size++;
    }

    public T removeLast() {
        if (tailNode == null) {
            return null;
        }

        T value = tailNode.getData().getValue();

        if (tailNode == rootNode) {
            // only one element
            rootNode = null;
            tailNode = null;
        } else {
            tailNode = tailNode.previous;
            if (tailNode != null) {
                tailNode.next = null;
            }
        }
        size--;
        return value;
    }

    public T removeFirst() {
        if (rootNode == null) {
            return null;
        }

        T value = rootNode.getData().getValue();

        if (rootNode == tailNode) {
            // only one element
            rootNode = null;
            tailNode = null;
        } else {
            rootNode = rootNode.next;
            if (rootNode != null) {
                rootNode.previous = null;
            }
        }
        size--;
        return value;
    }

    public T removeNode(QueueNode<T> node) {
        if (node == null) return null;

        if (node == rootNode) {
            return removeFirst();
        }
        if (node == tailNode) {
            return removeLast();
        }

        // node is in middle
        QueueNode<T> prev = node.previous;
        QueueNode<T> next = node.next;
        if (prev != null) prev.next = next;
        if (next != null) next.previous = prev;
        size--;
        return node.getData().getValue();
    }

    // Move an existing node to the front (root) without creating a new node.
    public void moveToFront(QueueNode<T> node) {
        if (node == null || node == rootNode) return;

        // Detach node
        if (node == tailNode) {
            tailNode = node.previous;
            if (tailNode != null) tailNode.next = null;
        } else {
            if (node.previous != null) node.previous.next = node.next;
            if (node.next != null) node.next.previous = node.previous;
        }

        // Insert at front
        node.previous = null;
        node.next = rootNode;
        if (rootNode != null) rootNode.previous = node;
        rootNode = node;

        // If list was empty before (shouldn't happen) ensure tailNode set
        if (tailNode == null) tailNode = node;
    }

    public void emptyQueue() {
        rootNode = null;
        tailNode = null;
        size = 0;
    }

}
