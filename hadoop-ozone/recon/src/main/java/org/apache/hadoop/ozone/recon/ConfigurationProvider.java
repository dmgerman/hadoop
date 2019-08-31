begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.recon
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|recon
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Provider
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
comment|/**  * Ozone Configuration Provider.  *<p>  * As the OzoneConfiguration is created by the CLI application here we inject  * it via a singleton instance to the Jax-RS/CDI instances.  */
end_comment

begin_class
DECL|class|ConfigurationProvider
specifier|public
class|class
name|ConfigurationProvider
implements|implements
name|Provider
argument_list|<
name|Configuration
argument_list|>
block|{
DECL|field|configuration
specifier|private
specifier|static
name|Configuration
name|configuration
decl_stmt|;
DECL|method|setConfiguration (Configuration conf)
specifier|static
name|void
name|setConfiguration
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|ConfigurationProvider
operator|.
name|configuration
operator|=
name|conf
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|Configuration
name|get
parameter_list|()
block|{
return|return
name|configuration
return|;
block|}
block|}
end_class

end_unit

