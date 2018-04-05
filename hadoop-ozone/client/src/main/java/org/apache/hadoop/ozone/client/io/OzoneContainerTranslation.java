begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.client.io
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|client
operator|.
name|io
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|KeyData
import|;
end_import

begin_comment
comment|/**  * This class contains methods that define the translation between the Ozone  * domain model and the storage container domain model.  */
end_comment

begin_class
DECL|class|OzoneContainerTranslation
specifier|final
class|class
name|OzoneContainerTranslation
block|{
comment|/**    * Creates key data intended for reading a container key.    *    * @param containerName container name    * @param containerKey container key    * @return KeyData intended for reading the container key    */
DECL|method|containerKeyDataForRead (String containerName, String containerKey)
specifier|public
specifier|static
name|KeyData
name|containerKeyDataForRead
parameter_list|(
name|String
name|containerName
parameter_list|,
name|String
name|containerKey
parameter_list|)
block|{
return|return
name|KeyData
operator|.
name|newBuilder
argument_list|()
operator|.
name|setContainerName
argument_list|(
name|containerName
argument_list|)
operator|.
name|setName
argument_list|(
name|containerKey
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * There is no need to instantiate this class.    */
DECL|method|OzoneContainerTranslation ()
specifier|private
name|OzoneContainerTranslation
parameter_list|()
block|{   }
block|}
end_class

end_unit

