begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|*
import|;
end_import

begin_comment
comment|/** Support for flat files of binary key/value pairs. */
end_comment

begin_class
DECL|class|TestArrayFile
specifier|public
class|class
name|TestArrayFile
extends|extends
name|TestCase
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestArrayFile
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|FILE
specifier|private
specifier|static
name|String
name|FILE
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"."
argument_list|)
operator|+
literal|"/test.array"
decl_stmt|;
DECL|method|TestArrayFile (String name)
specifier|public
name|TestArrayFile
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|testArrayFile ()
specifier|public
name|void
name|testArrayFile
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|RandomDatum
index|[]
name|data
init|=
name|generate
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
name|writeTest
argument_list|(
name|fs
argument_list|,
name|data
argument_list|,
name|FILE
argument_list|)
expr_stmt|;
name|readTest
argument_list|(
name|fs
argument_list|,
name|data
argument_list|,
name|FILE
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|testEmptyFile ()
specifier|public
name|void
name|testEmptyFile
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|writeTest
argument_list|(
name|fs
argument_list|,
operator|new
name|RandomDatum
index|[
literal|0
index|]
argument_list|,
name|FILE
argument_list|)
expr_stmt|;
name|ArrayFile
operator|.
name|Reader
name|reader
init|=
operator|new
name|ArrayFile
operator|.
name|Reader
argument_list|(
name|fs
argument_list|,
name|FILE
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|reader
operator|.
name|get
argument_list|(
literal|0
argument_list|,
operator|new
name|RandomDatum
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|generate (int count)
specifier|private
specifier|static
name|RandomDatum
index|[]
name|generate
parameter_list|(
name|int
name|count
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"generating "
operator|+
name|count
operator|+
literal|" records in debug"
argument_list|)
expr_stmt|;
block|}
name|RandomDatum
index|[]
name|data
init|=
operator|new
name|RandomDatum
index|[
name|count
index|]
decl_stmt|;
name|RandomDatum
operator|.
name|Generator
name|generator
init|=
operator|new
name|RandomDatum
operator|.
name|Generator
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|generator
operator|.
name|next
argument_list|()
expr_stmt|;
name|data
index|[
name|i
index|]
operator|=
name|generator
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
return|return
name|data
return|;
block|}
DECL|method|writeTest (FileSystem fs, RandomDatum[] data, String file)
specifier|private
specifier|static
name|void
name|writeTest
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|RandomDatum
index|[]
name|data
parameter_list|,
name|String
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|MapFile
operator|.
name|delete
argument_list|(
name|fs
argument_list|,
name|file
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"creating with "
operator|+
name|data
operator|.
name|length
operator|+
literal|" debug"
argument_list|)
expr_stmt|;
block|}
name|ArrayFile
operator|.
name|Writer
name|writer
init|=
operator|new
name|ArrayFile
operator|.
name|Writer
argument_list|(
name|conf
argument_list|,
name|fs
argument_list|,
name|file
argument_list|,
name|RandomDatum
operator|.
name|class
argument_list|)
decl_stmt|;
name|writer
operator|.
name|setIndexInterval
argument_list|(
literal|100
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|writer
operator|.
name|append
argument_list|(
name|data
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|readTest (FileSystem fs, RandomDatum[] data, String file, Configuration conf)
specifier|private
specifier|static
name|void
name|readTest
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|RandomDatum
index|[]
name|data
parameter_list|,
name|String
name|file
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|RandomDatum
name|v
init|=
operator|new
name|RandomDatum
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"reading "
operator|+
name|data
operator|.
name|length
operator|+
literal|" debug"
argument_list|)
expr_stmt|;
block|}
name|ArrayFile
operator|.
name|Reader
name|reader
init|=
operator|new
name|ArrayFile
operator|.
name|Reader
argument_list|(
name|fs
argument_list|,
name|file
argument_list|,
name|conf
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// try forwards
name|reader
operator|.
name|get
argument_list|(
name|i
argument_list|,
name|v
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|v
operator|.
name|equals
argument_list|(
name|data
index|[
name|i
index|]
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"wrong value at "
operator|+
name|i
argument_list|)
throw|;
block|}
block|}
for|for
control|(
name|int
name|i
init|=
name|data
operator|.
name|length
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
comment|// then backwards
name|reader
operator|.
name|get
argument_list|(
name|i
argument_list|,
name|v
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|v
operator|.
name|equals
argument_list|(
name|data
index|[
name|i
index|]
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"wrong value at "
operator|+
name|i
argument_list|)
throw|;
block|}
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"done reading "
operator|+
name|data
operator|.
name|length
operator|+
literal|" debug"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** For debugging and testing. */
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|count
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
name|boolean
name|create
init|=
literal|true
decl_stmt|;
name|boolean
name|check
init|=
literal|true
decl_stmt|;
name|String
name|file
init|=
name|FILE
decl_stmt|;
name|String
name|usage
init|=
literal|"Usage: TestArrayFile [-count N] [-nocreate] [-nocheck] file"
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|usage
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
name|Path
name|fpath
init|=
literal|null
decl_stmt|;
name|FileSystem
name|fs
init|=
literal|null
decl_stmt|;
try|try
block|{
for|for
control|(
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// parse command line
if|if
condition|(
name|args
index|[
name|i
index|]
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-count"
argument_list|)
condition|)
block|{
name|count
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-nocreate"
argument_list|)
condition|)
block|{
name|create
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-nocheck"
argument_list|)
condition|)
block|{
name|check
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
comment|// file is required parameter
name|file
operator|=
name|args
index|[
name|i
index|]
expr_stmt|;
name|fpath
operator|=
operator|new
name|Path
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
block|}
name|fs
operator|=
name|fpath
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"count = "
operator|+
name|count
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"create = "
operator|+
name|create
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"check = "
operator|+
name|check
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"file = "
operator|+
name|file
argument_list|)
expr_stmt|;
name|RandomDatum
index|[]
name|data
init|=
name|generate
argument_list|(
name|count
argument_list|)
decl_stmt|;
if|if
condition|(
name|create
condition|)
block|{
name|writeTest
argument_list|(
name|fs
argument_list|,
name|data
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|check
condition|)
block|{
name|readTest
argument_list|(
name|fs
argument_list|,
name|data
argument_list|,
name|file
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

