begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.interfaces
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
name|common
operator|.
name|interfaces
package|;
end_package

begin_comment
comment|/**  * Contract to define properties available on the JMX interface.  */
end_comment

begin_interface
DECL|interface|StorageLocationReportMXBean
specifier|public
interface|interface
name|StorageLocationReportMXBean
block|{
DECL|method|getId ()
name|String
name|getId
parameter_list|()
function_decl|;
DECL|method|isFailed ()
name|boolean
name|isFailed
parameter_list|()
function_decl|;
DECL|method|getCapacity ()
name|long
name|getCapacity
parameter_list|()
function_decl|;
DECL|method|getScmUsed ()
name|long
name|getScmUsed
parameter_list|()
function_decl|;
DECL|method|getRemaining ()
name|long
name|getRemaining
parameter_list|()
function_decl|;
DECL|method|getStorageLocation ()
name|String
name|getStorageLocation
parameter_list|()
function_decl|;
DECL|method|getStorageTypeName ()
name|String
name|getStorageTypeName
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

