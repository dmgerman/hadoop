begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|conf
operator|.
name|StorageUnit
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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
name|hdds
operator|.
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerReportsProto
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
name|volume
operator|.
name|HddsVolume
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
name|KeyValueContainer
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
name|KeyValueContainerData
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
name|test
operator|.
name|GenericTestUtils
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
name|org
operator|.
name|mockito
operator|.
name|Mockito
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
name|ArrayList
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
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|assertEquals
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
name|assertFalse
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
name|assertNull
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
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_comment
comment|/**  * Class used to test ContainerSet operations.  */
end_comment

begin_class
DECL|class|TestContainerSet
specifier|public
class|class
name|TestContainerSet
block|{
annotation|@
name|Test
DECL|method|testAddGetRemoveContainer ()
specifier|public
name|void
name|testAddGetRemoveContainer
parameter_list|()
throws|throws
name|StorageContainerException
block|{
name|ContainerSet
name|containerSet
init|=
operator|new
name|ContainerSet
argument_list|()
decl_stmt|;
name|long
name|containerId
init|=
literal|100L
decl_stmt|;
name|ContainerProtos
operator|.
name|ContainerDataProto
operator|.
name|State
name|state
init|=
name|ContainerProtos
operator|.
name|ContainerDataProto
operator|.
name|State
operator|.
name|CLOSED
decl_stmt|;
name|KeyValueContainerData
name|kvData
init|=
operator|new
name|KeyValueContainerData
argument_list|(
name|containerId
argument_list|,
operator|(
name|long
operator|)
name|StorageUnit
operator|.
name|GB
operator|.
name|toBytes
argument_list|(
literal|5
argument_list|)
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|kvData
operator|.
name|setState
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|KeyValueContainer
name|keyValueContainer
init|=
operator|new
name|KeyValueContainer
argument_list|(
name|kvData
argument_list|,
operator|new
name|OzoneConfiguration
argument_list|()
argument_list|)
decl_stmt|;
comment|//addContainer
name|boolean
name|result
init|=
name|containerSet
operator|.
name|addContainer
argument_list|(
name|keyValueContainer
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|result
argument_list|)
expr_stmt|;
try|try
block|{
name|result
operator|=
name|containerSet
operator|.
name|addContainer
argument_list|(
name|keyValueContainer
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Adding same container ID twice should fail."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StorageContainerException
name|ex
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Container already exists with"
operator|+
literal|" container Id "
operator|+
name|containerId
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
comment|//getContainer
name|KeyValueContainer
name|container
init|=
operator|(
name|KeyValueContainer
operator|)
name|containerSet
operator|.
name|getContainer
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
name|KeyValueContainerData
name|keyValueContainerData
init|=
operator|(
name|KeyValueContainerData
operator|)
name|container
operator|.
name|getContainerData
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|containerId
argument_list|,
name|keyValueContainerData
operator|.
name|getContainerID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|state
argument_list|,
name|keyValueContainerData
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|containerSet
operator|.
name|getContainer
argument_list|(
literal|1000L
argument_list|)
argument_list|)
expr_stmt|;
comment|//removeContainer
name|assertTrue
argument_list|(
name|containerSet
operator|.
name|removeContainer
argument_list|(
name|containerId
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|containerSet
operator|.
name|removeContainer
argument_list|(
literal|1000L
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIteratorsAndCount ()
specifier|public
name|void
name|testIteratorsAndCount
parameter_list|()
throws|throws
name|StorageContainerException
block|{
name|ContainerSet
name|containerSet
init|=
name|createContainerSet
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|containerSet
operator|.
name|containerCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// Using containerIterator.
name|Iterator
argument_list|<
name|Container
argument_list|>
name|containerIterator
init|=
name|containerSet
operator|.
name|getContainerIterator
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|containerIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Container
name|kv
init|=
name|containerIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|ContainerData
name|containerData
init|=
name|kv
operator|.
name|getContainerData
argument_list|()
decl_stmt|;
name|long
name|containerId
init|=
name|containerData
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
if|if
condition|(
name|containerId
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
name|ContainerProtos
operator|.
name|ContainerDataProto
operator|.
name|State
operator|.
name|CLOSED
argument_list|,
name|containerData
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|ContainerProtos
operator|.
name|ContainerDataProto
operator|.
name|State
operator|.
name|OPEN
argument_list|,
name|containerData
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|count
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|count
argument_list|)
expr_stmt|;
comment|//Using containerMapIterator.
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|Long
argument_list|,
name|Container
argument_list|>
argument_list|>
name|containerMapIterator
init|=
name|containerSet
operator|.
name|getContainerMapIterator
argument_list|()
decl_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
while|while
condition|(
name|containerMapIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Container
name|kv
init|=
name|containerMapIterator
operator|.
name|next
argument_list|()
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|ContainerData
name|containerData
init|=
name|kv
operator|.
name|getContainerData
argument_list|()
decl_stmt|;
name|long
name|containerId
init|=
name|containerData
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
if|if
condition|(
name|containerId
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
name|ContainerProtos
operator|.
name|ContainerDataProto
operator|.
name|State
operator|.
name|CLOSED
argument_list|,
name|containerData
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|ContainerProtos
operator|.
name|ContainerDataProto
operator|.
name|State
operator|.
name|OPEN
argument_list|,
name|containerData
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|count
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIteratorPerVolume ()
specifier|public
name|void
name|testIteratorPerVolume
parameter_list|()
throws|throws
name|StorageContainerException
block|{
name|HddsVolume
name|vol1
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|HddsVolume
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|vol1
operator|.
name|getStorageID
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"uuid-1"
argument_list|)
expr_stmt|;
name|HddsVolume
name|vol2
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|HddsVolume
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|vol2
operator|.
name|getStorageID
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"uuid-2"
argument_list|)
expr_stmt|;
name|ContainerSet
name|containerSet
init|=
operator|new
name|ContainerSet
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|KeyValueContainerData
name|kvData
init|=
operator|new
name|KeyValueContainerData
argument_list|(
name|i
argument_list|,
operator|(
name|long
operator|)
name|StorageUnit
operator|.
name|GB
operator|.
name|toBytes
argument_list|(
literal|5
argument_list|)
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|kvData
operator|.
name|setVolume
argument_list|(
name|vol1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|kvData
operator|.
name|setVolume
argument_list|(
name|vol2
argument_list|)
expr_stmt|;
block|}
name|kvData
operator|.
name|setState
argument_list|(
name|ContainerProtos
operator|.
name|ContainerDataProto
operator|.
name|State
operator|.
name|CLOSED
argument_list|)
expr_stmt|;
name|KeyValueContainer
name|kv
init|=
operator|new
name|KeyValueContainer
argument_list|(
name|kvData
argument_list|,
operator|new
name|OzoneConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|containerSet
operator|.
name|addContainer
argument_list|(
name|kv
argument_list|)
expr_stmt|;
block|}
name|Iterator
argument_list|<
name|Container
argument_list|>
name|iter1
init|=
name|containerSet
operator|.
name|getContainerIterator
argument_list|(
name|vol1
argument_list|)
decl_stmt|;
name|int
name|count1
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|iter1
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Container
name|c
init|=
name|iter1
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
operator|(
name|c
operator|.
name|getContainerData
argument_list|()
operator|.
name|getContainerID
argument_list|()
operator|%
literal|2
operator|)
operator|==
literal|0
argument_list|)
expr_stmt|;
name|count1
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|count1
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Container
argument_list|>
name|iter2
init|=
name|containerSet
operator|.
name|getContainerIterator
argument_list|(
name|vol2
argument_list|)
decl_stmt|;
name|int
name|count2
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|iter2
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Container
name|c
init|=
name|iter2
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
operator|(
name|c
operator|.
name|getContainerData
argument_list|()
operator|.
name|getContainerID
argument_list|()
operator|%
literal|2
operator|)
operator|==
literal|1
argument_list|)
expr_stmt|;
name|count2
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|count2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetContainerReport ()
specifier|public
name|void
name|testGetContainerReport
parameter_list|()
throws|throws
name|IOException
block|{
name|ContainerSet
name|containerSet
init|=
name|createContainerSet
argument_list|()
decl_stmt|;
name|ContainerReportsProto
name|containerReportsRequestProto
init|=
name|containerSet
operator|.
name|getContainerReport
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|containerReportsRequestProto
operator|.
name|getReportsList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testListContainer ()
specifier|public
name|void
name|testListContainer
parameter_list|()
throws|throws
name|StorageContainerException
block|{
name|ContainerSet
name|containerSet
init|=
name|createContainerSet
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ContainerData
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|containerSet
operator|.
name|listContainer
argument_list|(
literal|2
argument_list|,
literal|5
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ContainerData
name|containerData
range|:
name|result
control|)
block|{
name|assertTrue
argument_list|(
name|containerData
operator|.
name|getContainerID
argument_list|()
operator|>=
literal|2
operator|&&
name|containerData
operator|.
name|getContainerID
argument_list|()
operator|<=
literal|6
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createContainerSet ()
specifier|private
name|ContainerSet
name|createContainerSet
parameter_list|()
throws|throws
name|StorageContainerException
block|{
name|ContainerSet
name|containerSet
init|=
operator|new
name|ContainerSet
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|KeyValueContainerData
name|kvData
init|=
operator|new
name|KeyValueContainerData
argument_list|(
name|i
argument_list|,
operator|(
name|long
operator|)
name|StorageUnit
operator|.
name|GB
operator|.
name|toBytes
argument_list|(
literal|5
argument_list|)
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|kvData
operator|.
name|setState
argument_list|(
name|ContainerProtos
operator|.
name|ContainerDataProto
operator|.
name|State
operator|.
name|CLOSED
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|kvData
operator|.
name|setState
argument_list|(
name|ContainerProtos
operator|.
name|ContainerDataProto
operator|.
name|State
operator|.
name|OPEN
argument_list|)
expr_stmt|;
block|}
name|KeyValueContainer
name|kv
init|=
operator|new
name|KeyValueContainer
argument_list|(
name|kvData
argument_list|,
operator|new
name|OzoneConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|containerSet
operator|.
name|addContainer
argument_list|(
name|kv
argument_list|)
expr_stmt|;
block|}
return|return
name|containerSet
return|;
block|}
block|}
end_class

end_unit

