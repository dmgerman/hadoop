begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.streaming
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|streaming
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
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
name|Configuration
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
name|FileSystem
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
name|Path
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
name|hdfs
operator|.
name|MiniDFSCluster
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
name|streaming
operator|.
name|DumpTypedBytes
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
name|typedbytes
operator|.
name|TypedBytesInput
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_class
DECL|class|TestDumpTypedBytes
specifier|public
class|class
name|TestDumpTypedBytes
block|{
annotation|@
name|Test
DECL|method|testDumping ()
specifier|public
name|void
name|testDumping
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
name|MiniDFSCluster
name|cluster
init|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|2
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|PrintStream
name|psBackup
init|=
name|System
operator|.
name|out
decl_stmt|;
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintStream
name|psOut
init|=
operator|new
name|PrintStream
argument_list|(
name|out
argument_list|)
decl_stmt|;
name|System
operator|.
name|setOut
argument_list|(
name|psOut
argument_list|)
expr_stmt|;
name|DumpTypedBytes
name|dumptb
init|=
operator|new
name|DumpTypedBytes
argument_list|(
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|Path
name|root
init|=
operator|new
name|Path
argument_list|(
literal|"/typedbytestest"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
name|OutputStreamWriter
name|writer
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|root
argument_list|,
literal|"test.txt"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|writer
operator|.
name|write
argument_list|(
literal|""
operator|+
operator|(
literal|10
operator|*
name|i
operator|)
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[
literal|1
index|]
decl_stmt|;
name|args
index|[
literal|0
index|]
operator|=
literal|"/typedbytestest"
expr_stmt|;
name|int
name|ret
init|=
name|dumptb
operator|.
name|run
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Return value != 0."
argument_list|,
literal|0
argument_list|,
name|ret
argument_list|)
expr_stmt|;
name|ByteArrayInputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|out
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
name|TypedBytesInput
name|tbinput
init|=
operator|new
name|TypedBytesInput
argument_list|(
operator|new
name|DataInputStream
argument_list|(
name|in
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|counter
init|=
literal|0
decl_stmt|;
name|Object
name|key
init|=
name|tbinput
operator|.
name|read
argument_list|()
decl_stmt|;
while|while
condition|(
name|key
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
name|Long
operator|.
name|class
argument_list|,
name|key
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
comment|// offset
name|Object
name|value
init|=
name|tbinput
operator|.
name|read
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|String
operator|.
name|class
argument_list|,
name|value
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Invalid output."
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
operator|%
literal|10
operator|==
literal|0
argument_list|)
expr_stmt|;
name|counter
operator|++
expr_stmt|;
name|key
operator|=
name|tbinput
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Wrong number of outputs."
argument_list|,
literal|100
argument_list|,
name|counter
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{       }
name|System
operator|.
name|setOut
argument_list|(
name|psBackup
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

