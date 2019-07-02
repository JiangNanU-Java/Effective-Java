/*
 * Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package java.util.stream;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

/**
 * An extension of {@link Consumer} used to conduct values through the stages of
 * a stream pipeline, with additional methods to manage size information,
 * control flow, etc.  Before calling the {@code accept()} method on a
 * {@code Sink} for the first time, you must first call the {@code begin()}
 * method to inform it that data is coming (optionally informing the sink how
 * much data is coming), and after all data has been sent, you must call the
 * {@code end()} method.  After calling {@code end()}, you should not call
 * {@code accept()} without again calling {@code begin()}.  {@code Sink} also
 * offers a mechanism by which the sink can cooperatively signal that it does
 * not wish to receive any more data (the {@code cancellationRequested()}
 * method), which a source can poll before sending more data to the
 * {@code Sink}.
 *
 * <p>A sink may be in one of two states: an initial state and an active state.
 * It starts out in the initial state; the {@code begin()} method transitions
 * it to the active state, and the {@code end()} method transitions it back into
 * the initial state, where it can be re-used.  Data-accepting methods (such as
 * {@code accept()} are only valid in the active state.
 *
 * @param <T> type of elements for value streams
 * @apiNote A stream pipeline consists of a source, zero or more intermediate stages
 * (such as filtering or mapping), and a terminal stage, such as reduction or
 * for-each.  For concreteness, consider the pipeline:
 *
 * <pre>{@code
 *     int longestStringLengthStartingWithA
 *         = strings.stream()
 *                  .filter(s -> s.startsWith("A"))
 *                  .mapToInt(String::length)
 *                  .max();
 * }</pre>
 *
 * <p>Here, we have three stages, filtering, mapping, and reducing.  The
 * filtering stage consumes strings and emits a subset of those strings; the
 * mapping stage consumes strings and emits ints; the reduction stage consumes
 * those ints and computes the maximal value.
 *
 * <p>A {@code Sink} instance is used to represent each stage of this pipeline,
 * whether the stage accepts objects, ints, longs, or doubles.  Sink has entry
 * points for {@code accept(Object)}, {@code accept(int)}, etc, so that we do
 * not need a specialized interface for each primitive specialization.  (It
 * might be called a "kitchen sink" for this omnivorous tendency.)  The entry
 * point to the pipeline is the {@code Sink} for the filtering stage, which
 * sends some elements "downstream" -- into the {@code Sink} for the mapping
 * stage, which in turn sends integral values downstream into the {@code Sink}
 * for the reduction stage. The {@code Sink} implementations associated with a
 * given stage is expected to know the data type for the next stage, and call
 * the correct {@code accept} method on its downstream {@code Sink}.  Similarly,
 * each stage must implement the correct {@code accept} method corresponding to
 * the data type it accepts.
 *
 * <p>The specialized subtypes such as {@link Sink.OfInt} override
 * {@code accept(Object)} to call the appropriate primitive specialization of
 * {@code accept}, implement the appropriate primitive specialization of
 * {@code Consumer}, and re-abstract the appropriate primitive specialization of
 * {@code accept}.
 *
 * <p>The chaining subtypes such as {@link ChainedInt} not only implement
 * {@code Sink.OfInt}, but also maintain a {@code downstream} field which
 * represents the downstream {@code Sink}, and implement the methods
 * {@code begin()}, {@code end()}, and {@code cancellationRequested()} to
 * delegate to the downstream {@code Sink}.  Most implementations of
 * intermediate operations will use these chaining wrappers.  For example, the
 * mapping stage in the above example would look like:
 *
 * <pre>{@code
 *     IntSink is = new Sink.ChainedReference<U>(sink) {
 *         public void accept(U u) {
 *             downstream.accept(mapper.applyAsInt(u));
 *         }
 *     };
 * }</pre>
 *
 * <p>Here, we implement {@code Sink.ChainedReference<U>}, meaning that we expect
 * to receive elements of type {@code U} as input, and pass the downstream sink
 * to the constructor.  Because the next stage expects to receive integers, we
 * must call the {@code accept(int)} method when emitting values to the downstream.
 * The {@code accept()} method applies the mapping function from {@code U} to
 * {@code int} and passes the resulting value to the downstream {@code Sink}.
 * <p>
 * 使用者的扩展，用于在流管道的各个阶段中执行值，以及管理大小信息、控制流等的其他方法。
 * 在第一次调用接收上的accept()方法之前，您必须首先调用begin()方法来通知它数据即将到来(可选地通知接收方有多少数据即将到来)，
 * 并且在发送完所有数据之后，您必须调用end()方法。调用end()之后，如果没有再次调用begin()，就不应该调用accept()。
 * Sink还提供了一种机制，通过这种机制，Sink可以协作地发出信号，表示它不希望接收更多的数据(cancellationrequired()方法)，
 * 源可以在向Sink发送更多数据之前轮询这些数据。接收器可能处于两种状态之一:初始状态和活动状态。
 * 它从初始状态开始;begin()方法将其转换为活动状态，end()方法将其转换回初始状态，在初始状态中可以重用它。
 * 数据接受方法(如accept()仅在活动状态下有效。
 * @since 1.8
 */
interface Sink<T> extends Consumer<T> {
    /**
     * Resets the sink state to receive a fresh data set.  This must be called
     * before sending any data to the sink.  After calling {@link #end()},
     * you may call this method to reset the sink for another calculation.
     * <p>
     * 重置接收器状态以接收新的数据集。在向接收器发送任何数据之前，必须调用此操作。
     * 调用end()之后，可以调用此方法重置接收器，以便进行另一次计算。
     *
     * @param size The exact size of the data to be pushed downstream, if
     *             known or {@code -1} if unknown or infinite.
     *
     *             <p>Prior to this call, the sink must be in the initial state, and after
     *             this call it is in the active state.
     */
    default void begin(long size) {
    }

    /**
     * Indicates that all elements have been pushed.  If the {@code Sink} is
     * stateful, it should send any stored state downstream at this time, and
     * should clear any accumulated state (and associated resources).
     *
     * <p>Prior to this call, the sink must be in the active state, and after
     * this call it is returned to the initial state.
     * <p>
     * 表示所有元素都已被推送。如果接收器是有状态的，那么此时它应该将任何存储的状态发送到下游，
     * 并且应该清除任何累积的状态(以及相关的资源)。在此调用之前，接收器必须处于活动状态，
     * 在此调用之后，接收器将返回到初始状态。
     */
    default void end() {
    }

    /**
     * Indicates that this {@code Sink} does not wish to receive any more data.
     * <p>
     * 指示此接收器不希望接收任何其他数据。
     *
     * @return true if cancellation is requested
     * @implSpec The default implementation always returns false.
     */
    default boolean cancellationRequested() {
        return false;
    }

    // -------------------------------------------------------------- 重载各种消费方法

    /**
     * Accepts an int value.
     *
     * @throws IllegalStateException if this sink does not accept int values
     * @implSpec The default implementation throws IllegalStateException.
     */
    default void accept(int value) {
        throw new IllegalStateException("called wrong accept method");
    }

    /**
     * Accepts a long value.
     *
     * @throws IllegalStateException if this sink does not accept long values
     * @implSpec The default implementation throws IllegalStateException.
     */
    default void accept(long value) {
        throw new IllegalStateException("called wrong accept method");
    }

    /**
     * Accepts a double value.
     *
     * @throws IllegalStateException if this sink does not accept double values
     * @implSpec The default implementation throws IllegalStateException.
     */
    default void accept(double value) {
        throw new IllegalStateException("called wrong accept method");
    }



    /**
     * {@code Sink} that implements {@code Sink<Integer>}, re-abstracts
     * {@code accept(int)}, and wires {@code accept(Integer)} to bridge to
     * {@code accept(int)}.
     */
    interface OfInt extends Sink<Integer>, IntConsumer {
        @Override
        void accept(int value);

        @Override
        default void accept(Integer i) {
            if (Tripwire.ENABLED)
                Tripwire.trip(getClass(), "{0} calling Sink.OfInt.accept(Integer)");
            accept(i.intValue());
        }
    }

    /**
     * {@code Sink} that implements {@code Sink<Long>}, re-abstracts
     * {@code accept(long)}, and wires {@code accept(Long)} to bridge to
     * {@code accept(long)}.
     */
    interface OfLong extends Sink<Long>, LongConsumer {
        @Override
        void accept(long value);

        @Override
        default void accept(Long i) {
            if (Tripwire.ENABLED)
                Tripwire.trip(getClass(), "{0} calling Sink.OfLong.accept(Long)");
            accept(i.longValue());
        }
    }

    /**
     * {@code Sink} that implements {@code Sink<Double>}, re-abstracts
     * {@code accept(double)}, and wires {@code accept(Double)} to bridge to
     * {@code accept(double)}.
     */
    interface OfDouble extends Sink<Double>, DoubleConsumer {
        @Override
        void accept(double value);

        @Override
        default void accept(Double i) {
            if (Tripwire.ENABLED)
                Tripwire.trip(getClass(), "{0} calling Sink.OfDouble.accept(Double)");
            accept(i.doubleValue());
        }
    }

    /**
     * Abstract {@code Sink} implementation for creating chains of
     * sinks.  The {@code begin}, {@code end}, and
     * {@code cancellationRequested} methods are wired to chain to the
     * downstream {@code Sink}.  This implementation takes a downstream
     * {@code Sink} of unknown input shape and produces a {@code Sink<T>}.  The
     * implementation of the {@code accept()} method must call the correct
     * {@code accept()} method on the downstream {@code Sink}.
     */
    static abstract class ChainedReference<T, E_OUT> implements Sink<T> {
        protected final Sink<? super E_OUT> downstream;

        public ChainedReference(Sink<? super E_OUT> downstream) {
            this.downstream = Objects.requireNonNull(downstream);
        }

        @Override
        public void begin(long size) {
            downstream.begin(size);
        }

        @Override
        public void end() {
            downstream.end();
        }

        @Override
        public boolean cancellationRequested() {
            return downstream.cancellationRequested();
        }
    }

    /**
     * Abstract {@code Sink} implementation designed for creating chains of
     * sinks.  The {@code begin}, {@code end}, and
     * {@code cancellationRequested} methods are wired to chain to the
     * downstream {@code Sink}.  This implementation takes a downstream
     * {@code Sink} of unknown input shape and produces a {@code Sink.OfInt}.
     * The implementation of the {@code accept()} method must call the correct
     * {@code accept()} method on the downstream {@code Sink}.
     */
    static abstract class ChainedInt<E_OUT> implements Sink.OfInt {
        protected final Sink<? super E_OUT> downstream;

        public ChainedInt(Sink<? super E_OUT> downstream) {
            this.downstream = Objects.requireNonNull(downstream);
        }

        @Override
        public void begin(long size) {
            downstream.begin(size);
        }

        @Override
        public void end() {
            downstream.end();
        }

        @Override
        public boolean cancellationRequested() {
            return downstream.cancellationRequested();
        }
    }

    /**
     * Abstract {@code Sink} implementation designed for creating chains of
     * sinks.  The {@code begin}, {@code end}, and
     * {@code cancellationRequested} methods are wired to chain to the
     * downstream {@code Sink}.  This implementation takes a downstream
     * {@code Sink} of unknown input shape and produces a {@code Sink.OfLong}.
     * The implementation of the {@code accept()} method must call the correct
     * {@code accept()} method on the downstream {@code Sink}.
     */
    static abstract class ChainedLong<E_OUT> implements Sink.OfLong {
        protected final Sink<? super E_OUT> downstream;

        public ChainedLong(Sink<? super E_OUT> downstream) {
            this.downstream = Objects.requireNonNull(downstream);
        }

        @Override
        public void begin(long size) {
            downstream.begin(size);
        }

        @Override
        public void end() {
            downstream.end();
        }

        @Override
        public boolean cancellationRequested() {
            return downstream.cancellationRequested();
        }
    }

    /**
     * Abstract {@code Sink} implementation designed for creating chains of
     * sinks.  The {@code begin}, {@code end}, and
     * {@code cancellationRequested} methods are wired to chain to the
     * downstream {@code Sink}.  This implementation takes a downstream
     * {@code Sink} of unknown input shape and produces a {@code Sink.OfDouble}.
     * The implementation of the {@code accept()} method must call the correct
     * {@code accept()} method on the downstream {@code Sink}.
     */
    static abstract class ChainedDouble<E_OUT> implements Sink.OfDouble {
        protected final Sink<? super E_OUT> downstream;

        public ChainedDouble(Sink<? super E_OUT> downstream) {
            this.downstream = Objects.requireNonNull(downstream);
        }

        @Override
        public void begin(long size) {
            downstream.begin(size);
        }

        @Override
        public void end() {
            downstream.end();
        }

        @Override
        public boolean cancellationRequested() {
            return downstream.cancellationRequested();
        }
    }
}
