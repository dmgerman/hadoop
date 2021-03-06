begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util.curator
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|curator
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
name|nio
operator|.
name|charset
operator|.
name|Charset
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
name|LinkedList
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
name|org
operator|.
name|apache
operator|.
name|curator
operator|.
name|framework
operator|.
name|AuthInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|curator
operator|.
name|framework
operator|.
name|CuratorFramework
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|curator
operator|.
name|framework
operator|.
name|CuratorFrameworkFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|curator
operator|.
name|framework
operator|.
name|api
operator|.
name|transaction
operator|.
name|CuratorOp
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|curator
operator|.
name|retry
operator|.
name|RetryNTimes
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
name|fs
operator|.
name|CommonConfigurationKeys
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
name|security
operator|.
name|SecurityUtil
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
name|ZKUtil
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_comment
comment|/**  * Helper class that provides utility methods specific to ZK operations.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ZKCuratorManager
specifier|public
specifier|final
class|class
name|ZKCuratorManager
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
name|ZKCuratorManager
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Configuration for the ZooKeeper connection. */
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
comment|/** Curator for ZooKeeper. */
DECL|field|curator
specifier|private
name|CuratorFramework
name|curator
decl_stmt|;
DECL|method|ZKCuratorManager (Configuration config)
specifier|public
name|ZKCuratorManager
parameter_list|(
name|Configuration
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|conf
operator|=
name|config
expr_stmt|;
block|}
comment|/**    * Get the curator framework managing the ZooKeeper connection.    * @return Curator framework.    */
DECL|method|getCurator ()
specifier|public
name|CuratorFramework
name|getCurator
parameter_list|()
block|{
return|return
name|curator
return|;
block|}
comment|/**    * Close the connection with ZooKeeper.    */
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|curator
operator|!=
literal|null
condition|)
block|{
name|curator
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Utility method to fetch the ZK ACLs from the configuration.    * @throws java.io.IOException if the Zookeeper ACLs configuration file    * cannot be read    */
DECL|method|getZKAcls (Configuration conf)
specifier|public
specifier|static
name|List
argument_list|<
name|ACL
argument_list|>
name|getZKAcls
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Parse authentication from configuration.
name|String
name|zkAclConf
init|=
name|conf
operator|.
name|get
argument_list|(
name|CommonConfigurationKeys
operator|.
name|ZK_ACL
argument_list|,
name|CommonConfigurationKeys
operator|.
name|ZK_ACL_DEFAULT
argument_list|)
decl_stmt|;
try|try
block|{
name|zkAclConf
operator|=
name|ZKUtil
operator|.
name|resolveConfIndirection
argument_list|(
name|zkAclConf
argument_list|)
expr_stmt|;
return|return
name|ZKUtil
operator|.
name|parseACLs
argument_list|(
name|zkAclConf
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|ZKUtil
operator|.
name|BadAclFormatException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Couldn't read ACLs based on {}"
argument_list|,
name|CommonConfigurationKeys
operator|.
name|ZK_ACL
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
comment|/**    * Utility method to fetch ZK auth info from the configuration.    * @throws java.io.IOException if the Zookeeper ACLs configuration file    * cannot be read    * @throws ZKUtil.BadAuthFormatException if the auth format is invalid    */
DECL|method|getZKAuths (Configuration conf)
specifier|public
specifier|static
name|List
argument_list|<
name|ZKUtil
operator|.
name|ZKAuthInfo
argument_list|>
name|getZKAuths
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|SecurityUtil
operator|.
name|getZKAuthInfos
argument_list|(
name|conf
argument_list|,
name|CommonConfigurationKeys
operator|.
name|ZK_AUTH
argument_list|)
return|;
block|}
comment|/**    * Start the connection to the ZooKeeper ensemble.    * @throws IOException If the connection cannot be started.    */
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|start
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Start the connection to the ZooKeeper ensemble.    * @param authInfos List of authentication keys.    * @throws IOException If the connection cannot be started.    */
DECL|method|start (List<AuthInfo> authInfos)
specifier|public
name|void
name|start
parameter_list|(
name|List
argument_list|<
name|AuthInfo
argument_list|>
name|authInfos
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Connect to the ZooKeeper ensemble
name|String
name|zkHostPort
init|=
name|conf
operator|.
name|get
argument_list|(
name|CommonConfigurationKeys
operator|.
name|ZK_ADDRESS
argument_list|)
decl_stmt|;
if|if
condition|(
name|zkHostPort
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|CommonConfigurationKeys
operator|.
name|ZK_ADDRESS
operator|+
literal|" is not configured."
argument_list|)
throw|;
block|}
name|int
name|numRetries
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|CommonConfigurationKeys
operator|.
name|ZK_NUM_RETRIES
argument_list|,
name|CommonConfigurationKeys
operator|.
name|ZK_NUM_RETRIES_DEFAULT
argument_list|)
decl_stmt|;
name|int
name|zkSessionTimeout
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|CommonConfigurationKeys
operator|.
name|ZK_TIMEOUT_MS
argument_list|,
name|CommonConfigurationKeys
operator|.
name|ZK_TIMEOUT_MS_DEFAULT
argument_list|)
decl_stmt|;
name|int
name|zkRetryInterval
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|CommonConfigurationKeys
operator|.
name|ZK_RETRY_INTERVAL_MS
argument_list|,
name|CommonConfigurationKeys
operator|.
name|ZK_RETRY_INTERVAL_MS_DEFAULT
argument_list|)
decl_stmt|;
name|RetryNTimes
name|retryPolicy
init|=
operator|new
name|RetryNTimes
argument_list|(
name|numRetries
argument_list|,
name|zkRetryInterval
argument_list|)
decl_stmt|;
comment|// Set up ZK auths
name|List
argument_list|<
name|ZKUtil
operator|.
name|ZKAuthInfo
argument_list|>
name|zkAuths
init|=
name|getZKAuths
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|authInfos
operator|==
literal|null
condition|)
block|{
name|authInfos
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|ZKUtil
operator|.
name|ZKAuthInfo
name|zkAuth
range|:
name|zkAuths
control|)
block|{
name|authInfos
operator|.
name|add
argument_list|(
operator|new
name|AuthInfo
argument_list|(
name|zkAuth
operator|.
name|getScheme
argument_list|()
argument_list|,
name|zkAuth
operator|.
name|getAuth
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|CuratorFramework
name|client
init|=
name|CuratorFrameworkFactory
operator|.
name|builder
argument_list|()
operator|.
name|connectString
argument_list|(
name|zkHostPort
argument_list|)
operator|.
name|sessionTimeoutMs
argument_list|(
name|zkSessionTimeout
argument_list|)
operator|.
name|retryPolicy
argument_list|(
name|retryPolicy
argument_list|)
operator|.
name|authorization
argument_list|(
name|authInfos
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|client
operator|.
name|start
argument_list|()
expr_stmt|;
name|this
operator|.
name|curator
operator|=
name|client
expr_stmt|;
block|}
comment|/**    * Get ACLs for a ZNode.    * @param path Path of the ZNode.    * @return The list of ACLs.    * @throws Exception    */
DECL|method|getACL (final String path)
specifier|public
name|List
argument_list|<
name|ACL
argument_list|>
name|getACL
parameter_list|(
specifier|final
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|curator
operator|.
name|getACL
argument_list|()
operator|.
name|forPath
argument_list|(
name|path
argument_list|)
return|;
block|}
comment|/**    * Get the data in a ZNode.    * @param path Path of the ZNode.    * @return The data in the ZNode.    * @throws Exception If it cannot contact Zookeeper.    */
DECL|method|getData (final String path)
specifier|public
name|byte
index|[]
name|getData
parameter_list|(
specifier|final
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|curator
operator|.
name|getData
argument_list|()
operator|.
name|forPath
argument_list|(
name|path
argument_list|)
return|;
block|}
comment|/**    * Get the data in a ZNode.    * @param path Path of the ZNode.    * @param stat    * @return The data in the ZNode.    * @throws Exception If it cannot contact Zookeeper.    */
DECL|method|getData (final String path, Stat stat)
specifier|public
name|byte
index|[]
name|getData
parameter_list|(
specifier|final
name|String
name|path
parameter_list|,
name|Stat
name|stat
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|curator
operator|.
name|getData
argument_list|()
operator|.
name|storingStatIn
argument_list|(
name|stat
argument_list|)
operator|.
name|forPath
argument_list|(
name|path
argument_list|)
return|;
block|}
comment|/**    * Get the data in a ZNode.    * @param path Path of the ZNode.    * @return The data in the ZNode.    * @throws Exception If it cannot contact Zookeeper.    */
DECL|method|getStringData (final String path)
specifier|public
name|String
name|getStringData
parameter_list|(
specifier|final
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
name|byte
index|[]
name|bytes
init|=
name|getData
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytes
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|String
argument_list|(
name|bytes
argument_list|,
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Get the data in a ZNode.    * @param path Path of the ZNode.    * @param stat Output statistics of the ZNode.    * @return The data in the ZNode.    * @throws Exception If it cannot contact Zookeeper.    */
DECL|method|getStringData (final String path, Stat stat)
specifier|public
name|String
name|getStringData
parameter_list|(
specifier|final
name|String
name|path
parameter_list|,
name|Stat
name|stat
parameter_list|)
throws|throws
name|Exception
block|{
name|byte
index|[]
name|bytes
init|=
name|getData
argument_list|(
name|path
argument_list|,
name|stat
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytes
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|String
argument_list|(
name|bytes
argument_list|,
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Set data into a ZNode.    * @param path Path of the ZNode.    * @param data Data to set.    * @param version Version of the data to store.    * @throws Exception If it cannot contact Zookeeper.    */
DECL|method|setData (String path, byte[] data, int version)
specifier|public
name|void
name|setData
parameter_list|(
name|String
name|path
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|int
name|version
parameter_list|)
throws|throws
name|Exception
block|{
name|curator
operator|.
name|setData
argument_list|()
operator|.
name|withVersion
argument_list|(
name|version
argument_list|)
operator|.
name|forPath
argument_list|(
name|path
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set data into a ZNode.    * @param path Path of the ZNode.    * @param data Data to set as String.    * @param version Version of the data to store.    * @throws Exception If it cannot contact Zookeeper.    */
DECL|method|setData (String path, String data, int version)
specifier|public
name|void
name|setData
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|data
parameter_list|,
name|int
name|version
parameter_list|)
throws|throws
name|Exception
block|{
name|byte
index|[]
name|bytes
init|=
name|data
operator|.
name|getBytes
argument_list|(
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|setData
argument_list|(
name|path
argument_list|,
name|bytes
argument_list|,
name|version
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get children of a ZNode.    * @param path Path of the ZNode.    * @return The list of children.    * @throws Exception If it cannot contact Zookeeper.    */
DECL|method|getChildren (final String path)
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getChildren
parameter_list|(
specifier|final
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|curator
operator|.
name|getChildren
argument_list|()
operator|.
name|forPath
argument_list|(
name|path
argument_list|)
return|;
block|}
comment|/**    * Check if a ZNode exists.    * @param path Path of the ZNode.    * @return If the ZNode exists.    * @throws Exception If it cannot contact Zookeeper.    */
DECL|method|exists (final String path)
specifier|public
name|boolean
name|exists
parameter_list|(
specifier|final
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|curator
operator|.
name|checkExists
argument_list|()
operator|.
name|forPath
argument_list|(
name|path
argument_list|)
operator|!=
literal|null
return|;
block|}
comment|/**    * Create a ZNode.    * @param path Path of the ZNode.    * @return If the ZNode was created.    * @throws Exception If it cannot contact Zookeeper.    */
DECL|method|create (final String path)
specifier|public
name|boolean
name|create
parameter_list|(
specifier|final
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|create
argument_list|(
name|path
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Create a ZNode.    * @param path Path of the ZNode.    * @param zkAcl ACL for the node.    * @return If the ZNode was created.    * @throws Exception If it cannot contact Zookeeper.    */
DECL|method|create (final String path, List<ACL> zkAcl)
specifier|public
name|boolean
name|create
parameter_list|(
specifier|final
name|String
name|path
parameter_list|,
name|List
argument_list|<
name|ACL
argument_list|>
name|zkAcl
parameter_list|)
throws|throws
name|Exception
block|{
name|boolean
name|created
init|=
literal|false
decl_stmt|;
if|if
condition|(
operator|!
name|exists
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|curator
operator|.
name|create
argument_list|()
operator|.
name|withMode
argument_list|(
name|CreateMode
operator|.
name|PERSISTENT
argument_list|)
operator|.
name|withACL
argument_list|(
name|zkAcl
argument_list|)
operator|.
name|forPath
argument_list|(
name|path
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|created
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|created
return|;
block|}
comment|/**    * Utility function to ensure that the configured base znode exists.    * This recursively creates the znode as well as all of its parents.    * @param path Path of the znode to create.    * @throws Exception If it cannot create the file.    */
DECL|method|createRootDirRecursively (String path)
specifier|public
name|void
name|createRootDirRecursively
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
name|createRootDirRecursively
argument_list|(
name|path
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Utility function to ensure that the configured base znode exists.    * This recursively creates the znode as well as all of its parents.    * @param path Path of the znode to create.    * @param zkAcl ACLs for ZooKeeper.    * @throws Exception If it cannot create the file.    */
DECL|method|createRootDirRecursively (String path, List<ACL> zkAcl)
specifier|public
name|void
name|createRootDirRecursively
parameter_list|(
name|String
name|path
parameter_list|,
name|List
argument_list|<
name|ACL
argument_list|>
name|zkAcl
parameter_list|)
throws|throws
name|Exception
block|{
name|String
index|[]
name|pathParts
init|=
name|path
operator|.
name|split
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|pathParts
operator|.
name|length
operator|>=
literal|1
operator|&&
name|pathParts
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
argument_list|,
literal|"Invalid path: %s"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|pathParts
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
operator|.
name|append
argument_list|(
name|pathParts
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|create
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
name|zkAcl
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Delete a ZNode.    * @param path Path of the ZNode.    * @return If the znode was deleted.    * @throws Exception If it cannot contact ZooKeeper.    */
DECL|method|delete (final String path)
specifier|public
name|boolean
name|delete
parameter_list|(
specifier|final
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|exists
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|curator
operator|.
name|delete
argument_list|()
operator|.
name|deletingChildrenIfNeeded
argument_list|()
operator|.
name|forPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Get the path for a ZNode.    * @param root Root of the ZNode.    * @param nodeName Name of the ZNode.    * @return Path for the ZNode.    */
DECL|method|getNodePath (String root, String nodeName)
specifier|public
specifier|static
name|String
name|getNodePath
parameter_list|(
name|String
name|root
parameter_list|,
name|String
name|nodeName
parameter_list|)
block|{
return|return
name|root
operator|+
literal|"/"
operator|+
name|nodeName
return|;
block|}
DECL|method|safeCreate (String path, byte[] data, List<ACL> acl, CreateMode mode, List<ACL> fencingACL, String fencingNodePath)
specifier|public
name|void
name|safeCreate
parameter_list|(
name|String
name|path
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|List
argument_list|<
name|ACL
argument_list|>
name|acl
parameter_list|,
name|CreateMode
name|mode
parameter_list|,
name|List
argument_list|<
name|ACL
argument_list|>
name|fencingACL
parameter_list|,
name|String
name|fencingNodePath
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|exists
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|SafeTransaction
name|transaction
init|=
name|createTransaction
argument_list|(
name|fencingACL
argument_list|,
name|fencingNodePath
argument_list|)
decl_stmt|;
name|transaction
operator|.
name|create
argument_list|(
name|path
argument_list|,
name|data
argument_list|,
name|acl
argument_list|,
name|mode
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Deletes the path. Checks for existence of path as well.    * @param path Path to be deleted.    * @throws Exception if any problem occurs while performing deletion.    */
DECL|method|safeDelete (final String path, List<ACL> fencingACL, String fencingNodePath)
specifier|public
name|void
name|safeDelete
parameter_list|(
specifier|final
name|String
name|path
parameter_list|,
name|List
argument_list|<
name|ACL
argument_list|>
name|fencingACL
parameter_list|,
name|String
name|fencingNodePath
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|exists
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|SafeTransaction
name|transaction
init|=
name|createTransaction
argument_list|(
name|fencingACL
argument_list|,
name|fencingNodePath
argument_list|)
decl_stmt|;
name|transaction
operator|.
name|delete
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|safeSetData (String path, byte[] data, int version, List<ACL> fencingACL, String fencingNodePath)
specifier|public
name|void
name|safeSetData
parameter_list|(
name|String
name|path
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|int
name|version
parameter_list|,
name|List
argument_list|<
name|ACL
argument_list|>
name|fencingACL
parameter_list|,
name|String
name|fencingNodePath
parameter_list|)
throws|throws
name|Exception
block|{
name|SafeTransaction
name|transaction
init|=
name|createTransaction
argument_list|(
name|fencingACL
argument_list|,
name|fencingNodePath
argument_list|)
decl_stmt|;
name|transaction
operator|.
name|setData
argument_list|(
name|path
argument_list|,
name|data
argument_list|,
name|version
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
DECL|method|createTransaction (List<ACL> fencingACL, String fencingNodePath)
specifier|public
name|SafeTransaction
name|createTransaction
parameter_list|(
name|List
argument_list|<
name|ACL
argument_list|>
name|fencingACL
parameter_list|,
name|String
name|fencingNodePath
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|new
name|SafeTransaction
argument_list|(
name|fencingACL
argument_list|,
name|fencingNodePath
argument_list|)
return|;
block|}
comment|/**    * Use curator transactions to ensure zk-operations are performed in an all    * or nothing fashion. This is equivalent to using ZooKeeper#multi.    */
DECL|class|SafeTransaction
specifier|public
class|class
name|SafeTransaction
block|{
DECL|field|fencingNodePath
specifier|private
name|String
name|fencingNodePath
decl_stmt|;
DECL|field|curatorOperations
specifier|private
name|List
argument_list|<
name|CuratorOp
argument_list|>
name|curatorOperations
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|SafeTransaction (List<ACL> fencingACL, String fencingNodePath)
name|SafeTransaction
parameter_list|(
name|List
argument_list|<
name|ACL
argument_list|>
name|fencingACL
parameter_list|,
name|String
name|fencingNodePath
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|fencingNodePath
operator|=
name|fencingNodePath
expr_stmt|;
name|curatorOperations
operator|.
name|add
argument_list|(
name|curator
operator|.
name|transactionOp
argument_list|()
operator|.
name|create
argument_list|()
operator|.
name|withMode
argument_list|(
name|CreateMode
operator|.
name|PERSISTENT
argument_list|)
operator|.
name|withACL
argument_list|(
name|fencingACL
argument_list|)
operator|.
name|forPath
argument_list|(
name|fencingNodePath
argument_list|,
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|commit ()
specifier|public
name|void
name|commit
parameter_list|()
throws|throws
name|Exception
block|{
name|curatorOperations
operator|.
name|add
argument_list|(
name|curator
operator|.
name|transactionOp
argument_list|()
operator|.
name|delete
argument_list|()
operator|.
name|forPath
argument_list|(
name|fencingNodePath
argument_list|)
argument_list|)
expr_stmt|;
name|curator
operator|.
name|transaction
argument_list|()
operator|.
name|forOperations
argument_list|(
name|curatorOperations
argument_list|)
expr_stmt|;
name|curatorOperations
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|create (String path, byte[] data, List<ACL> acl, CreateMode mode)
specifier|public
name|void
name|create
parameter_list|(
name|String
name|path
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|List
argument_list|<
name|ACL
argument_list|>
name|acl
parameter_list|,
name|CreateMode
name|mode
parameter_list|)
throws|throws
name|Exception
block|{
name|curatorOperations
operator|.
name|add
argument_list|(
name|curator
operator|.
name|transactionOp
argument_list|()
operator|.
name|create
argument_list|()
operator|.
name|withMode
argument_list|(
name|mode
argument_list|)
operator|.
name|withACL
argument_list|(
name|acl
argument_list|)
operator|.
name|forPath
argument_list|(
name|path
argument_list|,
name|data
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|delete (String path)
specifier|public
name|void
name|delete
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
name|curatorOperations
operator|.
name|add
argument_list|(
name|curator
operator|.
name|transactionOp
argument_list|()
operator|.
name|delete
argument_list|()
operator|.
name|forPath
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|setData (String path, byte[] data, int version)
specifier|public
name|void
name|setData
parameter_list|(
name|String
name|path
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|int
name|version
parameter_list|)
throws|throws
name|Exception
block|{
name|curatorOperations
operator|.
name|add
argument_list|(
name|curator
operator|.
name|transactionOp
argument_list|()
operator|.
name|setData
argument_list|()
operator|.
name|withVersion
argument_list|(
name|version
argument_list|)
operator|.
name|forPath
argument_list|(
name|path
argument_list|,
name|data
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

