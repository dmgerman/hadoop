begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azure
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azure
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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

begin_comment
comment|/**  * Abstract base class for the SAS Key Generator implementation  *  */
end_comment

begin_class
DECL|class|SASKeyGeneratorImpl
specifier|public
specifier|abstract
class|class
name|SASKeyGeneratorImpl
implements|implements
name|SASKeyGeneratorInterface
block|{
comment|/**    * Configuration key to be used to specify the expiry period for SAS keys    * This value currently is specified in days. {@value}    */
DECL|field|KEY_SAS_KEY_EXPIRY_PERIOD
specifier|public
specifier|static
specifier|final
name|String
name|KEY_SAS_KEY_EXPIRY_PERIOD
init|=
literal|"fs.azure.sas.expiry.period"
decl_stmt|;
comment|/**    * Default value for the SAS key expiry period in days. {@value}    */
DECL|field|DEFAUL_CONTAINER_SAS_KEY_PERIOD
specifier|public
specifier|static
specifier|final
name|long
name|DEFAUL_CONTAINER_SAS_KEY_PERIOD
init|=
literal|90
decl_stmt|;
DECL|field|sasKeyExpiryPeriod
specifier|private
name|long
name|sasKeyExpiryPeriod
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|method|SASKeyGeneratorImpl (Configuration conf)
specifier|public
name|SASKeyGeneratorImpl
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
name|this
operator|.
name|sasKeyExpiryPeriod
operator|=
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|KEY_SAS_KEY_EXPIRY_PERIOD
argument_list|,
name|DEFAUL_CONTAINER_SAS_KEY_PERIOD
argument_list|,
name|TimeUnit
operator|.
name|DAYS
argument_list|)
expr_stmt|;
block|}
DECL|method|getSasKeyExpiryPeriod ()
specifier|public
name|long
name|getSasKeyExpiryPeriod
parameter_list|()
block|{
return|return
name|sasKeyExpiryPeriod
return|;
block|}
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
block|}
end_class

end_unit

