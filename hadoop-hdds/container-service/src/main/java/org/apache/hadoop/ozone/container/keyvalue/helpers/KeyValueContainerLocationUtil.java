begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.keyvalue.helpers
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|keyvalue
operator|.
name|helpers
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|OzoneConsts
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
name|common
operator|.
name|Storage
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_comment
comment|/**  * Class which provides utility methods for container locations.  */
end_comment

begin_class
DECL|class|KeyValueContainerLocationUtil
specifier|public
specifier|final
class|class
name|KeyValueContainerLocationUtil
block|{
comment|/* Never constructed. */
DECL|method|KeyValueContainerLocationUtil ()
specifier|private
name|KeyValueContainerLocationUtil
parameter_list|()
block|{    }
comment|/**    * Returns Container Metadata Location.    * @param hddsVolumeDir base dir of the hdds volume where scm directories    *                      are stored    * @param scmId    * @param containerId    * @return containerMetadata Path to container metadata location where    * .container file will be stored.    */
DECL|method|getContainerMetaDataPath (String hddsVolumeDir, String scmId, long containerId)
specifier|public
specifier|static
name|File
name|getContainerMetaDataPath
parameter_list|(
name|String
name|hddsVolumeDir
parameter_list|,
name|String
name|scmId
parameter_list|,
name|long
name|containerId
parameter_list|)
block|{
name|String
name|containerMetaDataPath
init|=
name|getBaseContainerLocation
argument_list|(
name|hddsVolumeDir
argument_list|,
name|scmId
argument_list|,
name|containerId
argument_list|)
decl_stmt|;
name|containerMetaDataPath
operator|=
name|containerMetaDataPath
operator|+
name|File
operator|.
name|separator
operator|+
name|OzoneConsts
operator|.
name|CONTAINER_META_PATH
expr_stmt|;
return|return
operator|new
name|File
argument_list|(
name|containerMetaDataPath
argument_list|)
return|;
block|}
comment|/**    * Returns Container Chunks Location.    * @param baseDir    * @param scmId    * @param containerId    * @return chunksPath    */
DECL|method|getChunksLocationPath (String baseDir, String scmId, long containerId)
specifier|public
specifier|static
name|File
name|getChunksLocationPath
parameter_list|(
name|String
name|baseDir
parameter_list|,
name|String
name|scmId
parameter_list|,
name|long
name|containerId
parameter_list|)
block|{
name|String
name|chunksPath
init|=
name|getBaseContainerLocation
argument_list|(
name|baseDir
argument_list|,
name|scmId
argument_list|,
name|containerId
argument_list|)
operator|+
name|File
operator|.
name|separator
operator|+
name|OzoneConsts
operator|.
name|STORAGE_DIR_CHUNKS
decl_stmt|;
return|return
operator|new
name|File
argument_list|(
name|chunksPath
argument_list|)
return|;
block|}
comment|/**    * Returns base directory for specified container.    * @param hddsVolumeDir    * @param scmId    * @param containerId    * @return base directory for container.    */
DECL|method|getBaseContainerLocation (String hddsVolumeDir, String scmId, long containerId)
specifier|private
specifier|static
name|String
name|getBaseContainerLocation
parameter_list|(
name|String
name|hddsVolumeDir
parameter_list|,
name|String
name|scmId
parameter_list|,
name|long
name|containerId
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|hddsVolumeDir
argument_list|,
literal|"Base Directory cannot be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|scmId
argument_list|,
literal|"scmUuid cannot be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|containerId
operator|>=
literal|0
argument_list|,
literal|"Container Id cannot be negative."
argument_list|)
expr_stmt|;
name|String
name|containerSubDirectory
init|=
name|getContainerSubDirectory
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
name|String
name|containerMetaDataPath
init|=
name|hddsVolumeDir
operator|+
name|File
operator|.
name|separator
operator|+
name|scmId
operator|+
name|File
operator|.
name|separator
operator|+
name|Storage
operator|.
name|STORAGE_DIR_CURRENT
operator|+
name|File
operator|.
name|separator
operator|+
name|containerSubDirectory
operator|+
name|File
operator|.
name|separator
operator|+
name|containerId
decl_stmt|;
return|return
name|containerMetaDataPath
return|;
block|}
comment|/**    * Returns subdirectory, where this container needs to be placed.    * @param containerId    * @return container sub directory    */
DECL|method|getContainerSubDirectory (long containerId)
specifier|private
specifier|static
name|String
name|getContainerSubDirectory
parameter_list|(
name|long
name|containerId
parameter_list|)
block|{
name|int
name|directory
init|=
call|(
name|int
call|)
argument_list|(
operator|(
name|containerId
operator|>>
literal|9
operator|)
operator|&
literal|0xFF
argument_list|)
decl_stmt|;
return|return
name|Storage
operator|.
name|CONTAINER_DIR
operator|+
name|directory
return|;
block|}
comment|/**    * Returns containerFile.    * @param containerMetaDataPath    * @param containerName    * @return .container File name    */
DECL|method|getContainerFile (File containerMetaDataPath, String containerName)
specifier|public
specifier|static
name|File
name|getContainerFile
parameter_list|(
name|File
name|containerMetaDataPath
parameter_list|,
name|String
name|containerName
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerMetaDataPath
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerName
argument_list|)
expr_stmt|;
return|return
operator|new
name|File
argument_list|(
name|containerMetaDataPath
argument_list|,
name|containerName
operator|+
name|OzoneConsts
operator|.
name|CONTAINER_EXTENSION
argument_list|)
return|;
block|}
comment|/**    * Return containerDB File.    * @param containerMetaDataPath    * @param containerName    * @return containerDB File name    */
DECL|method|getContainerDBFile (File containerMetaDataPath, String containerName)
specifier|public
specifier|static
name|File
name|getContainerDBFile
parameter_list|(
name|File
name|containerMetaDataPath
parameter_list|,
name|String
name|containerName
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerMetaDataPath
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerName
argument_list|)
expr_stmt|;
return|return
operator|new
name|File
argument_list|(
name|containerMetaDataPath
argument_list|,
name|containerName
operator|+
name|OzoneConsts
operator|.
name|DN_CONTAINER_DB
argument_list|)
return|;
block|}
comment|/**    * Returns container checksum file.    * @param containerMetaDataPath    * @param containerName    * @return container checksum file    */
DECL|method|getContainerCheckSumFile (File containerMetaDataPath, String containerName)
specifier|public
specifier|static
name|File
name|getContainerCheckSumFile
parameter_list|(
name|File
name|containerMetaDataPath
parameter_list|,
name|String
name|containerName
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerMetaDataPath
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerName
argument_list|)
expr_stmt|;
return|return
operator|new
name|File
argument_list|(
name|containerMetaDataPath
argument_list|,
name|containerName
operator|+
name|OzoneConsts
operator|.
name|CONTAINER_FILE_CHECKSUM_EXTENSION
argument_list|)
return|;
block|}
block|}
end_class

end_unit

