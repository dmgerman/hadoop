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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|HashMultimap
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
name|Multimap
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
name|UnmodifiableIterator
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
name|Iterables
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
name|Collections2
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
name|hdfs
operator|.
name|protocol
operator|.
name|DatanodeAdminProperties
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
name|DatanodeInfo
operator|.
name|AdminStates
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
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicate
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
name|util
operator|.
name|CombinedHostsFileReader
import|;
end_import

begin_comment
comment|/**  * This class manages datanode configuration using a json file.  * Please refer to {@link CombinedHostsFileReader} for the json format.  *<p>  * Entries may or may not specify a port.  If they don't, we consider  * them to apply to every DataNode on that host. The code canonicalizes the  * entries into IP addresses.  *<p>  * The code ignores all entries that the DNS fails to resolve their IP  * addresses. This is okay because by default the NN rejects the registrations  * of DNs when it fails to do a forward and reverse lookup. Note that DNS  * resolutions are only done during the loading time to minimize the latency.  */
end_comment

begin_class
DECL|class|CombinedHostFileManager
specifier|public
class|class
name|CombinedHostFileManager
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
name|CombinedHostFileManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|hostProperties
specifier|private
name|HostProperties
name|hostProperties
init|=
operator|new
name|HostProperties
argument_list|()
decl_stmt|;
DECL|class|HostProperties
specifier|static
class|class
name|HostProperties
block|{
DECL|field|allDNs
specifier|private
name|Multimap
argument_list|<
name|InetAddress
argument_list|,
name|DatanodeAdminProperties
argument_list|>
name|allDNs
init|=
name|HashMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
comment|// optimization. If every node in the file isn't in service, it implies
comment|// any node is allowed to register with nn. This is equivalent to having
comment|// an empty "include" file.
DECL|field|emptyInServiceNodeLists
specifier|private
name|boolean
name|emptyInServiceNodeLists
init|=
literal|true
decl_stmt|;
DECL|method|add (InetAddress addr, DatanodeAdminProperties properties)
specifier|synchronized
name|void
name|add
parameter_list|(
name|InetAddress
name|addr
parameter_list|,
name|DatanodeAdminProperties
name|properties
parameter_list|)
block|{
name|allDNs
operator|.
name|put
argument_list|(
name|addr
argument_list|,
name|properties
argument_list|)
expr_stmt|;
if|if
condition|(
name|properties
operator|.
name|getAdminState
argument_list|()
operator|.
name|equals
argument_list|(
name|AdminStates
operator|.
name|NORMAL
argument_list|)
condition|)
block|{
name|emptyInServiceNodeLists
operator|=
literal|false
expr_stmt|;
block|}
block|}
comment|// If the includes list is empty, act as if everything is in the
comment|// includes list.
DECL|method|isIncluded (final InetSocketAddress address)
specifier|synchronized
name|boolean
name|isIncluded
parameter_list|(
specifier|final
name|InetSocketAddress
name|address
parameter_list|)
block|{
return|return
name|emptyInServiceNodeLists
operator|||
name|Iterables
operator|.
name|any
argument_list|(
name|allDNs
operator|.
name|get
argument_list|(
name|address
operator|.
name|getAddress
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Predicate
argument_list|<
name|DatanodeAdminProperties
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|DatanodeAdminProperties
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|getPort
argument_list|()
operator|==
literal|0
operator|||
name|input
operator|.
name|getPort
argument_list|()
operator|==
name|address
operator|.
name|getPort
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|isExcluded (final InetSocketAddress address)
specifier|synchronized
name|boolean
name|isExcluded
parameter_list|(
specifier|final
name|InetSocketAddress
name|address
parameter_list|)
block|{
return|return
name|Iterables
operator|.
name|any
argument_list|(
name|allDNs
operator|.
name|get
argument_list|(
name|address
operator|.
name|getAddress
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Predicate
argument_list|<
name|DatanodeAdminProperties
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|DatanodeAdminProperties
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|getAdminState
argument_list|()
operator|.
name|equals
argument_list|(
name|AdminStates
operator|.
name|DECOMMISSIONED
argument_list|)
operator|&&
operator|(
name|input
operator|.
name|getPort
argument_list|()
operator|==
literal|0
operator|||
name|input
operator|.
name|getPort
argument_list|()
operator|==
name|address
operator|.
name|getPort
argument_list|()
operator|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|getUpgradeDomain (final InetSocketAddress address)
specifier|synchronized
name|String
name|getUpgradeDomain
parameter_list|(
specifier|final
name|InetSocketAddress
name|address
parameter_list|)
block|{
name|Iterable
argument_list|<
name|DatanodeAdminProperties
argument_list|>
name|datanode
init|=
name|Iterables
operator|.
name|filter
argument_list|(
name|allDNs
operator|.
name|get
argument_list|(
name|address
operator|.
name|getAddress
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Predicate
argument_list|<
name|DatanodeAdminProperties
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|DatanodeAdminProperties
name|input
parameter_list|)
block|{
return|return
operator|(
name|input
operator|.
name|getPort
argument_list|()
operator|==
literal|0
operator|||
name|input
operator|.
name|getPort
argument_list|()
operator|==
name|address
operator|.
name|getPort
argument_list|()
operator|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|datanode
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
condition|?
name|datanode
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getUpgradeDomain
argument_list|()
else|:
literal|null
return|;
block|}
DECL|method|getIncludes ()
name|Iterable
argument_list|<
name|InetSocketAddress
argument_list|>
name|getIncludes
parameter_list|()
block|{
return|return
operator|new
name|Iterable
argument_list|<
name|InetSocketAddress
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|InetSocketAddress
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|HostIterator
argument_list|(
name|allDNs
operator|.
name|entries
argument_list|()
argument_list|)
return|;
block|}
block|}
return|;
block|}
DECL|method|getExcludes ()
name|Iterable
argument_list|<
name|InetSocketAddress
argument_list|>
name|getExcludes
parameter_list|()
block|{
return|return
operator|new
name|Iterable
argument_list|<
name|InetSocketAddress
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|InetSocketAddress
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|HostIterator
argument_list|(
name|Collections2
operator|.
name|filter
argument_list|(
name|allDNs
operator|.
name|entries
argument_list|()
argument_list|,
operator|new
name|Predicate
argument_list|<
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
argument_list|<
name|InetAddress
argument_list|,
name|DatanodeAdminProperties
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
argument_list|<
name|InetAddress
argument_list|,
name|DatanodeAdminProperties
argument_list|>
name|entry
parameter_list|)
block|{
return|return
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getAdminState
argument_list|()
operator|.
name|equals
argument_list|(
name|AdminStates
operator|.
name|DECOMMISSIONED
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
return|;
block|}
block|}
return|;
block|}
DECL|method|getMaintenanceExpireTimeInMS ( final InetSocketAddress address)
specifier|synchronized
name|long
name|getMaintenanceExpireTimeInMS
parameter_list|(
specifier|final
name|InetSocketAddress
name|address
parameter_list|)
block|{
name|Iterable
argument_list|<
name|DatanodeAdminProperties
argument_list|>
name|datanode
init|=
name|Iterables
operator|.
name|filter
argument_list|(
name|allDNs
operator|.
name|get
argument_list|(
name|address
operator|.
name|getAddress
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Predicate
argument_list|<
name|DatanodeAdminProperties
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|DatanodeAdminProperties
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|getAdminState
argument_list|()
operator|.
name|equals
argument_list|(
name|AdminStates
operator|.
name|IN_MAINTENANCE
argument_list|)
operator|&&
operator|(
name|input
operator|.
name|getPort
argument_list|()
operator|==
literal|0
operator|||
name|input
operator|.
name|getPort
argument_list|()
operator|==
name|address
operator|.
name|getPort
argument_list|()
operator|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|// if DN isn't set to maintenance state, ignore MaintenanceExpireTimeInMS
comment|// set in the config.
return|return
name|datanode
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
condition|?
name|datanode
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getMaintenanceExpireTimeInMS
argument_list|()
else|:
literal|0
return|;
block|}
DECL|class|HostIterator
specifier|static
class|class
name|HostIterator
extends|extends
name|UnmodifiableIterator
argument_list|<
name|InetSocketAddress
argument_list|>
block|{
specifier|private
specifier|final
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|InetAddress
argument_list|,
DECL|field|it
name|DatanodeAdminProperties
argument_list|>
argument_list|>
name|it
decl_stmt|;
DECL|method|HostIterator (Collection<java.util.Map.Entry<InetAddress, DatanodeAdminProperties>> nodes)
specifier|public
name|HostIterator
parameter_list|(
name|Collection
argument_list|<
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
argument_list|<
name|InetAddress
argument_list|,
name|DatanodeAdminProperties
argument_list|>
argument_list|>
name|nodes
parameter_list|)
block|{
name|this
operator|.
name|it
operator|=
name|nodes
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasNext ()
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|it
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|next ()
specifier|public
name|InetSocketAddress
name|next
parameter_list|()
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|InetAddress
argument_list|,
name|DatanodeAdminProperties
argument_list|>
name|e
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
operator|new
name|InetSocketAddress
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getIncludes ()
specifier|public
name|Iterable
argument_list|<
name|InetSocketAddress
argument_list|>
name|getIncludes
parameter_list|()
block|{
return|return
name|hostProperties
operator|.
name|getIncludes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getExcludes ()
specifier|public
name|Iterable
argument_list|<
name|InetSocketAddress
argument_list|>
name|getExcludes
parameter_list|()
block|{
return|return
name|hostProperties
operator|.
name|getExcludes
argument_list|()
return|;
block|}
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
argument_list|)
expr_stmt|;
block|}
DECL|method|refresh (final String hostsFile)
specifier|private
name|void
name|refresh
parameter_list|(
specifier|final
name|String
name|hostsFile
parameter_list|)
throws|throws
name|IOException
block|{
name|HostProperties
name|hostProps
init|=
operator|new
name|HostProperties
argument_list|()
decl_stmt|;
name|DatanodeAdminProperties
index|[]
name|all
init|=
name|CombinedHostsFileReader
operator|.
name|readFile
argument_list|(
name|hostsFile
argument_list|)
decl_stmt|;
for|for
control|(
name|DatanodeAdminProperties
name|properties
range|:
name|all
control|)
block|{
name|InetSocketAddress
name|addr
init|=
name|parseEntry
argument_list|(
name|hostsFile
argument_list|,
name|properties
operator|.
name|getHostName
argument_list|()
argument_list|,
name|properties
operator|.
name|getPort
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|addr
operator|!=
literal|null
condition|)
block|{
name|hostProps
operator|.
name|add
argument_list|(
name|addr
operator|.
name|getAddress
argument_list|()
argument_list|,
name|properties
argument_list|)
expr_stmt|;
block|}
block|}
name|refresh
argument_list|(
name|hostProps
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|parseEntry (final String fn, final String hostName, final int port)
specifier|static
name|InetSocketAddress
name|parseEntry
parameter_list|(
specifier|final
name|String
name|fn
parameter_list|,
specifier|final
name|String
name|hostName
parameter_list|,
specifier|final
name|int
name|port
parameter_list|)
block|{
name|InetSocketAddress
name|addr
init|=
operator|new
name|InetSocketAddress
argument_list|(
name|hostName
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
literal|"Failed to resolve {} in {}. "
argument_list|,
name|hostName
argument_list|,
name|fn
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
annotation|@
name|Override
DECL|method|isIncluded (final DatanodeID dn)
specifier|public
specifier|synchronized
name|boolean
name|isIncluded
parameter_list|(
specifier|final
name|DatanodeID
name|dn
parameter_list|)
block|{
return|return
name|hostProperties
operator|.
name|isIncluded
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
DECL|method|isExcluded (final DatanodeID dn)
specifier|public
specifier|synchronized
name|boolean
name|isExcluded
parameter_list|(
specifier|final
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
DECL|method|isExcluded (final InetSocketAddress address)
specifier|private
name|boolean
name|isExcluded
parameter_list|(
specifier|final
name|InetSocketAddress
name|address
parameter_list|)
block|{
return|return
name|hostProperties
operator|.
name|isExcluded
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
return|return
name|hostProperties
operator|.
name|getUpgradeDomain
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
DECL|method|getMaintenanceExpirationTimeInMS (DatanodeID dn)
specifier|public
name|long
name|getMaintenanceExpirationTimeInMS
parameter_list|(
name|DatanodeID
name|dn
parameter_list|)
block|{
return|return
name|hostProperties
operator|.
name|getMaintenanceExpireTimeInMS
argument_list|(
name|dn
operator|.
name|getResolvedAddress
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Set the properties lists by the new instances. The    * old instance is discarded.    * @param hostProperties the new properties list    */
annotation|@
name|VisibleForTesting
DECL|method|refresh (final HostProperties hostProperties)
specifier|private
name|void
name|refresh
parameter_list|(
specifier|final
name|HostProperties
name|hostProperties
parameter_list|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|this
operator|.
name|hostProperties
operator|=
name|hostProperties
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

