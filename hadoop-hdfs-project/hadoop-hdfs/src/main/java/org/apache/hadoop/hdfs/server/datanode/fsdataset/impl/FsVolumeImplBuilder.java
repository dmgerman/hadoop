begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.fsdataset.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|fsdataset
operator|.
name|impl
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
name|hdfs
operator|.
name|server
operator|.
name|common
operator|.
name|Storage
operator|.
name|StorageDirectory
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|FileIoProvider
import|;
end_import

begin_comment
comment|/**  * This class is to be used as a builder for {@link FsVolumeImpl} objects.  */
end_comment

begin_class
DECL|class|FsVolumeImplBuilder
specifier|public
class|class
name|FsVolumeImplBuilder
block|{
DECL|field|dataset
specifier|private
name|FsDatasetImpl
name|dataset
decl_stmt|;
DECL|field|storageID
specifier|private
name|String
name|storageID
decl_stmt|;
DECL|field|sd
specifier|private
name|StorageDirectory
name|sd
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|fileIoProvider
specifier|private
name|FileIoProvider
name|fileIoProvider
decl_stmt|;
DECL|method|FsVolumeImplBuilder ()
specifier|public
name|FsVolumeImplBuilder
parameter_list|()
block|{
name|dataset
operator|=
literal|null
expr_stmt|;
name|storageID
operator|=
literal|null
expr_stmt|;
name|sd
operator|=
literal|null
expr_stmt|;
name|conf
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|setDataset (FsDatasetImpl dataset)
name|FsVolumeImplBuilder
name|setDataset
parameter_list|(
name|FsDatasetImpl
name|dataset
parameter_list|)
block|{
name|this
operator|.
name|dataset
operator|=
name|dataset
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setStorageID (String id)
name|FsVolumeImplBuilder
name|setStorageID
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|storageID
operator|=
name|id
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setStorageDirectory (StorageDirectory sd)
name|FsVolumeImplBuilder
name|setStorageDirectory
parameter_list|(
name|StorageDirectory
name|sd
parameter_list|)
block|{
name|this
operator|.
name|sd
operator|=
name|sd
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setConf (Configuration conf)
name|FsVolumeImplBuilder
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setFileIoProvider (FileIoProvider fileIoProvider)
name|FsVolumeImplBuilder
name|setFileIoProvider
parameter_list|(
name|FileIoProvider
name|fileIoProvider
parameter_list|)
block|{
name|this
operator|.
name|fileIoProvider
operator|=
name|fileIoProvider
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
name|FsVolumeImpl
name|build
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|FsVolumeImpl
argument_list|(
name|dataset
argument_list|,
name|storageID
argument_list|,
name|sd
argument_list|,
name|fileIoProvider
operator|!=
literal|null
condition|?
name|fileIoProvider
else|:
operator|new
name|FileIoProvider
argument_list|(
literal|null
argument_list|)
argument_list|,
name|conf
argument_list|)
return|;
block|}
block|}
end_class

end_unit

