begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
package|;
end_package

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|ObjectMetadata
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|S3Object
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|S3ObjectInputStream
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
name|FSDataInputStream
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
name|junit
operator|.
name|Test
import|;
end_import

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
name|Date
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
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|any
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|times
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|verify
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_comment
comment|/**  * Uses mocks to check that the {@link S3ObjectInputStream} is closed when  * {@link org.apache.hadoop.fs.CanUnbuffer#unbuffer} is called. Unlike the  * other unbuffer tests, this specifically tests that the underlying S3 object  * stream is closed.  */
end_comment

begin_class
DECL|class|TestS3AUnbuffer
specifier|public
class|class
name|TestS3AUnbuffer
extends|extends
name|AbstractS3AMockTest
block|{
annotation|@
name|Test
DECL|method|testUnbuffer ()
specifier|public
name|void
name|testUnbuffer
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Create mock ObjectMetadata for getFileStatus()
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/file"
argument_list|)
decl_stmt|;
name|ObjectMetadata
name|meta
init|=
name|mock
argument_list|(
name|ObjectMetadata
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|meta
operator|.
name|getContentLength
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|1L
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|meta
operator|.
name|getLastModified
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|Date
argument_list|(
literal|2L
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|meta
operator|.
name|getETag
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"mock-etag"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|s3
operator|.
name|getObjectMetadata
argument_list|(
name|any
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|meta
argument_list|)
expr_stmt|;
comment|// Create mock S3ObjectInputStream and S3Object for open()
name|S3ObjectInputStream
name|objectStream
init|=
name|mock
argument_list|(
name|S3ObjectInputStream
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|objectStream
operator|.
name|read
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|S3Object
name|s3Object
init|=
name|mock
argument_list|(
name|S3Object
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|s3Object
operator|.
name|getObjectContent
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|objectStream
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|s3Object
operator|.
name|getObjectMetadata
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|meta
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|s3
operator|.
name|getObject
argument_list|(
name|any
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|s3Object
argument_list|)
expr_stmt|;
comment|// Call read and then unbuffer
name|FSDataInputStream
name|stream
init|=
name|fs
operator|.
name|open
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stream
operator|.
name|read
argument_list|(
operator|new
name|byte
index|[
literal|8
index|]
argument_list|)
argument_list|)
expr_stmt|;
comment|// mocks read 0 bytes
name|stream
operator|.
name|unbuffer
argument_list|()
expr_stmt|;
comment|// Verify that unbuffer closed the object stream
name|verify
argument_list|(
name|objectStream
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

