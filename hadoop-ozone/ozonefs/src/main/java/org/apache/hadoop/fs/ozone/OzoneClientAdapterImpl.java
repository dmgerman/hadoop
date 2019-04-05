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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
import|;
end_import

begin_comment
comment|/**  * Implementation of the OzoneFileSystem calls.  */
end_comment

begin_class
DECL|class|OzoneClientAdapterImpl
specifier|public
class|class
name|OzoneClientAdapterImpl
extends|extends
name|BasicOzoneClientAdapterImpl
block|{
DECL|field|storageStatistics
specifier|private
name|OzoneFSStorageStatistics
name|storageStatistics
decl_stmt|;
DECL|method|OzoneClientAdapterImpl (String volumeStr, String bucketStr, OzoneFSStorageStatistics storageStatistics)
specifier|public
name|OzoneClientAdapterImpl
parameter_list|(
name|String
name|volumeStr
parameter_list|,
name|String
name|bucketStr
parameter_list|,
name|OzoneFSStorageStatistics
name|storageStatistics
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|volumeStr
argument_list|,
name|bucketStr
argument_list|)
expr_stmt|;
name|this
operator|.
name|storageStatistics
operator|=
name|storageStatistics
expr_stmt|;
block|}
DECL|method|OzoneClientAdapterImpl ( OzoneConfiguration conf, String volumeStr, String bucketStr, OzoneFSStorageStatistics storageStatistics)
specifier|public
name|OzoneClientAdapterImpl
parameter_list|(
name|OzoneConfiguration
name|conf
parameter_list|,
name|String
name|volumeStr
parameter_list|,
name|String
name|bucketStr
parameter_list|,
name|OzoneFSStorageStatistics
name|storageStatistics
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|conf
argument_list|,
name|volumeStr
argument_list|,
name|bucketStr
argument_list|)
expr_stmt|;
name|this
operator|.
name|storageStatistics
operator|=
name|storageStatistics
expr_stmt|;
block|}
DECL|method|OzoneClientAdapterImpl (String omHost, int omPort, Configuration hadoopConf, String volumeStr, String bucketStr, OzoneFSStorageStatistics storageStatistics)
specifier|public
name|OzoneClientAdapterImpl
parameter_list|(
name|String
name|omHost
parameter_list|,
name|int
name|omPort
parameter_list|,
name|Configuration
name|hadoopConf
parameter_list|,
name|String
name|volumeStr
parameter_list|,
name|String
name|bucketStr
parameter_list|,
name|OzoneFSStorageStatistics
name|storageStatistics
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|omHost
argument_list|,
name|omPort
argument_list|,
name|hadoopConf
argument_list|,
name|volumeStr
argument_list|,
name|bucketStr
argument_list|)
expr_stmt|;
name|this
operator|.
name|storageStatistics
operator|=
name|storageStatistics
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementCounter (Statistic objectsRead)
specifier|protected
name|void
name|incrementCounter
parameter_list|(
name|Statistic
name|objectsRead
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
name|objectsRead
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

