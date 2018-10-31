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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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
name|DFSConfigKeys
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
name|util
operator|.
name|HostsFileReader
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
name|InetSocketAddress
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
name|net
operator|.
name|URISyntaxException
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

begin_comment
comment|/**  * This class manages the include and exclude files for HDFS.  *<p>  * These files control which DataNodes the NameNode expects to see in the  * cluster.  Loosely speaking, the include file, if it exists and is not  * empty, is a list of everything we expect to see.  The exclude file is  * a list of everything we want to ignore if we do see it.  *<p>  * Entries may or may not specify a port.  If they don't, we consider  * them to apply to every DataNode on that host. The code canonicalizes the  * entries into IP addresses.  *<p>  * The code ignores all entries that the DNS fails to resolve their IP  * addresses. This is okay because by default the NN rejects the registrations  * of DNs when it fails to do a forward and reverse lookup. Note that DNS  * resolutions are only done during the loading time to minimize the latency.  */
end_comment

begin_class
DECL|class|HostFileManager
specifier|public
class|class
name|HostFileManager
extends|extends
name|HostConfigManager
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
name|HostFileManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|includes
specifier|private
name|HostSet
name|includes
init|=
operator|new
name|HostSet
argument_list|()
decl_stmt|;
DECL|field|excludes
specifier|private
name|HostSet
name|excludes
init|=
operator|new
name|HostSet
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
DECL|method|refresh ()
specifier|public
name|void
name|refresh
parameter_list|()
throws|throws
name|IOException
block|{
name|refresh
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HOSTS
argument_list|,
literal|""
argument_list|)
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HOSTS_EXCLUDE
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|readFile (String type, String filename)
specifier|private
specifier|static
name|HostSet
name|readFile
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|filename
parameter_list|)
throws|throws
name|IOException
block|{
name|HostSet
name|res
init|=
operator|new
name|HostSet
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|filename
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|HashSet
argument_list|<
name|String
argument_list|>
name|entrySet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|HostsFileReader
operator|.
name|readFileToSet
argument_list|(
name|type
argument_list|,
name|filename
argument_list|,
name|entrySet
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|str
range|:
name|entrySet
control|)
block|{
name|InetSocketAddress
name|addr
init|=
name|parseEntry
argument_list|(
name|type
argument_list|,
name|filename
argument_list|,
name|str
argument_list|)
decl_stmt|;
if|if
condition|(
name|addr
operator|!=
literal|null
condition|)
block|{
name|res
operator|.
name|add
argument_list|(
name|addr
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|res
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|parseEntry (String type, String fn, String line)
specifier|static
name|InetSocketAddress
name|parseEntry
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|fn
parameter_list|,
name|String
name|line
parameter_list|)
block|{
try|try
block|{
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"dummy"
argument_list|,
name|line
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|int
name|port
init|=
name|uri
operator|.
name|getPort
argument_list|()
operator|==
operator|-
literal|1
condition|?
literal|0
else|:
name|uri
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|InetSocketAddress
name|addr
init|=
operator|new
name|InetSocketAddress
argument_list|(
name|uri
operator|.
name|getHost
argument_list|()
argument_list|,
name|port
argument_list|)
decl_stmt|;
if|if
condition|(
name|addr
operator|.
name|isUnresolved
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Failed to resolve address `%s` in `%s`. "
operator|+
literal|"Ignoring in the %s list."
argument_list|,
name|line
argument_list|,
name|fn
argument_list|,
name|type
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
name|addr
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Failed to parse `%s` in `%s`. "
operator|+
literal|"Ignoring in "
operator|+
literal|"the %s list."
argument_list|,
name|line
argument_list|,
name|fn
argument_list|,
name|type
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getIncludes ()
specifier|public
specifier|synchronized
name|HostSet
name|getIncludes
parameter_list|()
block|{
return|return
name|includes
return|;
block|}
annotation|@
name|Override
DECL|method|getExcludes ()
specifier|public
specifier|synchronized
name|HostSet
name|getExcludes
parameter_list|()
block|{
return|return
name|excludes
return|;
block|}
comment|// If the includes list is empty, act as if everything is in the
comment|// includes list.
annotation|@
name|Override
DECL|method|isIncluded (DatanodeID dn)
specifier|public
specifier|synchronized
name|boolean
name|isIncluded
parameter_list|(
name|DatanodeID
name|dn
parameter_list|)
block|{
return|return
name|includes
operator|.
name|isEmpty
argument_list|()
operator|||
name|includes
operator|.
name|match
argument_list|(
name|dn
operator|.
name|getResolvedAddress
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isExcluded (DatanodeID dn)
specifier|public
specifier|synchronized
name|boolean
name|isExcluded
parameter_list|(
name|DatanodeID
name|dn
parameter_list|)
block|{
return|return
name|isExcluded
argument_list|(
name|dn
operator|.
name|getResolvedAddress
argument_list|()
argument_list|)
return|;
block|}
DECL|method|isExcluded (InetSocketAddress address)
specifier|private
name|boolean
name|isExcluded
parameter_list|(
name|InetSocketAddress
name|address
parameter_list|)
block|{
return|return
name|excludes
operator|.
name|match
argument_list|(
name|address
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getUpgradeDomain (final DatanodeID dn)
specifier|public
specifier|synchronized
name|String
name|getUpgradeDomain
parameter_list|(
specifier|final
name|DatanodeID
name|dn
parameter_list|)
block|{
comment|// The include/exclude files based config doesn't support upgrade domain
comment|// config.
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getMaintenanceExpirationTimeInMS (DatanodeID dn)
specifier|public
name|long
name|getMaintenanceExpirationTimeInMS
parameter_list|(
name|DatanodeID
name|dn
parameter_list|)
block|{
comment|// The include/exclude files based config doesn't support maintenance mode.
return|return
literal|0
return|;
block|}
comment|/**    * Read the includes and excludes lists from the named files.  Any previous    * includes and excludes lists are discarded.    * @param includeFile the path to the new includes list    * @param excludeFile the path to the new excludes list    * @throws IOException thrown if there is a problem reading one of the files    */
DECL|method|refresh (String includeFile, String excludeFile)
specifier|private
name|void
name|refresh
parameter_list|(
name|String
name|includeFile
parameter_list|,
name|String
name|excludeFile
parameter_list|)
throws|throws
name|IOException
block|{
name|HostSet
name|newIncludes
init|=
name|readFile
argument_list|(
literal|"included"
argument_list|,
name|includeFile
argument_list|)
decl_stmt|;
name|HostSet
name|newExcludes
init|=
name|readFile
argument_list|(
literal|"excluded"
argument_list|,
name|excludeFile
argument_list|)
decl_stmt|;
name|refresh
argument_list|(
name|newIncludes
argument_list|,
name|newExcludes
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set the includes and excludes lists by the new HostSet instances. The    * old instances are discarded.    * @param newIncludes the new includes list    * @param newExcludes the new excludes list    */
annotation|@
name|VisibleForTesting
DECL|method|refresh (HostSet newIncludes, HostSet newExcludes)
name|void
name|refresh
parameter_list|(
name|HostSet
name|newIncludes
parameter_list|,
name|HostSet
name|newExcludes
parameter_list|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|includes
operator|=
name|newIncludes
expr_stmt|;
name|excludes
operator|=
name|newExcludes
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

