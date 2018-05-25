begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.impl
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
name|impl
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
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|StorageContainerException
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

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_comment
comment|/**  * Class to perform KeyValue Container operations.  */
end_comment

begin_class
DECL|class|KeyValueContainer
specifier|public
class|class
name|KeyValueContainer
implements|implements
name|Container
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|Container
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|containerData
specifier|private
name|KeyValueContainerData
name|containerData
decl_stmt|;
DECL|method|KeyValueContainer (KeyValueContainerData containerData)
specifier|public
name|KeyValueContainer
parameter_list|(
name|KeyValueContainerData
name|containerData
parameter_list|)
block|{
name|this
operator|.
name|containerData
operator|=
name|containerData
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|create (ContainerData cData)
specifier|public
name|void
name|create
parameter_list|(
name|ContainerData
name|cData
parameter_list|)
throws|throws
name|StorageContainerException
block|{    }
annotation|@
name|Override
DECL|method|delete (boolean forceDelete)
specifier|public
name|void
name|delete
parameter_list|(
name|boolean
name|forceDelete
parameter_list|)
throws|throws
name|StorageContainerException
block|{    }
annotation|@
name|Override
DECL|method|update (boolean forceUpdate)
specifier|public
name|void
name|update
parameter_list|(
name|boolean
name|forceUpdate
parameter_list|)
throws|throws
name|StorageContainerException
block|{    }
annotation|@
name|Override
DECL|method|getContainerData ()
specifier|public
name|ContainerData
name|getContainerData
parameter_list|()
throws|throws
name|StorageContainerException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|StorageContainerException
throws|,
name|NoSuchAlgorithmException
block|{    }
block|}
end_class

end_unit

