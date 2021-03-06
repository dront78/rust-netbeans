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
package com.github.drrb.rust.netbeans.rustbridge;

import java.io.File;
import java.util.List;

/**
 *
 */
public class RustSemanticHighlighter {
    private final File sourceFile;

    public RustSemanticHighlighter(File sourceFile) {
        this.sourceFile = sourceFile;
    }

    public List<RustHighlight> getHighlights(RustParser.Result parseResult) {
        RustNative.HighlightAccumulator highlightAccumulator = new RustNative.HighlightAccumulator(sourceFile);
        //TODO: These checks are clumsy. Should they happen in Rust instead?
        if (parseResult.isSuccess()) {
            RustNative.INSTANCE.getHighlights(parseResult.getAst(), highlightAccumulator);
        }
        return highlightAccumulator.getHighlights();
    }
}
