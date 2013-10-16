begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|binary
operator|.
name|Base64
import|;
end_import

begin_class
DECL|class|AuxiliaryServiceHelper
specifier|public
class|class
name|AuxiliaryServiceHelper
block|{
DECL|field|NM_AUX_SERVICE
specifier|public
specifier|final
specifier|static
name|String
name|NM_AUX_SERVICE
init|=
literal|"NM_AUX_SERVICE_"
decl_stmt|;
DECL|method|getServiceDataFromEnv (String serviceName, Map<String, String> env)
specifier|public
specifier|static
name|ByteBuffer
name|getServiceDataFromEnv
parameter_list|(
name|String
name|serviceName
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
parameter_list|)
block|{
name|String
name|meta
init|=
name|env
operator|.
name|get
argument_list|(
name|getPrefixServiceName
argument_list|(
name|serviceName
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|meta
condition|)
block|{
return|return
literal|null
return|;
block|}
name|byte
index|[]
name|metaData
init|=
name|Base64
operator|.
name|decodeBase64
argument_list|(
name|meta
argument_list|)
decl_stmt|;
return|return
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|metaData
argument_list|)
return|;
block|}
DECL|method|setServiceDataIntoEnv (String serviceName, ByteBuffer metaData, Map<String, String> env)
specifier|public
specifier|static
name|void
name|setServiceDataIntoEnv
parameter_list|(
name|String
name|serviceName
parameter_list|,
name|ByteBuffer
name|metaData
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
parameter_list|)
block|{
name|byte
index|[]
name|byteData
init|=
name|metaData
operator|.
name|array
argument_list|()
decl_stmt|;
name|env
operator|.
name|put
argument_list|(
name|getPrefixServiceName
argument_list|(
name|serviceName
argument_list|)
argument_list|,
name|Base64
operator|.
name|encodeBase64String
argument_list|(
name|byteData
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getPrefixServiceName (String serviceName)
specifier|private
specifier|static
name|String
name|getPrefixServiceName
parameter_list|(
name|String
name|serviceName
parameter_list|)
block|{
return|return
name|NM_AUX_SERVICE
operator|+
name|serviceName
return|;
block|}
block|}
end_class

end_unit

