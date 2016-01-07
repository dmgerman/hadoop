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
name|float
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
block|}
end_interface

end_unit

