begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
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
name|conf
operator|.
name|Configuration
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

begin_comment
comment|/**  * Factory class to create different types of OzoneClients.  */
end_comment

begin_class
DECL|class|OzoneClientFactory
specifier|public
specifier|final
class|class
name|OzoneClientFactory
block|{
comment|/**    * Private constructor, class is not meant to be initialized.    */
DECL|method|OzoneClientFactory ()
specifier|private
name|OzoneClientFactory
parameter_list|()
block|{}
DECL|field|configuration
specifier|private
specifier|static
name|Configuration
name|configuration
decl_stmt|;
comment|/**    * Returns an OzoneClient which will use RPC protocol to perform    * client operations.    *    * @return OzoneClient    * @throws IOException    */
DECL|method|getRpcClient ()
specifier|public
specifier|static
name|OzoneClient
name|getRpcClient
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|OzoneClientImpl
argument_list|(
name|getConfiguration
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Sets the configuration, which will be used while creating OzoneClient.    *    * @param conf    */
DECL|method|setConfiguration (Configuration conf)
specifier|public
specifier|static
name|void
name|setConfiguration
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|configuration
operator|=
name|conf
expr_stmt|;
block|}
comment|/**    * Returns the configuration if it's already set, else creates a new    * {@link OzoneConfiguration} and returns it.    *    * @return Configuration    */
DECL|method|getConfiguration ()
specifier|private
specifier|static
specifier|synchronized
name|Configuration
name|getConfiguration
parameter_list|()
block|{
if|if
condition|(
name|configuration
operator|==
literal|null
condition|)
block|{
name|setConfiguration
argument_list|(
operator|new
name|OzoneConfiguration
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|configuration
return|;
block|}
block|}
end_class

end_unit

