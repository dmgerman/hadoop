begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.nfs.nfs3
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|nfs
operator|.
name|nfs3
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|fail
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
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|concurrent
operator|.
name|ConcurrentNavigableMap
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|DFSClient
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
name|HdfsConfiguration
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
name|hdfs
operator|.
name|client
operator|.
name|HdfsDataOutputStream
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
name|nfs
operator|.
name|nfs3
operator|.
name|OpenFileCtx
operator|.
name|COMMIT_STATUS
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
name|nfs
operator|.
name|nfs3
operator|.
name|OpenFileCtx
operator|.
name|CommitCtx
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
name|protocol
operator|.
name|HdfsFileStatus
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
name|server
operator|.
name|namenode
operator|.
name|NameNode
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
name|nfs
operator|.
name|nfs3
operator|.
name|FileHandle
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
name|nfs
operator|.
name|nfs3
operator|.
name|IdUserGroup
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
name|nfs
operator|.
name|nfs3
operator|.
name|Nfs3Constant
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
name|nfs
operator|.
name|nfs3
operator|.
name|Nfs3Constant
operator|.
name|WriteStableHow
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
name|nfs
operator|.
name|nfs3
operator|.
name|Nfs3FileAttributes
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
name|nfs
operator|.
name|nfs3
operator|.
name|request
operator|.
name|CREATE3Request
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
name|nfs
operator|.
name|nfs3
operator|.
name|request
operator|.
name|READ3Request
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
name|nfs
operator|.
name|nfs3
operator|.
name|request
operator|.
name|SetAttr3
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
name|nfs
operator|.
name|nfs3
operator|.
name|request
operator|.
name|WRITE3Request
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
name|nfs
operator|.
name|nfs3
operator|.
name|response
operator|.
name|CREATE3Response
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
name|nfs
operator|.
name|nfs3
operator|.
name|response
operator|.
name|READ3Response
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
name|oncrpc
operator|.
name|XDR
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
name|oncrpc
operator|.
name|security
operator|.
name|SecurityHandler
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
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_class
DECL|class|TestWrites
specifier|public
class|class
name|TestWrites
block|{
annotation|@
name|Test
DECL|method|testAlterWriteRequest ()
specifier|public
name|void
name|testAlterWriteRequest
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|len
init|=
literal|20
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|ByteBuffer
name|buffer
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|data
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
name|len
condition|;
name|i
operator|++
control|)
block|{
name|buffer
operator|.
name|put
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|flip
argument_list|()
expr_stmt|;
name|int
name|originalCount
init|=
name|buffer
operator|.
name|array
argument_list|()
operator|.
name|length
decl_stmt|;
name|WRITE3Request
name|request
init|=
operator|new
name|WRITE3Request
argument_list|(
operator|new
name|FileHandle
argument_list|()
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|,
name|WriteStableHow
operator|.
name|UNSTABLE
argument_list|,
name|buffer
argument_list|)
decl_stmt|;
name|WriteCtx
name|writeCtx1
init|=
operator|new
name|WriteCtx
argument_list|(
name|request
operator|.
name|getHandle
argument_list|()
argument_list|,
name|request
operator|.
name|getOffset
argument_list|()
argument_list|,
name|request
operator|.
name|getCount
argument_list|()
argument_list|,
name|WriteCtx
operator|.
name|INVALID_ORIGINAL_COUNT
argument_list|,
name|request
operator|.
name|getStableHow
argument_list|()
argument_list|,
name|request
operator|.
name|getData
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|,
name|WriteCtx
operator|.
name|DataState
operator|.
name|NO_DUMP
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|writeCtx1
operator|.
name|getData
argument_list|()
operator|.
name|array
argument_list|()
operator|.
name|length
operator|==
name|originalCount
argument_list|)
expr_stmt|;
comment|// Now change the write request
name|OpenFileCtx
operator|.
name|alterWriteRequest
argument_list|(
name|request
argument_list|,
literal|12
argument_list|)
expr_stmt|;
name|WriteCtx
name|writeCtx2
init|=
operator|new
name|WriteCtx
argument_list|(
name|request
operator|.
name|getHandle
argument_list|()
argument_list|,
name|request
operator|.
name|getOffset
argument_list|()
argument_list|,
name|request
operator|.
name|getCount
argument_list|()
argument_list|,
name|originalCount
argument_list|,
name|request
operator|.
name|getStableHow
argument_list|()
argument_list|,
name|request
operator|.
name|getData
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|,
name|WriteCtx
operator|.
name|DataState
operator|.
name|NO_DUMP
argument_list|)
decl_stmt|;
name|ByteBuffer
name|appendedData
init|=
name|writeCtx2
operator|.
name|getData
argument_list|()
decl_stmt|;
name|int
name|position
init|=
name|appendedData
operator|.
name|position
argument_list|()
decl_stmt|;
name|int
name|limit
init|=
name|appendedData
operator|.
name|limit
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|position
operator|==
literal|12
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|limit
operator|-
name|position
operator|==
literal|8
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|appendedData
operator|.
name|get
argument_list|(
name|position
argument_list|)
operator|==
operator|(
name|byte
operator|)
literal|12
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|appendedData
operator|.
name|get
argument_list|(
name|position
operator|+
literal|1
argument_list|)
operator|==
operator|(
name|byte
operator|)
literal|13
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|appendedData
operator|.
name|get
argument_list|(
name|position
operator|+
literal|2
argument_list|)
operator|==
operator|(
name|byte
operator|)
literal|14
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|appendedData
operator|.
name|get
argument_list|(
name|position
operator|+
literal|7
argument_list|)
operator|==
operator|(
name|byte
operator|)
literal|19
argument_list|)
expr_stmt|;
comment|// Test current file write offset is at boundaries
name|buffer
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|request
operator|=
operator|new
name|WRITE3Request
argument_list|(
operator|new
name|FileHandle
argument_list|()
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|,
name|WriteStableHow
operator|.
name|UNSTABLE
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
name|OpenFileCtx
operator|.
name|alterWriteRequest
argument_list|(
name|request
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|WriteCtx
name|writeCtx3
init|=
operator|new
name|WriteCtx
argument_list|(
name|request
operator|.
name|getHandle
argument_list|()
argument_list|,
name|request
operator|.
name|getOffset
argument_list|()
argument_list|,
name|request
operator|.
name|getCount
argument_list|()
argument_list|,
name|originalCount
argument_list|,
name|request
operator|.
name|getStableHow
argument_list|()
argument_list|,
name|request
operator|.
name|getData
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|,
name|WriteCtx
operator|.
name|DataState
operator|.
name|NO_DUMP
argument_list|)
decl_stmt|;
name|appendedData
operator|=
name|writeCtx3
operator|.
name|getData
argument_list|()
expr_stmt|;
name|position
operator|=
name|appendedData
operator|.
name|position
argument_list|()
expr_stmt|;
name|limit
operator|=
name|appendedData
operator|.
name|limit
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|position
operator|==
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|limit
operator|-
name|position
operator|==
literal|19
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|appendedData
operator|.
name|get
argument_list|(
name|position
argument_list|)
operator|==
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|appendedData
operator|.
name|get
argument_list|(
name|position
operator|+
literal|18
argument_list|)
operator|==
operator|(
name|byte
operator|)
literal|19
argument_list|)
expr_stmt|;
comment|// Reset buffer position before test another boundary
name|buffer
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|request
operator|=
operator|new
name|WRITE3Request
argument_list|(
operator|new
name|FileHandle
argument_list|()
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|,
name|WriteStableHow
operator|.
name|UNSTABLE
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
name|OpenFileCtx
operator|.
name|alterWriteRequest
argument_list|(
name|request
argument_list|,
literal|19
argument_list|)
expr_stmt|;
name|WriteCtx
name|writeCtx4
init|=
operator|new
name|WriteCtx
argument_list|(
name|request
operator|.
name|getHandle
argument_list|()
argument_list|,
name|request
operator|.
name|getOffset
argument_list|()
argument_list|,
name|request
operator|.
name|getCount
argument_list|()
argument_list|,
name|originalCount
argument_list|,
name|request
operator|.
name|getStableHow
argument_list|()
argument_list|,
name|request
operator|.
name|getData
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|,
name|WriteCtx
operator|.
name|DataState
operator|.
name|NO_DUMP
argument_list|)
decl_stmt|;
name|appendedData
operator|=
name|writeCtx4
operator|.
name|getData
argument_list|()
expr_stmt|;
name|position
operator|=
name|appendedData
operator|.
name|position
argument_list|()
expr_stmt|;
name|limit
operator|=
name|appendedData
operator|.
name|limit
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|position
operator|==
literal|19
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|limit
operator|-
name|position
operator|==
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|appendedData
operator|.
name|get
argument_list|(
name|position
argument_list|)
operator|==
operator|(
name|byte
operator|)
literal|19
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|// Validate all the commit check return codes OpenFileCtx.COMMIT_STATUS, which
comment|// includes COMMIT_FINISHED, COMMIT_WAIT, COMMIT_INACTIVE_CTX,
comment|// COMMIT_INACTIVE_WITH_PENDING_WRITE, COMMIT_ERROR, and COMMIT_DO_SYNC.
DECL|method|testCheckCommit ()
specifier|public
name|void
name|testCheckCommit
parameter_list|()
throws|throws
name|IOException
block|{
name|DFSClient
name|dfsClient
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|DFSClient
operator|.
name|class
argument_list|)
decl_stmt|;
name|Nfs3FileAttributes
name|attr
init|=
operator|new
name|Nfs3FileAttributes
argument_list|()
decl_stmt|;
name|HdfsDataOutputStream
name|fos
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|HdfsDataOutputStream
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|fos
operator|.
name|getPos
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|(
name|long
operator|)
literal|0
argument_list|)
expr_stmt|;
name|OpenFileCtx
name|ctx
init|=
operator|new
name|OpenFileCtx
argument_list|(
name|fos
argument_list|,
name|attr
argument_list|,
literal|"/dumpFilePath"
argument_list|,
name|dfsClient
argument_list|,
operator|new
name|IdUserGroup
argument_list|()
argument_list|)
decl_stmt|;
name|COMMIT_STATUS
name|ret
decl_stmt|;
comment|// Test inactive open file context
name|ctx
operator|.
name|setActiveStatusForTest
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|ret
operator|=
name|ctx
operator|.
name|checkCommit
argument_list|(
name|dfsClient
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|,
name|attr
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|ret
operator|==
name|COMMIT_STATUS
operator|.
name|COMMIT_INACTIVE_CTX
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|getPendingWritesForTest
argument_list|()
operator|.
name|put
argument_list|(
operator|new
name|OffsetRange
argument_list|(
literal|5
argument_list|,
literal|10
argument_list|)
argument_list|,
operator|new
name|WriteCtx
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|ret
operator|=
name|ctx
operator|.
name|checkCommit
argument_list|(
name|dfsClient
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|,
name|attr
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|ret
operator|==
name|COMMIT_STATUS
operator|.
name|COMMIT_INACTIVE_WITH_PENDING_WRITE
argument_list|)
expr_stmt|;
comment|// Test request with non zero commit offset
name|ctx
operator|.
name|setActiveStatusForTest
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|fos
operator|.
name|getPos
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|(
name|long
operator|)
literal|10
argument_list|)
expr_stmt|;
name|ret
operator|=
name|ctx
operator|.
name|checkCommit
argument_list|(
name|dfsClient
argument_list|,
literal|5
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|,
name|attr
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|ret
operator|==
name|COMMIT_STATUS
operator|.
name|COMMIT_DO_SYNC
argument_list|)
expr_stmt|;
name|ret
operator|=
name|ctx
operator|.
name|checkCommit
argument_list|(
name|dfsClient
argument_list|,
literal|10
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|,
name|attr
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|ret
operator|==
name|COMMIT_STATUS
operator|.
name|COMMIT_DO_SYNC
argument_list|)
expr_stmt|;
name|ConcurrentNavigableMap
argument_list|<
name|Long
argument_list|,
name|CommitCtx
argument_list|>
name|commits
init|=
name|ctx
operator|.
name|getPendingCommitsForTest
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|commits
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|ret
operator|=
name|ctx
operator|.
name|checkCommit
argument_list|(
name|dfsClient
argument_list|,
literal|11
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|,
name|attr
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|ret
operator|==
name|COMMIT_STATUS
operator|.
name|COMMIT_WAIT
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|commits
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|long
name|key
init|=
name|commits
operator|.
name|firstKey
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|key
operator|==
literal|11
argument_list|)
expr_stmt|;
comment|// Test request with zero commit offset
name|commits
operator|.
name|remove
argument_list|(
operator|new
name|Long
argument_list|(
literal|11
argument_list|)
argument_list|)
expr_stmt|;
comment|// There is one pending write [5,10]
name|ret
operator|=
name|ctx
operator|.
name|checkCommit
argument_list|(
name|dfsClient
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|,
name|attr
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|ret
operator|==
name|COMMIT_STATUS
operator|.
name|COMMIT_WAIT
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|commits
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|key
operator|=
name|commits
operator|.
name|firstKey
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|key
operator|==
literal|9
argument_list|)
expr_stmt|;
comment|// Empty pending writes
name|ctx
operator|.
name|getPendingWritesForTest
argument_list|()
operator|.
name|remove
argument_list|(
operator|new
name|OffsetRange
argument_list|(
literal|5
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|ret
operator|=
name|ctx
operator|.
name|checkCommit
argument_list|(
name|dfsClient
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|,
name|attr
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|ret
operator|==
name|COMMIT_STATUS
operator|.
name|COMMIT_FINISHED
argument_list|)
expr_stmt|;
block|}
DECL|method|waitWrite (RpcProgramNfs3 nfsd, FileHandle handle, int maxWaitTime)
specifier|private
name|void
name|waitWrite
parameter_list|(
name|RpcProgramNfs3
name|nfsd
parameter_list|,
name|FileHandle
name|handle
parameter_list|,
name|int
name|maxWaitTime
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|int
name|waitedTime
init|=
literal|0
decl_stmt|;
name|OpenFileCtx
name|ctx
init|=
name|nfsd
operator|.
name|getWriteManager
argument_list|()
operator|.
name|getOpenFileCtxCache
argument_list|()
operator|.
name|get
argument_list|(
name|handle
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|ctx
operator|!=
literal|null
argument_list|)
expr_stmt|;
do|do
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
name|waitedTime
operator|+=
literal|3000
expr_stmt|;
if|if
condition|(
name|ctx
operator|.
name|getPendingWritesForTest
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return;
block|}
block|}
do|while
condition|(
name|waitedTime
operator|<
name|maxWaitTime
condition|)
do|;
name|fail
argument_list|(
literal|"Write can't finish."
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWriteStableHow ()
specifier|public
name|void
name|testWriteStableHow
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|HdfsConfiguration
name|config
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|DFSClient
name|client
init|=
literal|null
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
name|RpcProgramNfs3
name|nfsd
decl_stmt|;
name|SecurityHandler
name|securityHandler
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|SecurityHandler
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|securityHandler
operator|.
name|getUser
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|config
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|1
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|client
operator|=
operator|new
name|DFSClient
argument_list|(
name|NameNode
operator|.
name|getAddress
argument_list|(
name|config
argument_list|)
argument_list|,
name|config
argument_list|)
expr_stmt|;
comment|// Use emphral port in case tests are running in parallel
name|config
operator|.
name|setInt
argument_list|(
literal|"nfs3.mountd.port"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|config
operator|.
name|setInt
argument_list|(
literal|"nfs3.server.port"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Start nfs
name|Nfs3
name|nfs3
init|=
operator|new
name|Nfs3
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|nfs3
operator|.
name|startServiceInternal
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|nfsd
operator|=
operator|(
name|RpcProgramNfs3
operator|)
name|nfs3
operator|.
name|getRpcProgram
argument_list|()
expr_stmt|;
name|HdfsFileStatus
name|status
init|=
name|client
operator|.
name|getFileInfo
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|FileHandle
name|rootHandle
init|=
operator|new
name|FileHandle
argument_list|(
name|status
operator|.
name|getFileId
argument_list|()
argument_list|)
decl_stmt|;
comment|// Create file1
name|CREATE3Request
name|createReq
init|=
operator|new
name|CREATE3Request
argument_list|(
name|rootHandle
argument_list|,
literal|"file1"
argument_list|,
name|Nfs3Constant
operator|.
name|CREATE_UNCHECKED
argument_list|,
operator|new
name|SetAttr3
argument_list|()
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|XDR
name|createXdr
init|=
operator|new
name|XDR
argument_list|()
decl_stmt|;
name|createReq
operator|.
name|serialize
argument_list|(
name|createXdr
argument_list|)
expr_stmt|;
name|CREATE3Response
name|createRsp
init|=
name|nfsd
operator|.
name|create
argument_list|(
name|createXdr
operator|.
name|asReadOnlyWrap
argument_list|()
argument_list|,
name|securityHandler
argument_list|,
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
argument_list|)
decl_stmt|;
name|FileHandle
name|handle
init|=
name|createRsp
operator|.
name|getObjHandle
argument_list|()
decl_stmt|;
comment|// Test DATA_SYNC
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|10
index|]
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|buffer
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|i
expr_stmt|;
block|}
name|WRITE3Request
name|writeReq
init|=
operator|new
name|WRITE3Request
argument_list|(
name|handle
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
name|WriteStableHow
operator|.
name|DATA_SYNC
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|buffer
argument_list|)
argument_list|)
decl_stmt|;
name|XDR
name|writeXdr
init|=
operator|new
name|XDR
argument_list|()
decl_stmt|;
name|writeReq
operator|.
name|serialize
argument_list|(
name|writeXdr
argument_list|)
expr_stmt|;
name|nfsd
operator|.
name|write
argument_list|(
name|writeXdr
operator|.
name|asReadOnlyWrap
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|,
name|securityHandler
argument_list|,
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
argument_list|)
expr_stmt|;
name|waitWrite
argument_list|(
name|nfsd
argument_list|,
name|handle
argument_list|,
literal|60000
argument_list|)
expr_stmt|;
comment|// Readback
name|READ3Request
name|readReq
init|=
operator|new
name|READ3Request
argument_list|(
name|handle
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|XDR
name|readXdr
init|=
operator|new
name|XDR
argument_list|()
decl_stmt|;
name|readReq
operator|.
name|serialize
argument_list|(
name|readXdr
argument_list|)
expr_stmt|;
name|READ3Response
name|readRsp
init|=
name|nfsd
operator|.
name|read
argument_list|(
name|readXdr
operator|.
name|asReadOnlyWrap
argument_list|()
argument_list|,
name|securityHandler
argument_list|,
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|buffer
argument_list|,
name|readRsp
operator|.
name|getData
argument_list|()
operator|.
name|array
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test FILE_SYNC
comment|// Create file2
name|CREATE3Request
name|createReq2
init|=
operator|new
name|CREATE3Request
argument_list|(
name|rootHandle
argument_list|,
literal|"file2"
argument_list|,
name|Nfs3Constant
operator|.
name|CREATE_UNCHECKED
argument_list|,
operator|new
name|SetAttr3
argument_list|()
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|XDR
name|createXdr2
init|=
operator|new
name|XDR
argument_list|()
decl_stmt|;
name|createReq2
operator|.
name|serialize
argument_list|(
name|createXdr2
argument_list|)
expr_stmt|;
name|CREATE3Response
name|createRsp2
init|=
name|nfsd
operator|.
name|create
argument_list|(
name|createXdr2
operator|.
name|asReadOnlyWrap
argument_list|()
argument_list|,
name|securityHandler
argument_list|,
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
argument_list|)
decl_stmt|;
name|FileHandle
name|handle2
init|=
name|createRsp2
operator|.
name|getObjHandle
argument_list|()
decl_stmt|;
name|WRITE3Request
name|writeReq2
init|=
operator|new
name|WRITE3Request
argument_list|(
name|handle2
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
name|WriteStableHow
operator|.
name|FILE_SYNC
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|buffer
argument_list|)
argument_list|)
decl_stmt|;
name|XDR
name|writeXdr2
init|=
operator|new
name|XDR
argument_list|()
decl_stmt|;
name|writeReq2
operator|.
name|serialize
argument_list|(
name|writeXdr2
argument_list|)
expr_stmt|;
name|nfsd
operator|.
name|write
argument_list|(
name|writeXdr2
operator|.
name|asReadOnlyWrap
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|,
name|securityHandler
argument_list|,
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
argument_list|)
expr_stmt|;
name|waitWrite
argument_list|(
name|nfsd
argument_list|,
name|handle2
argument_list|,
literal|60000
argument_list|)
expr_stmt|;
comment|// Readback
name|READ3Request
name|readReq2
init|=
operator|new
name|READ3Request
argument_list|(
name|handle2
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|XDR
name|readXdr2
init|=
operator|new
name|XDR
argument_list|()
decl_stmt|;
name|readReq2
operator|.
name|serialize
argument_list|(
name|readXdr2
argument_list|)
expr_stmt|;
name|READ3Response
name|readRsp2
init|=
name|nfsd
operator|.
name|read
argument_list|(
name|readXdr2
operator|.
name|asReadOnlyWrap
argument_list|()
argument_list|,
name|securityHandler
argument_list|,
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|buffer
argument_list|,
name|readRsp2
operator|.
name|getData
argument_list|()
operator|.
name|array
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// FILE_SYNC should sync the file size
name|status
operator|=
name|client
operator|.
name|getFileInfo
argument_list|(
literal|"/file2"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|status
operator|.
name|getLen
argument_list|()
operator|==
literal|10
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

