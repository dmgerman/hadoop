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
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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
name|FSNamesystem
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
name|CyclicIteration
import|;
end_import

begin_comment
comment|/**  * Manage node decommissioning.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|DecommissionManager
class|class
name|DecommissionManager
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|DecommissionManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|fsnamesystem
specifier|private
specifier|final
name|FSNamesystem
name|fsnamesystem
decl_stmt|;
DECL|field|blockManager
specifier|private
specifier|final
name|BlockManager
name|blockManager
decl_stmt|;
DECL|method|DecommissionManager (FSNamesystem namesystem)
name|DecommissionManager
parameter_list|(
name|FSNamesystem
name|namesystem
parameter_list|)
block|{
name|this
operator|.
name|fsnamesystem
operator|=
name|namesystem
expr_stmt|;
name|this
operator|.
name|blockManager
operator|=
name|fsnamesystem
operator|.
name|getBlockManager
argument_list|()
expr_stmt|;
block|}
comment|/** Periodically check decommission status. */
DECL|class|Monitor
class|class
name|Monitor
implements|implements
name|Runnable
block|{
comment|/** recheckInterval is how often namenode checks      *  if a node has finished decommission      */
DECL|field|recheckInterval
specifier|private
specifier|final
name|long
name|recheckInterval
decl_stmt|;
comment|/** The number of decommission nodes to check for each interval */
DECL|field|numNodesPerCheck
specifier|private
specifier|final
name|int
name|numNodesPerCheck
decl_stmt|;
comment|/** firstkey can be initialized to anything. */
DECL|field|firstkey
specifier|private
name|String
name|firstkey
init|=
literal|""
decl_stmt|;
DECL|method|Monitor (int recheckIntervalInSecond, int numNodesPerCheck)
name|Monitor
parameter_list|(
name|int
name|recheckIntervalInSecond
parameter_list|,
name|int
name|numNodesPerCheck
parameter_list|)
block|{
name|this
operator|.
name|recheckInterval
operator|=
name|recheckIntervalInSecond
operator|*
literal|1000L
expr_stmt|;
name|this
operator|.
name|numNodesPerCheck
operator|=
name|numNodesPerCheck
expr_stmt|;
block|}
comment|/**      * Check decommission status of numNodesPerCheck nodes      * for every recheckInterval milliseconds.      */
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
for|for
control|(
init|;
name|fsnamesystem
operator|.
name|isRunning
argument_list|()
condition|;
control|)
block|{
name|fsnamesystem
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
name|check
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|fsnamesystem
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|recheckInterval
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" interrupted: "
operator|+
name|ie
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|check ()
specifier|private
name|void
name|check
parameter_list|()
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|DatanodeDescriptor
argument_list|>
name|entry
range|:
name|blockManager
operator|.
name|getDatanodeManager
argument_list|()
operator|.
name|getDatanodeCyclicIteration
argument_list|(
name|firstkey
argument_list|)
control|)
block|{
specifier|final
name|DatanodeDescriptor
name|d
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|firstkey
operator|=
name|entry
operator|.
name|getKey
argument_list|()
expr_stmt|;
if|if
condition|(
name|d
operator|.
name|isDecommissionInProgress
argument_list|()
condition|)
block|{
try|try
block|{
name|blockManager
operator|.
name|checkDecommissionStateInternal
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"entry="
operator|+
name|entry
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|++
name|count
operator|==
name|numNodesPerCheck
condition|)
block|{
return|return;
block|}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

