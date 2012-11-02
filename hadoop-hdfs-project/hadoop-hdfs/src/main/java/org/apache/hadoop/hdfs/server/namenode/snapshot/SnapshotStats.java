begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.snapshot
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
operator|.
name|snapshot
package|;
end_package

begin_comment
comment|/**  * This is an interface used to retrieve statistic information related to  * snapshots  */
end_comment

begin_interface
DECL|interface|SnapshotStats
specifier|public
interface|interface
name|SnapshotStats
block|{
comment|/**    * @return The number of snapshottale directories in the system     */
DECL|method|getNumSnapshottableDirs ()
specifier|public
name|long
name|getNumSnapshottableDirs
parameter_list|()
function_decl|;
comment|/**    * @return The number of directories that have been snapshotted    */
DECL|method|getNumSnapshots ()
specifier|public
name|long
name|getNumSnapshots
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

