begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|security
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|TextFormat
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
name|io
operator|.
name|Text
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
name|security
operator|.
name|UserGroupInformation
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
name|security
operator|.
name|token
operator|.
name|TokenIdentifier
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
name|proto
operator|.
name|YarnSecurityTokenProtos
operator|.
name|DockerCredentialTokenIdentifierProto
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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
comment|/**  * TokenIdentifier for Docker registry credentials.  */
end_comment

begin_class
DECL|class|DockerCredentialTokenIdentifier
specifier|public
class|class
name|DockerCredentialTokenIdentifier
extends|extends
name|TokenIdentifier
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|org
operator|.
name|slf4j
operator|.
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DockerCredentialTokenIdentifier
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|proto
specifier|private
name|DockerCredentialTokenIdentifierProto
name|proto
decl_stmt|;
DECL|field|KIND
specifier|public
specifier|static
specifier|final
name|Text
name|KIND
init|=
operator|new
name|Text
argument_list|(
literal|"DOCKER_CLIENT_CREDENTIAL_TOKEN"
argument_list|)
decl_stmt|;
DECL|method|DockerCredentialTokenIdentifier (String registryUrl, String applicationId)
specifier|public
name|DockerCredentialTokenIdentifier
parameter_list|(
name|String
name|registryUrl
parameter_list|,
name|String
name|applicationId
parameter_list|)
block|{
name|DockerCredentialTokenIdentifierProto
operator|.
name|Builder
name|builder
init|=
name|DockerCredentialTokenIdentifierProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|registryUrl
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setRegistryUrl
argument_list|(
name|registryUrl
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|applicationId
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setApplicationId
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
block|}
name|proto
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
comment|/**    * Default constructor needed for the Service Loader.    */
DECL|method|DockerCredentialTokenIdentifier ()
specifier|public
name|DockerCredentialTokenIdentifier
parameter_list|()
block|{   }
comment|/**    * Write the TokenIdentifier to the output stream.    *    * @param out<code>DataOutput</code> to serialize this object into.    * @throws IOException if the write fails.    */
annotation|@
name|Override
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|write
argument_list|(
name|proto
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Populate the Proto object with the input.    *    * @param in<code>DataInput</code> to deserialize this object from.    * @throws IOException if the read fails.    */
annotation|@
name|Override
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|proto
operator|=
name|DockerCredentialTokenIdentifierProto
operator|.
name|parseFrom
argument_list|(
operator|(
name|DataInputStream
operator|)
name|in
argument_list|)
expr_stmt|;
block|}
comment|/**    * Return the ProtoBuf formatted data.    *    * @return the ProtoBuf representation of the data.    */
DECL|method|getProto ()
specifier|public
name|DockerCredentialTokenIdentifierProto
name|getProto
parameter_list|()
block|{
return|return
name|proto
return|;
block|}
comment|/**    * Return the TokenIdentifier kind.    *    * @return the TokenIdentifier kind.    */
annotation|@
name|Override
DECL|method|getKind ()
specifier|public
name|Text
name|getKind
parameter_list|()
block|{
return|return
name|KIND
return|;
block|}
comment|/**    * Return a remote user based on the registry URL and Application ID.    *    * @return a remote user based on the registry URL and Application ID.    */
annotation|@
name|Override
DECL|method|getUser ()
specifier|public
name|UserGroupInformation
name|getUser
parameter_list|()
block|{
return|return
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|getRegistryUrl
argument_list|()
operator|+
literal|"-"
operator|+
name|getApplicationId
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Get the registry URL.    *    * @return the registry URL.    */
DECL|method|getRegistryUrl ()
specifier|public
name|String
name|getRegistryUrl
parameter_list|()
block|{
name|String
name|registryUrl
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|proto
operator|.
name|hasRegistryUrl
argument_list|()
condition|)
block|{
name|registryUrl
operator|=
name|proto
operator|.
name|getRegistryUrl
argument_list|()
expr_stmt|;
block|}
return|return
name|registryUrl
return|;
block|}
comment|/**    * Get the application ID.    *    * @return the application ID.    */
DECL|method|getApplicationId ()
specifier|public
name|String
name|getApplicationId
parameter_list|()
block|{
name|String
name|applicationId
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|proto
operator|.
name|hasApplicationId
argument_list|()
condition|)
block|{
name|applicationId
operator|=
name|proto
operator|.
name|getApplicationId
argument_list|()
expr_stmt|;
block|}
return|return
name|applicationId
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|getProto
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object other)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
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
literal|false
return|;
block|}
if|if
condition|(
name|other
operator|.
name|getClass
argument_list|()
operator|.
name|isAssignableFrom
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|this
operator|.
name|getProto
argument_list|()
operator|.
name|equals
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|cast
argument_list|(
name|other
argument_list|)
operator|.
name|getProto
argument_list|()
argument_list|)
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
name|TextFormat
operator|.
name|shortDebugString
argument_list|(
name|getProto
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

