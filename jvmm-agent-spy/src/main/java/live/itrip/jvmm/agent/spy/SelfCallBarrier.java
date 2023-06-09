package live.itrip.jvmm.agent.spy;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author fengjianfeng
 * @date 2022/1/4
 * 功能描述: 本地线程
 */
public class SelfCallBarrier {

    public static class Node {
        private final Thread thread;
        private final ReentrantLock lock;
        private Node pre;
        private Node next;

//            Node() {
//                this(null);
//            }

        Node(final Thread thread) {
            this(thread, null);
        }

        Node(final Thread thread, final ReentrantLock lock) {
            this.thread = thread;
            this.lock = lock;
        }

    }

    /**
     * 删除节点
     * @param node node
     */
    void delete(final Node node) {
        node.pre.next = node.next;
        if (null != node.next) {
            node.next.pre = node.pre;
        }
        // help gc
        node.pre = (node.next = null);
    }

    /**
     * 插入节点
     * @param top top
     * @param node node
     */
    void insert(final Node top, final Node node) {
        if (null != top.next) {
            top.next.pre = node;
        }
        node.next = top.next;
        node.pre = top;
        top.next = node;
    }

    static final int THREAD_LOCAL_ARRAY_LENGTH = 512;

    final Node[] nodeArray = new Node[THREAD_LOCAL_ARRAY_LENGTH];

    SelfCallBarrier() {
        cleanAndInit();
    }

    Node createTopNode() {
        return new Node(null, new ReentrantLock());
    }

    void cleanAndInit() {
        for (int i = 0; i < THREAD_LOCAL_ARRAY_LENGTH; i++) {
            nodeArray[i] = createTopNode();
        }
    }

    int abs(int val) {
        return val < 0
                ? val * -1
                : val;
    }

    boolean isEnter(Thread thread) {
        final Node top = nodeArray[abs(thread.hashCode()) % THREAD_LOCAL_ARRAY_LENGTH];
        Node node = top;
        try {
            // spin for lock
            while (!top.lock.tryLock()) ;
            while (null != node.next) {
                node = node.next;
                if (thread == node.thread) {
                    return true;
                }
            }
            return false;
        } finally {
            top.lock.unlock();
        }
    }

    Node enter(Thread thread) {
        final Node top = nodeArray[abs(thread.hashCode()) % THREAD_LOCAL_ARRAY_LENGTH];
        final Node node = new Node(thread);
        try {
            while (!top.lock.tryLock()) ;
            insert(top, node);
        } finally {
            top.lock.unlock();
        }
        return node;
    }

    void exit(Thread thread, Node node) {
        final Node top = nodeArray[abs(thread.hashCode()) % THREAD_LOCAL_ARRAY_LENGTH];
        try {
            while (!top.lock.tryLock()) ;
            delete(node);
        } finally {
            top.lock.unlock();
        }
    }

}
