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
package com.github.drrb.rust.netbeans.project;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Cargo {

    private final Shell shell;
    private final RustProject project;

    public Cargo(RustProject project) {
        this(new Shell("Cargo"), project);
    }

    Cargo(Shell shell, RustProject project) {
        this.shell = shell;
        this.project = project;
    }

    public void run(String... commands) {
        List<String> cargoCommands = new ArrayList<>(commands.length);
        for (String command : commands) {
            cargoCommands.add(String.format("cargo %s --verbose", command));
        }
        String commandLine = Joiner.on(" && ").join(cargoCommands);
        shell.run(commandLine, project.dir());
    }

}
