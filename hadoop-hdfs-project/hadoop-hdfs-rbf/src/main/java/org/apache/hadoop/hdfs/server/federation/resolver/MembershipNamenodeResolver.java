begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.resolver
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
operator|.
name|resolver
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
name|server
operator|.
name|federation
operator|.
name|resolver
operator|.
name|FederationNamenodeServiceState
operator|.
name|ACTIVE
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
name|server
operator|.
name|federation
operator|.
name|resolver
operator|.
name|FederationNamenodeServiceState
operator|.
name|EXPIRED
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
name|server
operator|.
name|federation
operator|.
name|resolver
operator|.
name|FederationNamenodeServiceState
operator|.
name|UNAVAILABLE
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
name|Iterator
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
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|ConcurrentHashMap
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
name|store
operator|.
name|DisabledNameserviceStore
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
name|MembershipStore
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
name|RecordStore
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
name|StateStoreCache
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|StateStoreUnavailableException
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
name|protocol
operator|.
name|GetNamenodeRegistrationsRequest
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
name|protocol
operator|.
name|GetNamenodeRegistrationsResponse
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
name|protocol
operator|.
name|GetNamespaceInfoRequest
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
name|protocol
operator|.
name|GetNamespaceInfoResponse
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
name|protocol
operator|.
name|NamenodeHeartbeatRequest
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
name|protocol
operator|.
name|UpdateNamenodeRegistrationRequest
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
name|records
operator|.
name|MembershipState
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
name|records
operator|.
name|MembershipStats
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

begin_comment
comment|/**  * Implements a cached lookup of the most recently active namenode for a  * particular nameservice. Relies on the {@link StateStoreService} to  * discover available nameservices and namenodes.  */
end_comment

begin_class
DECL|class|MembershipNamenodeResolver
specifier|public
class|class
name|MembershipNamenodeResolver
implements|implements
name|ActiveNamenodeResolver
implements|,
name|StateStoreCache
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
name|MembershipNamenodeResolver
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Reference to the State Store. */
DECL|field|stateStore
specifier|private
specifier|final
name|StateStoreService
name|stateStore
decl_stmt|;
comment|/** Membership State Store interface. */
DECL|field|membershipInterface
specifier|private
name|MembershipStore
name|membershipInterface
decl_stmt|;
comment|/** Disabled Nameservice State Store interface. */
DECL|field|disabledNameserviceInterface
specifier|private
name|DisabledNameserviceStore
name|disabledNameserviceInterface
decl_stmt|;
comment|/** Parent router ID. */
DECL|field|routerId
specifier|private
name|String
name|routerId
decl_stmt|;
comment|/** Cached lookup of NN for nameservice. Invalidated on cache refresh. */
DECL|field|cacheNS
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
name|cacheNS
decl_stmt|;
comment|/** Cached lookup of NN for block pool. Invalidated on cache refresh. */
DECL|field|cacheBP
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
name|cacheBP
decl_stmt|;
DECL|method|MembershipNamenodeResolver ( Configuration conf, StateStoreService store)
specifier|public
name|MembershipNamenodeResolver
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|StateStoreService
name|store
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|stateStore
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|cacheNS
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|cacheBP
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|stateStore
operator|!=
literal|null
condition|)
block|{
comment|// Request cache updates from the state store
name|this
operator|.
name|stateStore
operator|.
name|registerCacheExternal
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getMembershipStore ()
specifier|private
specifier|synchronized
name|MembershipStore
name|getMembershipStore
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|this
operator|.
name|membershipInterface
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|membershipInterface
operator|=
name|getStoreInterface
argument_list|(
name|MembershipStore
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
return|return
name|this
operator|.
name|membershipInterface
return|;
block|}
DECL|method|getDisabledNameserviceStore ()
specifier|private
specifier|synchronized
name|DisabledNameserviceStore
name|getDisabledNameserviceStore
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|this
operator|.
name|disabledNameserviceInterface
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|disabledNameserviceInterface
operator|=
name|getStoreInterface
argument_list|(
name|DisabledNameserviceStore
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
return|return
name|this
operator|.
name|disabledNameserviceInterface
return|;
block|}
DECL|method|getStoreInterface (Class<T> clazz)
specifier|private
parameter_list|<
name|T
extends|extends
name|RecordStore
argument_list|<
name|?
argument_list|>
parameter_list|>
name|T
name|getStoreInterface
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
throws|throws
name|IOException
block|{
name|T
name|store
init|=
name|this
operator|.
name|stateStore
operator|.
name|getRegisteredRecordStore
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
if|if
condition|(
name|store
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"State Store does not have an interface for "
operator|+
name|clazz
operator|.
name|getSimpleName
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|store
return|;
block|}
annotation|@
name|Override
DECL|method|loadCache (boolean force)
specifier|public
name|boolean
name|loadCache
parameter_list|(
name|boolean
name|force
parameter_list|)
block|{
comment|// Our cache depends on the store, update it first
try|try
block|{
name|MembershipStore
name|membership
init|=
name|getMembershipStore
argument_list|()
decl_stmt|;
name|membership
operator|.
name|loadCache
argument_list|(
name|force
argument_list|)
expr_stmt|;
name|DisabledNameserviceStore
name|disabled
init|=
name|getDisabledNameserviceStore
argument_list|()
decl_stmt|;
name|disabled
operator|.
name|loadCache
argument_list|(
name|force
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot update membership from the State Store"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|// Force refresh of active NN cache
name|cacheBP
operator|.
name|clear
argument_list|()
expr_stmt|;
name|cacheNS
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|updateActiveNamenode ( final String nsId, final InetSocketAddress address)
specifier|public
name|void
name|updateActiveNamenode
parameter_list|(
specifier|final
name|String
name|nsId
parameter_list|,
specifier|final
name|InetSocketAddress
name|address
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Called when we have an RPC miss and successful hit on an alternate NN.
comment|// Temporarily update our cache, it will be overwritten on the next update.
try|try
block|{
name|MembershipState
name|partial
init|=
name|MembershipState
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|String
name|rpcAddress
init|=
name|address
operator|.
name|getHostName
argument_list|()
operator|+
literal|":"
operator|+
name|address
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|partial
operator|.
name|setRpcAddress
argument_list|(
name|rpcAddress
argument_list|)
expr_stmt|;
name|partial
operator|.
name|setNameserviceId
argument_list|(
name|nsId
argument_list|)
expr_stmt|;
name|GetNamenodeRegistrationsRequest
name|request
init|=
name|GetNamenodeRegistrationsRequest
operator|.
name|newInstance
argument_list|(
name|partial
argument_list|)
decl_stmt|;
name|MembershipStore
name|membership
init|=
name|getMembershipStore
argument_list|()
decl_stmt|;
name|GetNamenodeRegistrationsResponse
name|response
init|=
name|membership
operator|.
name|getNamenodeRegistrations
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|MembershipState
argument_list|>
name|records
init|=
name|response
operator|.
name|getNamenodeMemberships
argument_list|()
decl_stmt|;
if|if
condition|(
name|records
operator|!=
literal|null
operator|&&
name|records
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|MembershipState
name|record
init|=
name|records
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|UpdateNamenodeRegistrationRequest
name|updateRequest
init|=
name|UpdateNamenodeRegistrationRequest
operator|.
name|newInstance
argument_list|(
name|record
operator|.
name|getNameserviceId
argument_list|()
argument_list|,
name|record
operator|.
name|getNamenodeId
argument_list|()
argument_list|,
name|ACTIVE
argument_list|)
decl_stmt|;
name|membership
operator|.
name|updateNamenodeRegistration
argument_list|(
name|updateRequest
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|StateStoreUnavailableException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot update {} as active, State Store unavailable"
argument_list|,
name|address
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getNamenodesForNameserviceId ( final String nsId)
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|FederationNamenodeContext
argument_list|>
name|getNamenodesForNameserviceId
parameter_list|(
specifier|final
name|String
name|nsId
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|?
extends|extends
name|FederationNamenodeContext
argument_list|>
name|ret
init|=
name|cacheNS
operator|.
name|get
argument_list|(
name|nsId
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
operator|!=
literal|null
condition|)
block|{
return|return
name|ret
return|;
block|}
comment|// Not cached, generate the value
specifier|final
name|List
argument_list|<
name|MembershipState
argument_list|>
name|result
decl_stmt|;
try|try
block|{
name|MembershipState
name|partial
init|=
name|MembershipState
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|partial
operator|.
name|setNameserviceId
argument_list|(
name|nsId
argument_list|)
expr_stmt|;
name|GetNamenodeRegistrationsRequest
name|request
init|=
name|GetNamenodeRegistrationsRequest
operator|.
name|newInstance
argument_list|(
name|partial
argument_list|)
decl_stmt|;
name|result
operator|=
name|getRecentRegistrationForQuery
argument_list|(
name|request
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StateStoreUnavailableException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot get active NN for {}, State Store unavailable"
argument_list|,
name|nsId
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
if|if
condition|(
name|result
operator|==
literal|null
operator|||
name|result
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot locate eligible NNs for {}"
argument_list|,
name|nsId
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|// Mark disabled name services
try|try
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|disabled
init|=
name|getDisabledNameserviceStore
argument_list|()
operator|.
name|getDisabledNameservices
argument_list|()
decl_stmt|;
if|if
condition|(
name|disabled
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot get disabled name services"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|MembershipState
name|nn
range|:
name|result
control|)
block|{
if|if
condition|(
name|disabled
operator|.
name|contains
argument_list|(
name|nn
operator|.
name|getNameserviceId
argument_list|()
argument_list|)
condition|)
block|{
name|nn
operator|.
name|setState
argument_list|(
name|FederationNamenodeServiceState
operator|.
name|DISABLED
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|StateStoreUnavailableException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot get disabled name services, State Store unavailable"
argument_list|)
expr_stmt|;
block|}
comment|// Cache the response
name|ret
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|cacheNS
operator|.
name|put
argument_list|(
name|nsId
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
annotation|@
name|Override
DECL|method|getNamenodesForBlockPoolId ( final String bpId)
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|FederationNamenodeContext
argument_list|>
name|getNamenodesForBlockPoolId
parameter_list|(
specifier|final
name|String
name|bpId
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|?
extends|extends
name|FederationNamenodeContext
argument_list|>
name|ret
init|=
name|cacheBP
operator|.
name|get
argument_list|(
name|bpId
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|MembershipState
name|partial
init|=
name|MembershipState
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|partial
operator|.
name|setBlockPoolId
argument_list|(
name|bpId
argument_list|)
expr_stmt|;
name|GetNamenodeRegistrationsRequest
name|request
init|=
name|GetNamenodeRegistrationsRequest
operator|.
name|newInstance
argument_list|(
name|partial
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|MembershipState
argument_list|>
name|result
init|=
name|getRecentRegistrationForQuery
argument_list|(
name|request
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
operator|||
name|result
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot locate eligible NNs for {}"
argument_list|,
name|bpId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|cacheBP
operator|.
name|put
argument_list|(
name|bpId
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|ret
operator|=
name|result
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|StateStoreUnavailableException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot get active NN for {}, State Store unavailable"
argument_list|,
name|bpId
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
if|if
condition|(
name|ret
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|ret
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|registerNamenode (NamenodeStatusReport report)
specifier|public
name|boolean
name|registerNamenode
parameter_list|(
name|NamenodeStatusReport
name|report
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|this
operator|.
name|routerId
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Cannot register namenode, router ID is not known {}"
argument_list|,
name|report
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|MembershipState
name|record
init|=
name|MembershipState
operator|.
name|newInstance
argument_list|(
name|routerId
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
name|getClusterId
argument_list|()
argument_list|,
name|report
operator|.
name|getBlockPoolId
argument_list|()
argument_list|,
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
name|getState
argument_list|()
argument_list|,
name|report
operator|.
name|getSafemode
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|report
operator|.
name|statsValid
argument_list|()
condition|)
block|{
name|MembershipStats
name|stats
init|=
name|MembershipStats
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|stats
operator|.
name|setNumOfFiles
argument_list|(
name|report
operator|.
name|getNumFiles
argument_list|()
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setNumOfBlocks
argument_list|(
name|report
operator|.
name|getNumBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setNumOfBlocksMissing
argument_list|(
name|report
operator|.
name|getNumBlocksMissing
argument_list|()
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setNumOfBlocksPendingReplication
argument_list|(
name|report
operator|.
name|getNumOfBlocksPendingReplication
argument_list|()
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setNumOfBlocksUnderReplicated
argument_list|(
name|report
operator|.
name|getNumOfBlocksUnderReplicated
argument_list|()
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setNumOfBlocksPendingDeletion
argument_list|(
name|report
operator|.
name|getNumOfBlocksPendingDeletion
argument_list|()
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setAvailableSpace
argument_list|(
name|report
operator|.
name|getAvailableSpace
argument_list|()
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setTotalSpace
argument_list|(
name|report
operator|.
name|getTotalSpace
argument_list|()
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setProvidedSpace
argument_list|(
name|report
operator|.
name|getProvidedSpace
argument_list|()
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setNumOfDecommissioningDatanodes
argument_list|(
name|report
operator|.
name|getNumDecommissioningDatanodes
argument_list|()
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setNumOfActiveDatanodes
argument_list|(
name|report
operator|.
name|getNumLiveDatanodes
argument_list|()
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setNumOfDeadDatanodes
argument_list|(
name|report
operator|.
name|getNumDeadDatanodes
argument_list|()
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setNumOfStaleDatanodes
argument_list|(
name|report
operator|.
name|getNumStaleDatanodes
argument_list|()
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setNumOfDecomActiveDatanodes
argument_list|(
name|report
operator|.
name|getNumDecomLiveDatanodes
argument_list|()
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setNumOfDecomDeadDatanodes
argument_list|(
name|report
operator|.
name|getNumDecomDeadDatanodes
argument_list|()
argument_list|)
expr_stmt|;
name|record
operator|.
name|setStats
argument_list|(
name|stats
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|report
operator|.
name|getState
argument_list|()
operator|!=
name|UNAVAILABLE
condition|)
block|{
comment|// Set/update our last contact time
name|record
operator|.
name|setLastContact
argument_list|(
name|Time
operator|.
name|now
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|NamenodeHeartbeatRequest
name|request
init|=
name|NamenodeHeartbeatRequest
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|request
operator|.
name|setNamenodeMembership
argument_list|(
name|record
argument_list|)
expr_stmt|;
return|return
name|getMembershipStore
argument_list|()
operator|.
name|namenodeHeartbeat
argument_list|(
name|request
argument_list|)
operator|.
name|getResult
argument_list|()
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
name|GetNamespaceInfoRequest
name|request
init|=
name|GetNamespaceInfoRequest
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|GetNamespaceInfoResponse
name|response
init|=
name|getMembershipStore
argument_list|()
operator|.
name|getNamespaceInfo
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|FederationNamespaceInfo
argument_list|>
name|nss
init|=
name|response
operator|.
name|getNamespaceInfo
argument_list|()
decl_stmt|;
comment|// Filter disabled namespaces
name|Set
argument_list|<
name|FederationNamespaceInfo
argument_list|>
name|ret
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|disabled
init|=
name|getDisabledNamespaces
argument_list|()
decl_stmt|;
for|for
control|(
name|FederationNamespaceInfo
name|ns
range|:
name|nss
control|)
block|{
if|if
condition|(
operator|!
name|disabled
operator|.
name|contains
argument_list|(
name|ns
operator|.
name|getNameserviceId
argument_list|()
argument_list|)
condition|)
block|{
name|ret
operator|.
name|add
argument_list|(
name|ns
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ret
return|;
block|}
annotation|@
name|Override
DECL|method|getDisabledNamespaces ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getDisabledNamespaces
parameter_list|()
throws|throws
name|IOException
block|{
name|DisabledNameserviceStore
name|store
init|=
name|getDisabledNameserviceStore
argument_list|()
decl_stmt|;
return|return
name|store
operator|.
name|getDisabledNameservices
argument_list|()
return|;
block|}
comment|/**    * Picks the most relevant record registration that matches the query. Return    * registrations matching the query in this preference: 1) Most recently    * updated ACTIVE registration 2) Most recently updated STANDBY registration    * (if showStandby) 3) Most recently updated UNAVAILABLE registration (if    * showUnavailable). EXPIRED registrations are ignored.    *    * @param request The select query for NN registrations.    * @param addUnavailable include UNAVAILABLE registrations.    * @param addExpired include EXPIRED registrations.    * @return List of memberships or null if no registrations that    *         both match the query AND the selected states.    * @throws IOException    */
DECL|method|getRecentRegistrationForQuery ( GetNamenodeRegistrationsRequest request, boolean addUnavailable, boolean addExpired)
specifier|private
name|List
argument_list|<
name|MembershipState
argument_list|>
name|getRecentRegistrationForQuery
parameter_list|(
name|GetNamenodeRegistrationsRequest
name|request
parameter_list|,
name|boolean
name|addUnavailable
parameter_list|,
name|boolean
name|addExpired
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Retrieve a list of all registrations that match this query.
comment|// This may include all NN records for a namespace/blockpool, including
comment|// duplicate records for the same NN from different routers.
name|MembershipStore
name|membershipStore
init|=
name|getMembershipStore
argument_list|()
decl_stmt|;
name|GetNamenodeRegistrationsResponse
name|response
init|=
name|membershipStore
operator|.
name|getNamenodeRegistrations
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|MembershipState
argument_list|>
name|memberships
init|=
name|response
operator|.
name|getNamenodeMemberships
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|addExpired
operator|||
operator|!
name|addUnavailable
condition|)
block|{
name|Iterator
argument_list|<
name|MembershipState
argument_list|>
name|iterator
init|=
name|memberships
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|MembershipState
name|membership
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|membership
operator|.
name|getState
argument_list|()
operator|==
name|EXPIRED
operator|&&
operator|!
name|addExpired
condition|)
block|{
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|membership
operator|.
name|getState
argument_list|()
operator|==
name|UNAVAILABLE
operator|&&
operator|!
name|addUnavailable
condition|)
block|{
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|List
argument_list|<
name|MembershipState
argument_list|>
name|priorityList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|priorityList
operator|.
name|addAll
argument_list|(
name|memberships
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|priorityList
argument_list|,
operator|new
name|NamenodePriorityComparator
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Selected most recent NN {} for query"
argument_list|,
name|priorityList
argument_list|)
expr_stmt|;
return|return
name|priorityList
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
block|{
name|this
operator|.
name|routerId
operator|=
name|router
expr_stmt|;
block|}
block|}
end_class

end_unit

