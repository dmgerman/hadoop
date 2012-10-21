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
name|namenode
operator|.
name|INodeDirectory
import|;
end_import

begin_comment
comment|/** The root directory of a snapshot. */
end_comment

begin_class
DECL|class|INodeDirectorySnapshotRoot
specifier|public
class|class
name|INodeDirectorySnapshotRoot
extends|extends
name|INodeDirectory
block|{
DECL|method|INodeDirectorySnapshotRoot (String name, INodeDirectory dir)
name|INodeDirectorySnapshotRoot
parameter_list|(
name|String
name|name
parameter_list|,
name|INodeDirectory
name|dir
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|dir
operator|.
name|getPermissionStatus
argument_list|()
argument_list|)
expr_stmt|;
name|setLocalName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|parent
operator|=
name|dir
expr_stmt|;
block|}
block|}
end_class

end_unit

