begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import

begin_comment
comment|/**  * Testing the correctness of FileSystem.getFileBlockLocations.  */
end_comment

begin_class
DECL|class|TestGetFileBlockLocations
specifier|public
class|class
name|TestGetFileBlockLocations
extends|extends
name|TestCase
block|{
DECL|field|TEST_ROOT_DIR
specifier|private
specifier|static
name|String
name|TEST_ROOT_DIR
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp/testGetFileBlockLocations"
argument_list|)
decl_stmt|;
DECL|field|FileLength
specifier|private
specifier|static
specifier|final
name|int
name|FileLength
init|=
literal|4
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
comment|// 4MB
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|path
specifier|private
name|Path
name|path
decl_stmt|;
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
DECL|field|random
specifier|private
name|Random
name|random
decl_stmt|;
comment|/**    * @see TestCase#setUp()    */
annotation|@
name|Override
DECL|method|setUp ()
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|Path
name|rootPath
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|)
decl_stmt|;
name|path
operator|=
operator|new
name|Path
argument_list|(
name|rootPath
argument_list|,
literal|"TestGetFileBlockLocations"
argument_list|)
expr_stmt|;
name|fs
operator|=
name|rootPath
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|FSDataOutputStream
name|fsdos
init|=
name|fs
operator|.
name|create
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
while|while
condition|(
name|fsdos
operator|.
name|getPos
argument_list|()
operator|<
name|FileLength
condition|)
block|{
name|fsdos
operator|.
name|write
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
name|fsdos
operator|.
name|close
argument_list|()
expr_stmt|;
name|random
operator|=
operator|new
name|Random
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|oneTest (int offBegin, int offEnd, FileStatus status)
specifier|private
name|void
name|oneTest
parameter_list|(
name|int
name|offBegin
parameter_list|,
name|int
name|offEnd
parameter_list|,
name|FileStatus
name|status
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|offBegin
operator|>
name|offEnd
condition|)
block|{
name|int
name|tmp
init|=
name|offBegin
decl_stmt|;
name|offBegin
operator|=
name|offEnd
expr_stmt|;
name|offEnd
operator|=
name|tmp
expr_stmt|;
block|}
name|BlockLocation
index|[]
name|locations
init|=
name|fs
operator|.
name|getFileBlockLocations
argument_list|(
name|status
argument_list|,
name|offBegin
argument_list|,
name|offEnd
operator|-
name|offBegin
argument_list|)
decl_stmt|;
if|if
condition|(
name|offBegin
operator|<
name|status
operator|.
name|getLen
argument_list|()
condition|)
block|{
name|Arrays
operator|.
name|sort
argument_list|(
name|locations
argument_list|,
operator|new
name|Comparator
argument_list|<
name|BlockLocation
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|BlockLocation
name|arg0
parameter_list|,
name|BlockLocation
name|arg1
parameter_list|)
block|{
name|long
name|cmprv
init|=
name|arg0
operator|.
name|getOffset
argument_list|()
operator|-
name|arg1
operator|.
name|getOffset
argument_list|()
decl_stmt|;
if|if
condition|(
name|cmprv
operator|<
literal|0
condition|)
return|return
operator|-
literal|1
return|;
if|if
condition|(
name|cmprv
operator|>
literal|0
condition|)
return|return
literal|1
return|;
name|cmprv
operator|=
name|arg0
operator|.
name|getLength
argument_list|()
operator|-
name|arg1
operator|.
name|getLength
argument_list|()
expr_stmt|;
if|if
condition|(
name|cmprv
operator|<
literal|0
condition|)
return|return
operator|-
literal|1
return|;
if|if
condition|(
name|cmprv
operator|>
literal|0
condition|)
return|return
literal|1
return|;
return|return
literal|0
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|offBegin
operator|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|offBegin
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|offEnd
operator|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|offEnd
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|BlockLocation
name|first
init|=
name|locations
index|[
literal|0
index|]
decl_stmt|;
name|BlockLocation
name|last
init|=
name|locations
index|[
name|locations
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
name|assertTrue
argument_list|(
name|first
operator|.
name|getOffset
argument_list|()
operator|<=
name|offBegin
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|offEnd
operator|<=
name|last
operator|.
name|getOffset
argument_list|()
operator|+
name|last
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|locations
operator|.
name|length
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * @see TestCase#tearDown()    */
annotation|@
name|Override
DECL|method|tearDown ()
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|IOException
block|{
name|fs
operator|.
name|delete
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testFailureNegativeParameters ()
specifier|public
name|void
name|testFailureNegativeParameters
parameter_list|()
throws|throws
name|IOException
block|{
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
try|try
block|{
name|BlockLocation
index|[]
name|locations
init|=
name|fs
operator|.
name|getFileBlockLocations
argument_list|(
name|status
argument_list|,
operator|-
literal|1
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"Expecting exception being throw"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{      }
try|try
block|{
name|BlockLocation
index|[]
name|locations
init|=
name|fs
operator|.
name|getFileBlockLocations
argument_list|(
name|status
argument_list|,
literal|100
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"Expecting exception being throw"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{      }
block|}
DECL|method|testGetFileBlockLocations1 ()
specifier|public
name|void
name|testGetFileBlockLocations1
parameter_list|()
throws|throws
name|IOException
block|{
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|oneTest
argument_list|(
literal|0
argument_list|,
operator|(
name|int
operator|)
name|status
operator|.
name|getLen
argument_list|()
argument_list|,
name|status
argument_list|)
expr_stmt|;
name|oneTest
argument_list|(
literal|0
argument_list|,
operator|(
name|int
operator|)
name|status
operator|.
name|getLen
argument_list|()
operator|*
literal|2
argument_list|,
name|status
argument_list|)
expr_stmt|;
name|oneTest
argument_list|(
operator|(
name|int
operator|)
name|status
operator|.
name|getLen
argument_list|()
operator|*
literal|2
argument_list|,
operator|(
name|int
operator|)
name|status
operator|.
name|getLen
argument_list|()
operator|*
literal|4
argument_list|,
name|status
argument_list|)
expr_stmt|;
name|oneTest
argument_list|(
operator|(
name|int
operator|)
name|status
operator|.
name|getLen
argument_list|()
operator|/
literal|2
argument_list|,
operator|(
name|int
operator|)
name|status
operator|.
name|getLen
argument_list|()
operator|*
literal|3
argument_list|,
name|status
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
literal|10
condition|;
operator|++
name|i
control|)
block|{
name|oneTest
argument_list|(
operator|(
name|int
operator|)
name|status
operator|.
name|getLen
argument_list|()
operator|*
name|i
operator|/
literal|10
argument_list|,
operator|(
name|int
operator|)
name|status
operator|.
name|getLen
argument_list|()
operator|*
operator|(
name|i
operator|+
literal|1
operator|)
operator|/
literal|10
argument_list|,
name|status
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testGetFileBlockLocations2 ()
specifier|public
name|void
name|testGetFileBlockLocations2
parameter_list|()
throws|throws
name|IOException
block|{
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
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
literal|1000
condition|;
operator|++
name|i
control|)
block|{
name|int
name|offBegin
init|=
name|random
operator|.
name|nextInt
argument_list|(
call|(
name|int
call|)
argument_list|(
literal|2
operator|*
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|offEnd
init|=
name|random
operator|.
name|nextInt
argument_list|(
call|(
name|int
call|)
argument_list|(
literal|2
operator|*
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|oneTest
argument_list|(
name|offBegin
argument_list|,
name|offEnd
argument_list|,
name|status
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

