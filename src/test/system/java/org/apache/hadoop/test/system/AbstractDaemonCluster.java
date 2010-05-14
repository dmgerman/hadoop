begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.test.system
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|system
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
name|test
operator|.
name|system
operator|.
name|process
operator|.
name|ClusterProcessManager
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
name|system
operator|.
name|process
operator|.
name|RemoteProcess
import|;
end_import

begin_comment
comment|/**  * Abstract class which represent the cluster having multiple daemons.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|AbstractDaemonCluster
specifier|public
specifier|abstract
class|class
name|AbstractDaemonCluster
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
name|AbstractDaemonCluster
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|excludeExpList
specifier|private
name|String
index|[]
name|excludeExpList
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|clusterManager
specifier|protected
name|ClusterProcessManager
name|clusterManager
decl_stmt|;
DECL|field|daemons
specifier|private
name|Map
argument_list|<
name|Enum
argument_list|<
name|?
argument_list|>
argument_list|,
name|List
argument_list|<
name|AbstractDaemonClient
argument_list|>
argument_list|>
name|daemons
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|Enum
argument_list|<
name|?
argument_list|>
argument_list|,
name|List
argument_list|<
name|AbstractDaemonClient
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Constructor to create a cluster client.<br/>    *     * @param conf    *          Configuration to be used while constructing the cluster.    * @param rcluster    *          process manger instance to be used for managing the daemons.    *     * @throws IOException    */
DECL|method|AbstractDaemonCluster (Configuration conf, ClusterProcessManager rcluster)
specifier|public
name|AbstractDaemonCluster
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|ClusterProcessManager
name|rcluster
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|clusterManager
operator|=
name|rcluster
expr_stmt|;
name|createAllClients
argument_list|()
expr_stmt|;
block|}
comment|/**    * The method returns the cluster manager. The system test cases require an    * instance of HadoopDaemonRemoteCluster to invoke certain operation on the    * daemon.    *     * @return instance of clusterManager    */
DECL|method|getClusterManager ()
specifier|public
name|ClusterProcessManager
name|getClusterManager
parameter_list|()
block|{
return|return
name|clusterManager
return|;
block|}
DECL|method|createAllClients ()
specifier|protected
name|void
name|createAllClients
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|RemoteProcess
name|p
range|:
name|clusterManager
operator|.
name|getAllProcesses
argument_list|()
control|)
block|{
name|List
argument_list|<
name|AbstractDaemonClient
argument_list|>
name|dms
init|=
name|daemons
operator|.
name|get
argument_list|(
name|p
operator|.
name|getRole
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|dms
operator|==
literal|null
condition|)
block|{
name|dms
operator|=
operator|new
name|ArrayList
argument_list|<
name|AbstractDaemonClient
argument_list|>
argument_list|()
expr_stmt|;
name|daemons
operator|.
name|put
argument_list|(
name|p
operator|.
name|getRole
argument_list|()
argument_list|,
name|dms
argument_list|)
expr_stmt|;
block|}
name|dms
operator|.
name|add
argument_list|(
name|createClient
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Method to create the daemon client.<br/>    *     * @param process    *          to manage the daemon.    * @return instance of the daemon client    *     * @throws IOException    */
specifier|protected
specifier|abstract
name|AbstractDaemonClient
argument_list|<
name|DaemonProtocol
argument_list|>
DECL|method|createClient (RemoteProcess process)
name|createClient
parameter_list|(
name|RemoteProcess
name|process
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the global cluster configuration which was used to create the     * cluster.<br/>    *     * @return global configuration of the cluster.    */
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
comment|/**    *    /**    * Return the client handle of all the Daemons.<br/>    *     * @return map of role to daemon clients' list.    */
DECL|method|getDaemons ()
specifier|public
name|Map
argument_list|<
name|Enum
argument_list|<
name|?
argument_list|>
argument_list|,
name|List
argument_list|<
name|AbstractDaemonClient
argument_list|>
argument_list|>
name|getDaemons
parameter_list|()
block|{
return|return
name|daemons
return|;
block|}
comment|/**    * Checks if the cluster is ready for testing.<br/>    * Algorithm for checking is as follows :<br/>    *<ul>    *<li> Wait for Daemon to come up</li>    *<li> Check if daemon is ready</li>    *<li> If one of the daemon is not ready, return false</li>    *</ul>     *     * @return true if whole cluster is ready.    *     * @throws IOException    */
DECL|method|isReady ()
specifier|public
name|boolean
name|isReady
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|List
argument_list|<
name|AbstractDaemonClient
argument_list|>
name|set
range|:
name|daemons
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|AbstractDaemonClient
name|daemon
range|:
name|set
control|)
block|{
name|waitForDaemon
argument_list|(
name|daemon
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|daemon
operator|.
name|isReady
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|waitForDaemon (AbstractDaemonClient d)
specifier|protected
name|void
name|waitForDaemon
parameter_list|(
name|AbstractDaemonClient
name|d
parameter_list|)
block|{
specifier|final
name|int
name|TEN_SEC
init|=
literal|10000
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for daemon at "
operator|+
name|d
operator|.
name|getHostName
argument_list|()
operator|+
literal|" to come up."
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Daemon might not be "
operator|+
literal|"ready or the call to setReady() method hasn't been "
operator|+
literal|"injected to "
operator|+
name|d
operator|.
name|getClass
argument_list|()
operator|+
literal|" "
argument_list|)
expr_stmt|;
name|d
operator|.
name|connect
argument_list|()
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|TEN_SEC
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{         }
block|}
block|}
block|}
comment|/**    * Starts the cluster daemons.    * @throws IOException    */
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
name|clusterManager
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**    * Stops the cluster daemons.    * @throws IOException    */
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|IOException
block|{
name|clusterManager
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|/**    * Connect to daemon RPC ports.    * @throws IOException    */
DECL|method|connect ()
specifier|public
name|void
name|connect
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|List
argument_list|<
name|AbstractDaemonClient
argument_list|>
name|set
range|:
name|daemons
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|AbstractDaemonClient
name|daemon
range|:
name|set
control|)
block|{
name|daemon
operator|.
name|connect
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Disconnect to daemon RPC ports.    * @throws IOException    */
DECL|method|disconnect ()
specifier|public
name|void
name|disconnect
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|List
argument_list|<
name|AbstractDaemonClient
argument_list|>
name|set
range|:
name|daemons
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|AbstractDaemonClient
name|daemon
range|:
name|set
control|)
block|{
name|daemon
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Ping all the daemons of the cluster.    * @throws IOException    */
DECL|method|ping ()
specifier|public
name|void
name|ping
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|List
argument_list|<
name|AbstractDaemonClient
argument_list|>
name|set
range|:
name|daemons
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|AbstractDaemonClient
name|daemon
range|:
name|set
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Daemon is : "
operator|+
name|daemon
operator|.
name|getHostName
argument_list|()
operator|+
literal|" pinging...."
argument_list|)
expr_stmt|;
name|daemon
operator|.
name|ping
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Connect to the cluster and ensure that it is clean to run tests.    * @throws Exception    */
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
while|while
condition|(
operator|!
name|isReady
argument_list|()
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
name|connect
argument_list|()
expr_stmt|;
name|ping
argument_list|()
expr_stmt|;
name|clearAllControlActions
argument_list|()
expr_stmt|;
name|ensureClean
argument_list|()
expr_stmt|;
name|populateExceptionCounts
argument_list|()
expr_stmt|;
block|}
comment|/**    * This is mainly used for the test cases to set the list of exceptions    * that will be excluded.    * @param excludeExpList list of exceptions to exclude    */
DECL|method|setExcludeExpList (String [] excludeExpList)
specifier|public
name|void
name|setExcludeExpList
parameter_list|(
name|String
index|[]
name|excludeExpList
parameter_list|)
block|{
name|this
operator|.
name|excludeExpList
operator|=
name|excludeExpList
expr_stmt|;
block|}
DECL|method|clearAllControlActions ()
specifier|public
name|void
name|clearAllControlActions
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|List
argument_list|<
name|AbstractDaemonClient
argument_list|>
name|set
range|:
name|daemons
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|AbstractDaemonClient
name|daemon
range|:
name|set
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Daemon is : "
operator|+
name|daemon
operator|.
name|getHostName
argument_list|()
operator|+
literal|" pinging...."
argument_list|)
expr_stmt|;
name|daemon
operator|.
name|getProxy
argument_list|()
operator|.
name|clearActions
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Ensure that the cluster is clean to run tests.    * @throws IOException    */
DECL|method|ensureClean ()
specifier|public
name|void
name|ensureClean
parameter_list|()
throws|throws
name|IOException
block|{   }
comment|/**    * Ensure that cluster is clean. Disconnect from the RPC ports of the daemons.    * @throws IOException    */
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureClean
argument_list|()
expr_stmt|;
name|clearAllControlActions
argument_list|()
expr_stmt|;
name|assertNoExceptionMessages
argument_list|()
expr_stmt|;
name|disconnect
argument_list|()
expr_stmt|;
block|}
comment|/**    * Populate the exception counts in all the daemons so that it can be checked when     * the testcase has finished running.<br/>    * @throws IOException    */
DECL|method|populateExceptionCounts ()
specifier|protected
name|void
name|populateExceptionCounts
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|List
argument_list|<
name|AbstractDaemonClient
argument_list|>
name|lst
range|:
name|daemons
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|AbstractDaemonClient
name|d
range|:
name|lst
control|)
block|{
name|d
operator|.
name|populateExceptionCount
argument_list|(
name|excludeExpList
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Assert no exception has been thrown during the sequence of the actions.    *<br/>    * @throws IOException    */
DECL|method|assertNoExceptionMessages ()
specifier|protected
name|void
name|assertNoExceptionMessages
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|List
argument_list|<
name|AbstractDaemonClient
argument_list|>
name|lst
range|:
name|daemons
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|AbstractDaemonClient
name|d
range|:
name|lst
control|)
block|{
name|d
operator|.
name|assertNoExceptionsOccurred
argument_list|(
name|excludeExpList
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

