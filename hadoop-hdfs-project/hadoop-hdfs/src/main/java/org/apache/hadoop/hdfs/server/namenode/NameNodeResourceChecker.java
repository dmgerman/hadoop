begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|namenode
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|hdfs
operator|.
name|DFSConfigKeys
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
name|Util
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Collections2
import|;
end_import

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
name|Predicate
import|;
end_import

begin_comment
comment|/**  *   * NameNodeResourceChecker provides a method -  *<code>hasAvailableDiskSpace</code> - which will return true if and only if  * the NameNode has disk space available on all required volumes, and any volume  * which is configured to be redundant. Volumes containing file system edits dirs  * are added by default, and arbitrary extra volumes may be configured as well.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|NameNodeResourceChecker
specifier|public
class|class
name|NameNodeResourceChecker
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|NameNodeResourceChecker
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|// Space (in bytes) reserved per volume.
DECL|field|duReserved
specifier|private
name|long
name|duReserved
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|volumes
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|CheckedVolume
argument_list|>
name|volumes
decl_stmt|;
DECL|field|minimumRedundantVolumes
specifier|private
name|int
name|minimumRedundantVolumes
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|class|CheckedVolume
class|class
name|CheckedVolume
implements|implements
name|CheckableNameNodeResource
block|{
DECL|field|df
specifier|private
name|DF
name|df
decl_stmt|;
DECL|field|required
specifier|private
name|boolean
name|required
decl_stmt|;
DECL|field|volume
specifier|private
name|String
name|volume
decl_stmt|;
DECL|method|CheckedVolume (File dirToCheck, boolean required)
specifier|public
name|CheckedVolume
parameter_list|(
name|File
name|dirToCheck
parameter_list|,
name|boolean
name|required
parameter_list|)
throws|throws
name|IOException
block|{
name|df
operator|=
operator|new
name|DF
argument_list|(
name|dirToCheck
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|required
operator|=
name|required
expr_stmt|;
name|volume
operator|=
name|df
operator|.
name|getFilesystem
argument_list|()
expr_stmt|;
block|}
DECL|method|getVolume ()
specifier|public
name|String
name|getVolume
parameter_list|()
block|{
return|return
name|volume
return|;
block|}
annotation|@
name|Override
DECL|method|isRequired ()
specifier|public
name|boolean
name|isRequired
parameter_list|()
block|{
return|return
name|required
return|;
block|}
annotation|@
name|Override
DECL|method|isResourceAvailable ()
specifier|public
name|boolean
name|isResourceAvailable
parameter_list|()
block|{
name|long
name|availableSpace
init|=
name|df
operator|.
name|getAvailable
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Space available on volume '"
operator|+
name|volume
operator|+
literal|"' is "
operator|+
name|availableSpace
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|availableSpace
operator|<
name|duReserved
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Space available on volume '"
operator|+
name|volume
operator|+
literal|"' is "
operator|+
name|availableSpace
operator|+
literal|", which is below the configured reserved amount "
operator|+
name|duReserved
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
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
literal|"volume: "
operator|+
name|volume
operator|+
literal|" required: "
operator|+
name|required
operator|+
literal|" resource available: "
operator|+
name|isResourceAvailable
argument_list|()
return|;
block|}
block|}
comment|/**    * Create a NameNodeResourceChecker, which will check the edits dirs and any    * additional dirs to check set in<code>conf</code>.    */
DECL|method|NameNodeResourceChecker (Configuration conf)
specifier|public
name|NameNodeResourceChecker
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|volumes
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|CheckedVolume
argument_list|>
argument_list|()
expr_stmt|;
name|duReserved
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_DU_RESERVED_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_DU_RESERVED_DEFAULT
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|URI
argument_list|>
name|extraCheckedVolumes
init|=
name|Util
operator|.
name|stringCollectionAsURIs
argument_list|(
name|conf
operator|.
name|getTrimmedStringCollection
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_CHECKED_VOLUMES_KEY
argument_list|)
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|URI
argument_list|>
name|localEditDirs
init|=
name|Collections2
operator|.
name|filter
argument_list|(
name|FSNamesystem
operator|.
name|getNamespaceEditsDirs
argument_list|(
name|conf
argument_list|)
argument_list|,
operator|new
name|Predicate
argument_list|<
name|URI
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|URI
name|input
parameter_list|)
block|{
if|if
condition|(
name|input
operator|.
name|getScheme
argument_list|()
operator|.
name|equals
argument_list|(
name|NNStorage
operator|.
name|LOCAL_URI_SCHEME
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|// Add all the local edits dirs, marking some as required if they are
comment|// configured as such.
for|for
control|(
name|URI
name|editsDirToCheck
range|:
name|localEditDirs
control|)
block|{
name|addDirToCheck
argument_list|(
name|editsDirToCheck
argument_list|,
name|FSNamesystem
operator|.
name|getRequiredNamespaceEditsDirs
argument_list|(
name|conf
argument_list|)
operator|.
name|contains
argument_list|(
name|editsDirToCheck
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// All extra checked volumes are marked "required"
for|for
control|(
name|URI
name|extraDirToCheck
range|:
name|extraCheckedVolumes
control|)
block|{
name|addDirToCheck
argument_list|(
name|extraDirToCheck
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|minimumRedundantVolumes
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_CHECKED_VOLUMES_MINIMUM_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_CHECKED_VOLUMES_MINIMUM_DEFAULT
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add the volume of the passed-in directory to the list of volumes to check.    * If<code>required</code> is true, and this volume is already present, but    * is marked redundant, it will be marked required. If the volume is already    * present but marked required then this method is a no-op.    *     * @param directoryToCheck    *          The directory whose volume will be checked for available space.    */
DECL|method|addDirToCheck (URI directoryToCheck, boolean required)
specifier|private
name|void
name|addDirToCheck
parameter_list|(
name|URI
name|directoryToCheck
parameter_list|,
name|boolean
name|required
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|directoryToCheck
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|dir
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Missing directory "
operator|+
name|dir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
name|CheckedVolume
name|newVolume
init|=
operator|new
name|CheckedVolume
argument_list|(
name|dir
argument_list|,
name|required
argument_list|)
decl_stmt|;
name|CheckedVolume
name|volume
init|=
name|volumes
operator|.
name|get
argument_list|(
name|newVolume
operator|.
name|getVolume
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|volume
operator|==
literal|null
operator|||
operator|(
name|volume
operator|!=
literal|null
operator|&&
operator|!
name|volume
operator|.
name|isRequired
argument_list|()
operator|)
condition|)
block|{
name|volumes
operator|.
name|put
argument_list|(
name|newVolume
operator|.
name|getVolume
argument_list|()
argument_list|,
name|newVolume
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Return true if disk space is available on at least one of the configured    * redundant volumes, and all of the configured required volumes.    *     * @return True if the configured amount of disk space is available on at    *         least one redundant volume and all of the required volumes, false    *         otherwise.    */
DECL|method|hasAvailableDiskSpace ()
specifier|public
name|boolean
name|hasAvailableDiskSpace
parameter_list|()
block|{
return|return
name|NameNodeResourcePolicy
operator|.
name|areResourcesAvailable
argument_list|(
name|volumes
operator|.
name|values
argument_list|()
argument_list|,
name|minimumRedundantVolumes
argument_list|)
return|;
block|}
comment|/**    * Return the set of directories which are low on space.    *     * @return the set of directories whose free space is below the threshold.    */
annotation|@
name|VisibleForTesting
DECL|method|getVolumesLowOnSpace ()
name|Collection
argument_list|<
name|String
argument_list|>
name|getVolumesLowOnSpace
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Going to check the following volumes disk space: "
operator|+
name|volumes
argument_list|)
expr_stmt|;
block|}
name|Collection
argument_list|<
name|String
argument_list|>
name|lowVolumes
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|CheckedVolume
name|volume
range|:
name|volumes
operator|.
name|values
argument_list|()
control|)
block|{
name|lowVolumes
operator|.
name|add
argument_list|(
name|volume
operator|.
name|getVolume
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|lowVolumes
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setVolumes (Map<String, CheckedVolume> volumes)
name|void
name|setVolumes
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|CheckedVolume
argument_list|>
name|volumes
parameter_list|)
block|{
name|this
operator|.
name|volumes
operator|=
name|volumes
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setMinimumReduntdantVolumes (int minimumRedundantVolumes)
name|void
name|setMinimumReduntdantVolumes
parameter_list|(
name|int
name|minimumRedundantVolumes
parameter_list|)
block|{
name|this
operator|.
name|minimumRedundantVolumes
operator|=
name|minimumRedundantVolumes
expr_stmt|;
block|}
block|}
end_class

end_unit

