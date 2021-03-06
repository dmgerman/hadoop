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

begin_comment
comment|/**  * A step in the plan.  */
end_comment

begin_interface
DECL|interface|Step
specifier|public
interface|interface
name|Step
block|{
comment|/**    * Return the number of bytes to move.    *    * @return bytes    */
DECL|method|getBytesToMove ()
name|long
name|getBytesToMove
parameter_list|()
function_decl|;
comment|/**    * Gets the destination volume.    *    * @return - volume    */
DECL|method|getDestinationVolume ()
name|DiskBalancerVolume
name|getDestinationVolume
parameter_list|()
function_decl|;
comment|/**    * Gets the IdealStorage.    *    * @return idealStorage    */
DECL|method|getIdealStorage ()
name|double
name|getIdealStorage
parameter_list|()
function_decl|;
comment|/**    * Gets Source Volume.    *    * @return -- Source Volume    */
DECL|method|getSourceVolume ()
name|DiskBalancerVolume
name|getSourceVolume
parameter_list|()
function_decl|;
comment|/**    * Gets a volume Set ID.    *    * @return String    */
DECL|method|getVolumeSetID ()
name|String
name|getVolumeSetID
parameter_list|()
function_decl|;
comment|/**    * Returns a String representation of the Step Size.    *    * @return String    */
DECL|method|getSizeString (long size)
name|String
name|getSizeString
parameter_list|(
name|long
name|size
parameter_list|)
function_decl|;
comment|/**    * Returns maximum number of disk erros tolerated.    * @return long.    */
DECL|method|getMaxDiskErrors ()
name|long
name|getMaxDiskErrors
parameter_list|()
function_decl|;
comment|/**    * Returns tolerance percentage, the good enough value    * when we move data from one to disk to another.    * @return long.    */
DECL|method|getTolerancePercent ()
name|long
name|getTolerancePercent
parameter_list|()
function_decl|;
comment|/**    * Returns max disk bandwidth that disk balancer will use.    * Expressed in MB/sec. For example, a value like 10    * indicates that disk balancer will only move 10 MB / sec    * while it is running.    * @return long.    */
DECL|method|getBandwidth ()
name|long
name|getBandwidth
parameter_list|()
function_decl|;
comment|/**    * Sets Tolerance percent on a specific step.    * @param tolerancePercent - tolerance in percentage.    */
DECL|method|setTolerancePercent (long tolerancePercent)
name|void
name|setTolerancePercent
parameter_list|(
name|long
name|tolerancePercent
parameter_list|)
function_decl|;
comment|/**    * Set Bandwidth on a specific step.    * @param bandwidth - in MB/s    */
DECL|method|setBandwidth (long bandwidth)
name|void
name|setBandwidth
parameter_list|(
name|long
name|bandwidth
parameter_list|)
function_decl|;
comment|/**    * Set maximum errors to tolerate before disk balancer step fails.    * @param maxDiskErrors - error count.    */
DECL|method|setMaxDiskErrors (long maxDiskErrors)
name|void
name|setMaxDiskErrors
parameter_list|(
name|long
name|maxDiskErrors
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

