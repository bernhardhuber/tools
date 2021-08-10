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
package org.huberb.apacheactivemq.examples.picocli.jms.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Scanner;

/**
 *
 * @author pi
 */
public class EvaluateMessageText {

    public enum EvalMode {
        fromNone, fromStdin, fromString, fromFile
    }
    private final EvalMode evalMode;
    private final String defaultMessageText = "Hello world!";
    private final String messageText;
    private final File messageFile;
    private final String messageFileCharset;

    public static EvaluateMessageText createForCapturingStdin() {
        return new EvaluateMessageText(EvalMode.fromStdin, null, null, null);
    }

    public static EvaluateMessageText createForCapturingString(String s) {
        return new EvaluateMessageText(EvalMode.fromString, s, null, null);
    }

    public static EvaluateMessageText createForCapturingFile(File messageFile, String messageFileCharset) {
        return new EvaluateMessageText(EvalMode.fromFile, null, messageFile, messageFileCharset);
    }

    EvaluateMessageText(EvalMode evalMode, String messageText, File messageFile, String messageFileCharset) {
        this.messageText = messageText;
        this.messageFile = messageFile;
        this.messageFileCharset = messageFileCharset != null ? "UTF-8" : messageFileCharset;
        this.evalMode = evalMode;
    }

    public String build() {
        final StringBuilder sb = new StringBuilder();
        try {
            switch (evalMode) {
                case fromNone:
                    sb.append(defaultMessageText);
                    break;
                case fromStdin:
                    sb.append(captureFromStdin());
                    break;
                case fromString:
                    sb.append(captureFromString(messageText));
                    break;
                case fromFile:
                    sb.append(captureFromFile(messageFile, messageFileCharset));
                    break;
                default:
                    sb.append(defaultMessageText);
            }
        } catch (IOException ioex) {
            sb.delete(0, sb.length());
        }
        return sb.toString();
    }

    /**
     *
     * @return
     */
    StringBuilder captureFromStdin() throws IOException {
        return captureFromStdinViaBufferedReader(new NonClosingInputStream(System.in));
    }

    StringBuilder captureFromStdinViaScanner(InputStream is) {
        final StringBuilder sb = new StringBuilder();
        for (Scanner sc = new Scanner(is); sc.hasNextLine();) {
            final String line = sc.nextLine();
            sb.append(line);
            sb.append(System.lineSeparator());
        }
        return sb;
    }

    StringBuilder captureFromStdinViaBufferedReader(InputStream is) throws IOException {
        final StringBuilder sb = new StringBuilder();

        try (final InputStreamReader isr = new InputStreamReader(is);
                final BufferedReader br = new BufferedReader(isr)) {
            for (String line; (line = br.readLine()) != null;) {
                sb.append(line);
                sb.append(System.lineSeparator());
            }
        }
        return sb;
    }

    StringBuilder captureFromString(String messageText) {
        final StringBuilder sb = new StringBuilder();
        sb.append(messageText);
        return sb;
    }

    StringBuilder captureFromFile(File messageFile, String messageFileCharset) throws IOException {
        final StringBuilder sb = new StringBuilder();
        sb.append(Files.readString(messageFile.toPath(), Charset.forName(messageFileCharset)));
        return sb;
    }

}
