begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.core.zk
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|zk
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|CreateMode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|KeeperException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|WatchedEvent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|Watcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|ZooDefs
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|ZooKeeper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|data
operator|.
name|ACL
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|data
operator|.
name|Stat
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
name|Closeable
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
name|ArrayList
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
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_class
DECL|class|ZKIntegration
specifier|public
class|class
name|ZKIntegration
implements|implements
name|Watcher
implements|,
name|Closeable
block|{
comment|/**  * Base path for services  */
DECL|field|ZK_SERVICES
specifier|public
specifier|static
specifier|final
name|String
name|ZK_SERVICES
init|=
literal|"services"
decl_stmt|;
comment|/**    * Base path for all Slider references    */
DECL|field|ZK_SLIDER
specifier|public
specifier|static
specifier|final
name|String
name|ZK_SLIDER
init|=
literal|"slider"
decl_stmt|;
DECL|field|ZK_USERS
specifier|public
specifier|static
specifier|final
name|String
name|ZK_USERS
init|=
literal|"users"
decl_stmt|;
DECL|field|SVC_SLIDER
specifier|public
specifier|static
specifier|final
name|String
name|SVC_SLIDER
init|=
literal|"/"
operator|+
name|ZK_SERVICES
operator|+
literal|"/"
operator|+
name|ZK_SLIDER
decl_stmt|;
DECL|field|SVC_SLIDER_USERS
specifier|public
specifier|static
specifier|final
name|String
name|SVC_SLIDER_USERS
init|=
name|SVC_SLIDER
operator|+
literal|"/"
operator|+
name|ZK_USERS
decl_stmt|;
DECL|field|ZK_USERS_PATH_LIST
specifier|public
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|ZK_USERS_PATH_LIST
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
name|ZK_USERS_PATH_LIST
operator|.
name|add
argument_list|(
name|ZK_SERVICES
argument_list|)
expr_stmt|;
name|ZK_USERS_PATH_LIST
operator|.
name|add
argument_list|(
name|ZK_SLIDER
argument_list|)
expr_stmt|;
name|ZK_USERS_PATH_LIST
operator|.
name|add
argument_list|(
name|ZK_USERS
argument_list|)
expr_stmt|;
block|}
DECL|field|SESSION_TIMEOUT
specifier|public
specifier|static
specifier|final
name|int
name|SESSION_TIMEOUT
init|=
literal|30000
decl_stmt|;
DECL|field|log
specifier|protected
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ZKIntegration
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|zookeeper
specifier|private
name|ZooKeeper
name|zookeeper
decl_stmt|;
DECL|field|username
specifier|private
specifier|final
name|String
name|username
decl_stmt|;
DECL|field|clustername
specifier|private
specifier|final
name|String
name|clustername
decl_stmt|;
DECL|field|userPath
specifier|private
specifier|final
name|String
name|userPath
decl_stmt|;
DECL|field|sessionTimeout
specifier|private
name|int
name|sessionTimeout
init|=
name|SESSION_TIMEOUT
decl_stmt|;
DECL|field|ZK_SESSIONS
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ZooKeeper
argument_list|>
name|ZK_SESSIONS
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**  flag to set to indicate that the user path should be created if  it is not already there  */
DECL|field|toInit
specifier|private
specifier|final
name|AtomicBoolean
name|toInit
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|field|createClusterPath
specifier|private
specifier|final
name|boolean
name|createClusterPath
decl_stmt|;
DECL|field|watchEventHandler
specifier|private
specifier|final
name|Watcher
name|watchEventHandler
decl_stmt|;
DECL|field|zkConnection
specifier|private
specifier|final
name|String
name|zkConnection
decl_stmt|;
DECL|field|canBeReadOnly
specifier|private
specifier|final
name|boolean
name|canBeReadOnly
decl_stmt|;
DECL|method|ZKIntegration (String zkConnection, String username, String clustername, boolean canBeReadOnly, boolean createClusterPath, Watcher watchEventHandler, int sessionTimeout )
specifier|protected
name|ZKIntegration
parameter_list|(
name|String
name|zkConnection
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|clustername
parameter_list|,
name|boolean
name|canBeReadOnly
parameter_list|,
name|boolean
name|createClusterPath
parameter_list|,
name|Watcher
name|watchEventHandler
parameter_list|,
name|int
name|sessionTimeout
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|username
operator|=
name|username
expr_stmt|;
name|this
operator|.
name|clustername
operator|=
name|clustername
expr_stmt|;
name|this
operator|.
name|watchEventHandler
operator|=
name|watchEventHandler
expr_stmt|;
name|this
operator|.
name|zkConnection
operator|=
name|zkConnection
expr_stmt|;
name|this
operator|.
name|canBeReadOnly
operator|=
name|canBeReadOnly
expr_stmt|;
name|this
operator|.
name|createClusterPath
operator|=
name|createClusterPath
expr_stmt|;
name|this
operator|.
name|sessionTimeout
operator|=
name|sessionTimeout
expr_stmt|;
name|this
operator|.
name|userPath
operator|=
name|mkSliderUserPath
argument_list|(
name|username
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns true only if an active ZK session is available and retrieved from    * cache, false when it has to create a new one.    *    * @return true if from cache, false when new session created    * @throws IOException    */
DECL|method|init ()
specifier|public
specifier|synchronized
name|boolean
name|init
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|zookeeper
operator|!=
literal|null
operator|&&
name|getAlive
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
synchronized|synchronized
init|(
name|ZK_SESSIONS
init|)
block|{
if|if
condition|(
name|ZK_SESSIONS
operator|.
name|containsKey
argument_list|(
name|zkConnection
argument_list|)
condition|)
block|{
name|zookeeper
operator|=
name|ZK_SESSIONS
operator|.
name|get
argument_list|(
name|zkConnection
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|zookeeper
operator|==
literal|null
operator|||
operator|!
name|getAlive
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Binding ZK client to {}"
argument_list|,
name|zkConnection
argument_list|)
expr_stmt|;
name|zookeeper
operator|=
operator|new
name|ZooKeeper
argument_list|(
name|zkConnection
argument_list|,
name|sessionTimeout
argument_list|,
name|this
argument_list|,
name|canBeReadOnly
argument_list|)
expr_stmt|;
name|ZK_SESSIONS
operator|.
name|put
argument_list|(
name|zkConnection
argument_list|,
name|zookeeper
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
comment|/**    * Create an instance bonded to the specific closure    * @param zkConnection    * @param username    * @param clustername    * @param canBeReadOnly    * @param watchEventHandler    * @return the new instance    * @throws IOException    */
DECL|method|newInstance (String zkConnection, String username, String clustername, boolean createClusterPath, boolean canBeReadOnly, Watcher watchEventHandler, int sessionTimeout)
specifier|public
specifier|static
name|ZKIntegration
name|newInstance
parameter_list|(
name|String
name|zkConnection
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|clustername
parameter_list|,
name|boolean
name|createClusterPath
parameter_list|,
name|boolean
name|canBeReadOnly
parameter_list|,
name|Watcher
name|watchEventHandler
parameter_list|,
name|int
name|sessionTimeout
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ZKIntegration
argument_list|(
name|zkConnection
argument_list|,
name|username
argument_list|,
name|clustername
argument_list|,
name|canBeReadOnly
argument_list|,
name|createClusterPath
argument_list|,
name|watchEventHandler
argument_list|,
name|sessionTimeout
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|zookeeper
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|zookeeper
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignored
parameter_list|)
block|{        }
name|zookeeper
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|getConnectionString ()
specifier|public
name|String
name|getConnectionString
parameter_list|()
block|{
return|return
name|zkConnection
return|;
block|}
DECL|method|getClusterPath ()
specifier|public
name|String
name|getClusterPath
parameter_list|()
block|{
return|return
name|mkClusterPath
argument_list|(
name|username
argument_list|,
name|clustername
argument_list|)
return|;
block|}
DECL|method|getConnected ()
specifier|public
name|boolean
name|getConnected
parameter_list|()
block|{
return|return
name|zookeeper
operator|.
name|getState
argument_list|()
operator|.
name|isConnected
argument_list|()
return|;
block|}
DECL|method|getAlive ()
specifier|public
name|boolean
name|getAlive
parameter_list|()
block|{
return|return
name|zookeeper
operator|.
name|getState
argument_list|()
operator|.
name|isAlive
argument_list|()
return|;
block|}
DECL|method|getState ()
specifier|public
name|ZooKeeper
operator|.
name|States
name|getState
parameter_list|()
block|{
return|return
name|zookeeper
operator|.
name|getState
argument_list|()
return|;
block|}
DECL|method|getClusterStat ()
specifier|public
name|Stat
name|getClusterStat
parameter_list|()
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
return|return
name|stat
argument_list|(
name|getClusterPath
argument_list|()
argument_list|)
return|;
block|}
DECL|method|exists (String path)
specifier|public
name|boolean
name|exists
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
return|return
name|stat
argument_list|(
name|path
argument_list|)
operator|!=
literal|null
return|;
block|}
DECL|method|stat (String path)
specifier|public
name|Stat
name|stat
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
return|return
name|zookeeper
operator|.
name|exists
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ZK integration bound @  "
operator|+
name|zkConnection
operator|+
literal|": "
operator|+
name|zookeeper
return|;
block|}
comment|/**  * Event handler to notify of state events  * @param event  */
annotation|@
name|Override
DECL|method|process (WatchedEvent event)
specifier|public
name|void
name|process
parameter_list|(
name|WatchedEvent
name|event
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"{}"
argument_list|,
name|event
argument_list|)
expr_stmt|;
try|try
block|{
name|maybeInit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Failed to init"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|watchEventHandler
operator|!=
literal|null
condition|)
block|{
name|watchEventHandler
operator|.
name|process
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|maybeInit ()
specifier|private
name|void
name|maybeInit
parameter_list|()
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
if|if
condition|(
operator|!
name|toInit
operator|.
name|getAndSet
argument_list|(
literal|true
argument_list|)
operator|&&
name|createClusterPath
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"initing"
argument_list|)
expr_stmt|;
comment|//create the user path
name|mkPath
argument_list|(
name|ZK_USERS_PATH_LIST
argument_list|,
name|ZooDefs
operator|.
name|Ids
operator|.
name|OPEN_ACL_UNSAFE
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
comment|//create the specific user
name|createPath
argument_list|(
name|userPath
argument_list|,
literal|null
argument_list|,
name|ZooDefs
operator|.
name|Ids
operator|.
name|OPEN_ACL_UNSAFE
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Create a path under a parent, don't care if it already exists    * As the path isn't returned, this isn't the way to create sequentially    * numbered nodes.    * @param parent parent dir. Must have a trailing / if entry!=null||empty     * @param entry entry -can be null or "", in which case it is not appended    * @param acl    * @param createMode    * @return the path if created; null if not    */
DECL|method|createPath (String parent, String entry, List<ACL> acl, CreateMode createMode)
specifier|public
name|String
name|createPath
parameter_list|(
name|String
name|parent
parameter_list|,
name|String
name|entry
parameter_list|,
name|List
argument_list|<
name|ACL
argument_list|>
name|acl
parameter_list|,
name|CreateMode
name|createMode
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
comment|//initial create of full path
assert|assert
name|acl
operator|!=
literal|null
assert|;
assert|assert
operator|!
name|acl
operator|.
name|isEmpty
argument_list|()
assert|;
assert|assert
name|parent
operator|!=
literal|null
assert|;
name|String
name|path
init|=
name|parent
decl_stmt|;
if|if
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
name|path
operator|=
name|path
operator|+
name|entry
expr_stmt|;
block|}
try|try
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Creating ZK path {}"
argument_list|,
name|path
argument_list|)
expr_stmt|;
return|return
name|zookeeper
operator|.
name|create
argument_list|(
name|path
argument_list|,
literal|null
argument_list|,
name|acl
argument_list|,
name|createMode
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NodeExistsException
name|ignored
parameter_list|)
block|{
comment|//node already there
name|log
operator|.
name|debug
argument_list|(
literal|"node already present:{}"
argument_list|,
name|path
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
comment|/**    * Recursive path create    * @param paths path list    * @param acl acl list    * @param createMode create modes    */
DECL|method|mkPath (List<String> paths, List<ACL> acl, CreateMode createMode)
specifier|public
name|void
name|mkPath
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|,
name|List
argument_list|<
name|ACL
argument_list|>
name|acl
parameter_list|,
name|CreateMode
name|createMode
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|String
name|history
init|=
literal|"/"
decl_stmt|;
for|for
control|(
name|String
name|entry
range|:
name|paths
control|)
block|{
name|createPath
argument_list|(
name|history
argument_list|,
name|entry
argument_list|,
name|acl
argument_list|,
name|createMode
argument_list|)
expr_stmt|;
name|history
operator|=
name|history
operator|+
name|entry
operator|+
literal|"/"
expr_stmt|;
block|}
block|}
comment|/**    * Delete a node, does not throw an exception if the path is not fond    * @param path path to delete    * @return true if the path could be deleted, false if there was no node to delete     *    */
DECL|method|delete (String path)
specifier|public
name|boolean
name|delete
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|KeeperException
block|{
try|try
block|{
name|zookeeper
operator|.
name|delete
argument_list|(
name|path
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Deleting {}"
argument_list|,
name|path
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NoNodeException
name|ignored
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**    * Recursively delete a node, does not throw exception if any node does not exist.    * @param path    * @return true if delete was successful    */
DECL|method|deleteRecursive (String path)
specifier|public
name|boolean
name|deleteRecursive
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
try|try
block|{
name|List
argument_list|<
name|String
argument_list|>
name|children
init|=
name|zookeeper
operator|.
name|getChildren
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|child
range|:
name|children
control|)
block|{
name|deleteRecursive
argument_list|(
name|path
operator|+
literal|"/"
operator|+
name|child
argument_list|)
expr_stmt|;
block|}
name|delete
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NoNodeException
name|ignored
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/**  * Build the path to a cluster; exists once the cluster has come up.  * Even before that, a ZK watcher could wait for it.  * @param username user  * @param clustername name of the cluster  * @return a strin  */
DECL|method|mkClusterPath (String username, String clustername)
specifier|public
specifier|static
name|String
name|mkClusterPath
parameter_list|(
name|String
name|username
parameter_list|,
name|String
name|clustername
parameter_list|)
block|{
return|return
name|mkSliderUserPath
argument_list|(
name|username
argument_list|)
operator|+
literal|"/"
operator|+
name|clustername
return|;
block|}
comment|/**  * Build the path to a cluster; exists once the cluster has come up.  * Even before that, a ZK watcher could wait for it.  * @param username user  * @return a string  */
DECL|method|mkSliderUserPath (String username)
specifier|public
specifier|static
name|String
name|mkSliderUserPath
parameter_list|(
name|String
name|username
parameter_list|)
block|{
return|return
name|SVC_SLIDER_USERS
operator|+
literal|"/"
operator|+
name|username
return|;
block|}
block|}
end_class

end_unit

