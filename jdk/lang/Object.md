# Object 源码解析

## public class Object 


----------------------------------------------------------------------------------
### registerNatives()
__private static native void registerNatives();__
> 一个本地方法,具体是用C(C++)在DLL中实现的,然后通过JNI(Java Native Interface)调用

    对象初始化时自动调用此方法
    static {
        registerNatives();
    }
   
----------------------------------------------------------------------------------
### 构造方法Object()
    /**
    * Object类中没有显示的提供构造方法，这是编译器默认提供的。
    */
    public Object() {
        super();
    }
   
----------------------------------------------------------------------------------
### getClass()

__public final native Class<?> getClass();__

> 返回此Object的运行时类。
  该 Class 对象是由所表示类的 static synchronized 方法锁定的对象。
  
----------------------------------------------------------------------------------
### hashCode()

__public native int hashCode();__

> hashCode的常规协定是：

> 1.在java应用程序执行期间,在对同一对象多次调用hashCode()方法时,必须一致地返回相同的整数,前提是将对象进行equals比较时所用的信息没有被修改。
    从某一应用程序的一次执行到同一应用程序的另一次执行,该整数无需保持一致。
       
> 2.如果根据equals(object)方法,两个对象是相等的,那么对这两个对象中的每个对象调用hashCode方法都必须生成相同的整数结果。
       
> 3.如果根据equals(java.lang.Object)方法,两个对象不相等,那么对这两个对象中的任一对象上调用hashCode()方法不要求一定生成不同的整数结果。
    但是,程序员应该意识到,为不相等的对象生成不同整数结果可以提高哈希表的性能。
    
----------------------------------------------------------------------------------
### equals(Object obj)

__public boolean equals(Object obj)__

```java
public boolean equals(Object obj) {
        return (this == obj);
}
```

> 这里比较的是对象的内存地址

----------------------------------------------------------------------------------
### clone()

__protected native Object clone() throws CloneNotSupportedException;__

> 创建并返回当前对象的一份拷贝。

> 一般情况下，对于任何对象 x，表达式 x.clone() != x 为true，x.clone().getClass() == x.getClass()也为true。
 
> Object类的clone方法是一个protected的native方法。由于Object本身没有实现Cloneable接口，所以不重写clone方法并且进行调用的话会发生CloneNotSupportedException异常。

----------------------------------------------------------------------------------  
### toString()

__public String toString()__

```java
public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode())}
```

> 返回该对象的字符串表示,非常重要的方法

> getClass().getName();获取字节码文件的对应全路径名例如java.lang.Object

> Integer.toHexString(hashCode());将哈希值转成16进制数格式的字符串。

----------------------------------------------------------------------------------
### notify()

__public final native void notify();__

> 不能被重写，用于唤醒一个在因等待该对象（调用了wait方法）被处于等待状态（waiting 或 time_wait）的线程，该方法只能同步方法或同步块中调用

----------------------------------------------------------------------------------
### notifyAll()

__public final native void notifyAll();__

> 不能被重写，用于唤醒所有在因等待该对象（调用wait方法）被处于等待状态（waiting或time_waiting）的线程，该方法只能同步方法或同步块中调用

----------------------------------------------------------------------------------

### wait(long timeout)

__public final native void wait(long timeout) throws InterruptedException;__

> 不能被重写，用于在线程调用中，导致当前线程进入等待状态（time_waiting)，timeout单位为毫秒,该方法只能同步方法或同步块中调用,超过设置时间后线程重新进入可运行状态

----------------------------------------------------------------------------------
### wait(long timeout, int nanos)

__public final void wait(long timeout, int nanos) throws InterruptedException__

```java
public final void wait(long timeout, int nanos) throws InterruptedException {
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
```

> 跟wait(long timeout)方法类似，多了一个nanos参数，这个参数表示额外时间（以毫微秒为单位，范围是 0-999999）。

> 在实现中，只要nanos合规，则timeout+1即可。

----------------------------------------------------------------------------------
### wait() 

__public final void wait() throws InterruptedException__

```java
public final void wait() throws InterruptedException {
        wait(0);
    }
```

> 在其他线程调用此对象的notify()方法或notifyAll()方法前,导致当前线程等待。换句话说,此方法的行为就好像它仅执行wait(0)调用一样。当前线程必须拥有此对象监视器。
该线程发布对此监视器的所有权并等待,直到其他线程通过调用notify方法或notifyAll方法通知在此对象的监视器上等待的线程醒来,然后该线程将等到重新获得对监视器的所有权后才能继续执行。

----------------------------------------------------------------------------------
### finalize()

__protected void finalize() throws Throwable__
  
```java
protected void finalize() throws Throwable {
    }
```
  
> 这个方法用于当对象被回收时调用，这个由JVM支持，Object的finalize方法默认是什么都没有做。
> 如果子类需要在对象被回收时执行一些逻辑处理，则可以重写finalize方法。
> <p>
> 如果对象在进行可达性分析后发现没有与 GC Roots 相连接的引用链，那他将会被第一次标记并且进行一次筛选，筛选的条件是此对象是否有必要执行 finalize 方法。
> 注意：当对象没有覆盖 finalize 方法，或者 finalize 方法已经被虚拟机调用过，虚拟机将这两种情况都视为 “没有必要执行”。也就是说，finalize 方法只会被执行一次。
> <p>
> 如果这个对象被判定为有必要执行 finalize 方法，那么这个对象将会放置在一个叫做 F-Queue 的队列之中，并在稍后由一个虚拟机自动建立的，优先级为 8 的 Finalizer 线程去执行它。
> 注意：如果一个对象在 finalize 方法中运行缓慢，将会导致队列后的其他对象永远等待，严重时将会导致系统崩溃。
> <p>
> finalize 方法是对象逃脱死亡命运的最后一道关卡。稍后 GC 将对队列中的对象进行第二次规模的标记，如果对象要在 finalize 中 “拯救” 自己，只需要将自己关联到引用上即可，通常是 this。
> 如果这个对象关联上了引用，那么在第二次标记的时候他将被移除出 “即将回收” 的集合；如果对象这时候还没有逃脱，那基本上就是真的被回收了。