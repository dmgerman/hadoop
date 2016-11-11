begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.adl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|adl
package|;
end_package

begin_import
import|import
name|com
operator|.
name|squareup
operator|.
name|okhttp
operator|.
name|mockwebserver
operator|.
name|MockResponse
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
name|FileStatus
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
name|util
operator|.
name|Time
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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

begin_comment
comment|/**  * This class is responsible for testing local listStatus implementation to  * cover correct parsing of successful and error JSON response from the server.  * Adls ListStatus functionality is in detail covered in  * org.apache.hadoop.fs.adl.live testing package.  */
end_comment

begin_class
DECL|class|TestListStatus
specifier|public
class|class
name|TestListStatus
extends|extends
name|AdlMockWebServer
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestListStatus
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|listStatusReturnsAsExpected ()
specifier|public
name|void
name|listStatusReturnsAsExpected
parameter_list|()
throws|throws
name|IOException
block|{
name|getMockServer
argument_list|()
operator|.
name|enqueue
argument_list|(
operator|new
name|MockResponse
argument_list|()
operator|.
name|setResponseCode
argument_list|(
literal|200
argument_list|)
operator|.
name|setBody
argument_list|(
name|TestADLResponseData
operator|.
name|getListFileStatusJSONResponse
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|startTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|FileStatus
index|[]
name|ls
init|=
name|getMockAdlFileSystem
argument_list|()
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/test1/test2"
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|endTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Time : "
operator|+
operator|(
name|endTime
operator|-
name|startTime
operator|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ls
operator|.
name|length
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|getMockServer
argument_list|()
operator|.
name|enqueue
argument_list|(
operator|new
name|MockResponse
argument_list|()
operator|.
name|setResponseCode
argument_list|(
literal|200
argument_list|)
operator|.
name|setBody
argument_list|(
name|TestADLResponseData
operator|.
name|getListFileStatusJSONResponse
argument_list|(
literal|200
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|startTime
operator|=
name|Time
operator|.
name|monotonicNow
argument_list|()
expr_stmt|;
name|ls
operator|=
name|getMockAdlFileSystem
argument_list|()
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/test1/test2"
argument_list|)
argument_list|)
expr_stmt|;
name|endTime
operator|=
name|Time
operator|.
name|monotonicNow
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Time : "
operator|+
operator|(
name|endTime
operator|-
name|startTime
operator|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ls
operator|.
name|length
argument_list|,
literal|200
argument_list|)
expr_stmt|;
name|getMockServer
argument_list|()
operator|.
name|enqueue
argument_list|(
operator|new
name|MockResponse
argument_list|()
operator|.
name|setResponseCode
argument_list|(
literal|200
argument_list|)
operator|.
name|setBody
argument_list|(
name|TestADLResponseData
operator|.
name|getListFileStatusJSONResponse
argument_list|(
literal|2048
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|startTime
operator|=
name|Time
operator|.
name|monotonicNow
argument_list|()
expr_stmt|;
name|ls
operator|=
name|getMockAdlFileSystem
argument_list|()
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/test1/test2"
argument_list|)
argument_list|)
expr_stmt|;
name|endTime
operator|=
name|Time
operator|.
name|monotonicNow
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Time : "
operator|+
operator|(
name|endTime
operator|-
name|startTime
operator|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ls
operator|.
name|length
argument_list|,
literal|2048
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|listStatusOnFailure ()
specifier|public
name|void
name|listStatusOnFailure
parameter_list|()
throws|throws
name|IOException
block|{
name|getMockServer
argument_list|()
operator|.
name|enqueue
argument_list|(
operator|new
name|MockResponse
argument_list|()
operator|.
name|setResponseCode
argument_list|(
literal|403
argument_list|)
operator|.
name|setBody
argument_list|(
name|TestADLResponseData
operator|.
name|getErrorIllegalArgumentExceptionJSONResponse
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|FileStatus
index|[]
name|ls
init|=
literal|null
decl_stmt|;
name|long
name|startTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
try|try
block|{
name|ls
operator|=
name|getMockAdlFileSystem
argument_list|()
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/test1/test2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Invalid"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|long
name|endTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Time : "
operator|+
operator|(
name|endTime
operator|-
name|startTime
operator|)
argument_list|)
expr_stmt|;
comment|// SDK may increase number of retry attempts before error is propagated
comment|// to caller. Adding max 10 error responses in the queue to align with SDK.
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
name|getMockServer
argument_list|()
operator|.
name|enqueue
argument_list|(
operator|new
name|MockResponse
argument_list|()
operator|.
name|setResponseCode
argument_list|(
literal|500
argument_list|)
operator|.
name|setBody
argument_list|(
name|TestADLResponseData
operator|.
name|getErrorInternalServerExceptionJSONResponse
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|startTime
operator|=
name|Time
operator|.
name|monotonicNow
argument_list|()
expr_stmt|;
try|try
block|{
name|ls
operator|=
name|getMockAdlFileSystem
argument_list|()
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/test1/test2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Internal Server Error"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|endTime
operator|=
name|Time
operator|.
name|monotonicNow
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Time : "
operator|+
operator|(
name|endTime
operator|-
name|startTime
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

