/*
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.util.Recycler;
import io.netty.util.ReferenceCountUtil;

public final class PendingWriteBuffer {
    private final ChannelHandlerContext ctx;
    private final ChannelOutboundBuffer buffer;
    private PendingWrite head;
    private PendingWrite tail;

    /**
     * Creates new instance
     */
    public PendingWriteBuffer(ChannelHandlerContext ctx) {
        if (ctx == null) {
            throw new NullPointerException("ctx");
        }
        this.ctx = ctx;
        buffer = ctx.channel().unsafe().outboundBuffer();
    }

    /**
     * Returns {@code true} if there are no entries left in this queue.
     */
    public boolean isEmpty() {
        return head == null;
    }

    /**
     * Add the given {@code msg} and {@link io.netty.channel.ChannelPromise}.
     */
    public void add(Object msg, ChannelPromise promise) {
        if (msg == null) {
            throw new NullPointerException("msg");
        }
        if (promise == null) {
            throw new NullPointerException("promise");
        }
        PendingWrite write = PendingWrite.newInstance(msg, promise);
        PendingWrite currentTail = tail;
        if (currentTail == null) {
            tail = head = write;
        } else {
            currentTail.next = write;
            tail = write;
        }
        buffer.incrementPendingOutboundBytes(write.size);
    }

    public void failAll(Throwable cause) {
        if (cause == null) {
            throw new NullPointerException("cause");
        }
        PendingWrite write = head;
        while (write != null) {
            PendingWrite next = write.next;
            ReferenceCountUtil.safeRelease(write.msg);
            buffer.decrementPendingOutboundBytes(write.size);

            write.promise.tryFailure(cause);
            write.recycle();
            write = next;
        }
        head = tail = null;
    }

    /**
     * Return the current message or {@code null} if empty.
     */
    public Object current() {
        PendingWrite write = head;
        if (write == null) {
            return null;
        }
        return write.msg;
    }

    /**
     * Removes the queued {@link Object} {@link ChannelPromise} pair and write it via
     * {@link ChannelHandlerContext#write(Object, ChannelPromise)}. This method will return a {@link ChannelFuture} if
     * something was written and {@code null} if the {@link PendingWriteBuffer} is empty.
     */
    public ChannelFuture removeAndWrite() {
        PendingWrite write = head;
        if (write == null) {
            return null;
        }
        Object msg = write.msg;
        ChannelPromise promise = write.promise;
        buffer.decrementPendingOutboundBytes(write.size);
        recycle(write);
        return ctx.write(msg, promise);
    }

    /**
     * Removes the queued {@link Object} {@link ChannelPromise} pair and return the {@link ChannelPromise}.
     * If the {@link PendingWriteBuffer} is empty  it will just return {@code null}.
     *
     * The {@link Object} will be automatically be released via {@link ReferenceCountUtil#safeRelease(Object)}.
     */
    public ChannelPromise remove() {
        PendingWrite write = head;
        if (write == null) {
            return null;
        }
        ChannelPromise promise = write.promise;
        buffer.decrementPendingOutboundBytes(write.size);
        ReferenceCountUtil.safeRelease(write.msg);
        recycle(write);
        return promise;
    }

    private void recycle(PendingWrite write) {
        PendingWrite next = write.next;
        write.recycle();
        if (next == null) {
            head = tail = null;
        } else {
            head = next;
        }
    }

    static final class PendingWrite {
        private static final Recycler<PendingWrite> RECYCLER = new Recycler<PendingWrite>() {
            @Override
            protected PendingWrite newObject(Handle handle) {
                return new PendingWrite(handle);
            }
        };

        private final Recycler.Handle handle;
        private PendingWrite next;
        private long size;
        private ChannelPromise promise;
        private Object msg;

        private PendingWrite(Recycler.Handle handle) {
            this.handle = handle;
        }

        static PendingWrite newInstance(Object msg, ChannelPromise promise) {
            PendingWrite write = RECYCLER.get();
            write.size = size(msg);
            write.msg = msg;
            write.promise = promise;
            return write;
        }

        private static long size(Object msg) {
            if (msg instanceof ByteBuf) {
                return ((ByteBuf) msg).readableBytes();
            }
            if (msg instanceof ByteBufHolder) {
                return ((ByteBufHolder) msg).content().readableBytes();
            }
            return 0;
        }

        private void recycle() {
            size = 0;
            next = null;
            msg = null;
            promise = null;
            RECYCLER.recycle(this, handle);
        }
    }
}
