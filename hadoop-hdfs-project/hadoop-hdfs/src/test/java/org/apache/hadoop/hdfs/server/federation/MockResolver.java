begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation
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
name|federation
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|HashSet
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
name|Set
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
name|server
operator|.
name|federation
operator|.
name|resolver
operator|.
name|ActiveNamenodeResolver
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
name|federation
operator|.
name|resolver
operator|.
name|FederationNamenodeContext
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
name|federation
operator|.
name|resolver
operator|.
name|FederationNamenodeServiceState
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
name|federation
operator|.
name|resolver
operator|.
name|FederationNamespaceInfo
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
name|federation
operator|.
name|resolver
operator|.
name|FileSubclusterResolver
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
name|federation
operator|.
name|resolver
operator|.
name|NamenodePriorityComparator
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
name|federation
operator|.
name|resolver
operator|.
name|NamenodeStatusReport
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
name|federation
operator|.
name|resolver
operator|.
name|PathLocation
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
name|federation
operator|.
name|resolver
operator|.
name|RemoteLocation
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
name|federation
operator|.
name|store
operator|.
name|StateStoreService
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

begin_comment
comment|/**  * In-memory cache/mock of a namenode and file resolver. Stores the most  * recently updated NN information for each nameservice and block pool. It also  * stores a virtual mount table for resolving global namespace paths to local NN  * paths.  */
end_comment

begin_class
DECL|class|MockResolver
specifier|public
class|class
name|MockResolver
implements|implements
name|ActiveNamenodeResolver
implements|,
name|FileSubclusterResolver
block|{
DECL|field|resolver
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|FederationNamenodeContext
argument_list|>
argument_list|>
name|resolver
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|locations
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RemoteLocation
argument_list|>
argument_list|>
name|locations
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|namespaces
specifier|private
name|Set
argument_list|<
name|FederationNamespaceInfo
argument_list|>
name|namespaces
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|defaultNamespace
specifier|private
name|String
name|defaultNamespace
init|=
literal|null
decl_stmt|;
DECL|method|MockResolver (Configuration conf, StateStoreService store)
specifier|public
name|MockResolver
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|StateStoreService
name|store
parameter_list|)
block|{
name|this
operator|.
name|cleanRegistrations
argument_list|()
expr_stmt|;
block|}
DECL|method|addLocation (String mount, String nsId, String location)
specifier|public
name|void
name|addLocation
parameter_list|(
name|String
name|mount
parameter_list|,
name|String
name|nsId
parameter_list|,
name|String
name|location
parameter_list|)
block|{
name|List
argument_list|<
name|RemoteLocation
argument_list|>
name|locationsList
init|=
name|this
operator|.
name|locations
operator|.
name|get
argument_list|(
name|mount
argument_list|)
decl_stmt|;
if|if
condition|(
name|locationsList
operator|==
literal|null
condition|)
block|{
name|locationsList
operator|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|locations
operator|.
name|put
argument_list|(
name|mount
argument_list|,
name|locationsList
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RemoteLocation
name|remoteLocation
init|=
operator|new
name|RemoteLocation
argument_list|(
name|nsId
argument_list|,
name|location
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|locationsList
operator|.
name|contains
argument_list|(
name|remoteLocation
argument_list|)
condition|)
block|{
name|locationsList
operator|.
name|add
argument_list|(
name|remoteLocation
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|defaultNamespace
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|defaultNamespace
operator|=
name|nsId
expr_stmt|;
block|}
block|}
DECL|method|cleanRegistrations ()
specifier|public
specifier|synchronized
name|void
name|cleanRegistrations
parameter_list|()
block|{
name|this
operator|.
name|resolver
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|namespaces
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|updateActiveNamenode ( String nsId, InetSocketAddress successfulAddress)
specifier|public
name|void
name|updateActiveNamenode
parameter_list|(
name|String
name|nsId
parameter_list|,
name|InetSocketAddress
name|successfulAddress
parameter_list|)
block|{
name|String
name|address
init|=
name|successfulAddress
operator|.
name|getHostName
argument_list|()
operator|+
literal|":"
operator|+
name|successfulAddress
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|String
name|key
init|=
name|nsId
decl_stmt|;
if|if
condition|(
name|key
operator|!=
literal|null
condition|)
block|{
comment|// Update the active entry
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|List
argument_list|<
name|FederationNamenodeContext
argument_list|>
name|namenodes
init|=
operator|(
name|List
argument_list|<
name|FederationNamenodeContext
argument_list|>
operator|)
name|this
operator|.
name|resolver
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
for|for
control|(
name|FederationNamenodeContext
name|namenode
range|:
name|namenodes
control|)
block|{
if|if
condition|(
name|namenode
operator|.
name|getRpcAddress
argument_list|()
operator|.
name|equals
argument_list|(
name|address
argument_list|)
condition|)
block|{
name|MockNamenodeContext
name|nn
init|=
operator|(
name|MockNamenodeContext
operator|)
name|namenode
decl_stmt|;
name|nn
operator|.
name|setState
argument_list|(
name|FederationNamenodeServiceState
operator|.
name|ACTIVE
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
comment|// This operation modifies the list so we need to be careful
synchronized|synchronized
init|(
name|namenodes
init|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|namenodes
argument_list|,
operator|new
name|NamenodePriorityComparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|FederationNamenodeContext
argument_list|>
DECL|method|getNamenodesForNameserviceId (String nameserviceId)
name|getNamenodesForNameserviceId
parameter_list|(
name|String
name|nameserviceId
parameter_list|)
block|{
comment|// Return a copy of the list because it is updated periodically
name|List
argument_list|<
name|?
extends|extends
name|FederationNamenodeContext
argument_list|>
name|namenodes
init|=
name|this
operator|.
name|resolver
operator|.
name|get
argument_list|(
name|nameserviceId
argument_list|)
decl_stmt|;
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|namenodes
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getNamenodesForBlockPoolId ( String blockPoolId)
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|FederationNamenodeContext
argument_list|>
name|getNamenodesForBlockPoolId
parameter_list|(
name|String
name|blockPoolId
parameter_list|)
block|{
comment|// Return a copy of the list because it is updated periodically
name|List
argument_list|<
name|?
extends|extends
name|FederationNamenodeContext
argument_list|>
name|namenodes
init|=
name|this
operator|.
name|resolver
operator|.
name|get
argument_list|(
name|blockPoolId
argument_list|)
decl_stmt|;
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|namenodes
argument_list|)
argument_list|)
return|;
block|}
DECL|class|MockNamenodeContext
specifier|private
specifier|static
class|class
name|MockNamenodeContext
implements|implements
name|FederationNamenodeContext
block|{
DECL|field|namenodeId
specifier|private
name|String
name|namenodeId
decl_stmt|;
DECL|field|nameserviceId
specifier|private
name|String
name|nameserviceId
decl_stmt|;
DECL|field|webAddress
specifier|private
name|String
name|webAddress
decl_stmt|;
DECL|field|rpcAddress
specifier|private
name|String
name|rpcAddress
decl_stmt|;
DECL|field|serviceAddress
specifier|private
name|String
name|serviceAddress
decl_stmt|;
DECL|field|lifelineAddress
specifier|private
name|String
name|lifelineAddress
decl_stmt|;
DECL|field|state
specifier|private
name|FederationNamenodeServiceState
name|state
decl_stmt|;
DECL|field|dateModified
specifier|private
name|long
name|dateModified
decl_stmt|;
DECL|method|MockNamenodeContext ( String rpc, String service, String lifeline, String web, String ns, String nn, FederationNamenodeServiceState state)
name|MockNamenodeContext
parameter_list|(
name|String
name|rpc
parameter_list|,
name|String
name|service
parameter_list|,
name|String
name|lifeline
parameter_list|,
name|String
name|web
parameter_list|,
name|String
name|ns
parameter_list|,
name|String
name|nn
parameter_list|,
name|FederationNamenodeServiceState
name|state
parameter_list|)
block|{
name|this
operator|.
name|rpcAddress
operator|=
name|rpc
expr_stmt|;
name|this
operator|.
name|serviceAddress
operator|=
name|service
expr_stmt|;
name|this
operator|.
name|lifelineAddress
operator|=
name|lifeline
expr_stmt|;
name|this
operator|.
name|webAddress
operator|=
name|web
expr_stmt|;
name|this
operator|.
name|namenodeId
operator|=
name|nn
expr_stmt|;
name|this
operator|.
name|nameserviceId
operator|=
name|ns
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|dateModified
operator|=
name|Time
operator|.
name|now
argument_list|()
expr_stmt|;
block|}
DECL|method|setState (FederationNamenodeServiceState newState)
specifier|public
name|void
name|setState
parameter_list|(
name|FederationNamenodeServiceState
name|newState
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|newState
expr_stmt|;
name|this
operator|.
name|dateModified
operator|=
name|Time
operator|.
name|now
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getRpcAddress ()
specifier|public
name|String
name|getRpcAddress
parameter_list|()
block|{
return|return
name|rpcAddress
return|;
block|}
annotation|@
name|Override
DECL|method|getServiceAddress ()
specifier|public
name|String
name|getServiceAddress
parameter_list|()
block|{
return|return
name|serviceAddress
return|;
block|}
annotation|@
name|Override
DECL|method|getLifelineAddress ()
specifier|public
name|String
name|getLifelineAddress
parameter_list|()
block|{
return|return
name|lifelineAddress
return|;
block|}
annotation|@
name|Override
DECL|method|getWebAddress ()
specifier|public
name|String
name|getWebAddress
parameter_list|()
block|{
return|return
name|webAddress
return|;
block|}
annotation|@
name|Override
DECL|method|getNamenodeKey ()
specifier|public
name|String
name|getNamenodeKey
parameter_list|()
block|{
return|return
name|nameserviceId
operator|+
literal|" "
operator|+
name|namenodeId
operator|+
literal|" "
operator|+
name|rpcAddress
return|;
block|}
annotation|@
name|Override
DECL|method|getNameserviceId ()
specifier|public
name|String
name|getNameserviceId
parameter_list|()
block|{
return|return
name|nameserviceId
return|;
block|}
annotation|@
name|Override
DECL|method|getNamenodeId ()
specifier|public
name|String
name|getNamenodeId
parameter_list|()
block|{
return|return
name|namenodeId
return|;
block|}
annotation|@
name|Override
DECL|method|getState ()
specifier|public
name|FederationNamenodeServiceState
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
annotation|@
name|Override
DECL|method|getDateModified ()
specifier|public
name|long
name|getDateModified
parameter_list|()
block|{
return|return
name|dateModified
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|registerNamenode (NamenodeStatusReport report)
specifier|public
specifier|synchronized
name|boolean
name|registerNamenode
parameter_list|(
name|NamenodeStatusReport
name|report
parameter_list|)
throws|throws
name|IOException
block|{
name|MockNamenodeContext
name|context
init|=
operator|new
name|MockNamenodeContext
argument_list|(
name|report
operator|.
name|getRpcAddress
argument_list|()
argument_list|,
name|report
operator|.
name|getServiceAddress
argument_list|()
argument_list|,
name|report
operator|.
name|getLifelineAddress
argument_list|()
argument_list|,
name|report
operator|.
name|getWebAddress
argument_list|()
argument_list|,
name|report
operator|.
name|getNameserviceId
argument_list|()
argument_list|,
name|report
operator|.
name|getNamenodeId
argument_list|()
argument_list|,
name|report
operator|.
name|getState
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|nsId
init|=
name|report
operator|.
name|getNameserviceId
argument_list|()
decl_stmt|;
name|String
name|bpId
init|=
name|report
operator|.
name|getBlockPoolId
argument_list|()
decl_stmt|;
name|String
name|cId
init|=
name|report
operator|.
name|getClusterId
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|List
argument_list|<
name|MockNamenodeContext
argument_list|>
name|existingItems
init|=
operator|(
name|List
argument_list|<
name|MockNamenodeContext
argument_list|>
operator|)
name|this
operator|.
name|resolver
operator|.
name|get
argument_list|(
name|nsId
argument_list|)
decl_stmt|;
if|if
condition|(
name|existingItems
operator|==
literal|null
condition|)
block|{
name|existingItems
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|resolver
operator|.
name|put
argument_list|(
name|bpId
argument_list|,
name|existingItems
argument_list|)
expr_stmt|;
name|this
operator|.
name|resolver
operator|.
name|put
argument_list|(
name|nsId
argument_list|,
name|existingItems
argument_list|)
expr_stmt|;
block|}
name|boolean
name|added
init|=
literal|false
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
name|existingItems
operator|.
name|size
argument_list|()
operator|&&
operator|!
name|added
condition|;
name|i
operator|++
control|)
block|{
name|MockNamenodeContext
name|existing
init|=
name|existingItems
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|existing
operator|.
name|getNamenodeKey
argument_list|()
operator|.
name|equals
argument_list|(
name|context
operator|.
name|getNamenodeKey
argument_list|()
argument_list|)
condition|)
block|{
name|existingItems
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|added
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|added
condition|)
block|{
name|existingItems
operator|.
name|add
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|existingItems
argument_list|,
operator|new
name|NamenodePriorityComparator
argument_list|()
argument_list|)
expr_stmt|;
name|FederationNamespaceInfo
name|info
init|=
operator|new
name|FederationNamespaceInfo
argument_list|(
name|bpId
argument_list|,
name|cId
argument_list|,
name|nsId
argument_list|)
decl_stmt|;
name|this
operator|.
name|namespaces
operator|.
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|getNamespaces ()
specifier|public
name|Set
argument_list|<
name|FederationNamespaceInfo
argument_list|>
name|getNamespaces
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|this
operator|.
name|namespaces
return|;
block|}
annotation|@
name|Override
DECL|method|getDestinationForPath (String path)
specifier|public
name|PathLocation
name|getDestinationForPath
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|namespaceSet
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RemoteLocation
argument_list|>
name|remoteLocations
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|this
operator|.
name|locations
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|path
operator|.
name|startsWith
argument_list|(
name|key
argument_list|)
condition|)
block|{
for|for
control|(
name|RemoteLocation
name|location
range|:
name|this
operator|.
name|locations
operator|.
name|get
argument_list|(
name|key
argument_list|)
control|)
block|{
name|String
name|finalPath
init|=
name|location
operator|.
name|getDest
argument_list|()
operator|+
name|path
operator|.
name|substring
argument_list|(
name|key
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|nameservice
init|=
name|location
operator|.
name|getNameserviceId
argument_list|()
decl_stmt|;
name|RemoteLocation
name|remoteLocation
init|=
operator|new
name|RemoteLocation
argument_list|(
name|nameservice
argument_list|,
name|finalPath
argument_list|)
decl_stmt|;
name|remoteLocations
operator|.
name|add
argument_list|(
name|remoteLocation
argument_list|)
expr_stmt|;
name|namespaceSet
operator|.
name|add
argument_list|(
name|nameservice
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
if|if
condition|(
name|remoteLocations
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// Path isn't supported, mimic resolver behavior.
return|return
literal|null
return|;
block|}
return|return
operator|new
name|PathLocation
argument_list|(
name|path
argument_list|,
name|remoteLocations
argument_list|,
name|namespaceSet
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getMountPoints (String path)
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getMountPoints
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|mounts
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|equals
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
comment|// Mounts only supported under root level
for|for
control|(
name|String
name|mount
range|:
name|this
operator|.
name|locations
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|mount
operator|.
name|length
argument_list|()
operator|>
literal|1
condition|)
block|{
comment|// Remove leading slash, this is the behavior of the mount tree,
comment|// return only names.
name|mounts
operator|.
name|add
argument_list|(
name|mount
operator|.
name|replace
argument_list|(
literal|"/"
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|mounts
return|;
block|}
annotation|@
name|Override
DECL|method|setRouterId (String router)
specifier|public
name|void
name|setRouterId
parameter_list|(
name|String
name|router
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|getDefaultNamespace ()
specifier|public
name|String
name|getDefaultNamespace
parameter_list|()
block|{
return|return
name|defaultNamespace
return|;
block|}
block|}
end_class

end_unit

