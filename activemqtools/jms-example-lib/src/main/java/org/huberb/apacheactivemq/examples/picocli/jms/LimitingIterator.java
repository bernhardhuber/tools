/*
 * Copyright 2021 pi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.huberb.apacheactivemq.examples.picocli.jms;

import java.util.Iterator;

/**
 *
 * @author pi
 */
class LimitingIterator<T> implements Iterator<T> {
    
    private final Iterator<T> it;
    private final int maxIterationsCount;
    private int currentIterationCount;

    public LimitingIterator(Iterator<T> it, int maxIterationsCount) {
        this.it = it;
        this.maxIterationsCount = maxIterationsCount;
        this.currentIterationCount = 0;
    }

    @Override
    public boolean hasNext() {
        boolean hasNext = true;
        if (maxIterationsCount >= 0) {
            hasNext = hasNext && maxIterationsCount > currentIterationCount;
        } else {
            hasNext = hasNext && true;
        }
        hasNext = hasNext && it.hasNext();
        return hasNext;
    }

    @Override
    public T next() {
        currentIterationCount += 1;
        return it.next();
    }

    public int currentIterationCount() {
        return this.currentIterationCount;
    }
    
}
