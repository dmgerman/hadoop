begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records
package|package
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
name|InterfaceStability
operator|.
name|Evolving
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
operator|.
name|Public
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

begin_comment
comment|/**  * Collector info containing collector address and collector token passed from  * RM to AM in Allocate Response.  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Evolving
DECL|class|CollectorInfo
specifier|public
specifier|abstract
class|class
name|CollectorInfo
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
DECL|method|newInstance (String collectorAddr)
specifier|public
specifier|static
name|CollectorInfo
name|newInstance
parameter_list|(
name|String
name|collectorAddr
parameter_list|)
block|{
return|return
name|newInstance
argument_list|(
name|collectorAddr
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|newInstance (String collectorAddr, Token token)
specifier|public
specifier|static
name|CollectorInfo
name|newInstance
parameter_list|(
name|String
name|collectorAddr
parameter_list|,
name|Token
name|token
parameter_list|)
block|{
name|CollectorInfo
name|amCollectorInfo
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|CollectorInfo
operator|.
name|class
argument_list|)
decl_stmt|;
name|amCollectorInfo
operator|.
name|setCollectorAddr
argument_list|(
name|collectorAddr
argument_list|)
expr_stmt|;
name|amCollectorInfo
operator|.
name|setCollectorToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
return|return
name|amCollectorInfo
return|;
block|}
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

