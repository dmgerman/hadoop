begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.block
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|block
package|;
end_package

begin_comment
comment|/**  * JMX interface for the block manager.  */
end_comment

begin_interface
DECL|interface|BlockmanagerMXBean
specifier|public
interface|interface
name|BlockmanagerMXBean
block|{
comment|/**    * Number of open containers manager by the block manager.    */
DECL|method|getOpenContainersNo ()
name|int
name|getOpenContainersNo
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

