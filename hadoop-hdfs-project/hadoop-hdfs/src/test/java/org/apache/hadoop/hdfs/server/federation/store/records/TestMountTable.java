begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.store.records
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
name|federation
operator|.
name|store
operator|.
name|records
package|;
end_package

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
name|assertTrue
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
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|federation
operator|.
name|resolver
operator|.
name|RemoteLocation
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
name|federation
operator|.
name|resolver
operator|.
name|order
operator|.
name|DestinationOrder
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
name|federation
operator|.
name|store
operator|.
name|driver
operator|.
name|StateStoreSerializer
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

begin_comment
comment|/**  * Test the Mount Table entry in the State Store.  */
end_comment

begin_class
DECL|class|TestMountTable
specifier|public
class|class
name|TestMountTable
block|{
DECL|field|SRC
specifier|private
specifier|static
specifier|final
name|String
name|SRC
init|=
literal|"/test"
decl_stmt|;
DECL|field|DST_NS_0
specifier|private
specifier|static
specifier|final
name|String
name|DST_NS_0
init|=
literal|"ns0"
decl_stmt|;
DECL|field|DST_NS_1
specifier|private
specifier|static
specifier|final
name|String
name|DST_NS_1
init|=
literal|"ns1"
decl_stmt|;
DECL|field|DST_PATH_0
specifier|private
specifier|static
specifier|final
name|String
name|DST_PATH_0
init|=
literal|"/path1"
decl_stmt|;
DECL|field|DST_PATH_1
specifier|private
specifier|static
specifier|final
name|String
name|DST_PATH_1
init|=
literal|"/path/path2"
decl_stmt|;
DECL|field|DST
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|RemoteLocation
argument_list|>
name|DST
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
static|static
block|{
name|DST
operator|.
name|add
argument_list|(
operator|new
name|RemoteLocation
argument_list|(
name|DST_NS_0
argument_list|,
name|DST_PATH_0
argument_list|)
argument_list|)
expr_stmt|;
name|DST
operator|.
name|add
argument_list|(
operator|new
name|RemoteLocation
argument_list|(
name|DST_NS_1
argument_list|,
name|DST_PATH_1
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|field|DST_MAP
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|DST_MAP
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
static|static
block|{
name|DST_MAP
operator|.
name|put
argument_list|(
name|DST_NS_0
argument_list|,
name|DST_PATH_0
argument_list|)
expr_stmt|;
name|DST_MAP
operator|.
name|put
argument_list|(
name|DST_NS_1
argument_list|,
name|DST_PATH_1
argument_list|)
expr_stmt|;
block|}
DECL|field|DATE_CREATED
specifier|private
specifier|static
specifier|final
name|long
name|DATE_CREATED
init|=
literal|100
decl_stmt|;
DECL|field|DATE_MOD
specifier|private
specifier|static
specifier|final
name|long
name|DATE_MOD
init|=
literal|200
decl_stmt|;
annotation|@
name|Test
DECL|method|testGetterSetter ()
specifier|public
name|void
name|testGetterSetter
parameter_list|()
throws|throws
name|IOException
block|{
name|MountTable
name|record
init|=
name|MountTable
operator|.
name|newInstance
argument_list|(
name|SRC
argument_list|,
name|DST_MAP
argument_list|)
decl_stmt|;
name|validateDestinations
argument_list|(
name|record
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SRC
argument_list|,
name|record
operator|.
name|getSourcePath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DST
argument_list|,
name|record
operator|.
name|getDestinations
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|DATE_CREATED
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|DATE_MOD
operator|>
literal|0
argument_list|)
expr_stmt|;
name|MountTable
name|record2
init|=
name|MountTable
operator|.
name|newInstance
argument_list|(
name|SRC
argument_list|,
name|DST_MAP
argument_list|,
name|DATE_CREATED
argument_list|,
name|DATE_MOD
argument_list|)
decl_stmt|;
name|validateDestinations
argument_list|(
name|record2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SRC
argument_list|,
name|record2
operator|.
name|getSourcePath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DST
argument_list|,
name|record2
operator|.
name|getDestinations
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DATE_CREATED
argument_list|,
name|record2
operator|.
name|getDateCreated
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DATE_MOD
argument_list|,
name|record2
operator|.
name|getDateModified
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|record
operator|.
name|isReadOnly
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DestinationOrder
operator|.
name|HASH
argument_list|,
name|record
operator|.
name|getDestOrder
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSerialization ()
specifier|public
name|void
name|testSerialization
parameter_list|()
throws|throws
name|IOException
block|{
name|testSerialization
argument_list|(
name|DestinationOrder
operator|.
name|RANDOM
argument_list|)
expr_stmt|;
name|testSerialization
argument_list|(
name|DestinationOrder
operator|.
name|HASH
argument_list|)
expr_stmt|;
name|testSerialization
argument_list|(
name|DestinationOrder
operator|.
name|LOCAL
argument_list|)
expr_stmt|;
block|}
DECL|method|testSerialization (final DestinationOrder order)
specifier|private
name|void
name|testSerialization
parameter_list|(
specifier|final
name|DestinationOrder
name|order
parameter_list|)
throws|throws
name|IOException
block|{
name|MountTable
name|record
init|=
name|MountTable
operator|.
name|newInstance
argument_list|(
name|SRC
argument_list|,
name|DST_MAP
argument_list|,
name|DATE_CREATED
argument_list|,
name|DATE_MOD
argument_list|)
decl_stmt|;
name|record
operator|.
name|setReadOnly
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|record
operator|.
name|setDestOrder
argument_list|(
name|order
argument_list|)
expr_stmt|;
name|StateStoreSerializer
name|serializer
init|=
name|StateStoreSerializer
operator|.
name|getSerializer
argument_list|()
decl_stmt|;
name|String
name|serializedString
init|=
name|serializer
operator|.
name|serializeString
argument_list|(
name|record
argument_list|)
decl_stmt|;
name|MountTable
name|record2
init|=
name|serializer
operator|.
name|deserialize
argument_list|(
name|serializedString
argument_list|,
name|MountTable
operator|.
name|class
argument_list|)
decl_stmt|;
name|validateDestinations
argument_list|(
name|record2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SRC
argument_list|,
name|record2
operator|.
name|getSourcePath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DST
argument_list|,
name|record2
operator|.
name|getDestinations
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DATE_CREATED
argument_list|,
name|record2
operator|.
name|getDateCreated
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DATE_MOD
argument_list|,
name|record2
operator|.
name|getDateModified
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|record2
operator|.
name|isReadOnly
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|order
argument_list|,
name|record2
operator|.
name|getDestOrder
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReadOnly ()
specifier|public
name|void
name|testReadOnly
parameter_list|()
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|dest
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|dest
operator|.
name|put
argument_list|(
name|DST_NS_0
argument_list|,
name|DST_PATH_0
argument_list|)
expr_stmt|;
name|dest
operator|.
name|put
argument_list|(
name|DST_NS_1
argument_list|,
name|DST_PATH_1
argument_list|)
expr_stmt|;
name|MountTable
name|record1
init|=
name|MountTable
operator|.
name|newInstance
argument_list|(
name|SRC
argument_list|,
name|dest
argument_list|)
decl_stmt|;
name|record1
operator|.
name|setReadOnly
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|validateDestinations
argument_list|(
name|record1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SRC
argument_list|,
name|record1
operator|.
name|getSourcePath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DST
argument_list|,
name|record1
operator|.
name|getDestinations
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|DATE_CREATED
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|DATE_MOD
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|record1
operator|.
name|isReadOnly
argument_list|()
argument_list|)
expr_stmt|;
name|MountTable
name|record2
init|=
name|MountTable
operator|.
name|newInstance
argument_list|(
name|SRC
argument_list|,
name|DST_MAP
argument_list|,
name|DATE_CREATED
argument_list|,
name|DATE_MOD
argument_list|)
decl_stmt|;
name|record2
operator|.
name|setReadOnly
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|validateDestinations
argument_list|(
name|record2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SRC
argument_list|,
name|record2
operator|.
name|getSourcePath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DST
argument_list|,
name|record2
operator|.
name|getDestinations
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DATE_CREATED
argument_list|,
name|record2
operator|.
name|getDateCreated
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DATE_MOD
argument_list|,
name|record2
operator|.
name|getDateModified
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|record2
operator|.
name|isReadOnly
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOrder ()
specifier|public
name|void
name|testOrder
parameter_list|()
throws|throws
name|IOException
block|{
name|testOrder
argument_list|(
name|DestinationOrder
operator|.
name|HASH
argument_list|)
expr_stmt|;
name|testOrder
argument_list|(
name|DestinationOrder
operator|.
name|LOCAL
argument_list|)
expr_stmt|;
name|testOrder
argument_list|(
name|DestinationOrder
operator|.
name|RANDOM
argument_list|)
expr_stmt|;
block|}
DECL|method|testOrder (final DestinationOrder order)
specifier|private
name|void
name|testOrder
parameter_list|(
specifier|final
name|DestinationOrder
name|order
parameter_list|)
throws|throws
name|IOException
block|{
name|MountTable
name|record
init|=
name|MountTable
operator|.
name|newInstance
argument_list|(
name|SRC
argument_list|,
name|DST_MAP
argument_list|,
name|DATE_CREATED
argument_list|,
name|DATE_MOD
argument_list|)
decl_stmt|;
name|record
operator|.
name|setDestOrder
argument_list|(
name|order
argument_list|)
expr_stmt|;
name|validateDestinations
argument_list|(
name|record
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SRC
argument_list|,
name|record
operator|.
name|getSourcePath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DST
argument_list|,
name|record
operator|.
name|getDestinations
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DATE_CREATED
argument_list|,
name|record
operator|.
name|getDateCreated
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DATE_MOD
argument_list|,
name|record
operator|.
name|getDateModified
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|order
argument_list|,
name|record
operator|.
name|getDestOrder
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|validateDestinations (MountTable record)
specifier|private
name|void
name|validateDestinations
parameter_list|(
name|MountTable
name|record
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|SRC
argument_list|,
name|record
operator|.
name|getSourcePath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|record
operator|.
name|getDestinations
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|RemoteLocation
name|location1
init|=
name|record
operator|.
name|getDestinations
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|DST_NS_0
argument_list|,
name|location1
operator|.
name|getNameserviceId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DST_PATH_0
argument_list|,
name|location1
operator|.
name|getDest
argument_list|()
argument_list|)
expr_stmt|;
name|RemoteLocation
name|location2
init|=
name|record
operator|.
name|getDestinations
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|DST_NS_1
argument_list|,
name|location2
operator|.
name|getNameserviceId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DST_PATH_1
argument_list|,
name|location2
operator|.
name|getDest
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

