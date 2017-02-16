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
name|fs
operator|.
name|permission
operator|.
name|FsPermission
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

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|adl
operator|.
name|AdlConfKeys
operator|.
name|ADL_BLOCK_SIZE
import|;
end_import

begin_comment
comment|/**  * This class is responsible for testing local getFileStatus implementation  * to cover correct parsing of successful and error JSON response  * from the server.  * Adls GetFileStatus operation is in detail covered in  * org.apache.hadoop.fs.adl.live testing package.  */
end_comment

begin_class
DECL|class|TestGetFileStatus
specifier|public
class|class
name|TestGetFileStatus
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
name|TestGetFileStatus
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|getFileStatusReturnsAsExpected ()
specifier|public
name|void
name|getFileStatusReturnsAsExpected
parameter_list|()
throws|throws
name|URISyntaxException
throws|,
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
name|getGetFileStatusJSONResponse
argument_list|()
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
name|fileStatus
init|=
name|getMockAdlFileSystem
argument_list|()
operator|.
name|getFileStatus
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
name|assertTrue
argument_list|(
name|fileStatus
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"adl://"
operator|+
name|getMockServer
argument_list|()
operator|.
name|getHostName
argument_list|()
operator|+
literal|":"
operator|+
name|getMockServer
argument_list|()
operator|.
name|getPort
argument_list|()
operator|+
literal|"/test1/test2"
argument_list|,
name|fileStatus
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|4194304
argument_list|,
name|fileStatus
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ADL_BLOCK_SIZE
argument_list|,
name|fileStatus
operator|.
name|getBlockSize
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fileStatus
operator|.
name|getReplication
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|new
name|FsPermission
argument_list|(
literal|"777"
argument_list|)
argument_list|,
name|fileStatus
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"NotSupportYet"
argument_list|,
name|fileStatus
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"NotSupportYet"
argument_list|,
name|fileStatus
operator|.
name|getGroup
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|getFileStatusAclBit ()
specifier|public
name|void
name|getFileStatusAclBit
parameter_list|()
throws|throws
name|URISyntaxException
throws|,
name|IOException
block|{
comment|// With ACLBIT set to true
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
name|getGetFileStatusJSONResponse
argument_list|(
literal|true
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
name|fileStatus
init|=
name|getMockAdlFileSystem
argument_list|()
operator|.
name|getFileStatus
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
name|assertTrue
argument_list|(
name|fileStatus
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|fileStatus
operator|.
name|getPermission
argument_list|()
operator|.
name|getAclBit
argument_list|()
argument_list|)
expr_stmt|;
comment|// With ACLBIT set to false
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
name|getGetFileStatusJSONResponse
argument_list|(
literal|false
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
name|fileStatus
operator|=
name|getMockAdlFileSystem
argument_list|()
operator|.
name|getFileStatus
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
name|assertTrue
argument_list|(
name|fileStatus
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|fileStatus
operator|.
name|getPermission
argument_list|()
operator|.
name|getAclBit
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

