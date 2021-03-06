/*
 * Copyright (C) 2015 drrb
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.drrb.rust.netbeans.parsing;

import com.github.drrb.rust.netbeans.rustbridge.RustParser;
import com.github.drrb.rust.netbeans.rustbridge.RustParseMessage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import javax.swing.text.StyledDocument;
import org.netbeans.modules.csl.api.Error;
import org.netbeans.modules.csl.spi.DefaultError;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Task;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.SourceModificationEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.text.NbDocument;

/**
 *
 */
public class NetbeansRustParser extends Parser {
    private static final Logger LOG = Logger.getLogger(NetbeansRustParser.class.getName());
    private final RustParser rustParser = new RustParser();
    private RustParser.Result result = RustParser.Result.NONE;
    private Snapshot snapshot;

    @Override
    public void parse(final Snapshot snapshot, Task task, SourceModificationEvent event) {
        //TODO: if we get segfaults, it's probably to do with this.
        // we should probably make sure we don't try to access the AST from
        // a stale (invalidated) result because the AST will have been freed.
        // (assuming that's actually what ParserResult.invalidate() actually means)
        this.result.destroy();
        this.snapshot = snapshot;
        this.result = parse(snapshot);
    }

    private RustParser.Result parse(Snapshot snapshot) {
        File file = FileUtil.toFile(snapshot.getSource().getFileObject());
        String source = snapshot.getText().toString();
        return rustParser.parse(file, source);
    }

    @Override
    public NetbeansRustParserResult getResult(Task task) throws ParseException {
        return new NetbeansRustParserResult(snapshot, result, getDiagnostics());
    }

    private List<Error> getDiagnostics() {
        FileObject file = snapshot.getSource().getFileObject();
        StyledDocument document = NbDocument.getDocument(file);
        List<RustParseMessage> parseMessages = result.getParseMessages();
        List<Error> diagnostics = new ArrayList<>(parseMessages.size());
        for (RustParseMessage message : parseMessages) {
            int startOffset = NbDocument.findLineOffset(document, message.getStartLine() - 1) + message.getStartCol();
            int endOffset = NbDocument.findLineOffset(document, message.getEndLine() - 1) + message.getEndCol();
            diagnostics.add(new DefaultError("rust.parse.message", message.getMessage(), message.getMessage(), file, startOffset, endOffset, message.getLevel().severity()));
        }
        return diagnostics;
    }

    @Override
    public void addChangeListener(ChangeListener changeListener) {
    }

    @Override
    public void removeChangeListener(ChangeListener changeListener) {
    }

    public static class NetbeansRustParserResult extends ParserResult {

        private final RustParser.Result result;
        private final AtomicBoolean valid = new AtomicBoolean(true);
        private final List<Error> diagnostics;

        public NetbeansRustParserResult(Snapshot snapshot, RustParser.Result result, List<Error> diagnostics) {
            super(snapshot);
            this.result = result;
            this.diagnostics = Collections.unmodifiableList(diagnostics);
        }

        public RustParser.Result getResult() throws ParseException {
            //TODO: is this what we should be doing to ensure people don't
            // access a released AST?
            if (!valid.get()) {
                throw new ParseException();
            }
            return result;
        }

        @Override
        protected void invalidate() {
            valid.set(false);
        }

        @Override
        public List<? extends Error> getDiagnostics() {
            return diagnostics;
        }
//
//        public RustSourceIndex getIndex() {
//            return getAst().accept(new IndexingVisitor());
//        }
    }
}
