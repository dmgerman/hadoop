begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|crypto
operator|.
name|key
operator|.
name|KeyProviderTokenIssuer
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
name|fs
operator|.
name|FileSystem
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
name|fs
operator|.
name|GlobalStorageStatistics
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
name|fs
operator|.
name|StorageStatistics
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
name|DelegationTokenIssuer
import|;
end_import

begin_comment
comment|/**  * The Ozone Filesystem implementation.  *<p>  * This subclass is marked as private as code should not be creating it  * directly; use {@link FileSystem#get(Configuration)} and variants to create  * one. If cast to {@link OzoneFileSystem}, extra methods and features may be  * accessed. Consider those private and unstable.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|OzoneFileSystem
specifier|public
class|class
name|OzoneFileSystem
extends|extends
name|BasicOzoneFileSystem
implements|implements
name|KeyProviderTokenIssuer
block|{
DECL|field|storageStatistics
specifier|private
name|OzoneFSStorageStatistics
name|storageStatistics
decl_stmt|;
annotation|@
name|Override
DECL|method|getKeyProvider ()
specifier|public
name|KeyProvider
name|getKeyProvider
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|getAdapter
argument_list|()
operator|.
name|getKeyProvider
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getKeyProviderUri ()
specifier|public
name|URI
name|getKeyProviderUri
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|getAdapter
argument_list|()
operator|.
name|getKeyProviderUri
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getAdditionalTokenIssuers ()
specifier|public
name|DelegationTokenIssuer
index|[]
name|getAdditionalTokenIssuers
parameter_list|()
throws|throws
name|IOException
block|{
name|KeyProvider
name|keyProvider
decl_stmt|;
try|try
block|{
name|keyProvider
operator|=
name|getKeyProvider
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error retrieving KeyProvider."
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
if|if
condition|(
name|keyProvider
operator|instanceof
name|DelegationTokenIssuer
condition|)
block|{
return|return
operator|new
name|DelegationTokenIssuer
index|[]
block|{
operator|(
name|DelegationTokenIssuer
operator|)
name|keyProvider
block|}
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getOzoneFSOpsCountStatistics ()
name|StorageStatistics
name|getOzoneFSOpsCountStatistics
parameter_list|()
block|{
return|return
name|storageStatistics
return|;
block|}
annotation|@
name|Override
DECL|method|incrementCounter (Statistic statistic)
specifier|protected
name|void
name|incrementCounter
parameter_list|(
name|Statistic
name|statistic
parameter_list|)
block|{
if|if
condition|(
name|storageStatistics
operator|!=
literal|null
condition|)
block|{
name|storageStatistics
operator|.
name|incrementCounter
argument_list|(
name|statistic
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|createAdapter (Configuration conf, String bucketStr, String volumeStr, String omHost, String omPort, boolean isolatedClassloader)
specifier|protected
name|OzoneClientAdapter
name|createAdapter
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|bucketStr
parameter_list|,
name|String
name|volumeStr
parameter_list|,
name|String
name|omHost
parameter_list|,
name|String
name|omPort
parameter_list|,
name|boolean
name|isolatedClassloader
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|storageStatistics
operator|=
operator|(
name|OzoneFSStorageStatistics
operator|)
name|GlobalStorageStatistics
operator|.
name|INSTANCE
operator|.
name|put
argument_list|(
name|OzoneFSStorageStatistics
operator|.
name|NAME
argument_list|,
name|OzoneFSStorageStatistics
operator|::
operator|new
argument_list|)
expr_stmt|;
if|if
condition|(
name|isolatedClassloader
condition|)
block|{
return|return
name|OzoneClientAdapterFactory
operator|.
name|createAdapter
argument_list|(
name|volumeStr
argument_list|,
name|bucketStr
argument_list|,
name|storageStatistics
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|OzoneClientAdapterImpl
argument_list|(
name|omHost
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|omPort
argument_list|)
argument_list|,
name|conf
argument_list|,
name|volumeStr
argument_list|,
name|bucketStr
argument_list|,
name|storageStatistics
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

