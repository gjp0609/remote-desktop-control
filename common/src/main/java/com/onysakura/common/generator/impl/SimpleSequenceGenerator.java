package com.onysakura.common.generator.impl;

import com.onysakura.common.generator.SequenceGenerate;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Cool-Coding
 *         2018/7/25
 */
public class SimpleSequenceGenerator implements SequenceGenerate {
    /**
     * 简单的序号生成器
     */
    private final AtomicInteger sequence=new AtomicInteger();

    @Override
    public int next() {
        return sequence.getAndIncrement();
    }
}
