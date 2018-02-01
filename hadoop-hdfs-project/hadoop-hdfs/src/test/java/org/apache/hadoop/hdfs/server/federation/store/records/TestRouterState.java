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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|router
operator|.
name|RouterServiceState
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
comment|/**  * Test the Router State records.  */
end_comment

begin_class
DECL|class|TestRouterState
specifier|public
class|class
name|TestRouterState
block|{
DECL|field|ADDRESS
specifier|private
specifier|static
specifier|final
name|String
name|ADDRESS
init|=
literal|"address"
decl_stmt|;
DECL|field|VERSION
specifier|private
specifier|static
specifier|final
name|String
name|VERSION
init|=
literal|"version"
decl_stmt|;
DECL|field|COMPILE_INFO
specifier|private
specifier|static
specifier|final
name|String
name|COMPILE_INFO
init|=
literal|"compileInfo"
decl_stmt|;
DECL|field|START_TIME
specifier|private
specifier|static
specifier|final
name|long
name|START_TIME
init|=
literal|100
decl_stmt|;
DECL|field|DATE_MODIFIED
specifier|private
specifier|static
specifier|final
name|long
name|DATE_MODIFIED
init|=
literal|200
decl_stmt|;
DECL|field|DATE_CREATED
specifier|private
specifier|static
specifier|final
name|long
name|DATE_CREATED
init|=
literal|300
decl_stmt|;
DECL|field|FILE_RESOLVER_VERSION
specifier|private
specifier|static
specifier|final
name|long
name|FILE_RESOLVER_VERSION
init|=
literal|500
decl_stmt|;
DECL|field|STATE
specifier|private
specifier|static
specifier|final
name|RouterServiceState
name|STATE
init|=
name|RouterServiceState
operator|.
name|RUNNING
decl_stmt|;
DECL|method|generateRecord ()
specifier|private
name|RouterState
name|generateRecord
parameter_list|()
throws|throws
name|IOException
block|{
name|RouterState
name|record
init|=
name|RouterState
operator|.
name|newInstance
argument_list|(
name|ADDRESS
argument_list|,
name|START_TIME
argument_list|,
name|STATE
argument_list|)
decl_stmt|;
name|record
operator|.
name|setVersion
argument_list|(
name|VERSION
argument_list|)
expr_stmt|;
name|record
operator|.
name|setCompileInfo
argument_list|(
name|COMPILE_INFO
argument_list|)
expr_stmt|;
name|record
operator|.
name|setDateCreated
argument_list|(
name|DATE_CREATED
argument_list|)
expr_stmt|;
name|record
operator|.
name|setDateModified
argument_list|(
name|DATE_MODIFIED
argument_list|)
expr_stmt|;
name|StateStoreVersion
name|version
init|=
name|StateStoreVersion
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|version
operator|.
name|setMountTableVersion
argument_list|(
name|FILE_RESOLVER_VERSION
argument_list|)
expr_stmt|;
name|record
operator|.
name|setStateStoreVersion
argument_list|(
name|version
argument_list|)
expr_stmt|;
return|return
name|record
return|;
block|}
DECL|method|validateRecord (RouterState record)
specifier|private
name|void
name|validateRecord
parameter_list|(
name|RouterState
name|record
parameter_list|)
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
name|ADDRESS
argument_list|,
name|record
operator|.
name|getAddress
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|START_TIME
argument_list|,
name|record
operator|.
name|getDateStarted
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|STATE
argument_list|,
name|record
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|COMPILE_INFO
argument_list|,
name|record
operator|.
name|getCompileInfo
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|VERSION
argument_list|,
name|record
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|StateStoreVersion
name|version
init|=
name|record
operator|.
name|getStateStoreVersion
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|FILE_RESOLVER_VERSION
argument_list|,
name|version
operator|.
name|getMountTableVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|RouterState
name|record
init|=
name|generateRecord
argument_list|()
decl_stmt|;
name|validateRecord
argument_list|(
name|record
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
name|RouterState
name|record
init|=
name|generateRecord
argument_list|()
decl_stmt|;
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
name|RouterState
name|newRecord
init|=
name|serializer
operator|.
name|deserialize
argument_list|(
name|serializedString
argument_list|,
name|RouterState
operator|.
name|class
argument_list|)
decl_stmt|;
name|validateRecord
argument_list|(
name|newRecord
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

