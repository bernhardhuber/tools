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
package org.huberb.services.dbunit.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Define an {@link InputStream}, which ignores {@link InpputStream#close}.
 * <p>
 * Use this class with care, one use case is wrapping {@link System.in} as
 * you might want to keep System.in open, after processing some
 * unit-of-work.
 *
 */
public class DelegatingNonClosingInputStream extends InputStream {

    private final InputStream is;

    public DelegatingNonClosingInputStream(InputStream is) {
        this.is = is;
    }

    @Override
    public int read() throws IOException {
        return is.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return is.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return is.read(b, off, len);
    }

    @Override
    public byte[] readAllBytes() throws IOException {
        return is.readAllBytes();
    }

    @Override
    public byte[] readNBytes(int len) throws IOException {
        return is.readNBytes(len);
    }

    @Override
    public int readNBytes(byte[] b, int off, int len) throws IOException {
        return is.readNBytes(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return is.skip(n);
    }

    @Override
    public int available() throws IOException {
        return is.available();
    }

    @Override
    public void close() throws IOException {
        // do not close
    }

    @Override
    public synchronized void mark(int readlimit) {
        is.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        is.reset();
    }

    @Override
    public boolean markSupported() {
        return is.markSupported();
    }

    @Override
    public long transferTo(OutputStream out) throws IOException {
        return is.transferTo(out);
    }
    
}
