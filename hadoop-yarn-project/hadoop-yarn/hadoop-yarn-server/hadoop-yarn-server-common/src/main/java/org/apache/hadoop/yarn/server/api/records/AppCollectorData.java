begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.api.records
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|api
operator|.
name|records
package|;
end_package

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
operator|.
name|Private
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|Token
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
name|yarn
operator|.
name|util
operator|.
name|Records
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|AppCollectorData
specifier|public
specifier|abstract
class|class
name|AppCollectorData
block|{
DECL|field|DEFAULT_TIMESTAMP_VALUE
specifier|protected
specifier|static
specifier|final
name|long
name|DEFAULT_TIMESTAMP_VALUE
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|newInstance ( ApplicationId id, String collectorAddr, long rmIdentifier, long version, Token token)
specifier|public
specifier|static
name|AppCollectorData
name|newInstance
parameter_list|(
name|ApplicationId
name|id
parameter_list|,
name|String
name|collectorAddr
parameter_list|,
name|long
name|rmIdentifier
parameter_list|,
name|long
name|version
parameter_list|,
name|Token
name|token
parameter_list|)
block|{
name|AppCollectorData
name|appCollectorData
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|AppCollectorData
operator|.
name|class
argument_list|)
decl_stmt|;
name|appCollectorData
operator|.
name|setApplicationId
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|appCollectorData
operator|.
name|setCollectorAddr
argument_list|(
name|collectorAddr
argument_list|)
expr_stmt|;
name|appCollectorData
operator|.
name|setRMIdentifier
argument_list|(
name|rmIdentifier
argument_list|)
expr_stmt|;
name|appCollectorData
operator|.
name|setVersion
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|appCollectorData
operator|.
name|setCollectorToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
return|return
name|appCollectorData
return|;
block|}
DECL|method|newInstance ( ApplicationId id, String collectorAddr, long rmIdentifier, long version)
specifier|public
specifier|static
name|AppCollectorData
name|newInstance
parameter_list|(
name|ApplicationId
name|id
parameter_list|,
name|String
name|collectorAddr
parameter_list|,
name|long
name|rmIdentifier
parameter_list|,
name|long
name|version
parameter_list|)
block|{
return|return
name|newInstance
argument_list|(
name|id
argument_list|,
name|collectorAddr
argument_list|,
name|rmIdentifier
argument_list|,
name|version
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|newInstance (ApplicationId id, String collectorAddr, Token token)
specifier|public
specifier|static
name|AppCollectorData
name|newInstance
parameter_list|(
name|ApplicationId
name|id
parameter_list|,
name|String
name|collectorAddr
parameter_list|,
name|Token
name|token
parameter_list|)
block|{
return|return
name|newInstance
argument_list|(
name|id
argument_list|,
name|collectorAddr
argument_list|,
name|DEFAULT_TIMESTAMP_VALUE
argument_list|,
name|DEFAULT_TIMESTAMP_VALUE
argument_list|,
name|token
argument_list|)
return|;
block|}
DECL|method|newInstance (ApplicationId id, String collectorAddr)
specifier|public
specifier|static
name|AppCollectorData
name|newInstance
parameter_list|(
name|ApplicationId
name|id
parameter_list|,
name|String
name|collectorAddr
parameter_list|)
block|{
return|return
name|newInstance
argument_list|(
name|id
argument_list|,
name|collectorAddr
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Returns if a collector data item happens before another one. Null data    * items happens before any other non-null items. Non-null data items A    * happens before another non-null item B when A's rmIdentifier is less than    * B's rmIdentifier. Or A's version is less than B's if they have the same    * rmIdentifier.    *    * @param dataA first collector data item.    * @param dataB second collector data item.    * @return true if dataA happens before dataB.    */
DECL|method|happensBefore (AppCollectorData dataA, AppCollectorData dataB)
specifier|public
specifier|static
name|boolean
name|happensBefore
parameter_list|(
name|AppCollectorData
name|dataA
parameter_list|,
name|AppCollectorData
name|dataB
parameter_list|)
block|{
if|if
condition|(
name|dataA
operator|==
literal|null
operator|&&
name|dataB
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|dataA
operator|==
literal|null
operator|||
name|dataB
operator|==
literal|null
condition|)
block|{
return|return
name|dataA
operator|==
literal|null
return|;
block|}
return|return
operator|(
name|dataA
operator|.
name|getRMIdentifier
argument_list|()
operator|<
name|dataB
operator|.
name|getRMIdentifier
argument_list|()
operator|)
operator|||
operator|(
operator|(
name|dataA
operator|.
name|getRMIdentifier
argument_list|()
operator|==
name|dataB
operator|.
name|getRMIdentifier
argument_list|()
operator|)
operator|&&
operator|(
name|dataA
operator|.
name|getVersion
argument_list|()
operator|<
name|dataB
operator|.
name|getVersion
argument_list|()
operator|)
operator|)
return|;
block|}
comment|/**    * Returns if the collector data has been stamped by the RM with a RM cluster    * timestamp and a version number.    *    * @return true if RM has already assigned a timestamp for this collector.    * Otherwise, it means the RM has not recognized the existence of this    * collector.    */
DECL|method|isStamped ()
specifier|public
name|boolean
name|isStamped
parameter_list|()
block|{
return|return
operator|(
name|getRMIdentifier
argument_list|()
operator|!=
name|DEFAULT_TIMESTAMP_VALUE
operator|)
operator|||
operator|(
name|getVersion
argument_list|()
operator|!=
name|DEFAULT_TIMESTAMP_VALUE
operator|)
return|;
block|}
DECL|method|getApplicationId ()
specifier|public
specifier|abstract
name|ApplicationId
name|getApplicationId
parameter_list|()
function_decl|;
DECL|method|setApplicationId (ApplicationId id)
specifier|public
specifier|abstract
name|void
name|setApplicationId
parameter_list|(
name|ApplicationId
name|id
parameter_list|)
function_decl|;
DECL|method|getCollectorAddr ()
specifier|public
specifier|abstract
name|String
name|getCollectorAddr
parameter_list|()
function_decl|;
DECL|method|setCollectorAddr (String addr)
specifier|public
specifier|abstract
name|void
name|setCollectorAddr
parameter_list|(
name|String
name|addr
parameter_list|)
function_decl|;
DECL|method|getRMIdentifier ()
specifier|public
specifier|abstract
name|long
name|getRMIdentifier
parameter_list|()
function_decl|;
DECL|method|setRMIdentifier (long rmId)
specifier|public
specifier|abstract
name|void
name|setRMIdentifier
parameter_list|(
name|long
name|rmId
parameter_list|)
function_decl|;
DECL|method|getVersion ()
specifier|public
specifier|abstract
name|long
name|getVersion
parameter_list|()
function_decl|;
DECL|method|setVersion (long version)
specifier|public
specifier|abstract
name|void
name|setVersion
parameter_list|(
name|long
name|version
parameter_list|)
function_decl|;
comment|/**    * Get delegation token for app collector which AM will use to publish    * entities.    * @return the delegation token for app collector.    */
DECL|method|getCollectorToken ()
specifier|public
specifier|abstract
name|Token
name|getCollectorToken
parameter_list|()
function_decl|;
DECL|method|setCollectorToken (Token token)
specifier|public
specifier|abstract
name|void
name|setCollectorToken
parameter_list|(
name|Token
name|token
parameter_list|)
function_decl|;
block|}
end_class

end_unit

