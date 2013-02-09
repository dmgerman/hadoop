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
name|IOException
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
name|Random
import|;
end_import

begin_comment
comment|/** Unit tests for BoundedByteArrayOutputStream */
end_comment

begin_class
DECL|class|TestBoundedByteArrayOutputStream
specifier|public
class|class
name|TestBoundedByteArrayOutputStream
extends|extends
name|TestCase
block|{
DECL|field|SIZE
specifier|private
specifier|static
specifier|final
name|int
name|SIZE
init|=
literal|1024
decl_stmt|;
DECL|field|INPUT
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|INPUT
init|=
operator|new
name|byte
index|[
name|SIZE
index|]
decl_stmt|;
static|static
block|{
operator|new
name|Random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|INPUT
argument_list|)
expr_stmt|;
block|}
DECL|method|testBoundedStream ()
specifier|public
name|void
name|testBoundedStream
parameter_list|()
throws|throws
name|IOException
block|{
name|BoundedByteArrayOutputStream
name|stream
init|=
operator|new
name|BoundedByteArrayOutputStream
argument_list|(
name|SIZE
argument_list|)
decl_stmt|;
comment|// Write to the stream, get the data back and check for contents
name|stream
operator|.
name|write
argument_list|(
name|INPUT
argument_list|,
literal|0
argument_list|,
name|SIZE
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Array Contents Mismatch"
argument_list|,
name|Arrays
operator|.
name|equals
argument_list|(
name|INPUT
argument_list|,
name|stream
operator|.
name|getBuffer
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Try writing beyond end of buffer. Should throw an exception
name|boolean
name|caughtException
init|=
literal|false
decl_stmt|;
try|try
block|{
name|stream
operator|.
name|write
argument_list|(
name|INPUT
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|caughtException
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Writing beyond limit did not throw an exception"
argument_list|,
name|caughtException
argument_list|)
expr_stmt|;
comment|//Reset the stream and try, should succeed
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Limit did not get reset correctly"
argument_list|,
operator|(
name|stream
operator|.
name|getLimit
argument_list|()
operator|==
name|SIZE
operator|)
argument_list|)
expr_stmt|;
name|stream
operator|.
name|write
argument_list|(
name|INPUT
argument_list|,
literal|0
argument_list|,
name|SIZE
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Array Contents Mismatch"
argument_list|,
name|Arrays
operator|.
name|equals
argument_list|(
name|INPUT
argument_list|,
name|stream
operator|.
name|getBuffer
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Try writing one more byte, should fail
name|caughtException
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|stream
operator|.
name|write
argument_list|(
name|INPUT
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|caughtException
operator|=
literal|true
expr_stmt|;
block|}
comment|// Reset the stream, but set a lower limit. Writing beyond
comment|// the limit should throw an exception
name|stream
operator|.
name|reset
argument_list|(
name|SIZE
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Limit did not get reset correctly"
argument_list|,
operator|(
name|stream
operator|.
name|getLimit
argument_list|()
operator|==
name|SIZE
operator|-
literal|1
operator|)
argument_list|)
expr_stmt|;
name|caughtException
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|stream
operator|.
name|write
argument_list|(
name|INPUT
argument_list|,
literal|0
argument_list|,
name|SIZE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|caughtException
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Writing beyond limit did not throw an exception"
argument_list|,
name|caughtException
argument_list|)
expr_stmt|;
block|}
DECL|class|ResettableBoundedByteArrayOutputStream
specifier|static
class|class
name|ResettableBoundedByteArrayOutputStream
extends|extends
name|BoundedByteArrayOutputStream
block|{
DECL|method|ResettableBoundedByteArrayOutputStream (int capacity)
specifier|public
name|ResettableBoundedByteArrayOutputStream
parameter_list|(
name|int
name|capacity
parameter_list|)
block|{
name|super
argument_list|(
name|capacity
argument_list|)
expr_stmt|;
block|}
DECL|method|resetBuffer (byte[] buf, int offset, int length)
specifier|public
name|void
name|resetBuffer
parameter_list|(
name|byte
index|[]
name|buf
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|super
operator|.
name|resetBuffer
argument_list|(
name|buf
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testResetBuffer ()
specifier|public
name|void
name|testResetBuffer
parameter_list|()
throws|throws
name|IOException
block|{
name|ResettableBoundedByteArrayOutputStream
name|stream
init|=
operator|new
name|ResettableBoundedByteArrayOutputStream
argument_list|(
name|SIZE
argument_list|)
decl_stmt|;
comment|// Write to the stream, get the data back and check for contents
name|stream
operator|.
name|write
argument_list|(
name|INPUT
argument_list|,
literal|0
argument_list|,
name|SIZE
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Array Contents Mismatch"
argument_list|,
name|Arrays
operator|.
name|equals
argument_list|(
name|INPUT
argument_list|,
name|stream
operator|.
name|getBuffer
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Try writing beyond end of buffer. Should throw an exception
name|boolean
name|caughtException
init|=
literal|false
decl_stmt|;
try|try
block|{
name|stream
operator|.
name|write
argument_list|(
name|INPUT
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|caughtException
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Writing beyond limit did not throw an exception"
argument_list|,
name|caughtException
argument_list|)
expr_stmt|;
comment|//Reset the stream and try, should succeed
name|byte
index|[]
name|newBuf
init|=
operator|new
name|byte
index|[
name|SIZE
index|]
decl_stmt|;
name|stream
operator|.
name|resetBuffer
argument_list|(
name|newBuf
argument_list|,
literal|0
argument_list|,
name|newBuf
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Limit did not get reset correctly"
argument_list|,
operator|(
name|stream
operator|.
name|getLimit
argument_list|()
operator|==
name|SIZE
operator|)
argument_list|)
expr_stmt|;
name|stream
operator|.
name|write
argument_list|(
name|INPUT
argument_list|,
literal|0
argument_list|,
name|SIZE
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Array Contents Mismatch"
argument_list|,
name|Arrays
operator|.
name|equals
argument_list|(
name|INPUT
argument_list|,
name|stream
operator|.
name|getBuffer
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Try writing one more byte, should fail
name|caughtException
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|stream
operator|.
name|write
argument_list|(
name|INPUT
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|caughtException
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Writing beyond limit did not throw an exception"
argument_list|,
name|caughtException
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

