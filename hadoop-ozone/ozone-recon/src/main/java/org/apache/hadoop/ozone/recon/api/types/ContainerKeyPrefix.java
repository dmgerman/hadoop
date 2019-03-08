begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.recon.api.types
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
operator|.
name|api
operator|.
name|types
package|;
end_package

begin_comment
comment|/**  * Class to encapsulate the Key information needed for the Recon container DB.  * Currently, it is containerId and key prefix.  */
end_comment

begin_class
DECL|class|ContainerKeyPrefix
specifier|public
class|class
name|ContainerKeyPrefix
block|{
DECL|field|containerId
specifier|private
name|long
name|containerId
decl_stmt|;
DECL|field|keyPrefix
specifier|private
name|String
name|keyPrefix
decl_stmt|;
DECL|method|ContainerKeyPrefix (long containerId, String keyPrefix)
specifier|public
name|ContainerKeyPrefix
parameter_list|(
name|long
name|containerId
parameter_list|,
name|String
name|keyPrefix
parameter_list|)
block|{
name|this
operator|.
name|containerId
operator|=
name|containerId
expr_stmt|;
name|this
operator|.
name|keyPrefix
operator|=
name|keyPrefix
expr_stmt|;
block|}
DECL|method|getContainerId ()
specifier|public
name|long
name|getContainerId
parameter_list|()
block|{
return|return
name|containerId
return|;
block|}
DECL|method|setContainerId (long containerId)
specifier|public
name|void
name|setContainerId
parameter_list|(
name|long
name|containerId
parameter_list|)
block|{
name|this
operator|.
name|containerId
operator|=
name|containerId
expr_stmt|;
block|}
DECL|method|getKeyPrefix ()
specifier|public
name|String
name|getKeyPrefix
parameter_list|()
block|{
return|return
name|keyPrefix
return|;
block|}
DECL|method|setKeyPrefix (String keyPrefix)
specifier|public
name|void
name|setKeyPrefix
parameter_list|(
name|String
name|keyPrefix
parameter_list|)
block|{
name|this
operator|.
name|keyPrefix
operator|=
name|keyPrefix
expr_stmt|;
block|}
block|}
end_class

end_unit

