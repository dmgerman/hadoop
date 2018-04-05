begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.freon
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|freon
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
name|ozone
operator|.
name|MiniOzoneClassicCluster
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
name|MiniOzoneCluster
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
name|OzoneConfigKeys
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
name|OzoneConsts
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
name|ToolRunner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|ByteArrayOutputStream
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
name|io
operator|.
name|PrintStream
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
name|List
import|;
end_import

begin_comment
comment|/**  * Tests Freon, with MiniOzoneCluster and validate data.  */
end_comment

begin_class
DECL|class|TestDataValidate
specifier|public
class|class
name|TestDataValidate
block|{
DECL|field|cluster
specifier|private
specifier|static
name|MiniOzoneCluster
name|cluster
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|OzoneConfiguration
name|conf
decl_stmt|;
comment|/**    * Create a MiniDFSCluster for testing.    *<p>    * Ozone is made active by setting OZONE_ENABLED = true and    * OZONE_HANDLER_TYPE_KEY = "distributed"    *    * @throws IOException    */
annotation|@
name|BeforeClass
DECL|method|init ()
specifier|public
specifier|static
name|void
name|init
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|OzoneConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_HANDLER_TYPE_KEY
argument_list|,
name|OzoneConsts
operator|.
name|OZONE_HANDLER_DISTRIBUTED
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniOzoneClassicCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|setHandlerType
argument_list|(
name|OzoneConsts
operator|.
name|OZONE_HANDLER_DISTRIBUTED
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|5
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
comment|/**    * Shutdown MiniDFSCluster.    */
annotation|@
name|AfterClass
DECL|method|shutdown ()
specifier|public
specifier|static
name|void
name|shutdown
parameter_list|()
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|ratisTestLargeKey ()
specifier|public
name|void
name|ratisTestLargeKey
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|args
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"-validateWrites"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"-numOfVolumes"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"1"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"-numOfBuckets"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"1"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"-numOfKeys"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"1"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"-ratis"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"3"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"-keySize"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"104857600"
argument_list|)
expr_stmt|;
name|Freon
name|freon
init|=
operator|new
name|Freon
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|int
name|res
init|=
name|ToolRunner
operator|.
name|run
argument_list|(
name|conf
argument_list|,
name|freon
argument_list|,
name|args
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|freon
operator|.
name|getNumberOfVolumesCreated
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|freon
operator|.
name|getNumberOfBucketsCreated
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|freon
operator|.
name|getNumberOfKeysAdded
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|freon
operator|.
name|getUnsuccessfulValidationCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|standaloneTestLargeKey ()
specifier|public
name|void
name|standaloneTestLargeKey
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|args
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"-validateWrites"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"-numOfVolumes"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"1"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"-numOfBuckets"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"1"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"-numOfKeys"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"1"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"-keySize"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"104857600"
argument_list|)
expr_stmt|;
name|Freon
name|freon
init|=
operator|new
name|Freon
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|int
name|res
init|=
name|ToolRunner
operator|.
name|run
argument_list|(
name|conf
argument_list|,
name|freon
argument_list|,
name|args
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|freon
operator|.
name|getNumberOfVolumesCreated
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|freon
operator|.
name|getNumberOfBucketsCreated
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|freon
operator|.
name|getNumberOfKeysAdded
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|freon
operator|.
name|getUnsuccessfulValidationCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|validateWriteTest ()
specifier|public
name|void
name|validateWriteTest
parameter_list|()
throws|throws
name|Exception
block|{
name|PrintStream
name|originalStream
init|=
name|System
operator|.
name|out
decl_stmt|;
name|ByteArrayOutputStream
name|outStream
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|System
operator|.
name|setOut
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|outStream
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|args
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"-validateWrites"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"-numOfVolumes"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"2"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"-numOfBuckets"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"5"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"-numOfKeys"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"10"
argument_list|)
expr_stmt|;
name|Freon
name|freon
init|=
operator|new
name|Freon
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|int
name|res
init|=
name|ToolRunner
operator|.
name|run
argument_list|(
name|conf
argument_list|,
name|freon
argument_list|,
name|args
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|res
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|freon
operator|.
name|getNumberOfVolumesCreated
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|freon
operator|.
name|getNumberOfBucketsCreated
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|freon
operator|.
name|getNumberOfKeysAdded
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|freon
operator|.
name|getValidateWrites
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotEquals
argument_list|(
literal|0
argument_list|,
name|freon
operator|.
name|getTotalKeysValidated
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotEquals
argument_list|(
literal|0
argument_list|,
name|freon
operator|.
name|getSuccessfulValidationCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|freon
operator|.
name|getUnsuccessfulValidationCount
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setOut
argument_list|(
name|originalStream
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

