begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.blockmanagement
package|package
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
name|blockmanagement
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_MAX_FULL_BLOCK_REPORT_LEASES
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
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_FULL_BLOCK_REPORT_LEASE_LENGTH_MS
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Joiner
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Supplier
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Uninterruptibles
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
name|protocol
operator|.
name|DatanodeID
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
name|protocol
operator|.
name|BlockReportContext
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
name|test
operator|.
name|GenericTestUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
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
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|HashSet
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
name|Semaphore
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
name|TimeUnit
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
name|atomic
operator|.
name|AtomicReference
import|;
end_import

begin_class
DECL|class|TestBlockReportRateLimiting
specifier|public
class|class
name|TestBlockReportRateLimiting
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestBlockReportRateLimiting
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|setFailure (AtomicReference<String> failure, String what)
specifier|private
specifier|static
name|void
name|setFailure
parameter_list|(
name|AtomicReference
argument_list|<
name|String
argument_list|>
name|failure
parameter_list|,
name|String
name|what
parameter_list|)
block|{
name|failure
operator|.
name|compareAndSet
argument_list|(
literal|""
argument_list|,
name|what
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Test error: "
operator|+
name|what
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|restoreNormalBlockManagerFaultInjector ()
specifier|public
name|void
name|restoreNormalBlockManagerFaultInjector
parameter_list|()
block|{
name|BlockManagerFaultInjector
operator|.
name|instance
operator|=
operator|new
name|BlockManagerFaultInjector
argument_list|()
expr_stmt|;
block|}
annotation|@
name|BeforeClass
DECL|method|raiseBlockManagerLogLevels ()
specifier|public
specifier|static
name|void
name|raiseBlockManagerLogLevels
parameter_list|()
block|{
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|BlockManager
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|BlockReportLeaseManager
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|180000
argument_list|)
DECL|method|testRateLimitingDuringDataNodeStartup ()
specifier|public
name|void
name|testRateLimitingDuringDataNodeStartup
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
name|conf
operator|.
name|setInt
argument_list|(
name|DFS_NAMENODE_MAX_FULL_BLOCK_REPORT_LEASES
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFS_NAMENODE_FULL_BLOCK_REPORT_LEASE_LENGTH_MS
argument_list|,
literal|20L
operator|*
literal|60L
operator|*
literal|1000L
argument_list|)
expr_stmt|;
specifier|final
name|Semaphore
name|fbrSem
init|=
operator|new
name|Semaphore
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|HashSet
argument_list|<
name|DatanodeID
argument_list|>
name|expectedFbrDns
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|HashSet
argument_list|<
name|DatanodeID
argument_list|>
name|fbrDns
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|AtomicReference
argument_list|<
name|String
argument_list|>
name|failure
init|=
operator|new
name|AtomicReference
argument_list|<
name|String
argument_list|>
argument_list|(
literal|""
argument_list|)
decl_stmt|;
specifier|final
name|BlockManagerFaultInjector
name|injector
init|=
operator|new
name|BlockManagerFaultInjector
argument_list|()
block|{
specifier|private
name|int
name|numLeases
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|incomingBlockReportRpc
parameter_list|(
name|DatanodeID
name|nodeID
parameter_list|,
name|BlockReportContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Incoming full block report from "
operator|+
name|nodeID
operator|+
literal|".  Lease ID = 0x"
operator|+
name|Long
operator|.
name|toHexString
argument_list|(
name|context
operator|.
name|getLeaseId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|context
operator|.
name|getLeaseId
argument_list|()
operator|==
literal|0
condition|)
block|{
name|setFailure
argument_list|(
name|failure
argument_list|,
literal|"Got unexpected rate-limiting-"
operator|+
literal|"bypassing full block report RPC from "
operator|+
name|nodeID
argument_list|)
expr_stmt|;
block|}
name|fbrSem
operator|.
name|acquireUninterruptibly
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|fbrDns
operator|.
name|add
argument_list|(
name|nodeID
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|expectedFbrDns
operator|.
name|remove
argument_list|(
name|nodeID
argument_list|)
condition|)
block|{
name|setFailure
argument_list|(
name|failure
argument_list|,
literal|"Got unexpected full block report "
operator|+
literal|"RPC from "
operator|+
name|nodeID
operator|+
literal|".  expectedFbrDns = "
operator|+
name|Joiner
operator|.
name|on
argument_list|(
literal|", "
argument_list|)
operator|.
name|join
argument_list|(
name|expectedFbrDns
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Proceeding with full block report from "
operator|+
name|nodeID
operator|+
literal|".  Lease ID = 0x"
operator|+
name|Long
operator|.
name|toHexString
argument_list|(
name|context
operator|.
name|getLeaseId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|requestBlockReportLease
parameter_list|(
name|DatanodeDescriptor
name|node
parameter_list|,
name|long
name|leaseId
parameter_list|)
block|{
if|if
condition|(
name|leaseId
operator|==
literal|0
condition|)
block|{
return|return;
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
name|numLeases
operator|++
expr_stmt|;
name|expectedFbrDns
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"requestBlockReportLease(node="
operator|+
name|node
operator|+
literal|", leaseId=0x"
operator|+
name|Long
operator|.
name|toHexString
argument_list|(
name|leaseId
argument_list|)
operator|+
literal|").  "
operator|+
literal|"expectedFbrDns = "
operator|+
name|Joiner
operator|.
name|on
argument_list|(
literal|", "
argument_list|)
operator|.
name|join
argument_list|(
name|expectedFbrDns
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|numLeases
operator|>
literal|1
condition|)
block|{
name|setFailure
argument_list|(
name|failure
argument_list|,
literal|"More than 1 lease was issued at once."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeBlockReportLease
parameter_list|(
name|DatanodeDescriptor
name|node
parameter_list|,
name|long
name|leaseId
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"removeBlockReportLease(node="
operator|+
name|node
operator|+
literal|", leaseId=0x"
operator|+
name|Long
operator|.
name|toHexString
argument_list|(
name|leaseId
argument_list|)
operator|+
literal|")"
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|numLeases
operator|--
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|BlockManagerFaultInjector
operator|.
name|instance
operator|=
name|injector
expr_stmt|;
specifier|final
name|int
name|NUM_DATANODES
init|=
literal|5
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
name|NUM_DATANODES
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|n
init|=
literal|1
init|;
name|n
operator|<=
name|NUM_DATANODES
condition|;
name|n
operator|++
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for "
operator|+
name|n
operator|+
literal|" datanode(s) to report in."
argument_list|)
expr_stmt|;
name|fbrSem
operator|.
name|release
argument_list|()
expr_stmt|;
name|Uninterruptibles
operator|.
name|sleepUninterruptibly
argument_list|(
literal|20
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
specifier|final
name|int
name|currentN
init|=
name|n
decl_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
operator|new
name|Supplier
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
synchronized|synchronized
init|(
name|injector
init|)
block|{
if|if
condition|(
name|fbrDns
operator|.
name|size
argument_list|()
operator|>
name|currentN
condition|)
block|{
name|setFailure
argument_list|(
name|failure
argument_list|,
literal|"Expected at most "
operator|+
name|currentN
operator|+
literal|" datanodes to have sent a block report, but actually "
operator|+
name|fbrDns
operator|.
name|size
argument_list|()
operator|+
literal|" have."
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|fbrDns
operator|.
name|size
argument_list|()
operator|>=
name|currentN
operator|)
return|;
block|}
block|}
block|}
argument_list|,
literal|25
argument_list|,
literal|50000
argument_list|)
expr_stmt|;
block|}
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|failure
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Start a 2-node cluster with only one block report lease.  When the    * first datanode gets a lease, kill it.  Then wait for the lease to    * expire, and the second datanode to send a full block report.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|180000
argument_list|)
DECL|method|testLeaseExpiration ()
specifier|public
name|void
name|testLeaseExpiration
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
name|conf
operator|.
name|setInt
argument_list|(
name|DFS_NAMENODE_MAX_FULL_BLOCK_REPORT_LEASES
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFS_NAMENODE_FULL_BLOCK_REPORT_LEASE_LENGTH_MS
argument_list|,
literal|100L
argument_list|)
expr_stmt|;
specifier|final
name|Semaphore
name|gotFbrSem
init|=
operator|new
name|Semaphore
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|AtomicReference
argument_list|<
name|String
argument_list|>
name|failure
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|AtomicReference
argument_list|<
name|MiniDFSCluster
argument_list|>
name|cluster
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|AtomicReference
argument_list|<
name|String
argument_list|>
name|datanodeToStop
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|BlockManagerFaultInjector
name|injector
init|=
operator|new
name|BlockManagerFaultInjector
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|incomingBlockReportRpc
parameter_list|(
name|DatanodeID
name|nodeID
parameter_list|,
name|BlockReportContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|context
operator|.
name|getLeaseId
argument_list|()
operator|==
literal|0
condition|)
block|{
name|setFailure
argument_list|(
name|failure
argument_list|,
literal|"Got unexpected rate-limiting-"
operator|+
literal|"bypassing full block report RPC from "
operator|+
name|nodeID
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nodeID
operator|.
name|getXferAddr
argument_list|()
operator|.
name|equals
argument_list|(
name|datanodeToStop
operator|.
name|get
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Injecting failure into block "
operator|+
literal|"report RPC for "
operator|+
name|nodeID
argument_list|)
throw|;
block|}
name|gotFbrSem
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|requestBlockReportLease
parameter_list|(
name|DatanodeDescriptor
name|node
parameter_list|,
name|long
name|leaseId
parameter_list|)
block|{
if|if
condition|(
name|leaseId
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|datanodeToStop
operator|.
name|compareAndSet
argument_list|(
literal|null
argument_list|,
name|node
operator|.
name|getXferAddr
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeBlockReportLease
parameter_list|(
name|DatanodeDescriptor
name|node
parameter_list|,
name|long
name|leaseId
parameter_list|)
block|{       }
block|}
decl_stmt|;
try|try
block|{
name|BlockManagerFaultInjector
operator|.
name|instance
operator|=
name|injector
expr_stmt|;
name|cluster
operator|.
name|set
argument_list|(
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
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|get
argument_list|()
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|cluster
operator|.
name|get
argument_list|()
operator|.
name|stopDataNode
argument_list|(
name|datanodeToStop
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|gotFbrSem
operator|.
name|acquire
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|failure
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cluster
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|get
argument_list|()
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

