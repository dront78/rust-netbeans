#!/bin/bash
#
# Copyright (C) 2013 drrb
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#

set -e

echo "Downloading Rust ANTLR grammar"
cd `dirname "${BASH_SOURCE[0]}"`/..
mkdir -p src/main/antlr4/com/github/drrb/rust/netbeans/parsing
cd src/main/antlr4
wget https://raw.github.com/jbclements/rust-antlr/master/xidstart.g4
wget https://raw.github.com/jbclements/rust-antlr/master/xidcont.g4
cd -
cd src/main/antlr4/
wget https://raw.github.com/jbclements/rust-antlr/master/Rust.g4
git apply --reverse src/etc/rust-antlr.patch 
