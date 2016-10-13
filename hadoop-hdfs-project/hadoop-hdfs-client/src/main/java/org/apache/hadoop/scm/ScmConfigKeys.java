begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.scm
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|scm
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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * This class contains constants for configuration keys used in SCM  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|ScmConfigKeys
specifier|public
specifier|final
class|class
name|ScmConfigKeys
block|{
DECL|field|DFS_CONTAINER_IPC_PORT
specifier|public
specifier|static
specifier|final
name|String
name|DFS_CONTAINER_IPC_PORT
init|=
literal|"dfs.container.ipc"
decl_stmt|;
DECL|field|DFS_CONTAINER_IPC_PORT_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|DFS_CONTAINER_IPC_PORT_DEFAULT
init|=
literal|50011
decl_stmt|;
comment|// TODO : this is copied from OzoneConsts, may need to move to a better place
DECL|field|CHUNK_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|CHUNK_SIZE
init|=
literal|1
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
comment|// 1 MB
block|}
end_class

end_unit

