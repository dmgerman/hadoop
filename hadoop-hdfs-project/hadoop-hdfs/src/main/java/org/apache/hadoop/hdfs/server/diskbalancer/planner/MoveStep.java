begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.diskbalancer.planner
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
name|diskbalancer
operator|.
name|planner
package|;
end_package

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonInclude
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
name|diskbalancer
operator|.
name|datamodel
operator|.
name|DiskBalancerVolume
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
name|util
operator|.
name|StringUtils
import|;
end_import

begin_comment
comment|/**  * Ignore fields with default values. In most cases Throughtput, diskErrors  * tolerancePercent and bandwidth will be the system defaults.  * So we will avoid serializing them into JSON.  */
end_comment

begin_class
annotation|@
name|JsonInclude
argument_list|(
name|JsonInclude
operator|.
name|Include
operator|.
name|NON_DEFAULT
argument_list|)
comment|/**  * Move step is a step that planner can execute that will move data from one  * volume to another.  */
DECL|class|MoveStep
specifier|public
class|class
name|MoveStep
implements|implements
name|Step
block|{
DECL|field|sourceVolume
specifier|private
name|DiskBalancerVolume
name|sourceVolume
decl_stmt|;
DECL|field|destinationVolume
specifier|private
name|DiskBalancerVolume
name|destinationVolume
decl_stmt|;
DECL|field|idealStorage
specifier|private
name|double
name|idealStorage
decl_stmt|;
DECL|field|bytesToMove
specifier|private
name|long
name|bytesToMove
decl_stmt|;
DECL|field|volumeSetID
specifier|private
name|String
name|volumeSetID
decl_stmt|;
DECL|field|maxDiskErrors
specifier|private
name|long
name|maxDiskErrors
decl_stmt|;
DECL|field|tolerancePercent
specifier|private
name|long
name|tolerancePercent
decl_stmt|;
DECL|field|bandwidth
specifier|private
name|long
name|bandwidth
decl_stmt|;
comment|/**    * Constructs a MoveStep for the volume set.    *    * @param sourceVolume      - Source Disk    * @param idealStorage      - Ideal Storage Value for this disk set    * @param destinationVolume - Destination dis    * @param bytesToMove       - number of bytes to move    * @param volumeSetID       - a diskBalancer generated id.    */
DECL|method|MoveStep (DiskBalancerVolume sourceVolume, double idealStorage, DiskBalancerVolume destinationVolume, long bytesToMove, String volumeSetID)
specifier|public
name|MoveStep
parameter_list|(
name|DiskBalancerVolume
name|sourceVolume
parameter_list|,
name|double
name|idealStorage
parameter_list|,
name|DiskBalancerVolume
name|destinationVolume
parameter_list|,
name|long
name|bytesToMove
parameter_list|,
name|String
name|volumeSetID
parameter_list|)
block|{
name|this
operator|.
name|destinationVolume
operator|=
name|destinationVolume
expr_stmt|;
name|this
operator|.
name|idealStorage
operator|=
name|idealStorage
expr_stmt|;
name|this
operator|.
name|sourceVolume
operator|=
name|sourceVolume
expr_stmt|;
name|this
operator|.
name|bytesToMove
operator|=
name|bytesToMove
expr_stmt|;
name|this
operator|.
name|volumeSetID
operator|=
name|volumeSetID
expr_stmt|;
block|}
comment|/**    * Empty Constructor for JSON serialization.    */
DECL|method|MoveStep ()
specifier|public
name|MoveStep
parameter_list|()
block|{   }
comment|/**    * Returns number of bytes to move.    *    * @return - long    */
annotation|@
name|Override
DECL|method|getBytesToMove ()
specifier|public
name|long
name|getBytesToMove
parameter_list|()
block|{
return|return
name|bytesToMove
return|;
block|}
comment|/**    * Gets the destination volume.    *    * @return - volume    */
annotation|@
name|Override
DECL|method|getDestinationVolume ()
specifier|public
name|DiskBalancerVolume
name|getDestinationVolume
parameter_list|()
block|{
return|return
name|destinationVolume
return|;
block|}
comment|/**    * Gets the IdealStorage.    *    * @return float    */
annotation|@
name|Override
DECL|method|getIdealStorage ()
specifier|public
name|double
name|getIdealStorage
parameter_list|()
block|{
return|return
name|idealStorage
return|;
block|}
comment|/**    * Gets Source Volume.    *    * @return -- Source Volume    */
annotation|@
name|Override
DECL|method|getSourceVolume ()
specifier|public
name|DiskBalancerVolume
name|getSourceVolume
parameter_list|()
block|{
return|return
name|sourceVolume
return|;
block|}
comment|/**    * Gets a volume Set ID.    *    * @return String    */
annotation|@
name|Override
DECL|method|getVolumeSetID ()
specifier|public
name|String
name|getVolumeSetID
parameter_list|()
block|{
return|return
name|volumeSetID
return|;
block|}
comment|/**    * Set source volume.    *    * @param sourceVolume - volume    */
DECL|method|setSourceVolume (DiskBalancerVolume sourceVolume)
specifier|public
name|void
name|setSourceVolume
parameter_list|(
name|DiskBalancerVolume
name|sourceVolume
parameter_list|)
block|{
name|this
operator|.
name|sourceVolume
operator|=
name|sourceVolume
expr_stmt|;
block|}
comment|/**    * Sets destination volume.    *    * @param destinationVolume - volume    */
DECL|method|setDestinationVolume (DiskBalancerVolume destinationVolume)
specifier|public
name|void
name|setDestinationVolume
parameter_list|(
name|DiskBalancerVolume
name|destinationVolume
parameter_list|)
block|{
name|this
operator|.
name|destinationVolume
operator|=
name|destinationVolume
expr_stmt|;
block|}
comment|/**    * Sets Ideal Storage.    *    * @param idealStorage - ideal Storage    */
DECL|method|setIdealStorage (double idealStorage)
specifier|public
name|void
name|setIdealStorage
parameter_list|(
name|double
name|idealStorage
parameter_list|)
block|{
name|this
operator|.
name|idealStorage
operator|=
name|idealStorage
expr_stmt|;
block|}
comment|/**    * Sets bytes to move.    *    * @param bytesToMove - number of bytes    */
DECL|method|setBytesToMove (long bytesToMove)
specifier|public
name|void
name|setBytesToMove
parameter_list|(
name|long
name|bytesToMove
parameter_list|)
block|{
name|this
operator|.
name|bytesToMove
operator|=
name|bytesToMove
expr_stmt|;
block|}
comment|/**    * Sets volume id.    *    * @param volumeSetID - volume ID    */
DECL|method|setVolumeSetID (String volumeSetID)
specifier|public
name|void
name|setVolumeSetID
parameter_list|(
name|String
name|volumeSetID
parameter_list|)
block|{
name|this
operator|.
name|volumeSetID
operator|=
name|volumeSetID
expr_stmt|;
block|}
comment|/**    * Returns a string representation of the object.    *    * @return a string representation of the object.    */
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%s\t %s\t %s\t %s%n"
argument_list|,
name|this
operator|.
name|getSourceVolume
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|this
operator|.
name|getDestinationVolume
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|getSizeString
argument_list|(
name|this
operator|.
name|getBytesToMove
argument_list|()
argument_list|)
argument_list|,
name|this
operator|.
name|getDestinationVolume
argument_list|()
operator|.
name|getStorageType
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns human readable move sizes.    *    * @param size - bytes being moved.    * @return String    */
annotation|@
name|Override
DECL|method|getSizeString (long size)
specifier|public
name|String
name|getSizeString
parameter_list|(
name|long
name|size
parameter_list|)
block|{
return|return
name|StringUtils
operator|.
name|TraditionalBinaryPrefix
operator|.
name|long2String
argument_list|(
name|size
argument_list|,
literal|""
argument_list|,
literal|1
argument_list|)
return|;
block|}
comment|/**    * Gets Maximum numbers of errors to be tolerated before this    * move operation is aborted.    * @return  long.    */
annotation|@
name|Override
DECL|method|getMaxDiskErrors ()
specifier|public
name|long
name|getMaxDiskErrors
parameter_list|()
block|{
return|return
name|maxDiskErrors
return|;
block|}
comment|/**    * Sets the maximum numbers of Errors to be tolerated before this    * step is aborted.    * @param maxDiskErrors - long    */
annotation|@
name|Override
DECL|method|setMaxDiskErrors (long maxDiskErrors)
specifier|public
name|void
name|setMaxDiskErrors
parameter_list|(
name|long
name|maxDiskErrors
parameter_list|)
block|{
name|this
operator|.
name|maxDiskErrors
operator|=
name|maxDiskErrors
expr_stmt|;
block|}
comment|/**    * Tolerance Percentage indicates when a move operation is considered good    * enough. This is a percentage of deviation from ideal that is considered    * fine.    *    * For example : if the ideal amount on each disk was 1 TB and the    * tolerance was 10%, then getting to 900 GB on the destination disk is    * considerd good enough.    *    * @return tolerance percentage.    */
annotation|@
name|Override
DECL|method|getTolerancePercent ()
specifier|public
name|long
name|getTolerancePercent
parameter_list|()
block|{
return|return
name|tolerancePercent
return|;
block|}
comment|/**    * Sets the tolerance percentage.    * @param tolerancePercent  - long    */
annotation|@
name|Override
DECL|method|setTolerancePercent (long tolerancePercent)
specifier|public
name|void
name|setTolerancePercent
parameter_list|(
name|long
name|tolerancePercent
parameter_list|)
block|{
name|this
operator|.
name|tolerancePercent
operator|=
name|tolerancePercent
expr_stmt|;
block|}
comment|/**    * Gets the disk Bandwidth. That is the MB/Sec to copied. We will max out    * on this amount of throughput. This is useful to prevent too much I/O on    * datanode while data node is in use.    * @return  long.    */
annotation|@
name|Override
DECL|method|getBandwidth ()
specifier|public
name|long
name|getBandwidth
parameter_list|()
block|{
return|return
name|bandwidth
return|;
block|}
comment|/**    * Sets the maximum disk bandwidth per sec to use for this step.    * @param bandwidth  - Long, MB / Sec of data to be moved between    *                   source and destinatin volume.    */
annotation|@
name|Override
DECL|method|setBandwidth (long bandwidth)
specifier|public
name|void
name|setBandwidth
parameter_list|(
name|long
name|bandwidth
parameter_list|)
block|{
name|this
operator|.
name|bandwidth
operator|=
name|bandwidth
expr_stmt|;
block|}
block|}
end_class

end_unit

