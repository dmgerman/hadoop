begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.authorize
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|authorize
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Policy
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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * {@link PolicyProvider} provides the {@link Service} definitions to the  * security {@link Policy} in effect for Hadoop.  *  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|,
literal|"MapReduce"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|PolicyProvider
specifier|public
specifier|abstract
class|class
name|PolicyProvider
block|{
comment|/**    * Configuration key for the {@link PolicyProvider} implementation.    */
DECL|field|POLICY_PROVIDER_CONFIG
specifier|public
specifier|static
specifier|final
name|String
name|POLICY_PROVIDER_CONFIG
init|=
literal|"hadoop.security.authorization.policyprovider"
decl_stmt|;
comment|/**    * A default {@link PolicyProvider} without any defined services.    */
DECL|field|DEFAULT_POLICY_PROVIDER
specifier|public
specifier|static
specifier|final
name|PolicyProvider
name|DEFAULT_POLICY_PROVIDER
init|=
operator|new
name|PolicyProvider
argument_list|()
block|{
specifier|public
name|Service
index|[]
name|getServices
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
comment|/**    * Get the {@link Service} definitions from the {@link PolicyProvider}.    * @return the {@link Service} definitions    */
DECL|method|getServices ()
specifier|public
specifier|abstract
name|Service
index|[]
name|getServices
parameter_list|()
function_decl|;
block|}
end_class

end_unit

