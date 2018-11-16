begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.replication
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
name|replication
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
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|interfaces
operator|.
name|Container
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|interfaces
operator|.
name|ContainerPacker
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
name|ozone
operator|.
name|container
operator|.
name|keyvalue
operator|.
name|TarContainerPacker
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
name|Preconditions
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
name|ozone
operator|.
name|container
operator|.
name|ozoneimpl
operator|.
name|ContainerController
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * A naive implementation of the replication source which creates a tar file  * on-demand without pre-create the compressed archives.  */
end_comment

begin_class
DECL|class|OnDemandContainerReplicationSource
specifier|public
class|class
name|OnDemandContainerReplicationSource
implements|implements
name|ContainerReplicationSource
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ContainerReplicationSource
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|controller
specifier|private
name|ContainerController
name|controller
decl_stmt|;
DECL|field|packer
specifier|private
name|ContainerPacker
name|packer
init|=
operator|new
name|TarContainerPacker
argument_list|()
decl_stmt|;
DECL|method|OnDemandContainerReplicationSource ( ContainerController controller)
specifier|public
name|OnDemandContainerReplicationSource
parameter_list|(
name|ContainerController
name|controller
parameter_list|)
block|{
name|this
operator|.
name|controller
operator|=
name|controller
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|prepare (long containerId)
specifier|public
name|void
name|prepare
parameter_list|(
name|long
name|containerId
parameter_list|)
block|{    }
annotation|@
name|Override
DECL|method|copyData (long containerId, OutputStream destination)
specifier|public
name|void
name|copyData
parameter_list|(
name|long
name|containerId
parameter_list|,
name|OutputStream
name|destination
parameter_list|)
throws|throws
name|IOException
block|{
name|Container
name|container
init|=
name|controller
operator|.
name|getContainer
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|container
argument_list|,
literal|"Container is not found "
operator|+
name|containerId
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|container
operator|.
name|getContainerType
argument_list|()
condition|)
block|{
case|case
name|KeyValueContainer
case|:
name|packer
operator|.
name|pack
argument_list|(
name|container
argument_list|,
name|destination
argument_list|)
expr_stmt|;
break|break;
default|default:
name|LOG
operator|.
name|warn
argument_list|(
literal|"Container type "
operator|+
name|container
operator|.
name|getContainerType
argument_list|()
operator|+
literal|" is not replicable as no compression algorithm for that."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

