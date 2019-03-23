begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.ozone
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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
name|crypto
operator|.
name|key
operator|.
name|KeyProvider
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
name|ozone
operator|.
name|security
operator|.
name|OzoneTokenIdentifier
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
name|Token
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|Iterator
import|;
end_import

begin_comment
comment|/**  * Lightweight adapter to separte hadoop/ozone classes.  *<p>  * This class contains only the bare minimum Ozone classes in the signature.  * It could be loaded by a different classloader because only the objects in  * the method signatures should be shared between the classloader.  */
end_comment

begin_interface
DECL|interface|OzoneClientAdapter
specifier|public
interface|interface
name|OzoneClientAdapter
block|{
DECL|method|close ()
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|createInputStream (String key)
name|InputStream
name|createInputStream
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|createKey (String key)
name|OzoneFSOutputStream
name|createKey
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|renameKey (String key, String newKeyName)
name|void
name|renameKey
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|newKeyName
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|getKeyInfo (String keyName)
name|BasicKeyInfo
name|getKeyInfo
parameter_list|(
name|String
name|keyName
parameter_list|)
function_decl|;
DECL|method|isDirectory (BasicKeyInfo key)
name|boolean
name|isDirectory
parameter_list|(
name|BasicKeyInfo
name|key
parameter_list|)
function_decl|;
DECL|method|createDirectory (String keyName)
name|boolean
name|createDirectory
parameter_list|(
name|String
name|keyName
parameter_list|)
function_decl|;
DECL|method|deleteObject (String keyName)
name|boolean
name|deleteObject
parameter_list|(
name|String
name|keyName
parameter_list|)
function_decl|;
DECL|method|getCreationTime ()
name|long
name|getCreationTime
parameter_list|()
function_decl|;
DECL|method|hasNextKey (String key)
name|boolean
name|hasNextKey
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
DECL|method|listKeys (String pathKey)
name|Iterator
argument_list|<
name|BasicKeyInfo
argument_list|>
name|listKeys
parameter_list|(
name|String
name|pathKey
parameter_list|)
function_decl|;
DECL|method|getDelegationToken (String renewer)
name|Token
argument_list|<
name|OzoneTokenIdentifier
argument_list|>
name|getDelegationToken
parameter_list|(
name|String
name|renewer
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|getKeyProvider ()
name|KeyProvider
name|getKeyProvider
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|getKeyProviderUri ()
name|URI
name|getKeyProviderUri
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|getCanonicalServiceName ()
name|String
name|getCanonicalServiceName
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

