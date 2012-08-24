begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.qjournal.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|qjournal
operator|.
name|client
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
name|*
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
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|LogFactory
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
name|qjournal
operator|.
name|MiniJournalCluster
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
name|qjournal
operator|.
name|client
operator|.
name|AsyncLogger
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
name|qjournal
operator|.
name|client
operator|.
name|AsyncLoggerSet
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
name|qjournal
operator|.
name|client
operator|.
name|QuorumJournalManager
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
name|NamespaceInfo
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

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|invocation
operator|.
name|InvocationOnMock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|stubbing
operator|.
name|Answer
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
name|collect
operator|.
name|Lists
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
name|Futures
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
name|ListenableFuture
import|;
end_import

begin_class
DECL|class|TestEpochsAreUnique
specifier|public
class|class
name|TestEpochsAreUnique
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
name|TestEpochsAreUnique
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|JID
specifier|private
specifier|static
specifier|final
name|String
name|JID
init|=
literal|"testEpochsAreUnique-jid"
decl_stmt|;
DECL|field|FAKE_NSINFO
specifier|private
specifier|static
specifier|final
name|NamespaceInfo
name|FAKE_NSINFO
init|=
operator|new
name|NamespaceInfo
argument_list|(
literal|12345
argument_list|,
literal|"mycluster"
argument_list|,
literal|"my-bp"
argument_list|,
literal|0L
argument_list|)
decl_stmt|;
DECL|field|r
specifier|private
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
annotation|@
name|Test
DECL|method|testSingleThreaded ()
specifier|public
name|void
name|testSingleThreaded
parameter_list|()
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
name|MiniJournalCluster
name|cluster
init|=
operator|new
name|MiniJournalCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|URI
name|uri
init|=
name|cluster
operator|.
name|getQuorumJournalURI
argument_list|(
name|JID
argument_list|)
decl_stmt|;
name|QuorumJournalManager
name|qjm
init|=
operator|new
name|QuorumJournalManager
argument_list|(
name|conf
argument_list|,
name|uri
argument_list|,
name|FAKE_NSINFO
argument_list|)
decl_stmt|;
name|qjm
operator|.
name|format
argument_list|(
name|FAKE_NSINFO
argument_list|)
expr_stmt|;
try|try
block|{
comment|// With no failures or contention, epochs should increase one-by-one
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|AsyncLoggerSet
name|als
init|=
operator|new
name|AsyncLoggerSet
argument_list|(
name|QuorumJournalManager
operator|.
name|createLoggers
argument_list|(
name|conf
argument_list|,
name|uri
argument_list|,
name|FAKE_NSINFO
argument_list|,
name|IPCLoggerChannel
operator|.
name|FACTORY
argument_list|)
argument_list|)
decl_stmt|;
name|als
operator|.
name|createNewUniqueEpoch
argument_list|(
name|FAKE_NSINFO
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|i
operator|+
literal|1
argument_list|,
name|als
operator|.
name|getEpoch
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|long
name|prevEpoch
init|=
literal|5
decl_stmt|;
comment|// With some failures injected, it should still always increase, perhaps
comment|// skipping some
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|AsyncLoggerSet
name|als
init|=
operator|new
name|AsyncLoggerSet
argument_list|(
name|makeFaulty
argument_list|(
name|QuorumJournalManager
operator|.
name|createLoggers
argument_list|(
name|conf
argument_list|,
name|uri
argument_list|,
name|FAKE_NSINFO
argument_list|,
name|IPCLoggerChannel
operator|.
name|FACTORY
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|newEpoch
init|=
operator|-
literal|1
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|als
operator|.
name|createNewUniqueEpoch
argument_list|(
name|FAKE_NSINFO
argument_list|)
expr_stmt|;
name|newEpoch
operator|=
name|als
operator|.
name|getEpoch
argument_list|()
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// It's OK to fail to create an epoch, since we randomly inject
comment|// faults. It's possible we'll inject faults in too many of the
comment|// underlying nodes, and a failure is expected in that case
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Created epoch "
operator|+
name|newEpoch
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"New epoch "
operator|+
name|newEpoch
operator|+
literal|" should be greater than previous "
operator|+
name|prevEpoch
argument_list|,
name|newEpoch
operator|>
name|prevEpoch
argument_list|)
expr_stmt|;
name|prevEpoch
operator|=
name|newEpoch
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|makeFaulty (List<AsyncLogger> loggers)
specifier|private
name|List
argument_list|<
name|AsyncLogger
argument_list|>
name|makeFaulty
parameter_list|(
name|List
argument_list|<
name|AsyncLogger
argument_list|>
name|loggers
parameter_list|)
block|{
name|List
argument_list|<
name|AsyncLogger
argument_list|>
name|ret
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|AsyncLogger
name|l
range|:
name|loggers
control|)
block|{
name|AsyncLogger
name|spy
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|l
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|doAnswer
argument_list|(
operator|new
name|SometimesFaulty
argument_list|<
name|Long
argument_list|>
argument_list|(
literal|0.10f
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|spy
argument_list|)
operator|.
name|getJournalState
argument_list|()
expr_stmt|;
name|Mockito
operator|.
name|doAnswer
argument_list|(
operator|new
name|SometimesFaulty
argument_list|<
name|Void
argument_list|>
argument_list|(
literal|0.40f
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|spy
argument_list|)
operator|.
name|newEpoch
argument_list|(
name|Mockito
operator|.
name|anyLong
argument_list|()
argument_list|)
expr_stmt|;
name|ret
operator|.
name|add
argument_list|(
name|spy
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|class|SometimesFaulty
specifier|private
class|class
name|SometimesFaulty
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Answer
argument_list|<
name|ListenableFuture
argument_list|<
name|T
argument_list|>
argument_list|>
block|{
DECL|field|faultProbability
specifier|private
name|float
name|faultProbability
decl_stmt|;
DECL|method|SometimesFaulty (float faultProbability)
specifier|public
name|SometimesFaulty
parameter_list|(
name|float
name|faultProbability
parameter_list|)
block|{
name|this
operator|.
name|faultProbability
operator|=
name|faultProbability
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|answer (InvocationOnMock invocation)
specifier|public
name|ListenableFuture
argument_list|<
name|T
argument_list|>
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
if|if
condition|(
name|r
operator|.
name|nextFloat
argument_list|()
operator|<
name|faultProbability
condition|)
block|{
return|return
name|Futures
operator|.
name|immediateFailedFuture
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"Injected fault"
argument_list|)
argument_list|)
return|;
block|}
return|return
operator|(
name|ListenableFuture
argument_list|<
name|T
argument_list|>
operator|)
name|invocation
operator|.
name|callRealMethod
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

