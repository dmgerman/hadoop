begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.extdataset
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
name|extdataset
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
name|nio
operator|.
name|channels
operator|.
name|ClosedChannelException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|fs
operator|.
name|DF
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
name|StorageType
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
name|StorageLocation
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
name|DirectoryScanner
operator|.
name|ReportCompiler
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
name|checker
operator|.
name|VolumeCheckResult
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
name|fsdataset
operator|.
name|FsDatasetSpi
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
name|fsdataset
operator|.
name|FsVolumeReference
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
name|fsdataset
operator|.
name|FsVolumeSpi
import|;
end_import

begin_class
DECL|class|ExternalVolumeImpl
specifier|public
class|class
name|ExternalVolumeImpl
implements|implements
name|FsVolumeSpi
block|{
annotation|@
name|Override
DECL|method|obtainReference ()
specifier|public
name|FsVolumeReference
name|obtainReference
parameter_list|()
throws|throws
name|ClosedChannelException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getBlockPoolList ()
specifier|public
name|String
index|[]
name|getBlockPoolList
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getAvailable ()
specifier|public
name|long
name|getAvailable
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getStorageID ()
specifier|public
name|String
name|getStorageID
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getStorageType ()
specifier|public
name|StorageType
name|getStorageType
parameter_list|()
block|{
return|return
name|StorageType
operator|.
name|DEFAULT
return|;
block|}
annotation|@
name|Override
DECL|method|isTransientStorage ()
specifier|public
name|boolean
name|isTransientStorage
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|reserveSpaceForReplica (long bytesToReserve)
specifier|public
name|void
name|reserveSpaceForReplica
parameter_list|(
name|long
name|bytesToReserve
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|releaseReservedSpace (long bytesToRelease)
specifier|public
name|void
name|releaseReservedSpace
parameter_list|(
name|long
name|bytesToRelease
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|releaseLockedMemory (long bytesToRelease)
specifier|public
name|void
name|releaseLockedMemory
parameter_list|(
name|long
name|bytesToRelease
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|newBlockIterator (String bpid, String name)
specifier|public
name|BlockIterator
name|newBlockIterator
parameter_list|(
name|String
name|bpid
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|loadBlockIterator (String bpid, String name)
specifier|public
name|BlockIterator
name|loadBlockIterator
parameter_list|(
name|String
name|bpid
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getDataset ()
specifier|public
name|FsDatasetSpi
name|getDataset
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getStorageLocation ()
specifier|public
name|StorageLocation
name|getStorageLocation
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getBaseURI ()
specifier|public
name|URI
name|getBaseURI
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getUsageStats (Configuration conf)
specifier|public
name|DF
name|getUsageStats
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|compileReport (String bpid, LinkedList<ScanInfo> report, ReportCompiler reportCompiler)
specifier|public
name|LinkedList
argument_list|<
name|ScanInfo
argument_list|>
name|compileReport
parameter_list|(
name|String
name|bpid
parameter_list|,
name|LinkedList
argument_list|<
name|ScanInfo
argument_list|>
name|report
parameter_list|,
name|ReportCompiler
name|reportCompiler
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getFileIoProvider ()
specifier|public
name|FileIoProvider
name|getFileIoProvider
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|check (VolumeCheckContext context)
specifier|public
name|VolumeCheckResult
name|check
parameter_list|(
name|VolumeCheckContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|VolumeCheckResult
operator|.
name|HEALTHY
return|;
block|}
block|}
end_class

end_unit

