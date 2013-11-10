begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.nfs.nfs3
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|nfs
operator|.
name|nfs3
package|;
end_package

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
name|hdfs
operator|.
name|nfs
operator|.
name|mount
operator|.
name|Mountd
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
name|nfs
operator|.
name|nfs3
operator|.
name|Nfs3Base
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

begin_comment
comment|/**  * Nfs server. Supports NFS v3 using {@link RpcProgramNfs3}.  * Currently Mountd program is also started inside this class.  * Only TCP server is supported and UDP is not supported.  */
end_comment

begin_class
DECL|class|Nfs3
specifier|public
class|class
name|Nfs3
extends|extends
name|Nfs3Base
block|{
DECL|field|mountd
specifier|private
name|Mountd
name|mountd
decl_stmt|;
static|static
block|{
name|Configuration
operator|.
name|addDefaultResource
argument_list|(
literal|"hdfs-default.xml"
argument_list|)
expr_stmt|;
name|Configuration
operator|.
name|addDefaultResource
argument_list|(
literal|"hdfs-site.xml"
argument_list|)
expr_stmt|;
block|}
DECL|method|Nfs3 (Configuration conf)
specifier|public
name|Nfs3
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
operator|new
name|RpcProgramNfs3
argument_list|(
name|conf
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|mountd
operator|=
operator|new
name|Mountd
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|getMountd ()
specifier|public
name|Mountd
name|getMountd
parameter_list|()
block|{
return|return
name|mountd
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|startServiceInternal (boolean register)
specifier|public
name|void
name|startServiceInternal
parameter_list|(
name|boolean
name|register
parameter_list|)
throws|throws
name|IOException
block|{
name|mountd
operator|.
name|start
argument_list|(
name|register
argument_list|)
expr_stmt|;
comment|// Start mountd
name|start
argument_list|(
name|register
argument_list|)
expr_stmt|;
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|StringUtils
operator|.
name|startupShutdownMessage
argument_list|(
name|Nfs3
operator|.
name|class
argument_list|,
name|args
argument_list|,
name|LOG
argument_list|)
expr_stmt|;
specifier|final
name|Nfs3
name|nfsServer
init|=
operator|new
name|Nfs3
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|nfsServer
operator|.
name|startServiceInternal
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

