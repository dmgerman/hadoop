begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.store.records
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
name|store
operator|.
name|records
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
name|SortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|router
operator|.
name|FederationUtil
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
name|router
operator|.
name|RouterServiceState
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
name|driver
operator|.
name|StateStoreSerializer
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
comment|/**  * Entry to log the state of a  * {@link org.apache.hadoop.hdfs.server.federation.router.Router Router} in the  * {@link org.apache.hadoop.hdfs.server.federation.store.StateStoreService  * FederationStateStoreService}.  */
end_comment

begin_class
DECL|class|RouterState
specifier|public
specifier|abstract
class|class
name|RouterState
extends|extends
name|BaseRecord
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
name|RouterState
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Expiration time in ms for this entry. */
DECL|field|expirationMs
specifier|private
specifier|static
name|long
name|expirationMs
decl_stmt|;
comment|/**    * Constructors.    */
DECL|method|RouterState ()
specifier|public
name|RouterState
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|newInstance ()
specifier|public
specifier|static
name|RouterState
name|newInstance
parameter_list|()
block|{
name|RouterState
name|record
init|=
name|StateStoreSerializer
operator|.
name|newRecord
argument_list|(
name|RouterState
operator|.
name|class
argument_list|)
decl_stmt|;
name|record
operator|.
name|init
argument_list|()
expr_stmt|;
return|return
name|record
return|;
block|}
DECL|method|newInstance (String addr, long startTime, RouterServiceState status)
specifier|public
specifier|static
name|RouterState
name|newInstance
parameter_list|(
name|String
name|addr
parameter_list|,
name|long
name|startTime
parameter_list|,
name|RouterServiceState
name|status
parameter_list|)
block|{
name|RouterState
name|record
init|=
name|newInstance
argument_list|()
decl_stmt|;
name|record
operator|.
name|setDateStarted
argument_list|(
name|startTime
argument_list|)
expr_stmt|;
name|record
operator|.
name|setAddress
argument_list|(
name|addr
argument_list|)
expr_stmt|;
name|record
operator|.
name|setStatus
argument_list|(
name|status
argument_list|)
expr_stmt|;
name|record
operator|.
name|setCompileInfo
argument_list|(
name|FederationUtil
operator|.
name|getCompileInfo
argument_list|()
argument_list|)
expr_stmt|;
name|record
operator|.
name|setVersion
argument_list|(
name|FederationUtil
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|record
return|;
block|}
DECL|method|setAddress (String address)
specifier|public
specifier|abstract
name|void
name|setAddress
parameter_list|(
name|String
name|address
parameter_list|)
function_decl|;
DECL|method|setDateStarted (long dateStarted)
specifier|public
specifier|abstract
name|void
name|setDateStarted
parameter_list|(
name|long
name|dateStarted
parameter_list|)
function_decl|;
DECL|method|getAddress ()
specifier|public
specifier|abstract
name|String
name|getAddress
parameter_list|()
function_decl|;
DECL|method|getStateStoreVersion ()
specifier|public
specifier|abstract
name|StateStoreVersion
name|getStateStoreVersion
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|setStateStoreVersion (StateStoreVersion version)
specifier|public
specifier|abstract
name|void
name|setStateStoreVersion
parameter_list|(
name|StateStoreVersion
name|version
parameter_list|)
function_decl|;
DECL|method|getStatus ()
specifier|public
specifier|abstract
name|RouterServiceState
name|getStatus
parameter_list|()
function_decl|;
DECL|method|setStatus (RouterServiceState newStatus)
specifier|public
specifier|abstract
name|void
name|setStatus
parameter_list|(
name|RouterServiceState
name|newStatus
parameter_list|)
function_decl|;
DECL|method|getVersion ()
specifier|public
specifier|abstract
name|String
name|getVersion
parameter_list|()
function_decl|;
DECL|method|setVersion (String version)
specifier|public
specifier|abstract
name|void
name|setVersion
parameter_list|(
name|String
name|version
parameter_list|)
function_decl|;
DECL|method|getCompileInfo ()
specifier|public
specifier|abstract
name|String
name|getCompileInfo
parameter_list|()
function_decl|;
DECL|method|setCompileInfo (String info)
specifier|public
specifier|abstract
name|void
name|setCompileInfo
parameter_list|(
name|String
name|info
parameter_list|)
function_decl|;
DECL|method|getDateStarted ()
specifier|public
specifier|abstract
name|long
name|getDateStarted
parameter_list|()
function_decl|;
comment|/**    * Get the identifier for the Router. It uses the address.    *    * @return Identifier for the Router.    */
DECL|method|getRouterId ()
specifier|public
name|String
name|getRouterId
parameter_list|()
block|{
return|return
name|getAddress
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|like (BaseRecord o)
specifier|public
name|boolean
name|like
parameter_list|(
name|BaseRecord
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|RouterState
condition|)
block|{
name|RouterState
name|other
init|=
operator|(
name|RouterState
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|getAddress
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|getAddress
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getAddress
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getStatus
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|getStatus
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getStatus
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
return|return
literal|false
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
name|getAddress
argument_list|()
operator|+
literal|" -> "
operator|+
name|getStatus
argument_list|()
operator|+
literal|","
operator|+
name|getVersion
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getPrimaryKeys ()
specifier|public
name|SortedMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getPrimaryKeys
parameter_list|()
block|{
name|SortedMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"address"
argument_list|,
name|getAddress
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|map
return|;
block|}
annotation|@
name|Override
DECL|method|validate ()
specifier|public
name|void
name|validate
parameter_list|()
block|{
name|super
operator|.
name|validate
argument_list|()
expr_stmt|;
if|if
condition|(
operator|(
name|getAddress
argument_list|()
operator|==
literal|null
operator|||
name|getAddress
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|)
operator|&&
name|getStatus
argument_list|()
operator|!=
name|RouterServiceState
operator|.
name|INITIALIZING
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid router entry, no address specified "
operator|+
name|this
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|compareTo (BaseRecord other)
specifier|public
name|int
name|compareTo
parameter_list|(
name|BaseRecord
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|other
operator|instanceof
name|RouterState
condition|)
block|{
name|RouterState
name|router
init|=
operator|(
name|RouterState
operator|)
name|other
decl_stmt|;
return|return
name|this
operator|.
name|getAddress
argument_list|()
operator|.
name|compareTo
argument_list|(
name|router
operator|.
name|getAddress
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|compareTo
argument_list|(
name|other
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|checkExpired (long currentTime)
specifier|public
name|boolean
name|checkExpired
parameter_list|(
name|long
name|currentTime
parameter_list|)
block|{
if|if
condition|(
name|super
operator|.
name|checkExpired
argument_list|(
name|currentTime
argument_list|)
condition|)
block|{
name|setStatus
argument_list|(
name|RouterServiceState
operator|.
name|EXPIRED
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
annotation|@
name|Override
DECL|method|getExpirationMs ()
specifier|public
name|long
name|getExpirationMs
parameter_list|()
block|{
return|return
name|RouterState
operator|.
name|expirationMs
return|;
block|}
DECL|method|setExpirationMs (long time)
specifier|public
specifier|static
name|void
name|setExpirationMs
parameter_list|(
name|long
name|time
parameter_list|)
block|{
name|RouterState
operator|.
name|expirationMs
operator|=
name|time
expr_stmt|;
block|}
block|}
end_class

end_unit

