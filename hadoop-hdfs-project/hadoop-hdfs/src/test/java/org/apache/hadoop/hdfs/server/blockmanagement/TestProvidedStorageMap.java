begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.blockmanagement
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
name|blockmanagement
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
name|conf
operator|.
name|Configurable
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
name|fs
operator|.
name|StorageType
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
name|DFSConfigKeys
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
name|DFSTestUtil
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
name|HdfsConfiguration
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
name|protocol
operator|.
name|Block
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
name|server
operator|.
name|protocol
operator|.
name|DatanodeStorage
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
name|util
operator|.
name|RwLock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

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
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_comment
comment|/**  * This class tests the {@link ProvidedStorageMap}.  */
end_comment

begin_class
DECL|class|TestProvidedStorageMap
specifier|public
class|class
name|TestProvidedStorageMap
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|bm
specifier|private
name|BlockManager
name|bm
decl_stmt|;
DECL|field|nameSystemLock
specifier|private
name|RwLock
name|nameSystemLock
decl_stmt|;
DECL|field|providedStorageID
specifier|private
name|String
name|providedStorageID
decl_stmt|;
DECL|class|TestBlockProvider
specifier|static
class|class
name|TestBlockProvider
extends|extends
name|BlockProvider
implements|implements
name|Configurable
block|{
annotation|@
name|Override
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{     }
annotation|@
name|Override
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|iterator ()
specifier|public
name|Iterator
argument_list|<
name|Block
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|Block
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|Block
name|next
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
block|}
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|providedStorageID
operator|=
name|DFSConfigKeys
operator|.
name|DFS_PROVIDER_STORAGEUUID_DEFAULT
expr_stmt|;
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_PROVIDER_STORAGEUUID
argument_list|,
name|providedStorageID
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_PROVIDED_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_BLOCK_PROVIDER_CLASS
argument_list|,
name|TestBlockProvider
operator|.
name|class
argument_list|,
name|BlockProvider
operator|.
name|class
argument_list|)
expr_stmt|;
name|bm
operator|=
name|mock
argument_list|(
name|BlockManager
operator|.
name|class
argument_list|)
expr_stmt|;
name|nameSystemLock
operator|=
name|mock
argument_list|(
name|RwLock
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|createDatanodeDescriptor (int port)
specifier|private
name|DatanodeDescriptor
name|createDatanodeDescriptor
parameter_list|(
name|int
name|port
parameter_list|)
block|{
return|return
name|DFSTestUtil
operator|.
name|getDatanodeDescriptor
argument_list|(
literal|"127.0.0.1"
argument_list|,
name|port
argument_list|,
literal|"defaultRack"
argument_list|,
literal|"localhost"
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testProvidedStorageMap ()
specifier|public
name|void
name|testProvidedStorageMap
parameter_list|()
throws|throws
name|IOException
block|{
name|ProvidedStorageMap
name|providedMap
init|=
operator|new
name|ProvidedStorageMap
argument_list|(
name|nameSystemLock
argument_list|,
name|bm
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|DatanodeStorageInfo
name|providedMapStorage
init|=
name|providedMap
operator|.
name|getProvidedStorageInfo
argument_list|()
decl_stmt|;
comment|//the provided storage cannot be null
name|assertNotNull
argument_list|(
name|providedMapStorage
argument_list|)
expr_stmt|;
comment|//create a datanode
name|DatanodeDescriptor
name|dn1
init|=
name|createDatanodeDescriptor
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
comment|//associate two storages to the datanode
name|DatanodeStorage
name|dn1ProvidedStorage
init|=
operator|new
name|DatanodeStorage
argument_list|(
name|providedStorageID
argument_list|,
name|DatanodeStorage
operator|.
name|State
operator|.
name|NORMAL
argument_list|,
name|StorageType
operator|.
name|PROVIDED
argument_list|)
decl_stmt|;
name|DatanodeStorage
name|dn1DiskStorage
init|=
operator|new
name|DatanodeStorage
argument_list|(
literal|"sid-1"
argument_list|,
name|DatanodeStorage
operator|.
name|State
operator|.
name|NORMAL
argument_list|,
name|StorageType
operator|.
name|DISK
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|nameSystemLock
operator|.
name|hasWriteLock
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|DatanodeStorageInfo
name|dns1Provided
init|=
name|providedMap
operator|.
name|getStorage
argument_list|(
name|dn1
argument_list|,
name|dn1ProvidedStorage
argument_list|)
decl_stmt|;
name|DatanodeStorageInfo
name|dns1Disk
init|=
name|providedMap
operator|.
name|getStorage
argument_list|(
name|dn1
argument_list|,
name|dn1DiskStorage
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"The provided storages should be equal"
argument_list|,
name|dns1Provided
operator|==
name|providedMapStorage
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Disk storage has not yet been registered with block manager"
argument_list|,
name|dns1Disk
operator|==
literal|null
argument_list|)
expr_stmt|;
comment|//add the disk storage to the datanode.
name|DatanodeStorageInfo
name|dnsDisk
init|=
operator|new
name|DatanodeStorageInfo
argument_list|(
name|dn1
argument_list|,
name|dn1DiskStorage
argument_list|)
decl_stmt|;
name|dn1
operator|.
name|injectStorage
argument_list|(
name|dnsDisk
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Disk storage must match the injected storage info"
argument_list|,
name|dnsDisk
operator|==
name|providedMap
operator|.
name|getStorage
argument_list|(
name|dn1
argument_list|,
name|dn1DiskStorage
argument_list|)
argument_list|)
expr_stmt|;
comment|//create a 2nd datanode
name|DatanodeDescriptor
name|dn2
init|=
name|createDatanodeDescriptor
argument_list|(
literal|5010
argument_list|)
decl_stmt|;
comment|//associate a provided storage with the datanode
name|DatanodeStorage
name|dn2ProvidedStorage
init|=
operator|new
name|DatanodeStorage
argument_list|(
name|providedStorageID
argument_list|,
name|DatanodeStorage
operator|.
name|State
operator|.
name|NORMAL
argument_list|,
name|StorageType
operator|.
name|PROVIDED
argument_list|)
decl_stmt|;
name|DatanodeStorageInfo
name|dns2Provided
init|=
name|providedMap
operator|.
name|getStorage
argument_list|(
name|dn2
argument_list|,
name|dn2ProvidedStorage
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"The provided storages should be equal"
argument_list|,
name|dns2Provided
operator|==
name|providedMapStorage
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"The DatanodeDescriptor should contain the provided storage"
argument_list|,
name|dn2
operator|.
name|getStorageInfo
argument_list|(
name|providedStorageID
argument_list|)
operator|==
name|providedMapStorage
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

