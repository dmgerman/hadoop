begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.crypto.key
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|crypto
operator|.
name|key
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|conf
operator|.
name|Configuration
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
name|Credentials
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

begin_comment
comment|/**  * A KeyProvider factory for UGIs. It uses the credentials object associated  * with the current user to find keys. This provider is created using a  * URI of "user:///".  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|UserProvider
specifier|public
class|class
name|UserProvider
extends|extends
name|KeyProvider
block|{
DECL|field|SCHEME_NAME
specifier|public
specifier|static
specifier|final
name|String
name|SCHEME_NAME
init|=
literal|"user"
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|UserGroupInformation
name|user
decl_stmt|;
DECL|field|credentials
specifier|private
specifier|final
name|Credentials
name|credentials
decl_stmt|;
DECL|field|cache
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Metadata
argument_list|>
name|cache
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Metadata
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|UserProvider ()
specifier|private
name|UserProvider
parameter_list|()
throws|throws
name|IOException
block|{
name|user
operator|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
expr_stmt|;
name|credentials
operator|=
name|user
operator|.
name|getCredentials
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getKeyVersion (String versionName)
specifier|public
name|KeyVersion
name|getKeyVersion
parameter_list|(
name|String
name|versionName
parameter_list|)
block|{
name|byte
index|[]
name|bytes
init|=
name|credentials
operator|.
name|getSecretKey
argument_list|(
operator|new
name|Text
argument_list|(
name|versionName
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytes
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|KeyVersion
argument_list|(
name|versionName
argument_list|,
name|bytes
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getMetadata (String name)
specifier|public
name|Metadata
name|getMetadata
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|cache
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|cache
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
name|byte
index|[]
name|serialized
init|=
name|credentials
operator|.
name|getSecretKey
argument_list|(
operator|new
name|Text
argument_list|(
name|name
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|serialized
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Metadata
name|result
init|=
operator|new
name|Metadata
argument_list|(
name|serialized
argument_list|)
decl_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|createKey (String name, byte[] material, Options options)
specifier|public
name|KeyVersion
name|createKey
parameter_list|(
name|String
name|name
parameter_list|,
name|byte
index|[]
name|material
parameter_list|,
name|Options
name|options
parameter_list|)
throws|throws
name|IOException
block|{
name|Text
name|nameT
init|=
operator|new
name|Text
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|credentials
operator|.
name|getSecretKey
argument_list|(
name|nameT
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Key "
operator|+
name|name
operator|+
literal|" already exists in "
operator|+
name|this
argument_list|)
throw|;
block|}
if|if
condition|(
name|options
operator|.
name|getBitLength
argument_list|()
operator|!=
literal|8
operator|*
name|material
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Wrong key length. Required "
operator|+
name|options
operator|.
name|getBitLength
argument_list|()
operator|+
literal|", but got "
operator|+
operator|(
literal|8
operator|*
name|material
operator|.
name|length
operator|)
argument_list|)
throw|;
block|}
name|Metadata
name|meta
init|=
operator|new
name|Metadata
argument_list|(
name|options
operator|.
name|getCipher
argument_list|()
argument_list|,
name|options
operator|.
name|getBitLength
argument_list|()
argument_list|,
operator|new
name|Date
argument_list|()
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|meta
argument_list|)
expr_stmt|;
name|String
name|versionName
init|=
name|buildVersionName
argument_list|(
name|name
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|credentials
operator|.
name|addSecretKey
argument_list|(
name|nameT
argument_list|,
name|meta
operator|.
name|serialize
argument_list|()
argument_list|)
expr_stmt|;
name|credentials
operator|.
name|addSecretKey
argument_list|(
operator|new
name|Text
argument_list|(
name|versionName
argument_list|)
argument_list|,
name|material
argument_list|)
expr_stmt|;
return|return
operator|new
name|KeyVersion
argument_list|(
name|versionName
argument_list|,
name|material
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|deleteKey (String name)
specifier|public
name|void
name|deleteKey
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|Metadata
name|meta
init|=
name|getMetadata
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|meta
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Key "
operator|+
name|name
operator|+
literal|" does not exist in "
operator|+
name|this
argument_list|)
throw|;
block|}
for|for
control|(
name|int
name|v
init|=
literal|0
init|;
name|v
operator|<
name|meta
operator|.
name|getVersions
argument_list|()
condition|;
operator|++
name|v
control|)
block|{
name|credentials
operator|.
name|removeSecretKey
argument_list|(
operator|new
name|Text
argument_list|(
name|buildVersionName
argument_list|(
name|name
argument_list|,
name|v
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|credentials
operator|.
name|removeSecretKey
argument_list|(
operator|new
name|Text
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|cache
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rollNewVersion (String name, byte[] material)
specifier|public
name|KeyVersion
name|rollNewVersion
parameter_list|(
name|String
name|name
parameter_list|,
name|byte
index|[]
name|material
parameter_list|)
throws|throws
name|IOException
block|{
name|Metadata
name|meta
init|=
name|getMetadata
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|meta
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Key "
operator|+
name|name
operator|+
literal|" not found"
argument_list|)
throw|;
block|}
if|if
condition|(
name|meta
operator|.
name|getBitLength
argument_list|()
operator|!=
literal|8
operator|*
name|material
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Wrong key length. Required "
operator|+
name|meta
operator|.
name|getBitLength
argument_list|()
operator|+
literal|", but got "
operator|+
operator|(
literal|8
operator|*
name|material
operator|.
name|length
operator|)
argument_list|)
throw|;
block|}
name|int
name|nextVersion
init|=
name|meta
operator|.
name|addVersion
argument_list|()
decl_stmt|;
name|credentials
operator|.
name|addSecretKey
argument_list|(
operator|new
name|Text
argument_list|(
name|name
argument_list|)
argument_list|,
name|meta
operator|.
name|serialize
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|versionName
init|=
name|buildVersionName
argument_list|(
name|name
argument_list|,
name|nextVersion
argument_list|)
decl_stmt|;
name|credentials
operator|.
name|addSecretKey
argument_list|(
operator|new
name|Text
argument_list|(
name|versionName
argument_list|)
argument_list|,
name|material
argument_list|)
expr_stmt|;
return|return
operator|new
name|KeyVersion
argument_list|(
name|versionName
argument_list|,
name|material
argument_list|)
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
name|SCHEME_NAME
operator|+
literal|":///"
return|;
block|}
annotation|@
name|Override
DECL|method|flush ()
specifier|public
name|void
name|flush
parameter_list|()
block|{
name|user
operator|.
name|addCredentials
argument_list|(
name|credentials
argument_list|)
expr_stmt|;
block|}
DECL|class|Factory
specifier|public
specifier|static
class|class
name|Factory
extends|extends
name|KeyProviderFactory
block|{
annotation|@
name|Override
DECL|method|createProvider (URI providerName, Configuration conf)
specifier|public
name|KeyProvider
name|createProvider
parameter_list|(
name|URI
name|providerName
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|SCHEME_NAME
operator|.
name|equals
argument_list|(
name|providerName
operator|.
name|getScheme
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|new
name|UserProvider
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

