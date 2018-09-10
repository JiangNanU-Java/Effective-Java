package com.ten.common;

/**
 * Object 源码阅读
 */
public class TObject extends Object {

    /**
     * 其主要作用是将C/C++中的方法映射到Java中的native方法，实现方法命名的解耦。
     * 函数的执行是在静态代码块中执行的，在类首次进行加载的时候执行。
     */
    private static native void registerNatives();

    static {
        registerNatives();
    }

    /**
     * 返回一个对象的运行时类。该 Class 对象是由所表示类的 static synchronized 方法锁定的对象。
     * 返回：
     * 表示该对象的运行时类的 java.lang.Class 对象。此结果属于类型 Class<? extends X>，
     * 其中 X 表示清除表达式中的静态类型，该表达式调用 getClass。
     */
    public final native Class<?> getClassT();

    /**
     * Object类中没有显示的提供构造方法，这是编译器默认提供的。
     */
    public TObject() {
        super();
    }

    /**
     * 返回对象的哈希码，是一个整数。这个方法遵守以下三个规则：
     * 1. 在java程序运行期间，若用于equals方法的信息或者数据没有修改，name同一个对象多次调用此方法，
     * 返回的哈希码是相同的。而在两次独立的运行java程序时，对于同一对象，不需要返回的哈希码相同
     * 2. 如果根据equals方法，两个对象相同，则这两个对象的哈希码一定相同
     * 3. 假如两个对象通过equals方法比较不相同，那么这两个对象调用hashCode也不是要一定不同，相同也是可以的。
     * 但是使用者应该知道对不同的对象产生不同的hashCode是可以提高hash tables的性能的。
     * <p>
     * 在实际使用中，要尽量保证对于不同的对象产生不同的哈希码。hashCode的典型实现是将对象的内部地址转为一个整数，
     * 但是这种实现技术不是Java语言必须要采用的。
     */
    @Override
    public native int hashCode();

    /**
     * equals 方法在非空对象引用上实现相等关系：
     * <p>
     * 自反性：对于任何非空引用值 x，x.equals(x) 都应返回 true。
     * 对称性：对于任何非空引用值 x 和 y，当且仅当 y.equals(x) 返回 true 时，x.equals(y) 才应返回 true。
     * 传递性：对于任何非空引用值 x、y 和 z，如果 x.equals(y) 返回 true，并且 y.equals(z) 返回 true，那么 x.equals(z) 应返回 true。
     * 一致性：对于任何非空引用值 x 和 y，多次调用 x.equals(y) 始终返回 true 或始终返回 false，
     *       前提是对象上 equals 比较中所用的信息没有被修改。
     * 对于任何非空引用值 x，x.equals(null) 都应返回 false。
     * Object 类的 equals 方法实现对象上差别可能性最大的相等关系；即，对于任何非空引用值 x 和 y，当且仅当 x 和 y 引用同一个对象时，
     * 此方法才返回 true（x == y 具有值 true）。
     * <p>
     * 注意：当此方法被重写时，通常有必要重写 hashCode 方法，以维护 hashCode 方法的常规协定，该协定声明相等对象必须具有相等的哈希码。
     */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    /**
     * 创建并返回当前对象的一份拷贝。一般情况下，对于任何对象 x，表达式 x.clone() != x 为true，
     * x.clone().getClass() == x.getClass()也为true。
     * Object类的clone方法是一个protected的native方法。
     * 由于Object本身没有实现Cloneable接口，所以不重写clone方法并且进行调用的话会发生CloneNotSupportedException异常。
     */
    @Override
    protected native Object clone() throws CloneNotSupportedException;

    /**
     * Object对象的默认实现，即输出类的名字@实例的哈希码的16进制
     */
    @Override
    public String toString() {
        return super.getClass().getName();
    }

    /**
     * 唤醒一个在此对象监视器上等待的线程(监视器相当于就是锁的概念)。如果所有的线程都在此对象上等待，那么只会选择一个线程。
     * 选择是任意性的，并在对实现做出决定时发生。一个线程在对象监视器上等待可以调用wait方法。
     * 直到当前线程放弃对象上的锁之后，被唤醒的线程才可以继续处理。被唤醒的线程将以常规方式与在该对象上主动同步的其他所有线程进行竞争。
     * 例如，唤醒的线程在作为锁定此对象的下一个线程方面没有可靠的特权或劣势。
     * <p>
     * notify方法只能被作为此对象监视器的所有者的线程来调用。一个线程要想成为对象监视器的所有者，可以使用以下3种方法：
     * <p>
     * 执行对象的同步实例方法
     * 使用synchronized内置锁
     * 对于Class类型的对象，执行同步静态方法
     * 一次只能有一个线程拥有对象的监视器。
     * 如果当前线程不是此对象监视器的所有者的话会抛出IllegalMonitorStateException异常
     * 注意点：
     * 因为notify只能在拥有对象监视器的所有者线程中调用，否则会抛出IllegalMonitorStateException异常
     */
    public final native void notifyT();

    /**
     * 跟notify一样，唯一的区别就是会唤醒在此对象监视器上等待的所有线程，而不是一个线程。
     * 同样，如果当前线程不是对象监视器的所有者，那么调用notifyAll同样会发生IllegalMonitorStateException异常。
     */
    public final native void notifyAllT();

    /**
     * wait方法会让当前线程等待直到另外一个线程调用对象的notify或notifyAll方法，或者超过参数设置的timeout超时时间。
     * 跟notify和notifyAll方法一样，当前线程必须是此对象的监视器所有者，否则还是会发生IllegalMonitorStateException异常。
     * wait方法会让当前线程(我们先叫做线程T)将其自身放置在对象的等待集中，并且放弃该对象上的所有同步要求。出于线程调度目的，线
     * 程T是不可用并处于休眠状态，直到发生以下四件事中的任意一件：
     * <p>
     * 1、其他某个线程调用此对象的notify方法，并且线程T碰巧被任选为被唤醒的线程
     * 2、其他某个线程调用此对象的notifyAll方法
     * 3、其他某个线程调用Thread.interrupt方法中断线程T
     * 4、时间到了参数设置的超时时间。如果timeout参数为0，则不会超时，会一直进行等待
     * 所以可以理解wait方法相当于放弃了当前线程对对象监视器的所有者(也就是说释放了对象的锁)
     * <p>
     * 之后，线程T会被等待集中被移除，并且重新进行线程调度。然后，该线程以常规方式与其他线程竞争，以获得在该对象上同步的权利；
     * 一旦获得对该对象的控制权，
     * 该对象上的所有其同步声明都将被恢复到以前的状态，这就是调用wait方法时的情况。然后，线程T从wait方法的调用中返回。
     * 所以，从wait方法返回时，该对象和线程T的同步状态与调用wait方法时的情况完全相同。
     */
    public final native void waitT(long timeout) throws InterruptedException;

    /**
     * 跟wait(long timeout)方法类似，多了一个nanos参数，这个参数表示额外时间（以毫微秒为单位，范围是 0-999999）。
     * 在实现中，只要nanos合规，则timeout+1即可。
     */
    public final void waitT(long timeout, int nanos) throws InterruptedException {
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout value is negative");
        }

        if (nanos < 0 || nanos > 999999) {
            throw new IllegalArgumentException(
                    "nanosecond timeout value out of range");
        }

        if (nanos > 0) {
            timeout++;
        }

        wait(timeout);
    }

    /**
     * 跟之前的2个wait方法一样，只不过该方法一直等待，没有超时时间这个概念。
     */
    public final void waitT() throws InterruptedException {
        wait(0);
    }

    /**
     * finalize方法是一个protected方法，Object类的默认实现是不进行任何操作。
     * 该方法的作用是实例被垃圾回收器回收的时候触发的操作，就好比 “死前的最后一波挣扎”。
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
